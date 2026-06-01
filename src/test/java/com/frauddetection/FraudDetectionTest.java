package com.frauddetection;

import com.frauddetection.config.TestDataConfig;
import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.service.FraudAlertService;
import com.frauddetection.service.TransactionService;
import com.frauddetection.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to demonstrate the fraud detection features
 */
@SpringBootTest
public class FraudDetectionTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private FraudAlertService fraudAlertService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestDataConfig testDataConfig;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Clean up database before each test
        testDataConfig.cleanupAll();

        // Create a test user for our transactions
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");

        // Add Bangalore as a known location
        user.addKnownLocation("Bangalore");

        testUser = userService.registerUser(user);
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        testDataConfig.cleanupAll();
    }

    /**
     * Test for large transaction detection (above ₹5000)
     */
    @Test
    public void testLargeTransactionDetection() {
        // Create a large transaction (above ₹5000)
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setAmount(new BigDecimal("6000"));
        transaction.setLocation("Bangalore");
        transaction.setTransactionType("PURCHASE");
        transaction.setDescription("Large purchase");
        transaction.setRecipientAccount("RECIPIENT123");

        // Process the transaction
        Transaction processedTransaction = transactionService.processTransaction(transaction);

        // Verify that the transaction was flagged
        assertTrue(processedTransaction.isFlagged());
        assertTrue(processedTransaction.getFlagReason().contains("Large transaction"));
        assertEquals(Transaction.TransactionStatus.FLAGGED, processedTransaction.getStatus());

        // Verify that a fraud alert was created
        List<FraudAlert> alerts = fraudAlertService.getUserAlerts(testUser);
        assertTrue(alerts.stream().anyMatch(alert -> alert.getAlertType() == FraudAlert.AlertType.LARGE_TRANSACTION));
    }

    /**
     * Test for unusual location detection
     */
    @Test
    public void testUnusualLocationDetection() {
        // Create a transaction from an unknown location
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setLocation("Mumbai"); // Not in user's known locations
        transaction.setTransactionType("PURCHASE");
        transaction.setDescription("Purchase from new location");
        transaction.setRecipientAccount("RECIPIENT123");

        // Process the transaction
        Transaction processedTransaction = transactionService.processTransaction(transaction);

        // Verify that the transaction was flagged
        assertTrue(processedTransaction.isFlagged());
        assertTrue(processedTransaction.getFlagReason().contains("Unusual location"));
        assertEquals(Transaction.TransactionStatus.FLAGGED, processedTransaction.getStatus());

        // Verify that a fraud alert was created
        List<FraudAlert> alerts = fraudAlertService.getUserAlerts(testUser);
        assertTrue(alerts.stream().anyMatch(alert -> alert.getAlertType() == FraudAlert.AlertType.UNUSUAL_LOCATION));
    }

    /**
     * Test for late-night transaction detection (between 12 AM and 5 AM)
     */
    @Test
    public void testLateNightTransactionDetection() {
        // Create a transaction with a late-night timestamp
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setLocation("Bangalore");
        transaction.setTransactionType("PURCHASE");
        transaction.setDescription("Late night purchase");
        transaction.setRecipientAccount("RECIPIENT123");

        // Set transaction time to 2 AM by creating a specific LocalDateTime
        LocalDateTime lateNight = LocalDateTime.now().withHour(2).withMinute(30).withSecond(0);
        transaction.setTransactionDate(lateNight);

        // Process the transaction
        Transaction processedTransaction = transactionService.processTransaction(transaction);

        // Verify that the transaction was flagged
        assertTrue(processedTransaction.isFlagged(), "Transaction should be flagged for late-night transaction");
        assertTrue(processedTransaction.getFlagReason().contains("Late-night transaction"),
                "Flag reason should contain 'Late-night transaction', got: " + processedTransaction.getFlagReason());
        assertEquals(Transaction.TransactionStatus.FLAGGED, processedTransaction.getStatus());

        // Verify that a fraud alert was created
        List<FraudAlert> alerts = fraudAlertService.getUserAlerts(testUser);
        assertTrue(
                alerts.stream().anyMatch(alert -> alert.getAlertType() == FraudAlert.AlertType.LATE_NIGHT_TRANSACTION),
                "No LATE_NIGHT_TRANSACTION alert found");
    }

    /**
     * Test for multiple failed login attempts
     */
    @Test
    public void testMultipleFailedLoginAttempts() {
        // Simulate 3 failed login attempts
        for (int i = 0; i < 3; i++) {
            userService.updateLoginAttempts(testUser, false);
        }

        // Verify that the account is locked
        assertTrue(testUser.getAccountLocked());
        assertNotNull(testUser.getAccountLockedUntil());
        assertTrue(userService.isAccountLocked(testUser));
    }

    /**
     * Test for frequent transactions detection (more than 3 transactions within 1
     * minute)
     */
    @Test
    public void testFrequentTransactionsDetection() {
        // Create 3 transactions in quick succession
        for (int i = 0; i < 3; i++) {
            Transaction transaction = new Transaction();
            transaction.setUser(testUser);
            transaction.setAmount(new BigDecimal("100"));
            transaction.setLocation("Bangalore");
            transaction.setTransactionType("PURCHASE");
            transaction.setDescription("Purchase " + i);
            transaction.setRecipientAccount("RECIPIENT123");

            transactionService.processTransaction(transaction);
        }

        // Create a 4th transaction that should trigger the frequent transactions alert
        Transaction transaction = new Transaction();
        transaction.setUser(testUser);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setLocation("Bangalore");
        transaction.setTransactionType("PURCHASE");
        transaction.setDescription("4th Purchase");
        transaction.setRecipientAccount("RECIPIENT123");

        // Process the transaction
        Transaction processedTransaction = transactionService.processTransaction(transaction);

        // Verify that the transaction was flagged
        assertTrue(processedTransaction.isFlagged());
        assertTrue(processedTransaction.getFlagReason().contains("Frequent transactions"));
        assertEquals(Transaction.TransactionStatus.FLAGGED, processedTransaction.getStatus());

        // Verify that a fraud alert was created
        List<FraudAlert> alerts = fraudAlertService.getUserAlerts(testUser);
        assertTrue(
                alerts.stream().anyMatch(alert -> alert.getAlertType() == FraudAlert.AlertType.FREQUENT_TRANSACTIONS));
    }
}

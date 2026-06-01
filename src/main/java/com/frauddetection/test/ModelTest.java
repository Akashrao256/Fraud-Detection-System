package com.frauddetection.test;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Simple test class to verify that model classes can be instantiated
 * without Lombok after removing Lombok annotations.
 */
public class ModelTest {

    public static void main(String[] args) {
        System.out.println("Testing model classes after Lombok removal...");
        
        // Test User class
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");
        user.setKnownLocations(new HashSet<>());
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        
        System.out.println("User created: " + user.getUsername() + " / " + user.getEmail());
        
        // Test Transaction class
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType("PAYMENT");
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setLocation("New York");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        System.out.println("Transaction created: " + transaction.getTransactionType() + 
                " / Amount: " + transaction.getAmount());
        
        // Test FraudAlert class
        FraudAlert alert = new FraudAlert();
        alert.setUser(user);
        alert.setTransaction(transaction);
        alert.setAlertType(FraudAlert.AlertType.LARGE_TRANSACTION);
        alert.setAlertDate(LocalDateTime.now());
        alert.setDescription("Large transaction detected");
        alert.setResolved(false);
        
        System.out.println("FraudAlert created: " + alert.getAlertType() + 
                " / Description: " + alert.getDescription());
        
        System.out.println("All model classes instantiated successfully!");
    }
}

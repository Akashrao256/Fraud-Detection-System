/*TransactionService.java*/
package com.frauddetection.service;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.repository.TransactionRepository;
// Lombok annotations removed
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudAlertService fraudAlertService;
    private final EmailService emailService;

    public TransactionService(TransactionRepository transactionRepository,
            FraudAlertService fraudAlertService,
            EmailService emailService) {
        this.transactionRepository = transactionRepository;
        this.fraudAlertService = fraudAlertService;
        this.emailService = emailService;
    }

    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = new BigDecimal("5000");
    private static final int FREQUENT_TRANSACTION_THRESHOLD = 3;
    private static final int FREQUENT_TRANSACTION_MINUTES = 1;
    private static final int LARGE_TRANSACTION_RISK = 40;
    private static final int UNUSUAL_LOCATION_RISK = 30;
    private static final int FREQUENT_TRANSACTION_RISK = 20;
    private static final int LATE_NIGHT_TRANSACTION_RISK = 15;

    public Transaction processTransaction(Transaction transaction) {
        // First, save the transaction
        transaction = transactionRepository.save(transaction);

        // Then check for fraud indicators, calculate risk, and create alerts
        int riskScore = checkForFraudIndicators(transaction);
        transaction.setRiskScore(riskScore);
        transaction.setRiskLevel(determineRiskLevel(riskScore));

        if (transaction.isFlagged()) {
            // Transaction is flagged, set status to FLAGGED and generate OTP if needed
            transaction.setStatus(Transaction.TransactionStatus.FLAGGED);

            // OTP verification removed as per requirement
        } else {
            // Transaction is not flagged, set status to COMPLETED
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        }

        return transactionRepository.save(transaction);
    }

    private int checkForFraudIndicators(Transaction transaction) {
        User user = transaction.getUser();
        int riskScore = 0;

        // Check for large transaction
        if (isLargeTransaction(transaction)) {
            transaction.setFlagged(true);
            addFlagReason(transaction, "Large transaction");
            riskScore += LARGE_TRANSACTION_RISK;

            fraudAlertService.createFraudAlert(user, transaction, FraudAlert.AlertType.LARGE_TRANSACTION,
                    "Transaction amount exceeds INR 5000");
        }

        // Check for unusual location
        if (isUnusualLocation(transaction)) {
            transaction.setFlagged(true);
            addFlagReason(transaction, "Unusual location");
            riskScore += UNUSUAL_LOCATION_RISK;

            fraudAlertService.createFraudAlert(user, transaction, FraudAlert.AlertType.UNUSUAL_LOCATION,
                    "Transaction from unknown location: " + transaction.getLocation());
        }

        // Check for frequent transactions
        if (isFrequentTransaction(transaction)) {
            transaction.setFlagged(true);
            addFlagReason(transaction, "Frequent transactions");
            riskScore += FREQUENT_TRANSACTION_RISK;

            fraudAlertService.createFraudAlert(user, transaction, FraudAlert.AlertType.FREQUENT_TRANSACTIONS,
                    "More than 3 transactions within 1 minute");
        }

        // Check for late-night transaction
        if (isLateNightTransaction(transaction)) {
            transaction.setFlagged(true);
            addFlagReason(transaction, "Late-night transaction");
            riskScore += LATE_NIGHT_TRANSACTION_RISK;

            fraudAlertService.createFraudAlert(user, transaction, FraudAlert.AlertType.LATE_NIGHT_TRANSACTION,
                    "Transaction occurred between 12 AM and 5 AM");
        }

        return capRiskScore(riskScore);
    }

    private boolean isLargeTransaction(Transaction transaction) {
        return transaction.getAmount().compareTo(LARGE_TRANSACTION_THRESHOLD) >= 0;
    }

    private boolean isUnusualLocation(Transaction transaction) {
        User user = transaction.getUser();
        return !user.isKnownLocation(transaction.getLocation());
    }

    private boolean isFrequentTransaction(Transaction transaction) {
        User user = transaction.getUser();
        LocalDateTime endTime = transaction.getTransactionDate();
        LocalDateTime startTime = endTime.minusMinutes(FREQUENT_TRANSACTION_MINUTES);

        int recentTransactions = transactionRepository.countTransactionsInTimeRange(user, startTime, endTime);

        return recentTransactions >= FREQUENT_TRANSACTION_THRESHOLD;
    }

    private boolean isLateNightTransaction(Transaction transaction) {
        LocalTime transactionTime = transaction.getTransactionDate().toLocalTime();
        LocalTime midnight = LocalTime.of(0, 0);
        LocalTime fiveAM = LocalTime.of(5, 0);

        return (transactionTime.isAfter(midnight) || transactionTime.equals(midnight)) &&
                (transactionTime.isBefore(fiveAM));
    }

    private void addFlagReason(Transaction transaction, String reason) {
        if (transaction.getFlagReason() == null || transaction.getFlagReason().isBlank()) {
            transaction.setFlagReason(reason);
        } else if (!transaction.getFlagReason().contains(reason)) {
            transaction.setFlagReason(transaction.getFlagReason() + ", " + reason);
        }
    }

    private int capRiskScore(int riskScore) {
        return Math.min(100, Math.max(0, riskScore));
    }

    public String determineRiskLevel(int riskScore) {
        if (riskScore >= 70) {
            return "HIGH";
        }
        if (riskScore >= 30) {
            return "MEDIUM";
        }
        return "LOW";
    }

    // OTP verification methods removed as per requirement

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getFlaggedTransactions() {
        return transactionRepository.findByFlagged(true);
    }
}

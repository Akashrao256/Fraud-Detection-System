/*FraudDetectionDemo.java*/
package com.frauddetection.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple demonstration of the fraud detection system without requiring Spring Boot.
 * This class implements the core fraud detection logic for demonstration purposes.
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

public class FraudDetectionDemo {

    // Constants for fraud detection thresholds
    private static final BigDecimal LARGE_TRANSACTION_THRESHOLD = new BigDecimal("5000");
    private static final int FREQUENT_TRANSACTION_THRESHOLD = 3;
    private static final int FREQUENT_TRANSACTION_MINUTES = 1;

    public static void main(String[] args) {
        System.out.println("Fraud Detection System Demo");
        System.out.println("===========================");
        
        // Create a demo user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setKnownLocations(new HashSet<>());
        user.addKnownLocation("Bangalore");
        
        System.out.println("Created user: " + user.getUsername());
        System.out.println("Known locations: " + user.getKnownLocations());
        System.out.println();
        
        // Demo 1: Large Transaction Detection
        System.out.println("Demo 1: Large Transaction Detection");
        System.out.println("----------------------------------");
        Transaction largeTransaction = new Transaction();
        largeTransaction.setUser(user);
        largeTransaction.setAmount(new BigDecimal("6000"));
        largeTransaction.setLocation("Bangalore");
        largeTransaction.setTransactionDate(LocalDateTime.now());
        
        boolean isLarge = isLargeTransaction(largeTransaction);
        System.out.println("Transaction amount: ₹" + largeTransaction.getAmount());
        System.out.println("Is flagged as large transaction: " + isLarge);
        
        if (isLarge) {
            FraudAlert alert = createFraudAlert(user, largeTransaction, "LARGE_TRANSACTION", 
                    "Transaction amount exceeds ₹5000");
            System.out.println("Created fraud alert: " + alert.getAlertType() + " - " + alert.getDescription());
        }
        System.out.println();
        
        // Demo 2: Unusual Location Detection
        System.out.println("Demo 2: Unusual Location Detection");
        System.out.println("----------------------------------");
        Transaction unusualLocationTransaction = new Transaction();
        unusualLocationTransaction.setUser(user);
        unusualLocationTransaction.setAmount(new BigDecimal("1000"));
        unusualLocationTransaction.setLocation("Mumbai");
        unusualLocationTransaction.setTransactionDate(LocalDateTime.now());
        
        boolean isUnusual = isUnusualLocation(unusualLocationTransaction);
        System.out.println("Transaction location: " + unusualLocationTransaction.getLocation());
        System.out.println("User's known locations: " + user.getKnownLocations());
        System.out.println("Is flagged as unusual location: " + isUnusual);
        
        if (isUnusual) {
            FraudAlert alert = createFraudAlert(user, unusualLocationTransaction, "UNUSUAL_LOCATION", 
                    "Transaction from unknown location: " + unusualLocationTransaction.getLocation());
            System.out.println("Created fraud alert: " + alert.getAlertType() + " - " + alert.getDescription());
        }
        System.out.println();
        
        // Demo 3: Late Night Transaction Detection
        System.out.println("Demo 3: Late Night Transaction Detection");
        System.out.println("--------------------------------------");
        Transaction lateNightTransaction = new Transaction();
        lateNightTransaction.setUser(user);
        lateNightTransaction.setAmount(new BigDecimal("1000"));
        lateNightTransaction.setLocation("Bangalore");
        // Set transaction time to 2 AM
        lateNightTransaction.setTransactionDate(LocalDateTime.now().withHour(2).withMinute(0));
        
        boolean isLateNight = isLateNightTransaction(lateNightTransaction);
        System.out.println("Transaction time: " + lateNightTransaction.getTransactionDate().toLocalTime());
        System.out.println("Is flagged as late night transaction: " + isLateNight);
        
        if (isLateNight) {
            FraudAlert alert = createFraudAlert(user, lateNightTransaction, "LATE_NIGHT_TRANSACTION", 
                    "Transaction occurred between 12 AM and 5 AM");
            System.out.println("Created fraud alert: " + alert.getAlertType() + " - " + alert.getDescription());
        }
        System.out.println();
        
        // Demo 4: Frequent Transactions Detection
        System.out.println("Demo 4: Frequent Transactions Detection");
        System.out.println("--------------------------------------");
        List<Transaction> recentTransactions = new ArrayList<>();
        
        // Create 3 transactions in quick succession
        for (int i = 0; i < 3; i++) {
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setAmount(new BigDecimal("100"));
            transaction.setLocation("Bangalore");
            transaction.setTransactionDate(LocalDateTime.now().minusSeconds(i * 10));
            recentTransactions.add(transaction);
            System.out.println("Created transaction " + (i+1) + " at " + transaction.getTransactionDate());
        }
        
        // Create a 4th transaction
        Transaction frequentTransaction = new Transaction();
        frequentTransaction.setUser(user);
        frequentTransaction.setAmount(new BigDecimal("100"));
        frequentTransaction.setLocation("Bangalore");
        frequentTransaction.setTransactionDate(LocalDateTime.now());
        System.out.println("Created transaction 4 at " + frequentTransaction.getTransactionDate());
        
        boolean isFrequent = isFrequentTransaction(frequentTransaction, recentTransactions);
        System.out.println("Is flagged as frequent transaction: " + isFrequent);
        
        if (isFrequent) {
            FraudAlert alert = createFraudAlert(user, frequentTransaction, "FREQUENT_TRANSACTIONS", 
                    "More than 3 transactions within 1 minute");
            System.out.println("Created fraud alert: " + alert.getAlertType() + " - " + alert.getDescription());
        }
        System.out.println();
        
        // Demo 5: Multiple Failed Login Attempts
        System.out.println("Demo 5: Multiple Failed Login Attempts");
        System.out.println("------------------------------------");
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        
        System.out.println("Initial failed login attempts: " + user.getFailedLoginAttempts());
        System.out.println("Account locked: " + user.getAccountLocked());
        
        // Simulate 3 failed login attempts
        for (int i = 0; i < 3; i++) {
            incrementFailedLoginAttempts(user);
            System.out.println("Failed login attempt " + (i+1) + ": " + user.getFailedLoginAttempts() + 
                    " attempts, Account locked: " + user.getAccountLocked());
        }
        
        if (user.getAccountLocked()) {
            FraudAlert alert = createFraudAlert(user, null, "MULTIPLE_FAILED_LOGINS", 
                    "Multiple failed login attempts detected");
            System.out.println("Created fraud alert: " + alert.getAlertType() + " - " + alert.getDescription());
        }
    }
    
    // Fraud detection methods
    
    private static boolean isLargeTransaction(Transaction transaction) {
        return transaction.getAmount().compareTo(LARGE_TRANSACTION_THRESHOLD) >= 0;
    }
    
    private static boolean isUnusualLocation(Transaction transaction) {
        User user = transaction.getUser();
        return !user.getKnownLocations().contains(transaction.getLocation());
    }
    
    private static boolean isLateNightTransaction(Transaction transaction) {
        LocalTime transactionTime = transaction.getTransactionDate().toLocalTime();
        LocalTime midnight = LocalTime.of(0, 0);
        LocalTime fiveAM = LocalTime.of(5, 0);
        
        return (transactionTime.isAfter(midnight) || transactionTime.equals(midnight)) && 
               (transactionTime.isBefore(fiveAM));
    }
    
    private static boolean isFrequentTransaction(Transaction transaction, List<Transaction> recentTransactions) {
        LocalDateTime endTime = transaction.getTransactionDate();
        LocalDateTime startTime = endTime.minusMinutes(FREQUENT_TRANSACTION_MINUTES);
        
        int count = 0;
        for (Transaction t : recentTransactions) {
            if (t.getTransactionDate().isAfter(startTime) && 
                t.getTransactionDate().isBefore(endTime) &&
                t.getUser().equals(transaction.getUser())) {
                count++;
            }
        }
        
        return count >= FREQUENT_TRANSACTION_THRESHOLD;
    }
    
    private static void incrementFailedLoginAttempts(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= 3) {
            user.setAccountLocked(true);
            user.setAccountLockedUntil(LocalDateTime.now().plusHours(1));
        }
    }
    
    private static FraudAlert createFraudAlert(User user, Transaction transaction, String alertType, String description) {
        FraudAlert alert = new FraudAlert();
        alert.setUser(user);
        alert.setTransaction(transaction);
        alert.setAlertType(alertType);
        alert.setAlertDate(LocalDateTime.now());
        alert.setDescription(description);
        alert.setResolved(false);
        
        return alert;
    }
    
    // Simple model classes for demonstration
    
    public static class User {
        private String username;
        private String email;
        private Set<String> knownLocations = new HashSet<>();
        private int failedLoginAttempts = 0;
        private boolean accountLocked = false;
        private LocalDateTime accountLockedUntil;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public Set<String> getKnownLocations() {
            return knownLocations;
        }
        
        public void setKnownLocations(Set<String> knownLocations) {
            this.knownLocations = knownLocations;
        }
        
        public void addKnownLocation(String location) {
            this.knownLocations.add(location);
        }
        
        public int getFailedLoginAttempts() {
            return failedLoginAttempts;
        }
        
        public void setFailedLoginAttempts(int failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
        }
        
        public boolean getAccountLocked() {
            return accountLocked;
        }
        
        public void setAccountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
        }
        
        public LocalDateTime getAccountLockedUntil() {
            return accountLockedUntil;
        }
        
        public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
            this.accountLockedUntil = accountLockedUntil;
        }
    }
    
    public static class Transaction {
        private User user;
        private BigDecimal amount;
        private LocalDateTime transactionDate;
        private String location;
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public LocalDateTime getTransactionDate() {
            return transactionDate;
        }
        
        public void setTransactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
        }
        
        public String getLocation() {
            return location;
        }
        
        public void setLocation(String location) {
            this.location = location;
        }
    }
    
    public static class FraudAlert {
        private User user;
        private Transaction transaction;
        private String alertType;
        private LocalDateTime alertDate;
        private String description;
        private boolean resolved;
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public Transaction getTransaction() {
            return transaction;
        }
        
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
        
        public String getAlertType() {
            return alertType;
        }
        
        public void setAlertType(String alertType) {
            this.alertType = alertType;
        }
        
        public LocalDateTime getAlertDate() {
            return alertDate;
        }
        
        public void setAlertDate(LocalDateTime alertDate) {
            this.alertDate = alertDate;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public boolean isResolved() {
            return resolved;
        }
        
        public void setResolved(boolean resolved) {
            this.resolved = resolved;
        }
    }
}

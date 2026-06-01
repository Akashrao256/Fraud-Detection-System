package com.frauddetection.test;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Simple standalone test class to verify model classes
 */
public class SimpleTest {
    public static void main(String[] args) {
        System.out.println("Starting simple test of model classes...");
        
        try {
            // Create a user
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword("password");
            System.out.println("User created successfully: " + user.getUsername());
            
            // Create a transaction
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setAmount(new BigDecimal("1000.00"));
            transaction.setTransactionType("PAYMENT");
            transaction.setLocation("New York");
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            System.out.println("Transaction created successfully: " + transaction.getAmount());
            
            // Create a fraud alert
            FraudAlert alert = new FraudAlert();
            alert.setUser(user);
            alert.setTransaction(transaction);
            alert.setAlertType(FraudAlert.AlertType.LARGE_TRANSACTION);
            alert.setAlertDate(LocalDateTime.now());
            alert.setDescription("Large transaction detected");
            System.out.println("FraudAlert created successfully: " + alert.getAlertType());
            
            System.out.println("All model classes instantiated successfully!");
        } catch (Exception e) {
            System.err.println("Error testing model classes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/*TransactionController.java*/
package com.frauddetection.controller;

import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.service.MLFraudDetectionService;
import com.frauddetection.service.TransactionService;
import com.frauddetection.service.UserService;
// Lombok annotations removed
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final MLFraudDetectionService mlFraudDetectionService;

    public TransactionController(TransactionService transactionService,
            UserService userService,
            MLFraudDetectionService mlFraudDetectionService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.mlFraudDetectionService = mlFraudDetectionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest request) {
        try {
            Optional<User> userOpt = userService.findByUsername(request.getUsername());
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setAmount(new BigDecimal(request.getAmount()));
            transaction.setLocation(request.getLocation());
            transaction.setTransactionType(request.getTransactionType());
            transaction.setDescription(request.getDescription());
            transaction.setRecipientAccount(request.getRecipientAccount());
            transaction.setTransactionDate(LocalDateTime.now());

            // Process transaction (includes fraud detection)
            Transaction processedTransaction = transactionService.processTransaction(transaction);

            // Additional ML-based fraud detection
            boolean mlFlaggedAsFraud = mlFraudDetectionService.analyzeTransactionWithML(processedTransaction);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", processedTransaction.getId());
            response.put("status", processedTransaction.getStatus().toString());
            response.put("amount", processedTransaction.getAmount());
            response.put("timestamp", processedTransaction.getTransactionDate());
            response.put("riskScore", processedTransaction.getRiskScore());
            response.put("riskLevel", processedTransaction.getRiskLevel());
            response.put("mlFlagged", mlFlaggedAsFraud);

            if (processedTransaction.isFlagged()) {
                response.put("flagged", true);
                response.put("flagReason", processedTransaction.getFlagReason());

                response.put("message", "Transaction flagged as suspicious but processed.");
            } else {
                response.put("flagged", false);
                response.put("message", "Transaction processed successfully.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing the transaction: " + e.getMessage()));
        }
    }

    // OTP verification endpoint removed as per requirement

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserTransactions(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            List<Transaction> transactions = transactionService.getUserTransactions(user);

            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        try {
            Optional<Transaction> transactionOpt = transactionService.getTransactionById(id);
            if (!transactionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(transactionOpt.get());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the transaction: " + e.getMessage()));
        }
    }

    @GetMapping("/flagged")
    public ResponseEntity<?> getFlaggedTransactions() {
        try {
            List<Transaction> flaggedTransactions = transactionService.getFlaggedTransactions();
            return ResponseEntity.ok(flaggedTransactions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error",
                            "An error occurred while retrieving flagged transactions: " + e.getMessage()));
        }
    }

    // Request classes
    public static class TransactionRequest {
        private String username;
        private String amount;
        private String location;
        private String transactionType;
        private String description;
        private String recipientAccount;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRecipientAccount() {
            return recipientAccount;
        }

        public void setRecipientAccount(String recipientAccount) {
            this.recipientAccount = recipientAccount;
        }
    }

    // OTP verification request class removed as per requirement
}

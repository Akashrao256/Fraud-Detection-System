/*TransactionController.java*/
package com.frauddetection.controller;

import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.service.MLFraudDetectionService;
import com.frauddetection.service.TransactionService;
import com.frauddetection.service.UserService;
// Lombok annotations removed
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Transactions", description = "Transaction processing and management endpoints - Create, retrieve, and manage financial transactions with fraud detection")
@SecurityRequirement(name = "Bearer Authentication")
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
    @Operation(summary = "Create and Process Transaction", description = "Submit a new financial transaction for processing. The transaction undergoes fraud detection analysis including "
            +
            "risk scoring, rule-based flagging, and ML-assisted fraud detection. Returns transaction details with fraud assessment results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction processed successfully", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"transactionId\":123,\"status\":\"COMPLETED\",\"amount\":1000.00,\"riskScore\":25.5,\"riskLevel\":\"MEDIUM\",\"flagged\":false}"))),
            @ApiResponse(responseCode = "400", description = "Bad request - User not found or invalid request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"User not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"An error occurred while processing the transaction\"}")))
    })
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
    @Operation(summary = "Get User Transactions", description = "Retrieve all transactions for a specific user. Returns a list of transactions including status, amounts, and fraud detection results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request - User not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"User not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get Transaction by ID", description = "Retrieve details of a specific transaction by its ID. Returns complete transaction information including risk assessment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get Flagged Transactions", description = "Retrieve all transactions that have been flagged as suspicious or high-risk by the fraud detection system. "
            +
            "Useful for monitoring and investigation purposes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flagged transactions retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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

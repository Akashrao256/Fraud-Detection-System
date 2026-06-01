/*MLFraudDetectionService.java*/
package com.frauddetection.service;

import com.frauddetection.ml.FraudDetectionModel;
import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.repository.TransactionRepository;
// Lombok annotations removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MLFraudDetectionService {
    
    private static final Logger log = LoggerFactory.getLogger(MLFraudDetectionService.class);
    private static final int ML_DETECTION_RISK = 25;
    
    private final FraudDetectionModel fraudDetectionModel;
    private final TransactionRepository transactionRepository;
    private final FraudAlertService fraudAlertService;
    
    public MLFraudDetectionService(FraudDetectionModel fraudDetectionModel,
                                TransactionRepository transactionRepository,
                                FraudAlertService fraudAlertService) {
        this.fraudDetectionModel = fraudDetectionModel;
        this.transactionRepository = transactionRepository;
        this.fraudAlertService = fraudAlertService;
    }
    
    public boolean analyzeTransactionWithML(Transaction transaction) {
        User user = transaction.getUser();
        boolean isKnownLocation = user.isKnownLocation(transaction.getLocation());
        
        // Count recent transactions in the last 5 minutes
        LocalDateTime endTime = transaction.getTransactionDate();
        LocalDateTime startTime = endTime.minusMinutes(5);
        int recentTransactionCount = transactionRepository.countTransactionsInTimeRange(user, startTime, endTime);
        
        // Use ML model to predict if transaction is suspicious
        boolean isSuspicious = fraudDetectionModel.isSuspiciousTransaction(
                transaction, isKnownLocation, recentTransactionCount);
        
        if (isSuspicious) {
            log.info("ML model detected suspicious transaction: {}", transaction.getId());
            
            // Flag the transaction
            transaction.setFlagged(true);
            String flagReason = transaction.getFlagReason() != null ? 
                    transaction.getFlagReason() + ", ML detection" : "ML detection";
            transaction.setFlagReason(flagReason);
            int riskScore = Math.min(100, transaction.getRiskScore() + ML_DETECTION_RISK);
            transaction.setRiskScore(riskScore);
            transaction.setRiskLevel(determineRiskLevel(riskScore));
            transaction.setStatus(Transaction.TransactionStatus.FLAGGED);
            
            // Create fraud alert
            fraudAlertService.createFraudAlert(user, transaction, FraudAlert.AlertType.SUSPICIOUS_PATTERN, 
                    "Machine learning model detected suspicious pattern");

            transactionRepository.save(transaction);
            
            return true;
        }
        
        return false;
    }

    private String determineRiskLevel(int riskScore) {
        if (riskScore >= 70) {
            return "HIGH";
        }
        if (riskScore >= 30) {
            return "MEDIUM";
        }
        return "LOW";
    }
    
    public double getFraudProbability(Transaction transaction) {
        User user = transaction.getUser();
        boolean isKnownLocation = user.isKnownLocation(transaction.getLocation());
        
        // Count recent transactions in the last 5 minutes
        LocalDateTime endTime = transaction.getTransactionDate();
        LocalDateTime startTime = endTime.minusMinutes(5);
        int recentTransactionCount = transactionRepository.countTransactionsInTimeRange(user, startTime, endTime);
        
        // Get fraud probability from ML model
        return fraudDetectionModel.predictFraudProbability(transaction, isKnownLocation, recentTransactionCount);
    }
}

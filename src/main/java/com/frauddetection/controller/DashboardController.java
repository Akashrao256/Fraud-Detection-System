package com.frauddetection.controller;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.repository.FraudAlertRepository;
import com.frauddetection.repository.TransactionRepository;
import com.frauddetection.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final UserRepository userRepository;

    public DashboardController(TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository,
            UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboard() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<FraudAlert> alerts = fraudAlertRepository.findAll();

        long totalTransactions = transactions.size();
        long totalFlaggedTransactions = transactions.stream().filter(Transaction::isFlagged).count();
        long highRiskTransactions = transactions.stream()
                .filter(transaction -> "HIGH".equalsIgnoreCase(transaction.getRiskLevel()))
                .count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalTransactions", totalTransactions);
        summary.put("totalFlaggedTransactions", totalFlaggedTransactions);
        summary.put("fraudPercentage", calculatePercentage(totalFlaggedTransactions, totalTransactions));
        summary.put("totalUsers", userRepository.count());
        summary.put("highRiskTransactions", highRiskTransactions);
        summary.put("recentFraudAlerts", alerts.stream().filter(alert -> !alert.isResolved()).count());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("summary", summary);
        response.put("transactionsOverTime", countTransactionsByDate(transactions));
        response.put("fraudAlertsOverTime", countAlertsByDate(alerts));
        response.put("fraudVsSafe", fraudVsSafe(totalFlaggedTransactions, totalTransactions));
        response.put("riskDistribution", riskDistribution(transactions));
        response.put("highRiskTransactions", recentHighRiskTransactions(transactions));
        response.put("recentFraudAlerts", recentAlerts(alerts));

        return ResponseEntity.ok(response);
    }

    private double calculatePercentage(long flagged, long total) {
        if (total == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(flagged * 100.0 / total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private List<Map<String, Object>> countTransactionsByDate(List<Transaction> transactions) {
        Map<LocalDate, Long> grouped = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().toLocalDate(),
                        Collectors.counting()));

        return toDateSeries(grouped);
    }

    private List<Map<String, Object>> countAlertsByDate(List<FraudAlert> alerts) {
        Map<LocalDate, Long> grouped = alerts.stream()
                .collect(Collectors.groupingBy(
                        alert -> alert.getAlertDate().toLocalDate(),
                        Collectors.counting()));

        return toDateSeries(grouped);
    }

    private List<Map<String, Object>> toDateSeries(Map<LocalDate, Long> grouped) {
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", entry.getKey().toString());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Long> fraudVsSafe(long totalFlaggedTransactions, long totalTransactions) {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("fraud", totalFlaggedTransactions);
        counts.put("safe", Math.max(0, totalTransactions - totalFlaggedTransactions));
        return counts;
    }

    private Map<String, Long> riskDistribution(List<Transaction> transactions) {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("LOW", 0L);
        counts.put("MEDIUM", 0L);
        counts.put("HIGH", 0L);

        transactions.stream()
                .map(transaction -> transaction.getRiskLevel() == null ? "LOW" : transaction.getRiskLevel())
                .map(String::toUpperCase)
                .forEach(level -> counts.put(level, counts.getOrDefault(level, 0L) + 1));

        return counts;
    }

    private List<Map<String, Object>> recentHighRiskTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> "HIGH".equalsIgnoreCase(transaction.getRiskLevel()))
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .limit(10)
                .map(this::transactionSummary)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> recentAlerts(List<FraudAlert> alerts) {
        return alerts.stream()
                .sorted(Comparator.comparing(FraudAlert::getAlertDate).reversed())
                .limit(10)
                .map(this::alertSummary)
                .collect(Collectors.toList());
    }

    private Map<String, Object> transactionSummary(Transaction transaction) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", transaction.getId());
        item.put("username", transaction.getUser().getUsername());
        item.put("amount", transaction.getAmount());
        item.put("location", transaction.getLocation());
        item.put("status", transaction.getStatus());
        item.put("flagReason", transaction.getFlagReason());
        item.put("riskScore", transaction.getRiskScore());
        item.put("riskLevel", transaction.getRiskLevel());
        item.put("transactionDate", transaction.getTransactionDate());
        return item;
    }

    private Map<String, Object> alertSummary(FraudAlert alert) {
        Map<String, Object> item = new LinkedHashMap<>();
        Transaction transaction = alert.getTransaction();

        item.put("id", alert.getId());
        item.put("alertType", alert.getAlertType());
        item.put("description", alert.getDescription());
        item.put("alertDate", alert.getAlertDate());
        item.put("resolved", alert.isResolved());
        item.put("username", alert.getUser() != null ? alert.getUser().getUsername() : "");
        item.put("transactionId", transaction != null ? transaction.getId() : null);
        item.put("amount", transaction != null ? transaction.getAmount() : null);
        item.put("riskScore", transaction != null ? transaction.getRiskScore() : 0);
        item.put("riskLevel", transaction != null ? transaction.getRiskLevel() : "LOW");
        return item;
    }
}

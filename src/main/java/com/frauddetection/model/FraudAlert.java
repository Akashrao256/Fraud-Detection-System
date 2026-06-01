/*FraudAlert.java*/
package com.frauddetection.model;

// Lombok annotations removed

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
// Manually added constructors
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public FraudAlert() {
        // Default constructor
    }

    public FraudAlert(Long id, User user, Transaction transaction, AlertType alertType,
            LocalDateTime alertDate, String description, boolean resolved,
            LocalDateTime resolvedDate, String resolvedBy, String actionTaken) {
        this.id = id;
        this.user = user;
        this.transaction = transaction;
        this.alertType = alertType;
        this.alertDate = alertDate;
        this.description = description;
        this.resolved = resolved;
        this.resolvedDate = resolvedDate;
        this.resolvedBy = resolvedBy;
        this.actionTaken = actionTaken;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(name = "alert_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertType alertType;

    @Column(name = "alert_date", nullable = false)
    private LocalDateTime alertDate;

    @Column(name = "description")
    private String description;

    @Column(name = "resolved")
    private boolean resolved = false;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(name = "action_taken")
    private String actionTaken;

    @PrePersist
    protected void onCreate() {
        alertDate = LocalDateTime.now();
    }

    public enum AlertType {
        MULTIPLE_FAILED_LOGINS,
        LARGE_TRANSACTION,
        UNUSUAL_LOCATION,
        FREQUENT_TRANSACTIONS,
        LATE_NIGHT_TRANSACTION,
        SUSPICIOUS_PATTERN
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
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

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }
}

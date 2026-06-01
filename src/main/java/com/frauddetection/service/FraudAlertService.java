/*FraudAlertService.java*/
package com.frauddetection.service;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import com.frauddetection.repository.FraudAlertRepository;
// Lombok annotations removed
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FraudAlertService {
    
    private final FraudAlertRepository fraudAlertRepository;
    
    public FraudAlertService(FraudAlertRepository fraudAlertRepository) {
        this.fraudAlertRepository = fraudAlertRepository;
    }
    
    public FraudAlert createFraudAlert(User user, Transaction transaction, FraudAlert.AlertType alertType, String description) {
        FraudAlert alert = new FraudAlert();
        alert.setUser(user);
        alert.setTransaction(transaction);
        alert.setAlertType(alertType);
        alert.setAlertDate(LocalDateTime.now());
        alert.setDescription(description);
        alert.setResolved(false);
        
        return fraudAlertRepository.save(alert);
    }
    
    public List<FraudAlert> getAllAlerts() {
        return fraudAlertRepository.findAll();
    }
    
    public List<FraudAlert> getUnresolvedAlerts() {
        return fraudAlertRepository.findByResolved(false);
    }
    
    public List<FraudAlert> getUserAlerts(User user) {
        return fraudAlertRepository.findByUser(user);
    }
    
    public List<FraudAlert> getUserUnresolvedAlerts(User user) {
        return fraudAlertRepository.findByUserAndResolved(user, false);
    }
    
    public Optional<FraudAlert> getAlertById(Long id) {
        return fraudAlertRepository.findById(id);
    }
    
    public FraudAlert resolveAlert(FraudAlert alert, String resolvedBy, String actionTaken) {
        alert.setResolved(true);
        alert.setResolvedDate(LocalDateTime.now());
        alert.setResolvedBy(resolvedBy);
        alert.setActionTaken(actionTaken);
        
        return fraudAlertRepository.save(alert);
    }
    
    public List<FraudAlert> getAlertsByType(FraudAlert.AlertType alertType) {
        return fraudAlertRepository.findByAlertType(alertType);
    }
    
    public List<FraudAlert> getAlertsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return fraudAlertRepository.findByAlertDateBetween(startDate, endDate);
    }
}

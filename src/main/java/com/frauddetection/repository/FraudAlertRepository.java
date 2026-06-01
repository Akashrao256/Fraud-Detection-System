/*FraudAlertRepository.java*/
package com.frauddetection.repository;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByUser(User user);
    List<FraudAlert> findByTransaction(Transaction transaction);
    List<FraudAlert> findByAlertType(FraudAlert.AlertType alertType);
    List<FraudAlert> findByResolved(boolean resolved);
    List<FraudAlert> findByAlertDateBetween(LocalDateTime start, LocalDateTime end);
    List<FraudAlert> findByUserAndResolved(User user, boolean resolved);
}

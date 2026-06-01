/*TransactionRepository.java*/
package com.frauddetection.repository;

import com.frauddetection.model.Transaction;
import com.frauddetection.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.transactionDate BETWEEN :startTime AND :endTime")
    List<Transaction> findTransactionsInTimeRange(@Param("user") User user, 
                                                @Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user = :user AND t.transactionDate BETWEEN :startTime AND :endTime")
    int countTransactionsInTimeRange(@Param("user") User user, 
                                    @Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);
    
    List<Transaction> findByUserAndStatus(User user, Transaction.TransactionStatus status);
    
    List<Transaction> findByFlagged(boolean flagged);
}

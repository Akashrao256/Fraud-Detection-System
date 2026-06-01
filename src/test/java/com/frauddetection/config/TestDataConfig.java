package com.frauddetection.config;

import com.frauddetection.repository.FraudAlertRepository;
import com.frauddetection.repository.TransactionRepository;
import com.frauddetection.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataConfig {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;

    public TestDataConfig(UserRepository userRepository, TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    public void cleanupAll() {
        fraudAlertRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }
}
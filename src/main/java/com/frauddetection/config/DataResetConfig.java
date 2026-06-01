package com.frauddetection.config;

import com.frauddetection.model.User;
import com.frauddetection.repository.FraudAlertRepository;
import com.frauddetection.repository.TransactionRepository;
import com.frauddetection.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class to reset all data when the application starts
 */
@Configuration
public class DataResetConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataResetConfig.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private FraudAlertRepository fraudAlertRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.data.reset-on-startup:false}")
    private boolean resetOnStartup;
    
    /**
     * Bean that runs when the application starts to clear all data
     */
    @Bean
    public CommandLineRunner resetData() {
        return args -> {
            if (resetOnStartup) {
                logger.info("Resetting all data on application startup");

                // Clear all data from repositories
                fraudAlertRepository.deleteAll();
                transactionRepository.deleteAll();
                userRepository.deleteAll();
            }

            if (userRepository.existsByUsername("admin")) {
                logger.info("Demo admin user already exists");
                return;
            }

            User demoUser = new User();
            demoUser.setUsername("admin");
            demoUser.setPassword(passwordEncoder.encode("admin123"));
            demoUser.setEmail("admin@example.com");
            demoUser.setFullName("Demo Admin");
            demoUser.setPhoneNumber("1234567890");
            demoUser.addKnownLocation("Bangalore");
            userRepository.save(demoUser);
            
            logger.info("Demo admin user has been created");
        };
    }
}

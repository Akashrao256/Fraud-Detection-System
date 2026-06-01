/*UserService.java*/
package com.frauddetection.service;

import com.frauddetection.model.User;
import com.frauddetection.repository.UserRepository;
// Lombok annotations removed
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    public void updateLoginAttempts(User user, boolean successful) {
        if (successful) {
            user.resetFailedLoginAttempts();
            user.setLastLogin(LocalDateTime.now());
        } else {
            user.incrementFailedLoginAttempts();
        }
        userRepository.save(user);
    }
    
    public boolean isAccountLocked(User user) {
        if (user.getAccountLocked() && user.getAccountLockedUntil() != null) {
            if (LocalDateTime.now().isAfter(user.getAccountLockedUntil())) {
                user.resetFailedLoginAttempts();
                userRepository.save(user);
                return false;
            }
            return true;
        }
        return false;
    }
    
    public void addKnownLocation(User user, String location) {
        user.addKnownLocation(location);
        userRepository.save(user);
    }
    
    public boolean isKnownLocation(User user, String location) {
        return user.isKnownLocation(location);
    }
}

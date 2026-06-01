package com.frauddetection.controller;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.User;
import com.frauddetection.service.FraudAlertService;
import com.frauddetection.service.UserService;
// Lombok annotations removed
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/fraud-alerts")
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;
    private final UserService userService;

    public FraudAlertController(FraudAlertService fraudAlertService, UserService userService) {
        this.fraudAlertService = fraudAlertService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAlerts() {
        try {
            List<FraudAlert> alerts = fraudAlertService.getAllAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving alerts: " + e.getMessage()));
        }
    }

    @GetMapping("/unresolved")
    public ResponseEntity<?> getUnresolvedAlerts() {
        try {
            List<FraudAlert> alerts = fraudAlertService.getUnresolvedAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving unresolved alerts: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserAlerts(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            List<FraudAlert> alerts = fraudAlertService.getUserAlerts(user);

            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving user alerts: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlertById(@PathVariable Long id) {
        try {
            Optional<FraudAlert> alertOpt = fraudAlertService.getAlertById(id);
            if (!alertOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(alertOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the alert: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long id, @Valid @RequestBody ResolveAlertRequest request) {
        try {
            Optional<FraudAlert> alertOpt = fraudAlertService.getAlertById(id);
            if (!alertOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            FraudAlert alert = alertOpt.get();
            FraudAlert resolvedAlert = fraudAlertService.resolveAlert(alert, request.getResolvedBy(),
                    request.getActionTaken());

            return ResponseEntity.ok(Map.of(
                    "message", "Alert resolved successfully",
                    "alertId", resolvedAlert.getId(),
                    "resolvedBy", resolvedAlert.getResolvedBy(),
                    "resolvedDate", resolvedAlert.getResolvedDate(),
                    "actionTaken", resolvedAlert.getActionTaken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while resolving the alert: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{alertType}")
    public ResponseEntity<?> getAlertsByType(@PathVariable String alertType) {
        try {
            FraudAlert.AlertType type = FraudAlert.AlertType.valueOf(alertType.toUpperCase());
            List<FraudAlert> alerts = fraudAlertService.getAlertsByType(type);

            return ResponseEntity.ok(alerts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid alert type"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving alerts by type: " + e.getMessage()));
        }
    }

    // Request classes
    public static class ResolveAlertRequest {
        private String resolvedBy;
        private String actionTaken;

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
}

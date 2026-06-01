package com.frauddetection.controller;

import com.frauddetection.model.FraudAlert;
import com.frauddetection.model.User;
import com.frauddetection.service.FraudAlertService;
import com.frauddetection.service.UserService;
// Lombok annotations removed
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Fraud Alerts", description = "Fraud alert management endpoints - Retrieve, manage, and resolve fraud detection alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;
    private final UserService userService;

    public FraudAlertController(FraudAlertService fraudAlertService, UserService userService) {
        this.fraudAlertService = fraudAlertService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get All Fraud Alerts", description = "Retrieve all fraud alerts in the system. Returns a comprehensive list of both resolved and unresolved alerts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get Unresolved Alerts", description = "Retrieve all unresolved fraud alerts that require investigation and action. "
            +
            "Critical for identifying pending fraud cases.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unresolved alerts retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get User Fraud Alerts", description = "Retrieve all fraud alerts for a specific user. Useful for user-specific fraud monitoring and history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User alerts retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request - User not found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"User not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get Alert by ID", description = "Retrieve detailed information about a specific fraud alert including investigation details and status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Resolve Fraud Alert", description = "Mark a fraud alert as resolved with action taken and resolution details. "
            +
            "Used by investigators to close fraud cases and document resolution actions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert resolved successfully", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Alert resolved successfully\",\"alertId\":1,\"resolvedBy\":\"admin\",\"resolvedDate\":\"2026-06-01T10:30:00\",\"actionTaken\":\"Account locked\"}"))),
            @ApiResponse(responseCode = "404", description = "Alert not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Get Alerts by Type", description = "Retrieve fraud alerts filtered by alert type (e.g., HIGH_RISK_TRANSACTION, SUSPICIOUS_PATTERN, GEOGRAPHICAL_ANOMALY). "
            +
            "Useful for categorized monitoring and analysis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid alert type", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"Invalid alert type\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
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

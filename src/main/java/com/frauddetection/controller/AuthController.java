/*AuthController.java*/
package com.frauddetection.controller;

import com.frauddetection.model.User;
import com.frauddetection.security.JwtService;
import com.frauddetection.service.UserService;
// Lombok annotations removed
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints - Login and registration with JWT token generation")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate a user with username and password. Returns JWT Bearer token for authenticated requests. "
            +
            "Optional location parameter helps track user login locations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful - JWT token provided", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Login successful\",\"username\":\"user123\",\"email\":\"user@example.com\",\"token\":\"eyJhbGc...\",\"tokenType\":\"Bearer\",\"expiresIn\":86400000}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials or account locked", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"Invalid username or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"An error occurred during login\"}")))
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            User user = userOpt.get();

            // Check if account is locked
            if (userService.isAccountLocked(user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Account is locked due to multiple failed login attempts. " +
                                "Please try again later."));
            }

            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // If authentication is successful, update login attempts
            userService.updateLoginAttempts(user, true);

            // Add the current location to known locations if not already known
            if (loginRequest.getLocation() != null && !loginRequest.getLocation().isEmpty()) {
                if (!userService.isKnownLocation(user, loginRequest.getLocation())) {
                    userService.addKnownLocation(user, loginRequest.getLocation());
                }
            }

            // Return JWT token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("token", jwtService.generateToken(
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()));
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtService.getExpirationMillis());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                userService.updateLoginAttempts(userOpt.get(), false);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));

        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Account is locked. Please try again later."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred during login"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Register a new user account with username, password, email, and optional profile information. "
            +
            "Initial location parameter helps establish known login locations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"User registered successfully\",\"username\":\"user123\",\"email\":\"user@example.com\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request - Username or email already taken", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"Username is already taken\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\":\"An error occurred during registration\"}")))
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Check if username is available
            if (!userService.isUsernameAvailable(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is already taken"));
            }

            // Check if email is available
            if (!userService.isEmailAvailable(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email is already registered"));
            }

            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setEmail(registerRequest.getEmail());
            user.setFullName(registerRequest.getFullName());
            user.setPhoneNumber(registerRequest.getPhoneNumber());

            // Add initial location to known locations
            if (registerRequest.getLocation() != null && !registerRequest.getLocation().isEmpty()) {
                user.addKnownLocation(registerRequest.getLocation());
            }

            User savedUser = userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "username", savedUser.getUsername(),
                            "email", savedUser.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred during registration"));
        }
    }

    // Request classes
    public static class LoginRequest {
        private String username;
        private String password;
        private String location;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String location;

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}

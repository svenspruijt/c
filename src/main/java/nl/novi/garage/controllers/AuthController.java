package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.user.UserLoginRequestDTO;
import nl.novi.garage.dtos.user.UserLoginResponseDTO;
import nl.novi.garage.security.JwtService;
import nl.novi.garage.services.AuthService;
import nl.novi.garage.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        UserLoginResponseDTO response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = new HashMap<>();
                error.put("timestamp", java.time.LocalDateTime.now().toString());
                error.put("status", 401);
                error.put("error", "Unauthorized");
                error.put("message", "Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                throw new RuntimeException("Token is invalid or expired");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            response.put("status", 200);
            response.put("message", "Token is valid");
            response.put("username", username);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", java.time.LocalDateTime.now().toString());
            error.put("status", 401);
            error.put("error", "Unauthorized");
            error.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // Handle unsupported HTTP methods for /login
    @RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH })
    public ResponseEntity<Map<String, Object>> handleUnsupportedMethod() {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        error.put("status", 405);
        error.put("error", "Method Not Allowed");
        error.put("message", "Only POST method is allowed for /auth/login");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }
}
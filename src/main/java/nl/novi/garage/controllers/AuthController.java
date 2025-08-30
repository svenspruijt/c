package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.user.UserLoginRequestDTO;
import nl.novi.garage.dtos.user.UserLoginResponseDTO;
import nl.novi.garage.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO loginRequest) {
        UserLoginResponseDTO response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken() {
        return ResponseEntity.ok("Token is valid");
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
package nl.novi.garage.controllers;

import jakarta.validation.Valid;
import nl.novi.garage.dtos.user.UserLoginRequestDTO;
import nl.novi.garage.dtos.user.UserLoginResponseDTO;
import nl.novi.garage.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
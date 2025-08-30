package nl.novi.garage.services;

import nl.novi.garage.dtos.user.UserLoginRequestDTO;
import nl.novi.garage.dtos.user.UserLoginResponseDTO;
import nl.novi.garage.models.User;
import nl.novi.garage.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public UserLoginResponseDTO authenticate(UserLoginRequestDTO loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userDetailsService.getUserByUsername(userDetails.getUsername());

            // Generate JWT token
            String token = jwtService.generateToken(userDetails, user.getRole());

            // Create response
            return new UserLoginResponseDTO(
                    token,
                    user.getUsername(),
                    user.getName(),
                    user.getRole());

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }
}
package nl.novi.garage.config;

import nl.novi.garage.security.JwtRequestFilter;
import nl.novi.garage.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // User management endpoints - only BEHEER role
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("BEHEER")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("BEHEER")

                        // General access for authenticated users
                        .requestMatchers(HttpMethod.GET, "/customers/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.POST, "/customers/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.PUT, "/customers/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")

                        .requestMatchers(HttpMethod.GET, "/cars/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.POST, "/cars/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.PUT, "/cars/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")

                        // Parts and actions - read for all, modify only for BEHEER
                        .requestMatchers(HttpMethod.GET, "/parts/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.POST, "/parts/**").hasRole("BEHEER")
                        .requestMatchers(HttpMethod.PUT, "/parts/**").hasRole("BEHEER")
                        .requestMatchers(HttpMethod.DELETE, "/parts/**").hasRole("BEHEER")

                        .requestMatchers(HttpMethod.GET, "/actions/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")
                        .requestMatchers(HttpMethod.POST, "/actions/**").hasRole("BEHEER")
                        .requestMatchers(HttpMethod.PUT, "/actions/**").hasRole("BEHEER")
                        .requestMatchers(HttpMethod.DELETE, "/actions/**").hasRole("BEHEER")

                        // Inspections and repairs - only MONTEUR and BEHEER
                        .requestMatchers("/inspections/**").hasAnyRole("MONTEUR", "BEHEER")
                        .requestMatchers("/repairs/**").hasAnyRole("MONTEUR", "BEHEER")

                        // Receipts - all authenticated users
                        .requestMatchers("/receipts/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")

                        // Documents - all authenticated users
                        .requestMatchers("/documents/**").hasAnyRole("MEDEWERKER", "MONTEUR", "BEHEER")

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
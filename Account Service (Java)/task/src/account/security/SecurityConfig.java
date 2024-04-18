package account.security;

import account.service.EventService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final EventService eventService;

    public SecurityConfig(UserDetailsService userDetailsService, EventService eventService) {
        this.userDetailsService = userDetailsService;
        this.eventService = eventService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint(getAuthenticationEntryPoint()))
                .exceptionHandling().accessDeniedHandler(getHandleAccessDenied()).and()
                .csrf(AbstractHttpConfigurer::disable)// For Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/acct/**").hasRole("ACCOUNTANT")
                        .requestMatchers("/api/security/events/*").hasRole("AUDITOR")
                        .requestMatchers("/api/empl/**").hasAnyRole("USER", "ACCOUNTANT")
                        .requestMatchers("/error", "/h2-console", "/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no session
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    public AccessDeniedHandler getHandleAccessDenied() {
        return (request, response, accessDenied) -> {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
            String username = request.getUserPrincipal().getName();
            eventService.logAccessDenied(username);
        };
    }

    @Bean
    public AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        };
    }
}

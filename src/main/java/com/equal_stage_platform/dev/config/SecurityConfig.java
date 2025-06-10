package com.equal_stage_platform.dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable for API usage (or handle with token)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/lecturers/create", "/auth/**").permitAll()

                .requestMatchers("/lecturers/all", "/lecturers/{userId}").permitAll() // Public lecturer info
                .requestMatchers("/lectures/all", "/lectures/{lectureId}").permitAll() // Public lecture info
                

                // Only authenticated users
                .requestMatchers("/lecturers/**").authenticated()

                // Example for role-based access
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults()) // Or use formLogin(), or JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}

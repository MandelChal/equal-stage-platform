package com.equal_stage_platform.dev.config;

import com.equal_stage_platform.dev.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                //----Auth endpoints----
                .requestMatchers("/api/auth/registerAdmin").permitAll()  // Allow initial admin setup
                .requestMatchers("/api/auth/create-admin").hasRole("ADMIN")  // Only admins can create new admins
                //----Lecturer endpoints----
                .requestMatchers("/lecturers/create").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/lecturers/update/**").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lecturers/adminUpdate/**").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lecturers/all").hasRole("ADMIN")
                .requestMatchers("/lecturers/all/approved").permitAll()
                .requestMatchers("/lecturers/{userId}").permitAll()
                .requestMatchers("/lecturers/lectures/{lecturerId}/all").permitAll()
                .requestMatchers("/lecturers/lectures/{lecturerId}/{lectureId}").permitAll()
                .requestMatchers("/lecturers/pending").hasRole("ADMIN")
                .requestMatchers("/lecturers/approve/**").hasRole("ADMIN")
                .requestMatchers("/lecturers/reject/**").hasRole("ADMIN")
                //----Lecture endpoints----
                .requestMatchers("/lectures/create").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lectures/update/**").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lectures/all").permitAll()
                .requestMatchers("/lectures/{lectureId}").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(userDetailsService);
    
        return http.build();
    }
    

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

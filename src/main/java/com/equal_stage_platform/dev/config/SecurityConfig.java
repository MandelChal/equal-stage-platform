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
                .requestMatchers("/api/auth/registerAdmin", "/api/auth/forgot-pass", "/api/auth//reset-pass-token").permitAll()  // Allow initial admin setup
                .requestMatchers("/api/auth/create-admin").hasRole("ADMIN")  // Only admins can create new admins
                .requestMatchers("/api/auth/reset-pass").hasRole("USER") 
                //----Lecturer endpoints----
                .requestMatchers("/lecturers/create").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/lecturers/adminUpdate/**", "/lecturers/update/**", "/lecturers/delLecturerProfile").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lecturers/all", "/lecturers/del/{userId}").hasRole("ADMIN")
                .requestMatchers("/lecturers/lectures/{lecturerId}/{lectureId}", "/lecturers/lectures/{lecturerId}/all", "/lecturers/searchById/{userId}", "/lecturers/all/approved","/lecturers/search/{name}").permitAll()
                .requestMatchers("/lecturers/reject/**", "/lecturers/approve/**", "/lecturers/all", "/lecturers/pending").hasRole("ADMIN")
                //----Lecture endpoints----
                .requestMatchers("/lectures/update/**", "/lectures/create").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lectures/{lectureId}", "/all/isOnline", "/lectures/all", "/lectures/search/**").permitAll()
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

package com.equal_stage_platform.dev.config;

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

import com.equal_stage_platform.dev.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                //----Swagger endpoints----
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() //http://localhost:8080/swagger-ui/index.html
                //----Faker endpoints----
                .requestMatchers("/faker/**").permitAll()
                //----Auth endpoints----
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/registerAdmin", "/api/auth/forgot-pass", "/api/auth/reset-pass-token").permitAll()
                .requestMatchers("/api/auth/admin/**").hasRole("ADMIN")  // Only admins can create new admins
                .requestMatchers("/api/auth/refresh", "/api/auth/logout", "/api/auth/reset-pass").hasAnyRole("USER", "ADMIN", "LECTURER")
                .requestMatchers("/api/auth/delete-account").hasAnyRole("USER")
                //----Lecturer endpoints----
                .requestMatchers("/lecturers/create").hasAnyRole("USER", "ADMIN")
                .requestMatchers( "/lecturers/update/**", "/lecturers/del/self").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lecturers/{lecturerId}/lectures/{lectureId}", "/lecturers/{lecturerId}/lectures/all", "/lecturers/search/**", "/lecturers/all/approved","/lecturers/paginated").permitAll()
                .requestMatchers("/lecturers/admin/**").hasRole("ADMIN")
                //----Lecture endpoints----
                .requestMatchers("/lectures/del/**", "/lectures/update/**", "/lectures/create").hasAnyRole("LECTURER", "ADMIN")
                .requestMatchers("/lectures/admin/**").hasRole("ADMIN")
                .requestMatchers("/lectures/physical", "/lectures/{lectureId}", "/lectures/all/isOnline", "/lectures/search/**", "/lectures/paginated", "/lectures/topLectures/**").permitAll()
                //----Home Page Banner endpoints----
                .requestMatchers("/HomePage/banner/urls").permitAll()
                .requestMatchers("/HomePage/admin/**").hasRole("ADMIN")

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

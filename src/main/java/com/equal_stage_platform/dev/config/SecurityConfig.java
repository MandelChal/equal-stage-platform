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
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/login/google", "/api/auth/registerSuperAdmin", "/api/auth/forgot-pass", "/api/auth/reset-pass-token").permitAll()
                .requestMatchers("/api/auth/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")  // Only admins can create new admins
                .requestMatchers("/api/auth/super-admin/**").hasRole("SUPER_ADMIN")  // Only admins can create new admins
                .requestMatchers("/api/auth/refresh", "/api/auth/logout", "/api/auth/reset-pass", "/api/auth/self/del", "/api/auth/complete-registration").hasRole("CLIENT")

                //----Lecturer endpoints----
                .requestMatchers("/lecturers/create").hasRole("CLIENT")
                .requestMatchers( "/lecturers/update/**", "/lecturers/del/self").hasRole("LECTURER")
                .requestMatchers("/lecturers/{lecturerId}/lectures/{lectureId}", "/lecturers/{lecturerId}/lectures/all", "/lecturers/search/**", "/lecturers/all/approved","/lecturers/paginated", "/lecturers/filter/**", "/lecturers/paginated/filter/**", "/lecturers/filter/**").permitAll()
                .requestMatchers("/lecturers/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/lecturers/super-admin/**").hasRole("SUPER_ADMIN")
                
                //----Lecture endpoints----
                .requestMatchers("/lectures/del/**", "/lectures/update/**", "/lectures/create").hasRole("LECTURER")
                .requestMatchers("/lectures/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/lectures/super-admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/lectures/physical", "/lectures/{lectureId}", "/lectures/all/isOnline", "/lectures/search/**", "/lectures/paginated", "/lectures/topLectures/**", "/lectures/filter/**", "/lectures/paginated/filter/**", "/lectures/filter/**" ).permitAll()
                //----Home Page Banner endpoints----
                .requestMatchers("/HomePage/banner/urls", "/HomePage/about_us", "/HomePage/contact_us").permitAll()
                .requestMatchers("/HomePage/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/HomePage/super-admin/**").hasRole("SUPER_ADMIN")
                //---TargetAudience endpoints---
                .requestMatchers("/target-audiences/all", "/target-audiences/{id}", "/target-audiences/search/{prefix}").permitAll()
                .requestMatchers("/target-audiences/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/target-audiences/super-admin/**").hasRole("SUPER_ADMIN")
                //---Topic endpoints---
                .requestMatchers("/topics/all", "/topics/{id}", "/topics/search/{prefix}").permitAll()
                .requestMatchers("/topics/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/topics/super-admin/**").hasRole("SUPER_ADMIN")
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

package com.equal_stage_platform.dev.filter;

import com.equal_stage_platform.dev.service.CustomUserDetailsService;
import com.equal_stage_platform.dev.service.JwtService;
import com.equal_stage_platform.dev.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    // אפשר להוסיף פה מסלולים לדילוג על הפילטר (למשל המסלולים של התחברות וכו')
    private static final List<String> WHITELISTED_PATHS = List.of(
        "/api/auth/",
        "/api/auth/login",
        "/api/auth/register",
        "/api/advanced-faker/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // דילוג על אימות במסלולים לבנים (כמובן אפשר להרחיב)
        if (WHITELISTED_PATHS.stream().anyMatch(requestPath::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Authorization header or does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            String jti = jwtService.extractJti(jwt);
            if (refreshTokenService.isTokenBlacklisted(jti)) {
                logger.warn("JWT is blacklisted, jti: {}", jti);
                sendUnauthorized(response, "JWT blacklisted");
                return;
            }

            UUID userId = jwtService.extractUserId(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserById(userId);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("JWT valid for user: {}", userId);
                } else {
                    logger.warn("Invalid JWT token for user: {}", userId);
                    sendUnauthorized(response, "Invalid JWT token");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            logger.info("JWT token expired: {}", e.getMessage());
            sendUnauthorized(response, "JWT token expired");
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred while processing the JWT");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}

package com.gym.crm.filter;

import com.gym.crm.model.User;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.CustomUserDetails;
import com.gym.crm.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtService.isTokenValid(token)) {
                    String username = jwtService.extractUsername(token);

                    // check that user even exists in DB
                    User user = userRepository.findByUsername(username)
                            .orElse(null);

                    if (user != null) {
                        // check if the token was issued before the last logout
                        Date issuedAt = jwtService.extractIssuedAt(token);
                        LocalDateTime issuedAtLocal = issuedAt.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        boolean tokenInvalidated = user.getLastLogout() != null
                                && issuedAtLocal.isBefore(user.getLastLogout());

                        if (!tokenInvalidated) {
                            CustomUserDetails userDetails = new CustomUserDetails(user);
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.info("Jwt auth successful for user: {}", username);
                        } else {
                            log.warn("Token is invalidated because of logout for user: {}", username);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Jwt auth failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
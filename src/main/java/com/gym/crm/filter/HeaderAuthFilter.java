package com.gym.crm.filter;

import com.gym.crm.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HeaderAuthFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(HeaderAuthFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader("X-Username");
        String password = request.getHeader("X-Password");

        if (username != null && password != null) {
            try {
                userService.authenticate(username, password);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Authentication successful for user: {}", username); // add this
            } catch (Exception e) {
                log.error("Authentication failed for user: {}", username); // add this
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
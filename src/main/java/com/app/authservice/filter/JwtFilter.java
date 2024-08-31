package com.app.authservice.filter;

import com.app.authservice.service.JwtService;
import com.app.authservice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_SIGNATURE = "Invalid token signature";
    private static final String INVALID_TOKEN = "Invalid token";
    private static final String MALFORMED_TOKEN = "Malformed token";
    private static final String DESERIALIZATION_ERROR = "Token deserialization error";
    private static final String USER_NOT_FOUND = "User not found";

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String jwt = authorizationHeader.substring(BEARER_PREFIX.length());
            try {
                String username = jwtService.extractUsername(jwt);
                validateTokenAndSetAuthentication(username, jwt, request, response);
            } catch (IllegalArgumentException e) {
                logger.debug(e.getMessage());
                sendErrorResponse(response, INVALID_TOKEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void validateTokenAndSetAuthentication(String username, String jwt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(username);
                if (jwtService.validateToken(jwt, userDetails)) {
                    setAuthenticationContext(userDetails, request);
                }
            } catch (UsernameNotFoundException e) {
                sendErrorResponse(response, USER_NOT_FOUND);
            }
        }
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }
}
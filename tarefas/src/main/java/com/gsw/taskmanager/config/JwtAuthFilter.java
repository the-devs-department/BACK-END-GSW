package com.gsw.taskmanager.config;

import com.gsw.taskmanager.service.JwtService;
import com.gsw.taskmanager.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final TokenService tokenService;

    public JwtAuthFilter(JwtService jwtService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // permite passar se não houver token
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            if (tokenService.isRevoked(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token foi revogado. Faça login novamente.");
                return;
            }

            if (!jwtService.isTokenValid(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expirado ou inválido. Faça login novamente.");
                return;
            }

            // criar authentication no SecurityContext
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(jwtService.extractUserId(token), null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Erro ao validar token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"status\": %d, \"message\": \"%s\"}", status, message)
        );
    }
}
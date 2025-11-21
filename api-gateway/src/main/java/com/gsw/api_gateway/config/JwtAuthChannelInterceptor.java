package com.gsw.api_gateway.config;

import com.gsw.api_gateway.service.JwtService;
import com.gsw.api_gateway.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return message;
            }
            
            String token = authHeader.replace("Bearer ", "");

            try {
                if (tokenService.isRevoked(token)) {
                    throw new MessagingException("Token foi revogado. Faça login novamente.");
                }

                if (!jwtService.isTokenValid(token)) {
                    throw new MessagingException("Token expirado ou inválido. Faça login novamente.");
                }

                UsernamePasswordAuthenticationToken auth = jwtService.parseEncryptedToken(token);

                if (auth != null) {
                    accessor.setUser(auth);
                }

            } catch (Exception e) {
                System.err.println("Falha na autenticação do WebSocket: " + e.getMessage());
                throw new MessagingException("Erro de autenticação no WebSocket.", e);
            }
        }
        return message;
    }
}
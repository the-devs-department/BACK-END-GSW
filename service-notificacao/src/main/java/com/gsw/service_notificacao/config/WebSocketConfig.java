package com.gsw.service_notificacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // <--- ESSA ANOTAÇÃO É A CHAVE
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo para mensagens enviadas do servidor para o cliente
        config.enableSimpleBroker("/topic", "/queue");

        // Prefixo para mensagens enviadas do cliente para o servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // O endpoint que o front-end (React/Angular) vai conectar
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permite conexão de qualquer lugar (CORS)
                .withSockJS(); // Habilita fallback se o navegador não suportar WS puro
    }
}
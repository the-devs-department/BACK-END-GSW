package com.gsw.api_gateway.config;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

/**
 * Configuração principal de rotas do Spring Cloud Gateway MVC
 *
 * Este arquivo define todas as rotas do API Gateway programaticamente.
 * Cada microserviço é mapeado para uma rota específica.
 */
@Configuration
public class GatewayRoutesConfig {

    /**
     * Define todas as rotas do API Gateway
     *
     * Rotas configuradas:
     * - /usuarios/** -> http://localhost:8080 (Microservice de Usuários)
     * - /tarefas/** -> http://localhost:8081 (Microservice de Tarefas)
     * - /anexos/** -> http://localhost:8082 (Microservice de Anexos)
     * - /logs/** -> http://localhost:8083 (Microservice de Logs)
     * - /notificacoes/** -> http://localhost:8084 (Microservice de Notificações)
     * - /equipes/** -> http://localhost:8085 (Microservice de Equipes)
     */
    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("usuarios-service")
                .route(RequestPredicates.path("/usuarios/**"), HandlerFunctions.http("http://localhost:8080"))
                .build()
            .and(route("tarefas-service")
                .route(RequestPredicates.path("/tarefas/**"), HandlerFunctions.http("http://localhost:8081"))
                .build())
            .and(route("anexo-service")
                .route(RequestPredicates.path("/anexos/**"), HandlerFunctions.http("http://localhost:8082"))
                .build())
            .and(route("log-service")
                .route(RequestPredicates.path("/logs/**"), HandlerFunctions.http("http://localhost:8083"))
                .build())
            .and(route("notificacoes-service")
                .route(RequestPredicates.path("/notificacoes/**"), HandlerFunctions.http("http://localhost:8084"))
                .build())
            .and(route("equipe-service")
                .route(RequestPredicates.path("/equipes/**"), HandlerFunctions.http("http://localhost:8085"))
                .build());
    }
}

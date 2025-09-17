package com.gsw.taskmanager.config;

import com.gsw.taskmanager.service.JwtService;
import com.gsw.taskmanager.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!test")
public class SecurityConfig {

    private final JwtService jwtService;

    private final TokenService tokenService;

    public SecurityConfig(JwtService jwtService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService, tokenService);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/usuarios").authenticated()
                    .requestMatchers("/usuarios/criar").permitAll()
                    .requestMatchers("/usuarios/atualizar").authenticated()
                    .requestMatchers("/auth/login").permitAll()
                    .anyRequest().permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())  // disable default Basic Auth
            .formLogin(form -> form.disable());
        return http.build();
    }
}

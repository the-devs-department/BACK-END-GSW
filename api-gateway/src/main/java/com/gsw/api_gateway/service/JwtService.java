package com.gsw.api_gateway.service;

import com.gsw.api_gateway.client.UsuarioClient;
import com.gsw.api_gateway.dto.LoginRequest;
import com.gsw.api_gateway.dto.TokenResponse;
import com.gsw.api_gateway.dto.UsuarioDTO;
import com.gsw.api_gateway.entity.Usuario;
import com.gsw.api_gateway.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        String jwt = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(usuario.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("roles", usuario.getRoles())
                .signWith(getSigningKey())
                .compact();

        return encrypt(jwt);
    }

    public UsernamePasswordAuthenticationToken parseEncryptedToken(String encryptedToken) {
        String jwt = decrypt(encryptedToken);

        Claims claims = parseClaims(jwt);
        String email = claims.getSubject();

        UsuarioDTO dto;
        try {
            dto = usuarioClient.buscarUsuarioPorEmail(email).getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao buscar usuário no microserviço: " + e.getMessage());
        }

        if (dto == null) {
            throw new BusinessException("Usuário não encontrado");
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.id());
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setAtivo(dto.ativo());
        usuario.setRoles(dto.roles());

        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> resolver) {
        String jwt = decrypt(token);
        Claims claims = parseClaims(jwt);
        return resolver.apply(claims);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    public LocalDateTime getExpiry(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public boolean isTokenValid(String token) {
        try {
            String jwt = decrypt(token);
            Claims claims = parseClaims(jwt);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String jwt) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.substring(0, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting token", e);
        }
    }

    private String decrypt(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.substring(0, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting token", e);
        }
    }

    public TokenResponse autenticar(LoginRequest request) {

        if (request.senha() == null || request.senha().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia no login.");
        }

        UsuarioDTO dto;
        try {
            dto = usuarioClient.buscarUsuarioPorEmail(request.email()).getBody();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário não cadastrado ou erro na comunicação.");
        }

        if (dto == null) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.id());
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setAtivo(dto.ativo());
        usuario.setRoles(dto.roles());

        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Senha incorreta.");
        }

        String token = generateToken(usuario);
        return new TokenResponse(token, usuario.getId());
    }
}

package com.gsw.taskmanager.service;

import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.exception.BusinessException;
import com.gsw.taskmanager.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class JwtService {

    private static final String AES_ALGORITHM = "AES";
    private final UsuarioRepository usuarioRepository;

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    public JwtService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();
        String jwt = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(usuario.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("roles", usuario.getRoles())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return encrypt(jwt);
    }

    public UsernamePasswordAuthenticationToken parseEncryptedToken(String encryptedToken) {
        String jwt = decrypt(encryptedToken);
        return buildAuthenticationFromJwt(jwt);
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

    private UsernamePasswordAuthenticationToken buildAuthenticationFromJwt(String jwt) {
        Claims claims = parseClaims(jwt);
        String email = claims.getSubject();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }

    private Claims parseClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.substring(0, 16).getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting token", e);
        }
    }

    private String decrypt(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.substring(0, 16).getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting token", e);
        }
    }
}

package com.gsw.taskmanager.service;

import com.gsw.taskmanager.repository.UsuarioRepository;
import com.gsw.taskmanager.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    private final String PREFIX = "password_reset:";

    private final String ATTEMPT_PREFIX = "password_reset_attempts:";

    @Value("${front.base.url}")
    private String baseUrl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public String recuperarSenha(String email) {
        // generate a unique token random String
        String token = UUID.randomUUID().toString().replace("-", "");

        if (usuarioRepository.findByEmail(email).isEmpty()) {
            return null;
        }

        // store token -> email
        redisTemplate.opsForValue().set(PREFIX + token, email, 15, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(ATTEMPT_PREFIX + token, "0");

        // Build URL
        String resetUrl = baseUrl + "/auth/resetar-senha/" + token;

        String text = String.format("""
            <p>Olá %s,</p>
            <p>Clique no link abaixo para resetar sua senha:</p>
            <a href="%s">Resetar Senha</a>
            <p>Se você não solicitou, por favor desconsiderar esse e-mail.</p>
            """, email, resetUrl);
        String subject = "Redefinição de senha";
        emailService.sendEmailViaGmail(email, subject, text);

        System.out.println("Password reset url: " + resetUrl);

        return resetUrl;
    }

    public void resetarSenha(String novaSenha, String token) {
        String redisKey = PREFIX + token;
        String attemptsKey = ATTEMPT_PREFIX + token;

        String email = validarToken(token);

        // Increment attempts
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

        // Ensure expiration matches the reset token TTL (if first attempt)
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, 15, TimeUnit.MINUTES);
        }

        // If too many attempts -> invalidate token
        if (attempts != null && attempts > 3) {
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptsKey);
            throw new IllegalArgumentException("Muitas tentativas inválidas. O link expirou.");
        }

        usuarioService.atualizarSenha(email, novaSenha);

        // Remove token to prevent reuse
        redisTemplate.delete(redisKey);
        redisTemplate.delete(attemptsKey);

        System.out.println("Password reset for: " + email.replaceAll("(?<=.{2}).(?=.*@)", "*"));
    }

    public String validarToken(String token) {
        String redisKey = PREFIX + token;
        String email = redisTemplate.opsForValue().get(redisKey);
        if (email == null) {
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        return email;
    }
}

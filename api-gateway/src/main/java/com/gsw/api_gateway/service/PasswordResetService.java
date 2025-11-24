package com.gsw.api_gateway.service;

import com.gsw.api_gateway.client.UsuarioClient;
import com.gsw.api_gateway.dto.UpdatePasswordDTO;
import com.gsw.api_gateway.entity.Usuario;
import com.gsw.api_gateway.repository.UsuarioRepository;
import com.gsw.api_gateway.service.utils.EmailService;
import feign.FeignException;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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
    private UsuarioClient usuarioService;

    @Autowired
    private EmailService emailService;


    public void recuperarSenha(String email) {
        try {
            usuarioService.buscarUsuarioPorEmail(email);
        } catch (FeignException.NotFound e) {
            return;
        } catch (Exception e) {
            throw new RuntimeException("Erro de comunicação com serviço de usuário");
        }

        String token = UUID.randomUUID().toString().replace("-", "");

        redisTemplate.opsForValue().set(PREFIX + token, email, 15, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(ATTEMPT_PREFIX + token, "0", 15, TimeUnit.MINUTES);

        String resetUrl = baseUrl + "/auth/resetar-senha/" + token;

        String text = String.format("""
        <div style="font-family: sans-serif;">
            <p>Olá,</p>
            <p>Recebemos uma solicitação para redefinir sua senha.</p>
            <p>Clique no botão abaixo para prosseguir:</p>
            <a href="%s" style="padding: 10px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Redefinir Senha</a>
            <p><small>O link expira em 15 minutos.</small></p>
            <p><small>Se você não solicitou, por favor ignore este e-mail.</small></p>
        </div>
        """, resetUrl);

        String subject = "Redefinição de senha";
        emailService.sendEmailViaGmail(email, subject, text);
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

        UpdatePasswordDTO request = new UpdatePasswordDTO(email, novaSenha);

        usuarioService.atualizarSenhaUsuario(request);

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

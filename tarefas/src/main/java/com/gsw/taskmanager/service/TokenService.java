package com.gsw.taskmanager.service;

import com.gsw.taskmanager.entity.RevokedToken;
import com.gsw.taskmanager.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor; 
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor 
public class TokenService {

    private final RevokedTokenRepository revokedRepo;

    public void revoke(String token, LocalDateTime expiry) {
        revokedRepo.save(
                RevokedToken.builder()
                        .token(token)
                        .expiryDate(expiry)
                        .build()
        );
    }

    public boolean isRevoked(String token) {
        return revokedRepo.findByToken(token).isPresent();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpired() {
        revokedRepo.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
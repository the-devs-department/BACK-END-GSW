package com.gsw.taskmanager.service;

import com.gsw.taskmanager.entity.RevokedToken;
import com.gsw.taskmanager.repository.RevokedTokenRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Autowired
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
        boolean isRevolked = revokedRepo.findByToken(token).isPresent();
        return isRevolked;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpired() {
        revokedRepo.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}

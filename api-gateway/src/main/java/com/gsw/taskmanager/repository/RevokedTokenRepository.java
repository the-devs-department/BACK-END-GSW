package com.gsw.taskmanager.repository;

import com.gsw.taskmanager.entity.RevokedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RevokedTokenRepository extends MongoRepository<RevokedToken, String> {
    Optional<RevokedToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime date);
}

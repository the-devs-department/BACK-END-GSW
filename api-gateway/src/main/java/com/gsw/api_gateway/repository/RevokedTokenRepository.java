package com.gsw.api_gateway.repository;

import com.gsw.api_gateway.entity.RevokedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RevokedTokenRepository extends MongoRepository<RevokedToken, String> {
    Optional<RevokedToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime date);
}

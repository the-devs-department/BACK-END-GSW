package com.gsw.service_equipe.repository;

import com.gsw.service_equipe.entity.ConviteEquipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConviteEquipeRepository extends MongoRepository<ConviteEquipe, String> {

    Optional<ConviteEquipe> findByToken(String token);

    boolean existsByEmailConvidadoAndEquipeId(String emailConvidado, String equipeId);
}

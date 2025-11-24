package com.gsw.service_equipe.repository;

import com.gsw.service_equipe.entity.Equipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeRepository extends MongoRepository<Equipe,String> {
}

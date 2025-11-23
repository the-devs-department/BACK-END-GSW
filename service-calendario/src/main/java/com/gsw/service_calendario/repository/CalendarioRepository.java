package com.gsw.service_calendario.repository;

import com.gsw.service_calendario.entity.Calendario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarioRepository extends MongoRepository<Calendario, String> {
    // add query methods as needed
}
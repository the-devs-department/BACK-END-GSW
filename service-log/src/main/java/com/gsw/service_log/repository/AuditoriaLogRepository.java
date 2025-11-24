package com.gsw.service_log.repository;

import com.gsw.service_log.entity.AuditoriaLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaLogRepository extends MongoRepository<AuditoriaLog, String> {
    List<AuditoriaLog> findAllByTarefaId(String tarefaId);
}
package com.gsw.taskmanager.repository;

import com.gsw.taskmanager.entity.AuditoriaLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaLogRepository extends MongoRepository<AuditoriaLog, String> {
}
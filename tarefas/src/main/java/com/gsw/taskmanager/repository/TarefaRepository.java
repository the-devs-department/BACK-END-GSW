package com.gsw.taskmanager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.gsw.taskmanager.entity.Tarefa;

@Repository
public interface TarefaRepository extends MongoRepository<Tarefa, String> {
}

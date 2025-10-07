package com.gsw.taskmanager.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.gsw.taskmanager.entity.Tarefa;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface TarefaRepository extends MongoRepository<Tarefa, String> {
    // Busca tarefas pelo id do usuário responsável
    @Query("{ 'responsavel': ?0 }")
    List<Tarefa> findByResponsavel(String responsavelId);
}
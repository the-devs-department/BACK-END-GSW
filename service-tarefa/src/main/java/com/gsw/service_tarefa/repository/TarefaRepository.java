package com.gsw.service_tarefa.repository;
import com.gsw.service_tarefa.entity.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

@Repository
public interface TarefaRepository extends MongoRepository<Tarefa, String> {
    // Busca tarefas pelo id do usuário responsável
    @Query("{ 'responsavel._id': ?0 }")
    List<Tarefa> findByResponsavelId(String responsavelId);
}
package com.gsw.service_equipe.repository;

import com.gsw.service_equipe.entity.EquipeMembro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EquipeMembroRepository extends MongoRepository<EquipeMembro, String> {

    List<EquipeMembro> findByEmailUsuario(String emailUsuario);

    List<EquipeMembro> findByEquipeId(String equipeId);

    boolean existsByEquipeIdAndEmailUsuario(String equipeId, String emailUsuario);

    Optional<EquipeMembro> findByEquipeIdAndEmailUsuario(String equipeId, String emailUsuario);

    long countByEmailUsuario(String emailUsuario);

    long countByEquipeIdAndRole(String equipeId, String role);

    void deleteByEquipeId(String equipeId);
    
}

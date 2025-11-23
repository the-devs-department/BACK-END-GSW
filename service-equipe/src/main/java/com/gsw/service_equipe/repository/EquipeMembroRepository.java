package com.gsw.service_equipe.repository;

import com.gsw.service_equipe.entity.EquipeMembro;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EquipeMembroRepository extends MongoRepository<EquipeMembro, String> {

    List<EquipeMembro> findByEmailUsuario(String emailUsuario);

    List<EquipeMembro> findByEquipeId(String equipeId);

    boolean existsByEquipeIdAndEmailUsuario(String equipeId, String emailUsuario);
}

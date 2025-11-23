package com.gsw.service_equipe.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "equipe_membros")
public class EquipeMembro {

    @Id
    private String id;

    private String equipeId;
    private String emailUsuario;
    private String role;
}

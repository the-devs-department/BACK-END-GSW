package com.gsw.service_equipe.entity;

import com.gsw.service_equipe.dto.ListaUsuariosDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "teams")
@Data
public class Equipe {

    @Id
    private String id;

    @NotNull
    private String equipeNome;

    private String criadoPor;

    private LocalDateTime criadoEm = LocalDateTime.now();

    // Lista de administradores (usuários existentes)
    private List<ListaUsuariosDto> listAdmins;

    // Lista de membros confirmados
    private List<ListaUsuariosDto> listMembers;
}

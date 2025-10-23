package com.gsw.taskmanager.entity;


import com.gsw.taskmanager.dto.logs.ModificacaoLogDto;
import com.gsw.taskmanager.dto.logs.ResponsavelAlteracaoDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "logs")
@Getter
@Setter
public class AuditoriaLog {

    private String id;

    private LocalDateTime criadoEm;

    private String tarefaId;

    private ResponsavelAlteracaoDto responsavel;

    private List<ModificacaoLogDto> modificacoes;
}

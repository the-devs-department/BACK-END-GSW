package com.gsw.service_log.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gsw.service_log.dto.logs.ModificacaoLogDto;
import com.gsw.service_log.dto.logs.ResponsavelAlteracaoDto;

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

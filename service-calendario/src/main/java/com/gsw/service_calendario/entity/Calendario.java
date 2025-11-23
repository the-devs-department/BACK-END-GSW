package com.gsw.service_calendario.entity;

import com.gsw.service_calendario.dto.TarefaDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "calendar")
public class Calendario {
    @Id
    private String id;

    private String usuarioId;

    private List<TarefaDTO> tarefas;
}
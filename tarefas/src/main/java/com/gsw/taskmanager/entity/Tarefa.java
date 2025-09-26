package com.gsw.taskmanager.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "tasks")
@Getter
@Setter
public class Tarefa {

    @Id
    private String id;

    @NotNull
    private String status;

    @NotNull
    private String titulo;

    @NotNull
    private String descricao;

    private String responsavel;

    @NotNull
    private String dataEntrega;

    @NotNull
    private String tema;

    private LocalDateTime dataCriacao; 

    private boolean ativo; // controle de soft delete

    // ANEXOS:
    private List<Anexo> anexos = new ArrayList<>();

    public enum TipoAnexo {
        PDF("application/pdf"),
        DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        MP4("video/mp4"),
        JPEG("image/jpeg"),
        XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        private final String mimeType;

        TipoAnexo(String mimeType) {
            this.mimeType = mimeType;
        }

        @JsonValue
        public String getMimeType() {
            return mimeType;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Anexo {
        
        @Id
        private String id;

        @NotNull
        private String tarefaId;

        @NotNull
        private String usuarioId;

        @Length(min =  1, max = 255, message = "O nome do anexo deve ter no máximo 255 caracteres")
        private String nome;

        @NotNull
        private TipoAnexo tipo;

        @NotNull
        private String url;

        @NotNull
        private LocalDateTime dataUpload;

        @NotNull
        private Long tamanho;
    }
}

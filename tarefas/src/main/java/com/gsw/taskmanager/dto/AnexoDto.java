package com.gsw.taskmanager.dto;


import com.gsw.taskmanager.enums.TipoAnexo;
import lombok.Data;

@Data
public class AnexoDto {
    
    private String id;
    private String nome;
    private TipoAnexo tipo;
    private String url;
    private Long tamanho;
    private String usuarioId;
    private DataHoraDto dataUpload;
}
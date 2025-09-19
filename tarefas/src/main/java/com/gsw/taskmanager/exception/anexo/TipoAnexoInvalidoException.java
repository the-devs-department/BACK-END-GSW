package com.gsw.taskmanager.exception.anexo;

public class TipoAnexoInvalidoException extends RuntimeException {
    public TipoAnexoInvalidoException(String message) {
        super(message);
    }
    
    public TipoAnexoInvalidoException(String detectedType, String fileName) {
        super(String.format("Tipo de arquivo não suportado. Arquivo '%s' tem tipo '%s'. Tipos suportados: PDF, DOCX, MP4, JPEG, XLSX", fileName, detectedType));
    }
}

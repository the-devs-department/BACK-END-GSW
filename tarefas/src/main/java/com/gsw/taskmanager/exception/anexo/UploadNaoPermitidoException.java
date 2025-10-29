package com.gsw.taskmanager.exception.anexo;

public class UploadNaoPermitidoException extends RuntimeException {
    public UploadNaoPermitidoException(String message) {
        super(message);
    }

    public UploadNaoPermitidoException(String tarefaId, String status) {
        super(String.format("Upload não permitido: tarefa %s está em status '%s'. Apenas tarefas em andamento ou concluídas aceitam anexos.", tarefaId, status));
    }
}

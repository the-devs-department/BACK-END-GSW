package com.gsw.taskmanager.exception.anexo;

public class AnexoNaoEncontradoException extends RuntimeException {
    public AnexoNaoEncontradoException(String message) {
        super(message);
    }
    
    public AnexoNaoEncontradoException(String anexoId, String tarefaId) {
        super(String.format("Anexo com ID '%s' não encontrado na tarefa '%s'", anexoId, tarefaId));
    }
}

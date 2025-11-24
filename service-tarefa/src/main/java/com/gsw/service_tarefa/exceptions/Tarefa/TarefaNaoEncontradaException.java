package com.gsw.service_tarefa.exceptions.Tarefa;

public class TarefaNaoEncontradaException extends RuntimeException{
    public TarefaNaoEncontradaException(String message) {
        super(message);
    }

    public TarefaNaoEncontradaException(String tarefaId, String operation) {
        super(String.format("Tarefa com ID '%s' não encontrada para %s", tarefaId, operation));
    }
}

package com.gsw.taskmanager.exception.anexo;

import com.gsw.taskmanager.exception.BusinessException;

public class ArquivoNaoEncontradoException extends BusinessException {
    public ArquivoNaoEncontradoException(String message) {
        super(message);
    }

    public ArquivoNaoEncontradoException(String message, Throwable cause) {
        super(message);
    }
}
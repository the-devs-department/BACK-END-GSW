package com.gsw.service_anexo.exceptions;

public class ArquivoNaoEncontradoException extends BusinessException {
    public ArquivoNaoEncontradoException(String message) {
        super(message);
    }

    public ArquivoNaoEncontradoException(String message, Throwable cause) {
        super(message);
    }
}
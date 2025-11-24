package com.gsw.service_anexo.exceptions;


public class ErroArmazenamentoArquivoException extends BusinessException {
    public ErroArmazenamentoArquivoException(String message) {
        super(message);
    }

    public ErroArmazenamentoArquivoException(String message, Throwable cause) {
        super(message);
    }
}
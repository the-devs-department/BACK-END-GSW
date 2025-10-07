package com.gsw.taskmanager.exception.anexo;

import com.gsw.taskmanager.exception.BusinessException;

public class ErroArmazenamentoArquivoException extends BusinessException {
    public ErroArmazenamentoArquivoException(String message) {
        super(message);
    }

    public ErroArmazenamentoArquivoException(String message, Throwable cause) {
        super(message);
    }
}
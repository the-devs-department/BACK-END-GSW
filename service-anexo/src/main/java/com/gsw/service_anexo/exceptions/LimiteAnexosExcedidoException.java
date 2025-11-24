package com.gsw.service_anexo.exceptions;

public class LimiteAnexosExcedidoException extends RuntimeException {
    public LimiteAnexosExcedidoException(String message) {
        super(message);
    }
    
    public LimiteAnexosExcedidoException(long tamanhoAtualMB, long novoTamanhoMB, long limiteMB) {
        super(String.format("Limite de anexos excedido. Tamanho atual: %dMB, tentando adicionar: %dMB. Limite máximo: %dMB", 
                          tamanhoAtualMB, novoTamanhoMB, limiteMB));
    }
}
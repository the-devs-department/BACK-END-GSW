package com.gsw.taskmanager.service;

public class AtribuicaoRequest {
    private String usuarioId; // ✅ Mude de Long para String

    public String getUsuarioId() { // ✅ Mude o tipo de retorno
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) { // ✅ Mude o tipo do parâmetro
        this.usuarioId = usuarioId;
    }
}
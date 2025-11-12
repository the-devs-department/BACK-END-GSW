package com.gsw.service_tarefa.enum;

public enum Status {
    NAO_INICIADA("nao_iniciada"),
    EM_ANDAMENTO("em_andamento"),
    CONCLUIDA("concluida");

    private String status;
    Status(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
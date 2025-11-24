package com.gsw.service_tarefa.enums;

public enum CategoriaModificacao {
    EDICAO("Edição"),
    CRIACAO("Criação"),
    EXCLUSAO("Exclusão");

    private final String categoria;

    CategoriaModificacao(String categoria) {
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }
}
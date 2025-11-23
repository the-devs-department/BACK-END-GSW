package com.gsw.service_equipe.dto;

import lombok.Data;

import java.util.List;

@Data

public class CriarEquipeRequest {

    private String nome;

    private List<String> membrosEmails;

    private List<String> adminsEmails;
}

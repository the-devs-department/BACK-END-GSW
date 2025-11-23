package com.gsw.service_equipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriarEquipeRequest {
    
    public String criadorEmail;

    public String nome;

    public List<String> membrosEmails;

    public List<String> adminsEmails;
}

package com.gsw.service_usuario.dto.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

     private String nome;
     private String email;
     private List<String> roles;
}
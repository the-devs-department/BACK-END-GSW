package com.gsw.service_usuario.entity;

import lombok.*;
// import jakarta.validation.constraints.NotNull;
// import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gsw.service_usuario.dto.tarefa.TarefaDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter 
@Setter
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String nome;

    private String email;

    private String senha;

    private LocalDateTime dataCadastro;
    private boolean ativo;

    private List<TarefaDto> tarefas = new ArrayList<>();

    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
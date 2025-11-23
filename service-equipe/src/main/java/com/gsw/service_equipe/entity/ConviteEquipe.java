package com.gsw.service_equipe.entity;

import com.gsw.service_equipe.enums.StatusConvite;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "convites_equipes")
@Data
public class ConviteEquipe {

    @Id
    private String id;

    @NotNull
    private String emailConvidado;

    @NotNull
    private String equipeId;

    @NotNull
    private StatusConvite status = StatusConvite.PENDENTE;

    private String token; // UUID para validar o convite

    private LocalDateTime enviadoEm = LocalDateTime.now();
}

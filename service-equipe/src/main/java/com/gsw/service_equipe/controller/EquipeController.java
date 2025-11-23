package com.gsw.service_equipe.controller;

import com.gsw.service_equipe.entity.Equipe;
import com.gsw.service_equipe.service.EquipeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;  // ✅ INJEÇÃO DO SERVICE

    @GetMapping("/minhas-equipes")
    public ResponseEntity<List<Equipe>> minhasEquipes(@RequestHeader("email") String email) {
        return ResponseEntity.ok(equipeService.listarEquipesDoUsuario(email));
    }
}

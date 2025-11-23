package com.gsw.service_equipe.controller;

import com.gsw.service_equipe.dto.CriarEquipeRequest;
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

    private final EquipeService equipeService;

    @GetMapping("/minhas-equipes")
    public ResponseEntity<List<Equipe>> minhasEquipes(@RequestParam("email") String email) {
        return ResponseEntity.ok(equipeService.listarEquipesDoUsuario(email));
    }

    @PostMapping("/criar")
    public ResponseEntity<Equipe> criarEquipe(@RequestBody CriarEquipeRequest request) {
        return ResponseEntity.ok(
            equipeService.criarEquipe(request.getCriadorEmail(), request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipe> atualizarEquipe(
            @RequestParam("email") String email,
            @PathVariable String id,
            @RequestBody String request) {

        return ResponseEntity.ok(equipeService.atualizarEquipe(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEquipe(
            @RequestParam("email") String email,
            @PathVariable String id) {

        equipeService.excluirEquipe(email, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sair")
    public ResponseEntity<Void> sairEquipe(
            @RequestParam("email") String email,
            @PathVariable String id) {

        equipeService.sairDaEquipe(email, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{equipeId}/membros")
    public ResponseEntity<Void> removerMembro(
        @PathVariable String equipeId,
        @RequestParam String emailSolicitante,
        @RequestParam String emailRemovido
    ) {
        equipeService.removerMembro(equipeId, emailSolicitante, emailRemovido);
        return ResponseEntity.noContent().build();
    }
}

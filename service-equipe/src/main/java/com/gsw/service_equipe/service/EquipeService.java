package com.gsw.service_equipe.service;

import com.gsw.service_equipe.client.UsuarioClient;
import com.gsw.service_equipe.dto.CriarEquipeRequest;
import com.gsw.service_equipe.entity.ConviteEquipe;
import com.gsw.service_equipe.entity.Equipe;
import com.gsw.service_equipe.entity.EquipeMembro;
import com.gsw.service_equipe.enums.StatusConvite;
import com.gsw.service_equipe.repository.ConviteEquipeRepository;
import com.gsw.service_equipe.repository.EquipeMembroRepository;
import com.gsw.service_equipe.repository.EquipeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final ConviteEquipeRepository conviteRepository;
    private UsuarioClient usuarioClient;
    private final EquipeMembroRepository equipeMembroRepository;

    public Equipe criarEquipe(CriarEquipeRequest request, String emailCriador) {

        // 1 — Criar equipe
        Equipe equipe = new Equipe();
        equipe.setEquipeNome(request.getNome());
        equipe.setCriadoPor(emailCriador);
        equipe = equipeRepository.save(equipe);

        // 2 — Criador vira ADMIN e entra na equipe
        registrarMembro(equipe.getId(), emailCriador, "ADMIN");
        try {
            usuarioClient.atualizarRole(emailCriador, "ROLE_ADMIN");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar role do criador: " + e.getMessage());
        }

        // 3 — Admins adicionais entram direto (sem convite)
        if (request.getAdminsEmails() != null) {
            for (String adminEmail : request.getAdminsEmails()) {

                if (adminEmail.equalsIgnoreCase(emailCriador)) continue;

                registrarMembro(equipe.getId(), adminEmail, "ADMIN");

                try {
                    usuarioClient.atualizarRole(adminEmail, "ROLE_ADMIN");
                } catch (Exception e) {
                    System.out.println("Erro ao atualizar role do admin: " + adminEmail);
                }
            }
        }

        // 4 — Membros recebem convite
        if (request.getMembrosEmails() != null) {
            for (String memberEmail : request.getMembrosEmails()) {
                criarConvite(memberEmail, equipe.getId());
            }
        }

        return equipe;
    }

    private void registrarMembro(String equipeId, String email, String role) {

        if (equipeMembroRepository.existsByEquipeIdAndEmailUsuario(equipeId, email)) {
            return;
        }

        EquipeMembro membro = new EquipeMembro();
        membro.setEquipeId(equipeId);
        membro.setEmailUsuario(email);
        membro.setRole(role);

        equipeMembroRepository.save(membro);
    }

    private void criarConvite(String email, String equipeId) {

        if (conviteRepository.existsByEmailConvidadoAndEquipeId(email, equipeId)) {
            return;
        }

        ConviteEquipe convite = new ConviteEquipe();
        convite.setEmailConvidado(email);
        convite.setEquipeId(equipeId);
        convite.setToken(UUID.randomUUID().toString());
        convite.setStatus(StatusConvite.PENDENTE);

        conviteRepository.save(convite);
    }

    public String aceitarConvite(String token) {

        ConviteEquipe convite = conviteRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Convite inválido ou expirado"));

        if (convite.getStatus() == StatusConvite.ACEITO) {
            return "Este convite já foi utilizado.";
        }

        // Registrar membro como USER
        registrarMembro(convite.getEquipeId(), convite.getEmailConvidado(), "USER");

        // Atualizar role global
        try {
            usuarioClient.atualizarRole(convite.getEmailConvidado(), "ROLE_USER");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao atualizar role do usuário convidado");
        }

        convite.setStatus(StatusConvite.ACEITO);
        conviteRepository.save(convite);

        return "Convite aceito com sucesso!";
    }

    public List<Equipe> listarEquipesDoUsuario(String email) {

        // 1 — Buscar listas de relacionamento
        List<EquipeMembro> relacoes = equipeMembroRepository.findByEmailUsuario(email);

        // 2 — Extrair IDs das equipes
        List<String> idsEquipes = relacoes.stream()
                .map(EquipeMembro::getEquipeId)
                .collect(Collectors.toList());

        // 3 — Carregar equipes reais
        return equipeRepository.findAllById(idsEquipes);
    }
}

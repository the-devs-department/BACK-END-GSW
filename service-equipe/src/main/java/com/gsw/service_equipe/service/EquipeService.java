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
    private final UsuarioClient usuarioClient;
    private final EquipeMembroRepository equipeMembroRepository;

    // ============================================================
    // CRIAR EQUIPE
    // ============================================================
    public Equipe criarEquipe(String emailCriador, CriarEquipeRequest request) {

        validarLimiteEquipes(emailCriador);

        Equipe equipe = new Equipe();
        equipe.setEquipeNome(request.getNome());
        equipe.setCriadoPor(emailCriador);
        equipe = equipeRepository.save(equipe);

        registrarMembro(equipe.getId(), emailCriador, "ROLE_ADMIN");

        try {
            usuarioClient.atualizarRole(emailCriador, "ROLE_ADMIN");
        } catch (Exception ignored) {}

        if (request.getAdminsEmails() != null) {
            for (String adminEmail : request.getAdminsEmails()) {
                if (adminEmail.equalsIgnoreCase(emailCriador)) continue;

                registrarMembro(equipe.getId(), adminEmail, "ADMIN");

                try {
                    usuarioClient.atualizarRole(adminEmail, "ROLE_ADMIN");
                } catch (Exception ignored) {}
            }
        }

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
                .orElseThrow(() -> new RuntimeException("Convite inválido"));

        if (convite.getStatus() == StatusConvite.ACEITO) {
            return "Convite já utilizado.";
        }

        registrarMembro(convite.getEquipeId(), convite.getEmailConvidado(), "USER");

        try {
            usuarioClient.atualizarRole(convite.getEmailConvidado(), "ROLE_USER");
        } catch (Exception ignored) {}

        convite.setStatus(StatusConvite.ACEITO);
        conviteRepository.save(convite);

        return "Convite aceito.";
    }

    public List<Equipe> listarEquipesDoUsuario(String email) {

        List<EquipeMembro> relacoes = equipeMembroRepository.findByEmailUsuario(email);

        List<String> idsEquipes = relacoes.stream()
                .map(EquipeMembro::getEquipeId)
                .collect(Collectors.toList());

        return equipeRepository.findAllById(idsEquipes);
    }

    private void validarLimiteEquipes(String email) {
        int quantidade = equipeMembroRepository.findByEmailUsuario(email).size();

        if (quantidade >= 20) {
            throw new RuntimeException("Você atingiu o limite de 20 equipes. Saia de alguma equipe antes.");
        }
    }

    private void validarAcessoAdmin(String email, String equipeId) {

        EquipeMembro membro = equipeMembroRepository
                .findByEquipeIdAndEmailUsuario(equipeId, email)
                .orElseThrow(() -> new RuntimeException("Você não faz parte da equipe"));

        if (!membro.getRole().equals("ADMIN")) {
            throw new RuntimeException("Ação permitida apenas para administradores da equipe.");
        }
    }

    public void removerMembro(String equipeId, String emailSolicitante, String emailRemovido) {

    Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new RuntimeException("Equipe não encontrada"));

    List<EquipeMembro> membros = equipeMembroRepository.findByEquipeId(equipeId);

    EquipeMembro solicitante = membros.stream()
            .filter(m -> m.getEmailUsuario().equals(emailSolicitante))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Você não faz parte da equipe"));

    if (!"ADMIN".equals(solicitante.getRole())) {
        throw new RuntimeException("Apenas administradores podem remover membros.");
    }

    EquipeMembro removido = membros.stream()
            .filter(m -> m.getEmailUsuario().equals(emailRemovido))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Usuário não faz parte da equipe"));

    long qtdAdmins = membros.stream()
            .filter(m -> "ADMIN".equals(m.getRole()))
            .count();

    if ("ADMIN".equals(removido.getRole()) && qtdAdmins == 1) {
        throw new RuntimeException("Você não pode remover o único administrador da equipe.");
    }

    // Remover membro
    equipeMembroRepository.deleteById(removido.getId());
    }

    public String sairDaEquipe(String equipeId, String emailUsuario) {

    // Verificar se o usuário está na equipe
    EquipeMembro membro = equipeMembroRepository
            .findByEquipeIdAndEmailUsuario(equipeId, emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não está na equipe."));

    // Verificar se ele é admin
    boolean isAdmin = membro.getRole().equalsIgnoreCase("ADMIN");

    if (isAdmin) {
        long totalAdmins = equipeMembroRepository.countByEquipeIdAndRole(equipeId, "ADMIN");

        if (totalAdmins <= 1) {
            return "Você é o único administrador da equipe. Nomeie outro administrador antes de sair.";
        }
    }

    // Remover da equipe
    equipeMembroRepository.delete(membro);

    return "Você saiu da equipe com sucesso.";
}

public Equipe atualizarEquipe(String equipeId, String novoNome, String emailUsuario) {

    // Carregar equipe
    Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new RuntimeException("Equipe não encontrada."));

    // Verificar permissão
    EquipeMembro membro = equipeMembroRepository
            .findByEquipeIdAndEmailUsuario(equipeId, emailUsuario)
            .orElseThrow(() -> new RuntimeException("Você não pertence à equipe."));

    if (!membro.getRole().equalsIgnoreCase("ADMIN")) {
        throw new RuntimeException("Apenas administradores podem atualizar a equipe.");
    }

    // Verificar nome
    if (novoNome == null || novoNome.trim().isEmpty()) {
        throw new RuntimeException("O nome da equipe não pode estar vazio.");
    }

    equipe.setEquipeNome(novoNome);
    return equipeRepository.save(equipe);
}

public String excluirEquipe(String equipeId, String emailUsuario) {

    // Validar existência
    Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new RuntimeException("Equipe não encontrada."));

    // Validar permissão
    EquipeMembro membro = equipeMembroRepository
            .findByEquipeIdAndEmailUsuario(equipeId, emailUsuario)
            .orElseThrow(() -> new RuntimeException("Você não pertence à equipe."));

    if (!membro.getRole().equalsIgnoreCase("ADMIN")) {
        throw new RuntimeException("Apenas administradores podem excluir a equipe.");
    }

    // Remover membros
    equipeMembroRepository.deleteByEquipeId(equipeId);

    // Remover convites
    conviteRepository.deleteAll(
            conviteRepository.findAll().stream()
                    .filter(c -> c.getEquipeId().equals(equipeId))
                    .toList()
    );

    // Remover equipe
    equipeRepository.delete(equipe);

    return "Equipe excluída com sucesso.";
}

}

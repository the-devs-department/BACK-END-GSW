package com.gsw.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsw.taskmanager.dto.AnexoDto;
import com.gsw.taskmanager.entity.Anexo;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.enums.Status;
import com.gsw.taskmanager.enums.TipoAnexo;
import com.gsw.taskmanager.repository.TarefaRepository;
import com.gsw.taskmanager.repository.UsuarioRepository;
import com.gsw.taskmanager.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnexoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Tarefa tarefa;
    private String token;
    private String tarefaId;
    private String usuarioId;

    private Anexo criarAnexoExemplo(
            String id,
            String tarefaId,
            String usuarioId,
            String nome,
            TipoAnexo tipo,
            String url,
            Long tamanho,
            LocalDateTime dataUpload
    ) {
        Anexo anexo = new Anexo();

        anexo.setId(id != null ? id : UUID.randomUUID().toString());
        anexo.setTarefaId(tarefaId != null ? tarefaId : "tarefa-default");
        anexo.setUsuarioId(usuarioId != null ? usuarioId : "usuario-default");
        anexo.setNome(nome != null ? nome : "documento-teste.pdf");
        anexo.setTipo(tipo != null ? tipo : TipoAnexo.PDF);
        anexo.setUrl(url != null ? url : "/uploads/documento-teste.pdf");
        anexo.setTamanho(tamanho != null ? tamanho : 1024L * 1024L);
        anexo.setDataUpload(dataUpload != null ? dataUpload : LocalDateTime.now());

        return anexo;
    }

    @BeforeEach
    void setUp() {
        // Limpar coleções
        mongoTemplate.dropCollection(Usuario.class);
        mongoTemplate.dropCollection(Tarefa.class);

        // Criar usuário de teste
        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("thedevs@gsw.com");
        usuario.setSenha(passwordEncoder.encode("thedevs123")); // Senha criptografada com PasswordEncoder
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true);
        usuario.setTarefas(new ArrayList<>());
        usuario.setRoles(List.of("USER"));
        usuario = usuarioRepository.save(usuario);
        usuarioId = usuario.getId();

        // Gerar token JWT
        token = jwtService.generateToken(usuario);

        // Criar tarefa de teste
        tarefa = new Tarefa();
        tarefa.setTitulo("Tarefa de Teste");
        tarefa.setDescricao("Descrição da tarefa para testar anexos");
        tarefa.setResponsavel(usuarioId);
        tarefa.setDataEntrega("2024-12-31");
        tarefa.setTema("Desenvolvimento");
        tarefa.setDataCriacao(LocalDateTime.now());
        tarefa.setStatus(Status.EM_ANDAMENTO);
        tarefa.setAtivo(true);
        tarefa.setAnexos(new ArrayList<>());
        tarefa = tarefaRepository.save(tarefa);
        tarefaId = tarefa.getId();
    }

    @Test
    void listarAnexos_DeveRetornarListaVazia_QuandoNaoHaAnexos() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listarAnexos_DeveRetornarAnexos_QuandoExistem() throws Exception {
        Anexo anexo1 = criarAnexoExemplo("anexo1", tarefaId, usuarioId, "documento1.pdf", null, null, null, null);
        Anexo anexo2 = criarAnexoExemplo("anexo2", tarefaId + "D3", usuarioId, "planilha.xlsx", TipoAnexo.XLSX, null, null, null);

        tarefa.getAnexos().add(anexo1);
        tarefa.getAnexos().add(anexo2);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("anexo1")))
                .andExpect(jsonPath("$[0].nome", is("documento1.pdf")))
                .andExpect(jsonPath("$[0].tipo", is(TipoAnexo.PDF.getMimeType())))
                .andExpect(jsonPath("$[1].id", is("anexo2")))
                .andExpect(jsonPath("$[1].nome", is("planilha.xlsx")))
                .andExpect(jsonPath("$[1].tipo", is(TipoAnexo.XLSX.getMimeType())));
    }

    @Test
    void listarAnexos_DeveRetornar404_QuandoTarefaNaoExiste() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", "tarefaInexistente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarAnexo_DeveRetornarAnexo_QuandoExiste() throws Exception {

        Anexo anexo = criarAnexoExemplo("anexo123", tarefaId, usuarioId, "documento.pdf", TipoAnexo.PDF, "/uploads/documento.pdf", null, null);

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("anexo123")))
                .andExpect(jsonPath("$.nome", is("documento.pdf")))
                .andExpect(jsonPath("$.tipo", is(TipoAnexo.PDF.getMimeType())))
                .andExpect(jsonPath("$.usuarioId", is(usuarioId)))
                .andExpect(jsonPath("$.tamanho", is(1048576)));
    }

    @Test
    void buscarAnexo_DeveRetornar404_QuandoAnexoNaoExiste() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexoInexistente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarAnexo_DeveAtualizarComSucesso() throws Exception {

        Anexo anexoExistente = criarAnexoExemplo("anexo123", null , null, "documento_original.pdf", TipoAnexo.PDF, "/uploads/documento_original.pdf", null, null);

        tarefa.getAnexos().add(anexoExistente);
        tarefaRepository.save(tarefa);

        AnexoDto anexoAtualizado = new AnexoDto();
        anexoAtualizado.setNome("documento_atualizado.pdf");
        anexoAtualizado.setTipo(TipoAnexo.DOCX);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoAtualizado);

        mockMvc.perform(put("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("documento_atualizado.pdf")))
                .andExpect(jsonPath("$.tipo", is(TipoAnexo.DOCX.getMimeType())));
    }

    @Test
    void removerAnexo_DeveRetornar403_QuandoUsuarioNaoTemPermissao() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setNome("Usuario Teste");
        outroUsuario.setEmail("usuario@gsw.com");
        outroUsuario.setSenha(passwordEncoder.encode("usuario123")); // Senha criptografada com PasswordEncoder
        outroUsuario.setDataCadastro(LocalDateTime.now());
        outroUsuario.setAtivo(true);
        outroUsuario.setTarefas(new ArrayList<>());
        outroUsuario.setRoles(List.of("USER"));
        outroUsuario = usuarioRepository.save(outroUsuario);
        String outroToken = jwtService.generateToken(outroUsuario);

        Anexo anexo = criarAnexoExemplo("anexo123", tarefaId , "usuarioProprietarioDoAnexo", "documento.pdf", TipoAnexo.PDF, "/uploads/documento.pdf", null, null);

        tarefa.setResponsavel("outroResponsavel"); // Tarefa não pertence ao usuário autenticado
        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(delete("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + outroToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("Você não tem permissão para excluir este anexo")));
    }

    @Test
    void removerAnexo_DeveRetornar404_QuandoAnexoNaoExiste() throws Exception {
        mockMvc.perform(delete("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexoInexistente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerAnexo_DeveRetornar404_QuandoTarefaNaoExiste() throws Exception {
        mockMvc.perform(delete("/tarefas/{tarefaId}/anexos/{anexoId}", "tarefaInexistente", "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void devePermitirLeituraESimplesOperacoes_QuandoEmModoTeste() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId))
                .andExpect(status().isOk());

        Anexo anexo = criarAnexoExemplo("anexo_leitura_teste", tarefaId , usuarioId, "documento_leitura.pdf", TipoAnexo.PDF, "/uploads/documento_leitura.pdf", null, null);

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);


        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo_leitura_teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("documento_leitura.pdf")))
                .andExpect(jsonPath("$.tipo", is(TipoAnexo.PDF.getMimeType())));


        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("documento_leitura.pdf")));
    }

    @Test
    void listarAnexos_DeveRetornarAnexosOrdenadosPorDataUpload() throws Exception {
        LocalDateTime agora = LocalDateTime.now();

        Anexo anexo1 = criarAnexoExemplo("anexo1", tarefaId , usuarioId, "primeiro.pdf", TipoAnexo.PDF, "/uploads/primeiro.pdf", null, agora.minusHours(2));

        Anexo anexo2 = criarAnexoExemplo("anexo2", tarefaId , usuarioId, "segundo.pdf", TipoAnexo.PDF, "/uploads/segundo.pdf", null, agora.minusHours(1));

        tarefa.getAnexos().add(anexo1);
        tarefa.getAnexos().add(anexo2);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("primeiro.pdf")))
                .andExpect(jsonPath("$[1].nome", is("segundo.pdf")));
    }

    @Test
    void adicionarAnexoComUpload_DeveCriarAnexoComSucesso() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "documento.pdf",
                TipoAnexo.PDF.getMimeType(),
                "conteudo do arquivo PDF".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", tarefaId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("documento.pdf")))
                .andExpect(jsonPath("$.tipo", is(TipoAnexo.PDF.getMimeType())))
                .andExpect(jsonPath("$.tarefaId", is(tarefaId)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dataUpload", notNullValue()));
    }

    @Test
    void adicionarAnexoComUpload_DevePermitirArquivoVazio() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "arquivo_vazio.pdf",
                TipoAnexo.PDF.getMimeType(),
                new byte[0]
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", tarefaId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("arquivo_vazio.pdf")))
                .andExpect(jsonPath("$.tamanho", is(0)));
    }

    @Test
    void obterUrlDownload_DeveRetornarUrlCorreta() throws Exception {

        Anexo anexo = criarAnexoExemplo("anexo123", tarefaId , usuarioId, "documento.pdf", TipoAnexo.PDF, "/uploads/documento.pdf", null, null);

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}/download", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/tarefas/" + tarefaId + "/anexos/anexo123/arquivo/download")));
    }

    @Test
    void obterUrlDownload_DeveRetornarUrlSempreQuandoTarefaExiste() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}/download", tarefaId, "anexoInexistente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/tarefas/" + tarefaId + "/anexos/anexoInexistente/arquivo/download")));
    }

    @Test
    void baixarArquivoAnexo_DeveRetornar400_QuandoArquivoNaoExisteNoSistema() throws Exception {

        Anexo anexo = criarAnexoExemplo("anexo123", tarefaId , usuarioId, "documento.pdf", TipoAnexo.PDF, "/uploads/test/documento_inexistente.pdf", null, null);

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}/arquivo/download", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adicionarAnexoComUpload_DeveRetornar400_QuandoTipoArquivoInvalido() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "documento.txt",
                "text/plain",
                "conteudo do arquivo TXT".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", tarefaId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Tipo de arquivo não suportado")));
    }

    @Test
    void adicionarAnexoComUpload_DeveRetornar400_QuandoLimiteAnexosExcedido() throws Exception {
        // Criar um arquivo grande que excede o limite (> 20MB)
        byte[] arquivoGrande = new byte[21 * 1024 * 1024]; // 21MB
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "arquivo_grande.pdf",
                TipoAnexo.PDF.getMimeType(),
                arquivoGrande
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", tarefaId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Limite de anexos excedido")));
    }

    @Test
    void atualizarAnexo_DeveRetornar404_QuandoTarefaNaoExiste() throws Exception {
        AnexoDto anexoAtualizado = new AnexoDto();
        anexoAtualizado.setNome("documento_atualizado.pdf");
        anexoAtualizado.setTipo(TipoAnexo.PDF);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoAtualizado);

        mockMvc.perform(put("/tarefas/{tarefaId}/anexos/{anexoId}", "tarefaInexistente", "anexo123")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarAnexo_DeveRetornar404_QuandoAnexoNaoExiste() throws Exception {
        AnexoDto anexoAtualizado = new AnexoDto();
        anexoAtualizado.setNome("documento_atualizado.pdf");
        anexoAtualizado.setTipo(TipoAnexo.PDF);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoAtualizado);

        mockMvc.perform(put("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexoInexistente")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void adicionarAnexoComUpload_DeveRetornar404_QuandoTarefaNaoExiste() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "documento.pdf",
                "application/pdf",
                "conteudo do arquivo PDF".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", "tarefaInexistente")
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
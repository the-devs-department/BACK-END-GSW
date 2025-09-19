package com.gsw.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsw.taskmanager.dto.AnexoDto;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.repository.TarefaRepository;
import com.gsw.taskmanager.repository.UsuarioRepository;
import com.gsw.taskmanager.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

    private Usuario usuario;
    private Tarefa tarefa;
    private String token;
    private String tarefaId;
    private String usuarioId;

    @BeforeEach
    void setUp() {
        // Limpar coleções
        mongoTemplate.dropCollection(Usuario.class);
        mongoTemplate.dropCollection(Tarefa.class);

        // Criar usuário de teste
        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setSenha("$2a$10$dummyHashedPassword"); // Password já hasheada para testes
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
        Tarefa.Anexo anexo1 = Tarefa.Anexo.builder()
                .id("anexo1")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento1.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento1.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        Tarefa.Anexo anexo2 = Tarefa.Anexo.builder()
                .id("anexo2")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("planilha.xlsx")
                .tipo(Tarefa.TipoAnexo.XLSX)
                .url("/uploads/planilha.xlsx")
                .tamanho(2L * 1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(anexo1);
        tarefa.getAnexos().add(anexo2);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("anexo1")))
                .andExpect(jsonPath("$[0].nome", is("documento1.pdf")))
                .andExpect(jsonPath("$[0].tipo", is("application/pdf")))
                .andExpect(jsonPath("$[1].id", is("anexo2")))
                .andExpect(jsonPath("$[1].nome", is("planilha.xlsx")))
                .andExpect(jsonPath("$[1].tipo", is("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")));
    }

    @Test
    void listarAnexos_DeveRetornar404_QuandoTarefaNaoExiste() throws Exception {
        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", "tarefaInexistente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarAnexo_DeveRetornarAnexo_QuandoExiste() throws Exception {
        Tarefa.Anexo anexo = Tarefa.Anexo.builder()
                .id("anexo123")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("anexo123")))
                .andExpect(jsonPath("$.nome", is("documento.pdf")))
                .andExpect(jsonPath("$.tipo", is("application/pdf")))
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
    void adicionarAnexo_DeveCriarAnexoComSucesso() throws Exception {
        AnexoDto anexoDto = new AnexoDto();
        anexoDto.setNome("novo_documento.pdf");
        anexoDto.setTipo(Tarefa.TipoAnexo.PDF);
        anexoDto.setUrl("/uploads/novo_documento.pdf");
        anexoDto.setTamanho(1024L * 1024L); // 1MB
        anexoDto.setUsuarioId(usuarioId);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoDto);

        mockMvc.perform(post("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("novo_documento.pdf")))
                .andExpect(jsonPath("$.tipo", is("application/pdf")))
                .andExpect(jsonPath("$.usuarioId", is(usuarioId)))
                .andExpect(jsonPath("$.tarefaId", is(tarefaId)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dataUpload", notNullValue()));
    }

    @Test
    void adicionarAnexo_DeveRetornar400_QuandoExcedeLimite() throws Exception {
        for (int i = 0; i < 19; i++) {
            Tarefa.Anexo anexoExistente = Tarefa.Anexo.builder()
                    .id("anexo" + i)
                    .tarefaId(tarefaId)
                    .usuarioId(usuarioId)
                    .nome("arquivo" + i + ".pdf")
                    .tipo(Tarefa.TipoAnexo.PDF)
                    .url("/uploads/arquivo" + i + ".pdf")
                    .tamanho(1024L * 1024L) // 1MB cada
                    .dataUpload(LocalDateTime.now())
                    .build();
            tarefa.getAnexos().add(anexoExistente);
        }
        tarefaRepository.save(tarefa);

        AnexoDto anexoDto = new AnexoDto();
        anexoDto.setNome("arquivo_grande.pdf");
        anexoDto.setTipo(Tarefa.TipoAnexo.PDF);
        anexoDto.setUrl("/uploads/arquivo_grande.pdf");
        anexoDto.setTamanho(2L * 1024L * 1024L); // 2MB
        anexoDto.setUsuarioId(usuarioId);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoDto);

        mockMvc.perform(post("/tarefas/{tarefaId}/anexos", tarefaId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Limite de anexos excedido")));
    }

    @Test
    void atualizarAnexo_DeveAtualizarComSucesso() throws Exception {
        Tarefa.Anexo anexoExistente = Tarefa.Anexo.builder()
                .id("anexo123")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento_original.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento_original.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(anexoExistente);
        tarefaRepository.save(tarefa);

        AnexoDto anexoAtualizado = new AnexoDto();
        anexoAtualizado.setNome("documento_atualizado.pdf");
        anexoAtualizado.setTipo(Tarefa.TipoAnexo.DOCX);

        String anexoDtoJson = objectMapper.writeValueAsString(anexoAtualizado);

        mockMvc.perform(put("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("documento_atualizado.pdf")))
                .andExpect(jsonPath("$.tipo", is("application/vnd.openxmlformats-officedocument.wordprocessingml.document")));
    }

    @Test
    void atualizarAnexo_DeveRetornar404_QuandoAnexoNaoExiste() throws Exception {
        AnexoDto anexoAtualizado = new AnexoDto();
        anexoAtualizado.setNome("documento_atualizado.pdf");

        String anexoDtoJson = objectMapper.writeValueAsString(anexoAtualizado);

        mockMvc.perform(put("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexoInexistente")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(anexoDtoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerAnexo_DeveRetornar403_QuandoUsuarioNaoTemPermissao() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setNome("Maria Santos");
        outroUsuario.setEmail("maria@example.com");
        outroUsuario.setSenha("$2a$10$dummyHashedPassword");
        outroUsuario.setDataCadastro(LocalDateTime.now());
        outroUsuario.setAtivo(true);
        outroUsuario.setTarefas(new ArrayList<>());
        outroUsuario.setRoles(List.of("USER"));
        outroUsuario = usuarioRepository.save(outroUsuario);
        String outroToken = jwtService.generateToken(outroUsuario);

        Tarefa.Anexo anexo = Tarefa.Anexo.builder()
                .id("anexo123")
                .tarefaId(tarefaId)
                .usuarioId("usuarioProprietarioDoAnexo") // Diferente do usuário autenticado
                .nome("documento.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

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

        Tarefa.Anexo anexo = Tarefa.Anexo.builder()
                .id("anexo_leitura_teste")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento_leitura.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento_leitura.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);


        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}", tarefaId, "anexo_leitura_teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("documento_leitura.pdf")))
                .andExpect(jsonPath("$.tipo", is("application/pdf")));


        mockMvc.perform(get("/tarefas/{tarefaId}/anexos", tarefaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("documento_leitura.pdf")));
    }

    @Test
    void adicionarAnexo_DeveValidarTiposDeAnexoSuportados() throws Exception {
        Tarefa.TipoAnexo[] tiposSuportados = {
            Tarefa.TipoAnexo.PDF,
            Tarefa.TipoAnexo.DOCX,
            Tarefa.TipoAnexo.MP4,
            Tarefa.TipoAnexo.JPEG,
            Tarefa.TipoAnexo.XLSX
        };

        for (Tarefa.TipoAnexo tipo : tiposSuportados) {
            AnexoDto anexoDto = new AnexoDto();
            anexoDto.setNome("arquivo." + tipo.name().toLowerCase());
            anexoDto.setTipo(tipo);
            anexoDto.setUrl("/uploads/arquivo." + tipo.name().toLowerCase());
            anexoDto.setTamanho(1024L * 1024L); // 1MB
            anexoDto.setUsuarioId(usuarioId);

            String anexoDtoJson = objectMapper.writeValueAsString(anexoDto);

            mockMvc.perform(post("/tarefas/{tarefaId}/anexos", tarefaId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(anexoDtoJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipo", is(tipo.getMimeType())));

            // Limpar anexos para o próximo teste
            tarefa.getAnexos().clear();
            tarefaRepository.save(tarefa);
        }
    }

    @Test
    void listarAnexos_DeveRetornarAnexosOrdenadosPorDataUpload() throws Exception {
        LocalDateTime agora = LocalDateTime.now();
        
        Tarefa.Anexo anexo1 = Tarefa.Anexo.builder()
                .id("anexo1")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("primeiro.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/primeiro.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(agora.minusHours(2))
                .build();

        Tarefa.Anexo anexo2 = Tarefa.Anexo.builder()
                .id("anexo2")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("segundo.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/segundo.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(agora.minusHours(1))
                .build();

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
                "application/pdf",
                "conteudo do arquivo PDF".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/tarefas/{tarefaId}/anexos/upload", tarefaId)
                .file(arquivo)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("documento.pdf")))
                .andExpect(jsonPath("$.tipo", is("application/pdf")))
                .andExpect(jsonPath("$.tarefaId", is(tarefaId)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.dataUpload", notNullValue()));
    }

    @Test
    void adicionarAnexoComUpload_DevePermitirArquivoVazio() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo",
                "arquivo_vazio.pdf",
                "application/pdf",
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
        Tarefa.Anexo anexo = Tarefa.Anexo.builder()
                .id("anexo123")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/documento.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

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
        Tarefa.Anexo anexo = Tarefa.Anexo.builder()
                .id("anexo123")
                .tarefaId(tarefaId)
                .usuarioId(usuarioId)
                .nome("documento.pdf")
                .tipo(Tarefa.TipoAnexo.PDF)
                .url("/uploads/test/documento_inexistente.pdf")
                .tamanho(1024L * 1024L)
                .dataUpload(LocalDateTime.now())
                .build();

        tarefa.getAnexos().add(anexo);
        tarefaRepository.save(tarefa);

        mockMvc.perform(get("/tarefas/{tarefaId}/anexos/{anexoId}/arquivo/download", tarefaId, "anexo123")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }
}
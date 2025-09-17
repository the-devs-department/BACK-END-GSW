package com.gsw.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsw.taskmanager.entity.Tarefa;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(Usuario.class);
        mongoTemplate.dropCollection(Tarefa.class);

        List<Usuario> seedUsuarios = List.of(
                new Usuario(null, "Pedro", "pedro@example.com", "123456", LocalDateTime.now(), true, List.of(), List.of("USER")),
                new Usuario(null, "Maria", "maria@example.com", "123456", LocalDateTime.now(), true, List.of(), List.of("ADMIN")),
                new Usuario(null, "João", "joao@example.com", "123456", LocalDateTime.now(), true, List.of(), List.of("USER"))
        );

        mongoTemplate.insert(List.of(seedUsuarios.get(0), seedUsuarios.get(1), seedUsuarios.get(2)), Usuario.class);

    }

    @Test
    void deveRetornarTodosUsuários() throws Exception {
        mockMvc.perform(get("/usuarios")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.nome == 'Pedro')]").exists())
                .andExpect(jsonPath("$[?(@.email == 'maria@example.com')]").exists())
                .andExpect(jsonPath("$[?(@.nome == 'João')]").exists());
    }

    @Test
    void deveRetornarUmUsuárioPeloId() throws Exception {

        Usuario maria = new Usuario();
        maria.setNome("Maria");
        maria.setEmail("maria@example.com");
        maria.setSenha("123");
        Usuario salvo = usuarioRepository.save(maria);

        // 2. Chama o endpoint com o id real
        mockMvc.perform(get("/usuarios/" + salvo.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    void naoDeveRetornarUmUsuárioPeloIdQuandoIdNaoExiste() throws Exception {
        String idNaoExistente = "disandsad13";
        mockMvc.perform(get("/usuarios/" + idNaoExistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriarUmUsuarioComInformacoesValidas() throws Exception {

        Usuario novoUsuario = new Usuario(null, "Teste", "teste@gmail.com", "123456", LocalDateTime.now(), true, List.of(), List.of("USER"));
        String usuarioJson = objectMapper.writeValueAsString(novoUsuario);

        mockMvc.perform(post("/usuarios/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("teste@gmail.com"))
                .andExpect(jsonPath("$.nome").value("Teste"));
    }

    @Test
    void naoDeveCriarUmUsuarioQuandoONomeEhNulo() throws Exception {

        Usuario novoUsuario = new Usuario(null, null, "teste@gmail.com", "123456", LocalDateTime.now(), true, List.of(), List.of("USER"));
        String usuarioJson = objectMapper.writeValueAsString(novoUsuario);

        mockMvc.perform(post("/usuarios/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCriarUmUsuarioQuandoASenhaEhInvalida() throws Exception {

        Usuario novoUsuario = new Usuario(null, "Teste", "teste@gmail.com", "1234", LocalDateTime.now(), true, List.of(), List.of("USER"));
        String usuarioJson = objectMapper.writeValueAsString(novoUsuario);

        mockMvc.perform(post("/usuarios/criar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAtualizarUmUsuarioComInformacoesValidas() throws Exception {
        Usuario maria = new Usuario();
        maria.setNome("Maria");
        maria.setEmail("maria@example.com");
        maria.setSenha("123456");
        Usuario salvo = usuarioRepository.save(maria);

        salvo.setEmail("novoemail@gmail.com");

        String usuarioJson = objectMapper.writeValueAsString(salvo);

        mockMvc.perform(put("/usuarios/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("novoemail@gmail.com"));

    }

    @Test
    void naoDeveAtualizarUmUsuarioComUsuarioNulo() throws Exception {
        Usuario maria = new Usuario();

        String usuarioJson = objectMapper.writeValueAsString(maria);

        mockMvc.perform(put("/usuarios/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveExcluirUsuario() throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findByEmail("joao@example.com");

        if (usuario.isPresent()) {
            mockMvc.perform(delete("/usuarios/" + usuario.get().getId())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
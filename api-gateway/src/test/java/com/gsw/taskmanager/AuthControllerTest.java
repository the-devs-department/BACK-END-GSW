package com.gsw.taskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsw.taskmanager.entity.RevokedToken;
import com.gsw.taskmanager.entity.Usuario;
import com.gsw.taskmanager.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
public class AuthControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Usuario usuarioTeste;

    @BeforeEach
    void setup() {
        // verifica se o usuario de teste já existe
        if (!usuarioRepository.findByEmail("teste@integration.com").isPresent()) {
            // Cria usuário com senha criptografada
            usuarioTeste = new Usuario();
            usuarioTeste.setNome("Teste");
            usuarioTeste.setEmail("teste@integration.com");
            usuarioTeste.setSenha(passwordEncoder.encode("123456"));
            usuarioTeste.setAtivo(true);
            usuarioTeste.setRoles(List.of("ADMIN"));
            usuarioTeste.setDataCadastro(LocalDateTime.now());

            usuarioRepository.save(usuarioTeste);
        }
    }

    public String logarUsuarioMockado() throws Exception {
        Map<String, String> login = Map.of(
                "email", "teste@integration.com",
                "senha", "123456"
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    void deveLogarComCredenciaisValidas() throws Exception {
        Map<String, String> login = Map.of(
                "email", "teste@integration.com",
                "senha", "123456"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void naoDeveLogarComSenhaInvalida() throws Exception {
        Map<String, String> login = Map.of(
                "email", "teste@integration.com",
                "senha", "12456"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveLogarComEmailNaoCadastrado() throws Exception {
        Map<String, String> login = Map.of(
                "email", "teste@naoexiste.com",
                "senha", "124561"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAcessarUsuariosComTokenValido() throws Exception {
        String token = logarUsuarioMockado();

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveAutorizarAcessoComTokenInvalido() throws Exception {
        String token = logarUsuarioMockado();

        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + "TOKENINVALIDO")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveDeslogarUsuarioComTokenValido() throws Exception {
        String token = logarUsuarioMockado();

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        RevokedToken revokedToken = mongoTemplate.findOne(
                Query.query(Criteria.where("token").is(token)),
                RevokedToken.class
        );

        assertThat(revokedToken).isNotNull();
        assertThat(revokedToken.getToken()).isEqualTo(token);
        assertThat(revokedToken.getExpiryDate()).isAfter(LocalDateTime.now());
    }

}

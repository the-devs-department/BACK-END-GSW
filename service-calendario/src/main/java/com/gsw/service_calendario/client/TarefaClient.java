package com.gsw.service_calendario.client;

import com.gsw.service_calendario.dto.TarefaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TarefaClient {

    private static final String BASE_URL = "http://localhost:8081/tarefas";
    private final RestTemplate rest = new RestTemplate();

    public ResponseEntity<List<TarefaDTO>> listarPorResponsavel(String responsavel) {
        String q = "?responsavel=" + URLEncoder.encode(responsavel, StandardCharsets.UTF_8);
        ResponseEntity<TarefaDTO[]> resp = rest.getForEntity(URI.create(BASE_URL + q), TarefaDTO[].class);
        TarefaDTO[] body = resp.getBody();
        List<TarefaDTO> list = body != null ? Arrays.asList(body) : new ArrayList<>();
        return ResponseEntity.status(resp.getStatusCode()).body(list);
    }
}

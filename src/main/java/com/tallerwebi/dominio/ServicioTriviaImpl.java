package com.tallerwebi.dominio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServicioTriviaImpl implements ServicioTrivia {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public RespuestaTrivia buscarPreguntas(int amount, int category, String difficulty) {
        String url = String.format(
                "https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s&type=multiple",
                amount, category, difficulty
        );
        try {
            String json = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, RespuestaTrivia.class);
        } catch (Exception e) {
            return null;
        }
    }
}

package com.tallerwebi.dominio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ServicioTriviaImpl implements ServicioTrivia {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ServicioDificultad servicioDificultad;

    @Autowired
    public ServicioTriviaImpl(ServicioDificultad servicioDificultad) {
        this.servicioDificultad = servicioDificultad;
    }

    @Override
    public RespuestaTrivia buscarPreguntas(int amount, int category, String difficulty) {
        System.out.println("test" + difficulty);
        if ("multi".equals(difficulty)) {
            return buscarPreguntasMulti(amount, category);
        }

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

    private RespuestaTrivia buscarPreguntasMulti(int amount, int category) {
        List<PreguntaTrivia> todasLasPreguntas = new ArrayList<>();

        List<Dificultad> dificultades = servicioDificultad.obtenerTodas();

        for (Dificultad diff : dificultades) {
            String url = String.format(
                    "https://opentdb.com/api.php?amount=%d&category=%d&difficulty=%s&type=multiple",
                    amount, category, diff.getNombre().toLowerCase()
            );

            try {
                String json = restTemplate.getForObject(url, String.class);
                ObjectMapper mapper = new ObjectMapper();
                RespuestaTrivia respuesta = mapper.readValue(json, RespuestaTrivia.class);
                if (respuesta != null && respuesta.getResults() != null) {
                    todasLasPreguntas.addAll(respuesta.getResults());
                }
                Thread.sleep(5000); // "too many requests" :(
            } catch (Exception e) {
                System.out.println("Error:" + e.getMessage());
            }
        }

        Collections.shuffle(todasLasPreguntas);

        RespuestaTrivia respuestaFinal = new RespuestaTrivia();
        respuestaFinal.setResults(todasLasPreguntas);
        respuestaFinal.setResponse_code(0);
        return respuestaFinal;
    }

    @Override
    public RespuestaCategorias obtenerCategorias() {
        String url = "https://opentdb.com/api_category.php";
        try {
            String json = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, RespuestaCategorias.class);
        } catch (Exception e) {
            return null;
        }
    }
}

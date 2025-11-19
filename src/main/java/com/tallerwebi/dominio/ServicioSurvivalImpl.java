package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioSurvivalImpl implements ServicioSurvival {

    private final ServicioTrivia servicioTrivia;
    private final ServicioDificultad servicioDificultad;
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioSurvivalImpl(ServicioTrivia servicioTrivia, ServicioDificultad servicioDificultad, RepositorioUsuario repositorioUsuario) {
        this.servicioTrivia = servicioTrivia;
        this.servicioDificultad = servicioDificultad;
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public List<Preguntas> obtenerPreguntasSurvival(String dificultad, int cantidad) {
        // Category 9 = General Knowledge
        RespuestaTrivia respuestaTrivia = servicioTrivia.buscarPreguntas(cantidad, 9, dificultad);
        if (respuestaTrivia == null || respuestaTrivia.getResults() == null) {
            return new ArrayList<>();
        }
        return mapearPreguntasSurvival(respuestaTrivia);
    }

    @Override
    public String obtenerDificultadSurvival(int respuestasCorrectas) {
        // 0-4 correctas: easy, 5-9: medium, 10+: hard
        if (respuestasCorrectas < 5) return "easy";
        if (respuestasCorrectas < 10) return "medium";
        return "hard";
    }

    @Override
    public void asignarMonedas(Usuario jugador, Integer puntaje) {
        if (jugador.getMonedas() == null) jugador.setMonedas(0L);

        Long monedasGanadas = (long) (puntaje * 0.1);
        jugador.setMonedas(jugador.getMonedas() + monedasGanadas);
        repositorioUsuario.modificar(jugador);
    }

    @Override
    public int calcularMultiplicadorSurvival(String dificultad) {
        if (dificultad == null) return 1;
        String nombre = dificultad.trim().toLowerCase();
        switch (nombre) {
            case "easy":
                return 1;
            case "medium":
                return 2;
            case "hard":
                return 3;
            default:
                return 1;
        }
    }

    @Override
    public Usuario actualizarPuntajeYMonedas(Long idUsuario, Integer puntosGanados) {
        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        if (usuario == null) {
            return null;
        }
        
        // Acumular puntaje
        Long puntajeAnterior = usuario.getPuntaje() != null ? usuario.getPuntaje() : 0L;
        usuario.setPuntaje(puntajeAnterior + puntosGanados);
        
        // Calcular y acumular monedas
        Long monedasGanadas = (long) Math.floor(puntosGanados * 0.1);
        Long monedasAnteriores = usuario.getMonedas() != null ? usuario.getMonedas() : 0L;
        usuario.setMonedas(monedasAnteriores + monedasGanadas);
        
        // Persistir en la base de datos
        repositorioUsuario.modificar(usuario);
        
        return usuario;
    }
    
    @Override
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return repositorioUsuario.buscarPorId(idUsuario);
    }

    private List<Preguntas> mapearPreguntasSurvival(RespuestaTrivia respuestaTrivia) {
        return respuestaTrivia.getResults().stream()
                .map(this::mapearPreguntaSurvival)
                .collect(Collectors.toList());
    }

    private Preguntas mapearPreguntaSurvival(PreguntaTrivia preguntaTrivia) {
        Preguntas p = new Preguntas();
        p.setCategoria(decodeHtmlEntities(preguntaTrivia.getCategory()));
        p.setDificultad(obtenerDificultadEntidadSurvival(preguntaTrivia.getDifficulty()));
        p.setEnunciado(decodeHtmlEntities(preguntaTrivia.getQuestion()));
        p.setRespuestaCorrecta(decodeHtmlEntities(preguntaTrivia.getCorrect_answer()));

        List<String> incorrects = preguntaTrivia.getIncorrect_answers();
        if (incorrects != null) {
            if (incorrects.size() > 0) p.setRespuestaIncorrecta1(decodeHtmlEntities(incorrects.get(0)));
            if (incorrects.size() > 1) p.setRespuestaIncorrecta2(decodeHtmlEntities(incorrects.get(1)));
            if (incorrects.size() > 2) p.setRespuestaIncorrecta3(decodeHtmlEntities(incorrects.get(2)));
        }
        return p;
    }

    private String decodeHtmlEntities(String text) {
        if (text == null) {
            return null;
        }
        return HtmlUtils.htmlUnescape(text);
    }

    private Dificultad obtenerDificultadEntidadSurvival(String nombre) {
        if (nombre == null) return null;
        String normalizado = nombre.trim();
        if (normalizado.equalsIgnoreCase("easy")) normalizado = "Easy";
        else if (normalizado.equalsIgnoreCase("medium")) normalizado = "Medium";
        else if (normalizado.equalsIgnoreCase("hard")) normalizado = "Hard";
        return servicioDificultad.obtenerPorNombre(normalizado);
    }
}


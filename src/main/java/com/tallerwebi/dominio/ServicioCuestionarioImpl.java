package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("servicioCuestionario")
@Transactional
public class ServicioCuestionarioImpl implements ServicioCuestionario {

    private final RepositorioCuestionario repositorioCuestionario;

    @Autowired
    private ServicioTrivia servicioTrivia;

    @Autowired
    private ServicioDificultad servicioDificultad;

    @Autowired
    public ServicioCuestionarioImpl(RepositorioCuestionario repositorioCuestionario) {
        this.repositorioCuestionario = repositorioCuestionario;
    }

    @Override
    public List<Cuestionario> buscarPorDificultad(String dificultad) {
        return repositorioCuestionario.buscarPorDificultad(dificultad);
    }

    @Override
    public List<Cuestionario> buscarPorCategoria(String categoria) {
        return repositorioCuestionario.buscarPorCategoria(categoria);
    }

    @Override
    public void guardar(Cuestionario cuestionario) {
        repositorioCuestionario.guardar(cuestionario);
    }

    @Override
    public Cuestionario buscar(Long idCuestionario) {
        return repositorioCuestionario.buscar(idCuestionario);
    }

    @Override
    public void modificar(Cuestionario cuestionario) {
        repositorioCuestionario.modificar(cuestionario);
    }

    @Override
    public List<Cuestionario> buscarTodo() {
        return repositorioCuestionario.buscarTodo();
    }

    @Override
    public void crearCuestionario(String nombre, String descripcion, int cantidadPreguntas, int categoria, String dificultad) {
        RespuestaTrivia respuestaTrivia = servicioTrivia.buscarPreguntas(cantidadPreguntas, categoria, dificultad);
        List<Preguntas> preguntas = mapearPreguntas(respuestaTrivia);

        Cuestionario nuevoCuestionario = new Cuestionario();
        nuevoCuestionario.setNombre(nombre);
        nuevoCuestionario.setDescripcion(descripcion);
        if (!preguntas.isEmpty()) {
            nuevoCuestionario.setCategoria(preguntas.get(0).getCategoria());
        }
        nuevoCuestionario.setDificultad(obtenerDificultadEntidad(dificultad));
        for (Preguntas p : preguntas) {
            p.setCuestionario(nuevoCuestionario);
        }
        nuevoCuestionario.setPreguntas(preguntas);

        repositorioCuestionario.guardar(nuevoCuestionario);
    }

    private List<Preguntas> mapearPreguntas(RespuestaTrivia respuestaTrivia) {
        return respuestaTrivia.getResults().stream()
                .map(this::mapearIncorrectas)
                .collect(Collectors.toList());
    }

    private Preguntas mapearIncorrectas(PreguntaTrivia pregunta) {
        Preguntas p = new Preguntas();
        p.setCategoria(decodeHtmlEntities(pregunta.getCategory()));
        p.setDificultad(obtenerDificultadEntidad(pregunta.getDifficulty()));
        p.setEnunciado(decodeHtmlEntities(pregunta.getQuestion()));
        p.setRespuestaCorrecta(decodeHtmlEntities(pregunta.getCorrect_answer()));

        List<String> incorrects = pregunta.getIncorrect_answers();
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

    private Dificultad obtenerDificultadEntidad(String nombre) {
        if (nombre == null) return null;
        String normalizado = nombre.trim();
        if (normalizado.equalsIgnoreCase("easy")) normalizado = "Easy";
        else if (normalizado.equalsIgnoreCase("medium")) normalizado = "Medium";
        else if (normalizado.equalsIgnoreCase("hard")) normalizado = "Hard";
        else if (normalizado.equalsIgnoreCase("multi")) normalizado = "Multi";
        return servicioDificultad.obtenerPorNombre(normalizado);
    }

    @Override
    public void asignarVidasSegunDificultad(Cuestionario cuestionario) {
        if (cuestionario.getDificultad() == null) {
            cuestionario.setVidas(5);
            return;
        }

        switch (cuestionario.getDificultad().getNombre().toLowerCase()) {
            case "easy":
                cuestionario.setVidas(5);
                break;
            case "medium":
                cuestionario.setVidas(4);
                break;
            case "hard":
                cuestionario.setVidas(3);
                break;
            case "multi":
                cuestionario.setVidas(4);
                break;
            default:
            cuestionario.setVidas(5);
            break;
        }
    }

    @Override
    public List<Cuestionario> filtrarPorDificultadYCategoria(String dificultad, String categoria) {
        return repositorioCuestionario.filtrarPorDificultadYCategoria(dificultad, categoria);
    }

    @Override
    public List<String> obtenerTodasLasCategorias() {
        return repositorioCuestionario.obtenerTodasLasCategorias();
    }

    @Override
    public List<Cuestionario> obtenerCuestionariosSugeridos(int limite) {
        List<Cuestionario> todosCuestionarios = repositorioCuestionario.buscarTodo();
        Collections.shuffle(todosCuestionarios);
        return todosCuestionarios.stream()
                .limit(limite)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cuestionario> obtenerCuestionariosFiltrados(HttpServletRequest request, String dificultad, String categoria) {
        boolean showFiltered = request.getParameterMap().containsKey("dificultad") || request.getParameterMap().containsKey("categoria");
        if (showFiltered) {
            String dificultadFilter = (dificultad != null && dificultad.isEmpty()) ? null : dificultad;
            String categoriaFilter = (categoria != null && categoria.isEmpty()) ? null : categoria;

            if (dificultadFilter == null && categoriaFilter == null) {
                return repositorioCuestionario.buscarTodo();
            } else {
                return repositorioCuestionario.filtrarPorDificultadYCategoria(dificultadFilter, categoriaFilter);
            }
        } else return null;
    }
}

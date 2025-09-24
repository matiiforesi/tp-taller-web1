package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service ("servicioCuestionario")
@Transactional
public class ServicioCuestionario {

    private final RepositorioCuestionario repositorioCuestionario;

    @Autowired
    private ServicioTrivia servicioTrivia;

    @Autowired
    public ServicioCuestionario(RepositorioCuestionario repositorioCuestionario) {
        this.repositorioCuestionario = repositorioCuestionario;
    }

    public Cuestionario buscarPorDificultad(String dificultad){
        return repositorioCuestionario.buscarPorDificultad(dificultad);
    }

    public Cuestionario buscarPorCategoria(String categoria){
        return repositorioCuestionario.buscarPorCategoria(categoria);
    }

    public void guardar(Cuestionario cuestionario){
        repositorioCuestionario.guardar(cuestionario);
    }

    public Cuestionario buscar(Long idCuestionario){

        return repositorioCuestionario.buscar(idCuestionario);

    }
    public void modificar(Cuestionario cuestionario){
        repositorioCuestionario.modificar(cuestionario);
    }

    public List<Cuestionario> buscarTodo(){
        return repositorioCuestionario.buscarTodo();
    }

    public void crearCuestionario(String nombre, String descripcion, int cantidadPreguntas, int categoria, String dificultad) {
        RespuestaTrivia respuestaTrivia = servicioTrivia.buscarPreguntas(cantidadPreguntas, categoria, dificultad);
        List<Preguntas> preguntas = mapearPreguntas(respuestaTrivia);

        Cuestionario nuevoCuestionario = new Cuestionario();
        nuevoCuestionario.setNombre(nombre);
        nuevoCuestionario.setDescripcion(descripcion);
        nuevoCuestionario.setCategoria(String.valueOf(categoria));
        nuevoCuestionario.setDificultad(dificultad);
        for (Preguntas p : preguntas) {
            p.setCuestionario(nuevoCuestionario);
        }
        nuevoCuestionario.setPreguntas(preguntas);

        repositorioCuestionario.guardar(nuevoCuestionario);
    }

    private List<Preguntas> mapearPreguntas (RespuestaTrivia respuestaTrivia){
        return respuestaTrivia.getResults().stream()
                .map(this::mapearIncorrectas)
                .collect(Collectors.toList());
    }

    private Preguntas mapearIncorrectas (PreguntaTrivia pregunta) {
        Preguntas p = new Preguntas();
        p.setCategoria(pregunta.getCategory());
        p.setDificultad(pregunta.getDifficulty());
        p.setEnunciado(pregunta.getQuestion());
        p.setRespuestaCorrecta(pregunta.getCorrect_answer());

        List<String> incorrects = pregunta.getIncorrect_answers();
        if (incorrects.size() > 0) p.setRespuestaIncorrecta1(incorrects.get(0));
        if (incorrects.size() > 1) p.setRespuestaIncorrecta2(incorrects.get(1));
        if (incorrects.size() > 2) p.setRespuestaIncorrecta3(incorrects.get(2));

        return p;
    }
}

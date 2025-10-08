package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioJuegoImpl implements ServicioJuego {

    private ServicioCuestionario servicioCuestionario;
    private ServicioPregunta servicioPregunta;
    private Integer puntajeTotal=0;

    public ServicioJuegoImpl(ServicioCuestionario servicioCuestionario, ServicioPregunta servicioPregunta) {
        this.servicioCuestionario = servicioCuestionario;
        this.servicioPregunta = servicioPregunta;
    }

    @Override
    public Boolean validarRespuesta(String respuesta,Long id) {
       // Preguntas pregunta= servicioCuestionario.buscar(id).getPreguntas().get(0);
        Preguntas pregunta= servicioPregunta.obtenerPorId(id);
        if(pregunta.getRespuestaCorrecta().equals(respuesta.trim())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Integer obtenerPuntaje(Long idPregunta,String respuesta) {

        Integer puntaje=0;

        if(validarRespuesta(respuesta,idPregunta)){
            puntaje=200;
        }
        this.puntajeTotal+=puntaje;

        return this.puntajeTotal;
    }

    @Override
    public Preguntas obtenerPregunta(Cuestionario cuestionario,Integer indicePregunta) {
        return cuestionario.getPreguntas().get(indicePregunta);
    }

    @Override
    public Cuestionario obtenerCuestionario (Long id) {
        return servicioCuestionario.buscar(id);
        //return servicioCuestionario.buscarTodo().get(0);
    }
    @Override
    public void reiniciarPuntaje() {
        this.puntajeTotal=0;
    }

}

package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioJuegoImpl implements ServicioJuego {

    private ServicioCuestionario servicioCuestionario;

    public ServicioJuegoImpl(ServicioCuestionario servicioCuestionario) {
        this.servicioCuestionario = servicioCuestionario;
    }

    @Override
    public Boolean validarRespuesta(String respuesta,Long id) {
        Preguntas pregunta= servicioCuestionario.buscar(id).getPreguntas().get(0);
        if(pregunta.getRespuestaCorrecta().equals(respuesta.trim())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Integer obtenerPuntaje(Long idPregunta, Long idJugador,String respuesta) {

        Integer puntaje=0;

        if(validarRespuesta(respuesta,idPregunta)){
            puntaje+=200;
        }

        return puntaje;
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


}

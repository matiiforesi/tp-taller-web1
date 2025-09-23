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
}

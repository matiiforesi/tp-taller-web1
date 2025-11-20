package com.tallerwebi.dominio;

public interface ServicioPregunta {

    Preguntas obtenerPorId(Long id);

    void guardarPregunta(Preguntas pregunta);
}

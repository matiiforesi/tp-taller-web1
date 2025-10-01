package com.tallerwebi.dominio;

public interface ServicioJuego {

    Boolean validarRespuesta(String respuesta, Long id);
    Integer acumularPuntaje(Long idPregunta,Long idJugador, String respuesta);
    Preguntas obtenerPregunta(Cuestionario cuestionario,Integer indicePregunta);
    Cuestionario obtenerCuestionario (Long id);
}

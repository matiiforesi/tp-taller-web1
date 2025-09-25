package com.tallerwebi.dominio;

public interface ServicioJuego {

    Boolean validarRespuesta(String respuesta, Long id);
    Integer obtenerPuntaje(Long idPregunta,Long idJugador, String respuesta);
}

package com.tallerwebi.dominio;

public interface ServicioJuego {

    Boolean validarRespuesta(String respuesta, Long id);
    Integer acumularPuntaje(Long idPregunta,Long idJugador, String respuesta);
}

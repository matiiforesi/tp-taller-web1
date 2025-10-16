package com.tallerwebi.dominio;

public interface ServicioJuego {
    Boolean validarRespuesta(String respuesta, Long id);

    Integer obtenerPuntaje(Long idPregunta, String respuesta);

    Preguntas obtenerPregunta(Cuestionario cuestionario, Integer indicePregunta);

    Cuestionario obtenerCuestionario(Long id);

    void actualizarPuntajeYCrearHistorial(Usuario jugador, Cuestionario cuestionario, int preguntasCorrectas, int preguntasIncorrectas);

    void reiniciarPuntaje();
}

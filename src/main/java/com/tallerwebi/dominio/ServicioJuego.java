package com.tallerwebi.dominio;

public interface ServicioJuego {
    void inicializarVidas(Cuestionario cuestionario);

    Boolean validarRespuesta(String respuesta, Long id);

    Integer obtenerPuntaje(Long idPregunta, String respuesta, TimerPregunta timerPregunta);

    Preguntas obtenerPregunta(Cuestionario cuestionario, Integer indicePregunta);

    Cuestionario obtenerCuestionario(Long id);

    void actualizarPuntajeYCrearHistorial(Usuario jugador, Cuestionario cuestionario, int preguntasCorrectas, int preguntasIncorrectas, Integer puntajePenalizado);

    void reiniciarPuntaje();

    Integer registrarIntento(Long idUsuario, Long idCuestionario, Integer puntajePartida);

    void setPuntajeTotal(Integer puntajeTotal);

    Integer calcularPenalizacion(Long idUsuario, Long idCuestionario, Integer puntajePartida);

//    Integer obtenerIntentosPrevios(Long idUsuario, Long idCuestionario);

}

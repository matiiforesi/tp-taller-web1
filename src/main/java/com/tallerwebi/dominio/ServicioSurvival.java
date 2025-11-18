package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioSurvival {
    
    List<Preguntas> obtenerPreguntasSurvival(String dificultad, int cantidad);
    
    String obtenerDificultadSurvival(int respuestasCorrectas);
    
    void asignarMonedas(Usuario jugador, Integer puntaje);
    
    int calcularMultiplicadorSurvival(String dificultad);
    
    Usuario actualizarPuntajeYMonedas(Long idUsuario, Integer puntosGanados);
    
    Usuario obtenerUsuarioPorId(Long idUsuario);
}


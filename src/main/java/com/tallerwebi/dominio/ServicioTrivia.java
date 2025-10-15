package com.tallerwebi.dominio;

public interface ServicioTrivia {

    RespuestaTrivia buscarPreguntas(int amount, int category, String difficulty);
}

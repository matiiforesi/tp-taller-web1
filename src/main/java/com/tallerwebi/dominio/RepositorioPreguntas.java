package com.tallerwebi.dominio;

public interface RepositorioPreguntas {

    Preguntas buscarPregunta(Long idPregunta);

    void guardar(Preguntas pregunta);

    void modificar(Preguntas pregunta);

    Preguntas buscarPorCategoria(String categoria);
}

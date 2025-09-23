package com.tallerwebi.dominio;

public interface RepositorioCuestionario {

    void guardar(Cuestionario cuestionario);
    Cuestionario buscar(Long id);
    void modificar(Cuestionario cuestionario);
    Cuestionario buscarPorCategoria(String categoria);
    Cuestionario buscarPorDificultad(String dificultad);
}

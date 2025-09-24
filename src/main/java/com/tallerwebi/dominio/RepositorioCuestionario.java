package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCuestionario {

    void guardar(Cuestionario cuestionario);
    Cuestionario buscar(Long id);
    void modificar(Cuestionario cuestionario);
    Cuestionario buscarPorCategoria(String categoria);
    Cuestionario buscarPorDificultad(String dificultad);
    List<Cuestionario> buscarTodo();
}

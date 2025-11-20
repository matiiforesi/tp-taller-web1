package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCuestionario {

    void guardar(Cuestionario cuestionario);

    Cuestionario buscar(Long id);

    void modificar(Cuestionario cuestionario);

    List<Cuestionario> buscarPorCategoria(String categoria);

    List<Cuestionario> buscarPorDificultad(String dificultad);

    List<Cuestionario> buscarTodo();

    Integer contarCuestionarios();

    List<Cuestionario> filtrarPorDificultadYCategoria(String dificultad, String categoria);

    List<String> obtenerTodasLasCategorias();
}

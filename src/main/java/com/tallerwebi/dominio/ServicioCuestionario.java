package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioCuestionario {

    List<Cuestionario> buscarPorDificultad(String dificultad);

    List<Cuestionario> buscarPorCategoria(String categoria);

    void guardar(Cuestionario cuestionario);

    Cuestionario buscar(Long idCuestionario);

    void modificar(Cuestionario cuestionario);

    List<Cuestionario> buscarTodo();

    void crearCuestionario(String nombre, String descripcion, int cantidadPreguntas, int categoria, String dificultad);
}

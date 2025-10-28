package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioDificultad {

	Dificultad obtenerPorNombre(String nombre);

	Dificultad obtenerPorId(Long id);

	void guardar(Dificultad dificultad);

	void modificar(Dificultad dificultad);

    int calcularMultiplicador(Dificultad dificultad);
    
    List<Dificultad> obtenerTodas();
}



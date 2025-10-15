package com.tallerwebi.dominio;

public interface ServicioDificultad {

	Dificultad obtenerPorNombre(String nombre);

	Dificultad obtenerPorId(Long id);

	void guardar(Dificultad dificultad);

	void modificar(Dificultad dificultad);
}



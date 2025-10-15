package com.tallerwebi.dominio;

public interface RepositorioDificultad {

	Dificultad buscarPorNombre(String nombre);

	Dificultad buscarPorId(Long id);

	void guardar(Dificultad dificultad);

	void modificar(Dificultad dificultad);
}



package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioDificultad {

	Dificultad buscarPorNombre(String nombre);

	Dificultad buscarPorId(Long id);

	void guardar(Dificultad dificultad);

	void modificar(Dificultad dificultad);
	
	List<Dificultad> buscarTodas();
}



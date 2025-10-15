package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioDificultadImpl implements ServicioDificultad {

	private RepositorioDificultad repositorioDificultad;

	@Autowired
	public ServicioDificultadImpl(RepositorioDificultad repositorioDificultad) {
		this.repositorioDificultad = repositorioDificultad;
	}

	@Override
	public Dificultad obtenerPorNombre(String nombre) {
		return repositorioDificultad.buscarPorNombre(nombre);
	}

	@Override
	public Dificultad obtenerPorId(Long id) {
		return repositorioDificultad.buscarPorId(id);
	}

    @Override
    public void guardar(Dificultad dificultad) {
        repositorioDificultad.guardar(dificultad);
    }

    @Override
    public void modificar(Dificultad dificultad) {
        repositorioDificultad.modificar(dificultad);
    }
}



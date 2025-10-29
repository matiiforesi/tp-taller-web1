package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    @Override
    public int calcularMultiplicador(Dificultad dificultad) {
        if (dificultad == null) {return 1;}

        Integer mult = dificultad.getMultiplicadorDificultad();
        if (mult != null && mult > 0) {return mult;}

        String nombre = dificultad.getNombre();
        if (nombre == null) {return 1;}

        switch (nombre.trim().toLowerCase()) {
            // case "facil":
            case "easy":
                return 1;
            // case "medio":
            case "medium":
                return 2;
            // case "dificil":
            case "hard":
                return 3;
            default:
                return 1;
        }
    }

    @Override
    public List<Dificultad> obtenerTodas() {
        return repositorioDificultad.buscarTodas();
    }
}

package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ServicioRankingImpl implements ServicioRanking {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioHistorial repositorioHistorial;

    @Autowired
    public ServicioRankingImpl (RepositorioUsuario repositorioUsuario, RepositorioHistorial repositorioHistorial) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioHistorial = repositorioHistorial;
    }

    @Override
    public List<Usuario> rankingGeneral() {
        return repositorioHistorial.obtenerRankingGeneral();
    }

    @Override
    public List<HistorialCuestionario> rankingCuestionario(String nombreCuestionario) {
        return repositorioHistorial.obtenerRankingCuestionario(nombreCuestionario);
    }
}

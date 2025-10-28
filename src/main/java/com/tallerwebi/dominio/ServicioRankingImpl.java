package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.RepositorioHistorialImpl;
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
    public ServicioRankingImpl(RepositorioUsuario repositorioUsuario, RepositorioHistorial repositorioHistorial) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioHistorial = repositorioHistorial;
    }

    @Override
    public List<Usuario> obtenerRankingGeneral() {
        return repositorioHistorial.buscarRankingGeneral();
    }

    // individual por intento, solo vista perfil?
    @Override
    public List<HistorialCuestionario> obtenerRankingCuestionarioPorNombre(String nombreCuestionario) {
        return repositorioHistorial.buscarRankingCuestionarioPorNombre(nombreCuestionario);
    }

    // individual por intento, solo vista perfil?
    @Override
    public List<HistorialCuestionario> obtenerRankingCuestionarioPorId(Long id) {
        return repositorioHistorial.buscarRankingCuestionarioPorId(id);
    }

    @Override
    public List<Object[]> obtenerRankingCuestionarioAgregadoPorId(Long idCuestionario) {
        return ((RepositorioHistorialImpl) repositorioHistorial).buscarRankingCuestionarioAgregadoPorId(idCuestionario);
    }

    @Override
    public List<Object[]> obtenerRankingCuestionarioAgregadoPorNombre(String nombreCuestionario) {
        return repositorioHistorial.buscarRankingCuestionarioAgregadoPorNombre(nombreCuestionario);
    }
}

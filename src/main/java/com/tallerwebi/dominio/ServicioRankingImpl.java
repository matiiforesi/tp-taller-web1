package com.tallerwebi.dominio;

import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;
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
    public List<RankingGeneralDTO> obtenerRankingGeneral() {
        return repositorioHistorial.buscarRankingGeneral();
    }

    @Override
    public List<RankingCuestionarioDTO> obtenerRankingCuestionarioPorId(Long idCuestionario) {
        return repositorioHistorial.buscarRankingCuestionarioPorId(idCuestionario);
    }

    @Override
    public List<RankingCuestionarioDTO> obtenerRankingCuestionarioPorNombre(String nombreCuestionario) {
        return repositorioHistorial.buscarRankingCuestionarioPorNombre(nombreCuestionario);
    }
}

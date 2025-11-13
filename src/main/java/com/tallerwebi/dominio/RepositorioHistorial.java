package com.tallerwebi.dominio;

import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;

import java.util.List;

public interface RepositorioHistorial {

    HistorialCuestionario buscar(Long id);

    void guardar(HistorialCuestionario historialCuestionario);

    void modificar(HistorialCuestionario historialCuestionario);

    List<RankingGeneralDTO> buscarRankingGeneral();

    List<RankingCuestionarioDTO> buscarRankingCuestionarioPorId(Long idCuestionario);

    List<RankingCuestionarioDTO> buscarRankingCuestionarioPorNombre(String nombreCuestionario);
}

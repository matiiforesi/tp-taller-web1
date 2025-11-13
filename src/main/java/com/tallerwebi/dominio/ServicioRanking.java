package com.tallerwebi.dominio;

import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;

import java.util.List;

public interface ServicioRanking {

    List<RankingGeneralDTO> obtenerRankingGeneral();

    List<RankingCuestionarioDTO> obtenerRankingCuestionarioPorId(Long idCuestionario);

    List<RankingCuestionarioDTO> obtenerRankingCuestionarioPorNombre(String nombreCuestionario);
}

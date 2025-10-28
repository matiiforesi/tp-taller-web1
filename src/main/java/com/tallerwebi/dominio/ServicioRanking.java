package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioRanking {

    List<Usuario> obtenerRankingGeneral();

    List<HistorialCuestionario> obtenerRankingCuestionarioPorNombre(String nombreCuestionario);

    List<HistorialCuestionario> obtenerRankingCuestionarioPorId(Long idCuestionario);

    List<Object[]> obtenerRankingCuestionarioAgregadoPorId(Long idCuestionario);

    List<Object[]> obtenerRankingCuestionarioAgregadoPorNombre(String nombreCuestionario);
}

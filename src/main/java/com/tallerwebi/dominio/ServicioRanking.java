package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioRanking {

    List<Object[]> obtenerRankingGeneral();

//    List<HistorialCuestionario> obtenerRankingCuestionarioPorNombre(String nombreCuestionario);

//    List<HistorialCuestionario> obtenerRankingCuestionarioPorId(Long idCuestionario);

    List<Object[]> obtenerRankingCuestionarioPorId(Long idCuestionario);

    List<Object[]> obtenerRankingCuestionarioPorNombre(String nombreCuestionario);
}

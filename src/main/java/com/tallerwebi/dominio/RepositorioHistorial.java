package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioHistorial {

    HistorialCuestionario buscar(Long id);

    void guardar(HistorialCuestionario historialCuestionario);

    void modificar(HistorialCuestionario historialCuestionario);

    List<Object[]> buscarRankingGeneral();

//    List<HistorialCuestionario> buscarRankingCuestionarioPorNombre(String nombreCuestionario);

//    List<HistorialCuestionario> buscarRankingCuestionarioPorId(Long idCuestionario);

    List<Object[]> buscarRankingCuestionarioPorId(Long idCuestionario);

    List<Object[]> buscarRankingCuestionarioPorNombre(String nombreCuestionario);
}

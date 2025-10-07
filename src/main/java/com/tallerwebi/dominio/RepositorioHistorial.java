package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioHistorial {

    HistorialCuestionario buscar(Long id);

    void guardar(HistorialCuestionario historialCuestionario);

    void modificar(HistorialCuestionario historialCuestionario);

    List<Usuario> buscarRankingGeneral();

    List<HistorialCuestionario> buscarRankingCuestionarioPorNombre(String nombreCuestionario);

    List<HistorialCuestionario> buscarRankingCuestionarioPorId(Long idCuestionario);

}

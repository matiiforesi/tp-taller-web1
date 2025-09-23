package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioHistorial {

    List<Usuario> obtenerRankingGeneral();

    List<HistorialCuestionario> obtenerRankingCuestionario(String nombreCuestionario);
}

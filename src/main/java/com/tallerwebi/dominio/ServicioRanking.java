    package com.tallerwebi.dominio;

    import java.util.List;

    public interface ServicioRanking {

        List<Usuario> rankingGeneral();

        List<HistorialCuestionario> rankingCuestionario(String nombreCuestionario);
    }

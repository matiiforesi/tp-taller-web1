package com.tallerwebi.dominio;

public interface RepositorioIntento {
    IntentoCuestionario buscarPorUsuarioYCuestionario(Long idUsuario,Long idCuestionario);
    void guardar(IntentoCuestionario intentoCuestionario);
    void actualizar(IntentoCuestionario intentoCuestionario);
    Integer contarIntentos(Long idUsuario,Long idCuestionario);
}

package com.tallerwebi.dominio;

public interface RepositorioConfigJuego {
    String getValor(String clave, String valorPorDefecto);
    Integer getInt(String clave, Integer valorPorDefecto);
    void save(ConfiguracionJuego configuracionJuego);
}

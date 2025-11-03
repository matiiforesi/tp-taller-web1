package com.tallerwebi.dominio;

public interface ServicioConfigJuego {
    String getValor(String clave, String valorPorDefecto);
    Integer getInt(String clave, Integer valorPorDefecto);
    void save(ConfiguracionJuego configuracionJuego);
}

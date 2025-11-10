package com.tallerwebi.dominio;

public interface RepositorioItem {
    void guardar(Item item);
    void actualizar(Item item);
    Item obtenerPorId(Long id);
}

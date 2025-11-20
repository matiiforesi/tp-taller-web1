package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioItem {
    void guardar(Item item);

    void actualizar(Item item);

    Item obtenerPorId(Long id);

    List<Item> obtenerTodos();
}

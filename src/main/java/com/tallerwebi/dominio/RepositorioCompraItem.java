package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCompraItem {
    void guardar(CompraItem compra);
    List<CompraItem> obtenerComprasPorUsuario(Long idUsuario);
    void modificar(CompraItem compra);
    Long contarComprasPorUsuarioYTipo(Long idUsuario,TIPO_ITEMS tipo);
}

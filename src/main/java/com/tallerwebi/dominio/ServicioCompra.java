package com.tallerwebi.dominio;

public interface ServicioCompra {
    Boolean comprarItem(Long idUsuario, Long idItem);
    Usuario obtenerUsuarioActualizado(Long idUsuario);
    Long contarComprasPorUsuarioYTipo(Long idUsuario,TIPO_ITEMS tipo);
}

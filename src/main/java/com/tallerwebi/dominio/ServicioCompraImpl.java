package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioCompraImpl implements ServicioCompra {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioItem repositorioItem;
    private final RepositorioCompraItem repositorioCompraItem;

    public ServicioCompraImpl(RepositorioUsuario repositorioUsuario, RepositorioItem repositorioItem,RepositorioCompraItem repositorioCompraItem) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioItem = repositorioItem;
        this.repositorioCompraItem = repositorioCompraItem;
    }

    @Override
    public Boolean comprarItem(Long idUsuario, Long idItem) {
        Usuario usuario= this.repositorioUsuario.buscarPorId(idUsuario);
        System.out.println("puntaje inicial= " + usuario.getPuntaje());
        Item item= this.repositorioItem.obtenerPorId(idItem);

        if(usuario.getMonedas()>=item.getPrecio()) {
            usuario.setMonedas(usuario.getMonedas()-item.getPrecio());
            System.out.println("puntaje= " + usuario.getPuntaje());
            repositorioUsuario.modificar(usuario);

            CompraItem compra = new CompraItem();
            compra.setUsuario(usuario);
            compra.setItem(item);
            compra.setUsado(false);
            this.repositorioCompraItem.guardar(compra);

            return true;
        }
        return false;
    }

    @Override
    public Usuario obtenerUsuarioActualizado(Long idUsuario) {
        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        System.out.println("Usuario actualizado:");
        System.out.println("Monedas: " + usuario.getMonedas());
        System.out.println("Puntaje: " + usuario.getPuntaje());
        return usuario;

    }
}

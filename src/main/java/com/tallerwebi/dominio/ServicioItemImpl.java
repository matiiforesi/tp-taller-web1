package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicioItemImpl implements ServicioItem {

    private final RepositorioItem repositorio;

    @Autowired
    public ServicioItemImpl(RepositorioItem repositorio) {this.repositorio = repositorio;}

    @Override
    public void guardar(Item item) {repositorio.guardar(item);}

    @Override
    public void actualizar(Item item) {repositorio.actualizar(item);}

    @Override
    public Item obtenerPorId(Long id) {return repositorio.obtenerPorId(id);}

    @Override
    public List<Item> obtenerTodos() {return repositorio.obtenerTodos();}
}

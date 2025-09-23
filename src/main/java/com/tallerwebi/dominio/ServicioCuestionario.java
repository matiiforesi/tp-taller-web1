package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.RepositorioCuestionarioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service ("servicioCuestionario")
public class ServicioCuestionario {

    @Autowired
    private RepositorioCuestionario repositorioCuestionario;

    public ServicioCuestionario(RepositorioCuestionario repositorioCuestionario) {
        this.repositorioCuestionario = repositorioCuestionario;
    }

    public Cuestionario buscarPorDificultad(String dificultad){
        return repositorioCuestionario.buscarPorDificultad(dificultad);
    }

    public Cuestionario buscarPorCategoria(String categoria){
        return repositorioCuestionario.buscarPorCategoria(categoria);
    }

    public void guardar(Cuestionario cuestionario){
        repositorioCuestionario.guardar(cuestionario);
    }

    public Cuestionario buscar(Long idCuestionario){
        return repositorioCuestionario.buscar(idCuestionario);
    }
    public void modificar(Cuestionario cuestionario){
        repositorioCuestionario.modificar(cuestionario);
    }
    // create cuestionario
    //  - difficulty & category}
    // save db

    // get cuestionario
    // - by dif & cat
    // get all cuestionarios
    // - names
}

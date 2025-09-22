package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.RepositorioCuestionarioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioCuestionario {

    @Autowired
    private RepositorioCuestionarioImpl repositorioCuestionario;

    public Cuestionario buscarPorDificultad(String dificultad){
        return repositorioCuestionario.buscarPorDificultad(dificultad);
    }


    // create cuestionario
    //  - difficulty & category}
    // save db

    // get cuestionario
    // - by dif & cat
    // get all cuestionarios
    // - names
}

package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioAdmin")
@Transactional
public class ServicioAdminImpl implements ServicioAdmin {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCuestionario repositorioCuestionario;

    @Autowired
    public ServicioAdminImpl(RepositorioUsuario repositorioUsuario,RepositorioCuestionario repositorioCuestionario) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCuestionario = repositorioCuestionario;
    }

    @Override
    public Integer contarUsuarios() {
        return repositorioUsuario.contarUsuarios();
    }
    @Override
    public Integer contarCuestionarios() {
        return repositorioCuestionario.contarCuestionarios();
    }
}

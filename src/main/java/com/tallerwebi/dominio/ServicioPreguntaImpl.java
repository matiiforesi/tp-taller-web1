package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioPreguntaImpl implements ServicioPregunta {

    private RepositorioPreguntas repositorioPreguntas;

    @Autowired
    public ServicioPreguntaImpl(RepositorioPreguntas repositorioPreguntas) {
        this.repositorioPreguntas = repositorioPreguntas;
    }

    @Override
    public Preguntas obtenerPorId(Long id) {
        return this.repositorioPreguntas.buscarPregunta(id);
    }

    @Override
    public void guardarPregunta(Preguntas pregunta) {this.repositorioPreguntas.guardar(pregunta);}
}

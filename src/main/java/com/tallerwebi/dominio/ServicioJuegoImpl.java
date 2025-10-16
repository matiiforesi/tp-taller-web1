package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.RepositorioHistorialImpl;
import com.tallerwebi.infraestructura.RepositorioUsuarioImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioJuegoImpl implements ServicioJuego {

    private final RepositorioUsuarioImpl repositorioUsuarioImpl;
    private final RepositorioHistorialImpl repositorioHistorialImpl;
    private ServicioCuestionario servicioCuestionario;
    private ServicioPregunta servicioPregunta;

    private Integer puntajeTotal = 0;
    private Integer preguntasCorrectas = 0;
    private Integer preguntasErradas = 0;

    public ServicioJuegoImpl(RepositorioUsuarioImpl repositorioUsuarioImpl, RepositorioHistorialImpl repositorioHistorialImpl, ServicioCuestionario servicioCuestionario, ServicioPregunta servicioPregunta) {
        this.repositorioUsuarioImpl = repositorioUsuarioImpl;
        this.repositorioHistorialImpl = repositorioHistorialImpl;
        this.servicioCuestionario = servicioCuestionario;
        this.servicioPregunta = servicioPregunta;
    }

    @Override
    public Boolean validarRespuesta(String respuesta, Long idPregunta) {
        // Preguntas pregunta= servicioCuestionario.buscar(id).getPreguntas().get(0);
        Preguntas pregunta = servicioPregunta.obtenerPorId(idPregunta);
        if (pregunta.getRespuestaCorrecta().equals(respuesta.trim())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Integer obtenerPuntaje(Long idPregunta, String respuesta) {
        Integer puntosGanados = 0;

        if (validarRespuesta(respuesta, idPregunta)) {
            puntosGanados = 100;
            preguntasCorrectas++;
        } else {
            preguntasErradas++;
        }

        this.puntajeTotal += puntosGanados;
        return this.puntajeTotal;
    }

    @Override
    public Preguntas obtenerPregunta(Cuestionario cuestionario, Integer indicePregunta) {
        return cuestionario.getPreguntas().get(indicePregunta);
    }

    @Override
    public Cuestionario obtenerCuestionario(Long id) {
        return servicioCuestionario.buscar(id);
        //return servicioCuestionario.buscarTodo().get(0);
    }

    @Override
    public void actualizarPuntajeYCrearHistorial(Usuario jugador, Cuestionario cuestionario, int preguntasCorrectas, int preguntasErradas) {
        jugador.setPuntaje(jugador.getPuntaje() + this.puntajeTotal);
        repositorioUsuarioImpl.modificar(jugador);

        HistorialCuestionario historialCuestionario = new HistorialCuestionario();
        historialCuestionario.setJugador(jugador);
        historialCuestionario.setNombreUsuario(jugador.getNombre());
        historialCuestionario.setNombreCuestionario(cuestionario.getNombre());
        historialCuestionario.setIdCuestionario(cuestionario.getId());
        historialCuestionario.setPuntaje((long) this.puntajeTotal);
        historialCuestionario.setPreguntasCorrectas(preguntasCorrectas);
        historialCuestionario.setPreguntasErradas(preguntasErradas);

        repositorioHistorialImpl.guardar(historialCuestionario);

        reiniciarPuntaje();
    }

    @Override
    public void reiniciarPuntaje() {
        this.puntajeTotal = 0;
        this.preguntasCorrectas = 0;
        this.preguntasErradas = 0;
    }


}

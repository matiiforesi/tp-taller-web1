package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioJuegoImpl implements ServicioJuego {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioHistorial repositorioHistorial;
    private ServicioCuestionario servicioCuestionario;
    private ServicioPregunta servicioPregunta;
    private ServicioDificultad servicioDificultad;

    private Integer puntajeTotal = 0;
    private Integer preguntasCorrectas = 0;
    private Integer preguntasErradas = 0;

    public ServicioJuegoImpl(RepositorioUsuario repositorioUsuario, RepositorioHistorial repositorioHistorial, ServicioCuestionario servicioCuestionario, ServicioPregunta servicioPregunta, ServicioDificultad servicioDificultad) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioHistorial = repositorioHistorial;
        this.servicioCuestionario = servicioCuestionario;
        this.servicioPregunta = servicioPregunta;
        this.servicioDificultad = servicioDificultad;
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
    public Integer obtenerPuntaje(Long idPregunta, String respuesta, TimerPregunta timerPregunta) {
        Integer puntosGanados = 0;

        Preguntas pregunta = servicioPregunta.obtenerPorId(idPregunta);

        int mult = 1;
        if (pregunta != null && pregunta.getDificultad() != null) {
            mult = servicioDificultad.calcularMultiplicador(pregunta.getDificultad());
        }

        if (validarRespuesta(respuesta, idPregunta)) {
            preguntasCorrectas++;
            int puntajeBase = 100;

            int tiempoBonus = 0;
            if (timerPregunta != null) {
                tiempoBonus = timerPregunta.segundosRestantes().intValue() * 10;
            }

            puntosGanados = (puntajeBase + tiempoBonus) * mult;
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
        repositorioUsuario.modificar(jugador);

        HistorialCuestionario historialCuestionario = new HistorialCuestionario();
        historialCuestionario.setJugador(jugador);
        historialCuestionario.setNombreUsuario(jugador.getNombre());
        historialCuestionario.setNombreCuestionario(cuestionario.getNombre());
        historialCuestionario.setIdCuestionario(cuestionario.getId());
        historialCuestionario.setPuntaje((long) this.puntajeTotal);
        historialCuestionario.setPreguntasCorrectas(preguntasCorrectas);
        historialCuestionario.setPreguntasErradas(preguntasErradas);

        repositorioHistorial.guardar(historialCuestionario);

        reiniciarPuntaje();
    }

    @Override
    public void reiniciarPuntaje() {
        this.puntajeTotal = 0;
        this.preguntasCorrectas = 0;
        this.preguntasErradas = 0;
    }
}

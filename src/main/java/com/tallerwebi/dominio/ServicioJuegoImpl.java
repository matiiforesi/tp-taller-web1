package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioJuegoImpl implements ServicioJuego {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioHistorial repositorioHistorial;
    private final RepositorioIntento repositorioIntento;
    private final ServicioCuestionario servicioCuestionario;
    private final ServicioPregunta servicioPregunta;
    private final ServicioDificultad servicioDificultad;
    private final ServicioConfigJuego servicioConfigJuego;

    private Integer puntajeTotal = 0;
    private Integer preguntasCorrectas = 0;
    private Integer preguntasErradas = 0;
    private Integer vidasRestantes = 0;

    public ServicioJuegoImpl(RepositorioUsuario repositorioUsuario, RepositorioHistorial repositorioHistorial, RepositorioIntento repositorioIntento, ServicioCuestionario servicioCuestionario, ServicioPregunta servicioPregunta, ServicioDificultad servicioDificultad, ServicioConfigJuego servicioConfigJuego) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioHistorial = repositorioHistorial;
        this.repositorioIntento = repositorioIntento;
        this.servicioCuestionario = servicioCuestionario;
        this.servicioPregunta = servicioPregunta;
        this.servicioDificultad = servicioDificultad;
        this.servicioConfigJuego = servicioConfigJuego;
    }

    @Override
    public void inicializarVidas(Cuestionario cuestionario) {
        if (cuestionario.getVidas() == null) {
            servicioCuestionario.asignarVidasSegunDificultad(cuestionario);
        }
        this.vidasRestantes = cuestionario.getVidas();
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
        Integer puntajeBase= this.servicioConfigJuego.getInt("puntaje.base",100);
        Integer bonificacionTiempo= this.servicioConfigJuego.getInt("bonificacion.tiempo",10);
        Integer penalizacionVida= this.servicioConfigJuego.getInt("penalizacion.vida",1);

        int mult = 1;

        if (pregunta != null && pregunta.getDificultad() != null) {
            mult = servicioDificultad.calcularMultiplicador(pregunta.getDificultad());
        }

        if (validarRespuesta(respuesta, idPregunta)) {
            preguntasCorrectas++;
           // int puntajeBase = 100;
            int tiempoBonus = (timerPregunta != null) ? timerPregunta.segundosRestantes().intValue() * bonificacionTiempo : 0;
            puntosGanados = (puntajeBase + tiempoBonus) * mult;
        } else {
            preguntasErradas++;
           // vidasRestantes--;
            vidasRestantes -= penalizacionVida;
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
        // return servicioCuestionario.buscarTodo().get(0);
    }

    @Override
    public void actualizarPuntajeYCrearHistorial(Usuario jugador, Cuestionario cuestionario, int preguntasCorrectas, int preguntasErradas, Integer puntajePenalizado) {
        // registrarIntento(jugador.getId(), cuestionario.getId());
        // jugador.setPuntaje(jugador.getPuntaje() + this.puntajeTotal);
        Usuario usuarioPersistido = repositorioUsuario.buscarPorId(jugador.getId());
        usuarioPersistido.setPuntaje(usuarioPersistido.getPuntaje() + puntajePenalizado);
        repositorioUsuario.modificar(usuarioPersistido);

        HistorialCuestionario historialCuestionario = new HistorialCuestionario();
        historialCuestionario.setJugador(jugador);
        historialCuestionario.setNombreUsuario(jugador.getNombre());
        historialCuestionario.setNombreCuestionario(cuestionario.getNombre());
        historialCuestionario.setIdCuestionario(cuestionario.getId());
        historialCuestionario.setPuntaje((long) puntajePenalizado);
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

    @Override
    public Integer registrarIntento(Long idUsuario, Long idCuestionario, Integer puntajePartida) {
        Integer puntajePenalizado = calcularPenalizacion(idUsuario, idCuestionario, puntajePartida);

        Usuario usuario = repositorioUsuario.buscarPorId(idUsuario);
        Cuestionario cuestionario = servicioCuestionario.buscar(idCuestionario);

        IntentoCuestionario intentoCuestionario = new IntentoCuestionario();
        intentoCuestionario.setUsuario(usuario);
        intentoCuestionario.setCuestionario(cuestionario);
        intentoCuestionario.setPuntaje((long) puntajePenalizado);

//        System.out.println("Puntaje total antes de penalizar: " + this.puntajeTotal);
//        System.out.println("Intento registrado: Usuario " + idUsuario + ", Cuestionario " + idCuestionario);
//        System.out.println("Cantidad de reintentos " + reintentos);
//        System.out.println("Puntaje penalizado " + puntajePenalizado);

        repositorioIntento.guardar(intentoCuestionario);
        return puntajePenalizado;
    }

    @Override
    public void setPuntajeTotal(Integer puntajeTotal) {this.puntajeTotal = puntajeTotal;}

    @Override
    public Integer calcularPenalizacion(Long idUsuario, Long idCuestionario, Integer puntajePartida) {
        Integer reintentos = repositorioIntento.contarIntentos(idUsuario, idCuestionario);
        if (reintentos == 0) {return puntajePartida;}
        return puntajePartida / (1 + reintentos);
    }

//    @Override
//    public Integer obtenerIntentosPrevios(Long idUsuario, Long idCuestionario) {
//        return repositorioIntento.contarIntentos(idUsuario, idCuestionario);
//    }
}

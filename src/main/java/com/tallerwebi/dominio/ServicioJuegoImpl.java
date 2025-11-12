package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private final RepositorioCompraItem repositorioCompraItem;

    private Integer puntajeTotal = 0;
    private Integer preguntasCorrectas = 0;
    private Integer preguntasErradas = 0;
    private Integer vidasRestantes = 0;

    public ServicioJuegoImpl(RepositorioCompraItem repositorioCompraItem,RepositorioUsuario repositorioUsuario, RepositorioHistorial repositorioHistorial, RepositorioIntento repositorioIntento, ServicioCuestionario servicioCuestionario, ServicioPregunta servicioPregunta, ServicioDificultad servicioDificultad, ServicioConfigJuego servicioConfigJuego) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioHistorial = repositorioHistorial;
        this.repositorioIntento = repositorioIntento;
        this.servicioCuestionario = servicioCuestionario;
        this.servicioPregunta = servicioPregunta;
        this.servicioDificultad = servicioDificultad;
        this.servicioConfigJuego = servicioConfigJuego;
        this.repositorioCompraItem = repositorioCompraItem;
    }

    @Override
    public void asignarMonedas(Usuario jugador, Integer puntaje) {
        if (jugador.getMonedas() == null) jugador.setMonedas(0L);

        Long monedasGanadas = (long) (puntaje * 0.1);
        jugador.setMonedas(jugador.getMonedas() + monedasGanadas);
        repositorioUsuario.modificar(jugador);
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
        //Usuario usuarioPersistido = repositorioUsuario.buscarPorId(jugador.getId());
        jugador.setPuntaje(jugador.getPuntaje() + puntajePenalizado);
       // asignarMonedas(jugador, puntajePenalizado);
        repositorioUsuario.modificar(jugador);

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

    @Override
    public Boolean tieneTrampasDisponibles(Long idUsuario, TIPO_ITEMS tipo) {
       List<CompraItem> trampas= repositorioCompraItem.obtenerComprasPorUsuario(idUsuario);
        for (CompraItem compraItem : trampas) {
            if(compraItem.getItem().getTipoItem()==tipo && Boolean.FALSE.equals(compraItem.getUsado())){
                return true;
           }
       }
        return false;
    }

    @Override
    public void usarTrampa(Long idUsuario, TIPO_ITEMS tipo) {
        List<CompraItem>items= repositorioCompraItem.obtenerComprasPorUsuario(idUsuario);

        for (CompraItem compraItem : items) {
            if(compraItem.getItem().getTipoItem()==tipo && Boolean.FALSE.equals(compraItem.getUsado())){
                compraItem.setUsado(true);
                repositorioCompraItem.guardar(compraItem);
                break;
            }
        }
    }

    @Override
    public Integer obtenerPuntajeConTrampa(Long idPregunta, String respuesta, TimerPregunta timerPregunta, Long idUsuario, TIPO_ITEMS trampaActivada) {
        Integer puntosGanados = 0;
        Preguntas pregunta = servicioPregunta.obtenerPorId(idPregunta);
        Integer puntajeBase = servicioConfigJuego.getInt("puntaje.base", 100);
        Integer bonificacionTiempo = servicioConfigJuego.getInt("bonificacion.tiempo", 10);
        Integer penalizacionVida = servicioConfigJuego.getInt("penalizacion.vida", 1);

        int mult = (pregunta != null && pregunta.getDificultad() != null)
                ? servicioDificultad.calcularMultiplicador(pregunta.getDificultad())
                : 1;

        boolean esCorrecta = validarRespuesta(respuesta, idPregunta);

        if (esCorrecta) {
            preguntasCorrectas++;
            int tiempoBonus = (timerPregunta != null) ? timerPregunta.segundosRestantes().intValue() * bonificacionTiempo : 0;
            puntosGanados = (puntajeBase + tiempoBonus) * mult;

            if (trampaActivada == TIPO_ITEMS.DUPLICAR_PUNTAJE && tieneTrampasDisponibles(idUsuario, TIPO_ITEMS.DUPLICAR_PUNTAJE)) {
                puntosGanados *= 2;
                usarTrampa(idUsuario, TIPO_ITEMS.DUPLICAR_PUNTAJE);
            }
        } else {
            preguntasErradas++;
            vidasRestantes -= penalizacionVida;
        }

        this.puntajeTotal += puntosGanados;
        return this.puntajeTotal;

    }

    @Override
    public List<Item> obtenerTrampasDisponibles(Long idUsuario) {

        List<CompraItem>compra= repositorioCompraItem.obtenerComprasPorUsuario(idUsuario);
        List<Item>disponibles= new ArrayList<Item>();
        for (CompraItem compraItem : compra) {
            if(Boolean.FALSE.equals(compraItem.getUsado())){
                Item item=compraItem.getItem();
                disponibles.add(item);

            }
        }
        return disponibles;
    }

    @Override
    public List<String> obtenerOpcionesFiltradas(Preguntas pregunta, Long idUsuario, TIPO_ITEMS trampaActivada) {
        List<String> opciones = new ArrayList<>();

        opciones.add(pregunta.getRespuestaCorrecta());
        opciones.add(pregunta.getRespuestaIncorrecta1());
        opciones.add(pregunta.getRespuestaIncorrecta2());
        opciones.add(pregunta.getRespuestaIncorrecta3());

        if (trampaActivada == TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS &&
                tieneTrampasDisponibles(idUsuario, TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS)) {

            usarTrampa(idUsuario, TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS);

            List<String> incorrectas = opciones.stream()
                    .filter(op -> !op.equals(pregunta.getRespuestaCorrecta()))
                    .collect(Collectors.toList());

            Collections.shuffle(incorrectas);
            opciones = new ArrayList<>();
            opciones.add(pregunta.getRespuestaCorrecta());
            opciones.add(incorrectas.get(0)); // dejar solo una incorrecta
        }

        Collections.shuffle(opciones);
        System.out.println("Trampa activada: " + trampaActivada);

        return opciones;

    }

//    @Override
//    public Integer obtenerIntentosPrevios(Long idUsuario, Long idCuestionario) {
//        return repositorioIntento.contarIntentos(idUsuario, idCuestionario);
//    }
}

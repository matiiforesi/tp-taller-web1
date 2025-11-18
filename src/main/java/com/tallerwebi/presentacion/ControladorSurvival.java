package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes({"gestorPreguntas", "survivalPuntaje", "survivalCorrectas", 
                   "survivalErradas", "survivalVidas", "survivalDificultad", "usuario", "respondida"})
public class ControladorSurvival {

    private static final String DIFICULTAD_EASY = "easy";
    private static final int CANTIDAD_PREGUNTAS_INICIALES = 5;
    private static final int TIEMPO_PREGUNTA_SEGUNDOS = 10;
    private static final int VIDAS_INICIALES = 3;
    private static final int PUNTAJE_BASE = 100;
    private static final int BONIFICACION_TIEMPO = 10;
    private static final double FACTOR_CONVERSION_MONEDAS = 0.1;
    private static final long ID_CUESTIONARIO_DUMMY = -1L;
    private static final String NOMBRE_CUESTIONARIO_SURVIVAL = "Survival Mode";

    private final ServicioSurvival servicioSurvival;

    @Autowired
    public ControladorSurvival(ServicioSurvival servicioSurvival) {
        this.servicioSurvival = servicioSurvival;
    }

    @RequestMapping("/survival/iniciar")
    public ModelAndView iniciarSurvival(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        limpiarSessionSurvival(session);

        GestorPreguntasSurvival gestor = (GestorPreguntasSurvival) session.getAttribute("gestorPreguntas");
        if (gestor == null) {
            gestor = new GestorPreguntasSurvival();
            session.setAttribute("gestorPreguntas", gestor);
        }

        List<Preguntas> preguntas = servicioSurvival.obtenerPreguntasSurvival(
                DIFICULTAD_EASY, CANTIDAD_PREGUNTAS_INICIALES);
        
        if (preguntas == null || preguntas.isEmpty()) {
            ModelMap errorModel = new ModelMap();
            errorModel.put("error", "No se pudieron cargar las preguntas. Intenta nuevamente.");
            return new ModelAndView("redirect:/home", errorModel);
        }

        inicializarSessionSurvival(session, usuario, preguntas, gestor);

        return siguientePreguntaSurvival(session);
    }

    private void inicializarSessionSurvival(HttpSession session, Usuario usuario, 
                                           List<Preguntas> preguntas, GestorPreguntasSurvival gestor) {
        gestor.inicializar(preguntas, DIFICULTAD_EASY);
        
        session.setAttribute("gestorPreguntas", gestor);
        session.setAttribute("survivalPuntaje", 0);
        session.setAttribute("survivalCorrectas", 0);
        session.setAttribute("survivalErradas", 0);
        session.setAttribute("survivalVidas", VIDAS_INICIALES);
        session.setAttribute("survivalMonedasGanadas", 0);
        session.setAttribute("puntajeGanado", 0);
        session.setAttribute("survivalDificultad", DIFICULTAD_EASY);
        session.setAttribute("usuario", usuario);
        session.setAttribute("timer", new TimerPregunta(TIEMPO_PREGUNTA_SEGUNDOS));
        session.setAttribute("respondida", false);
    }

    @RequestMapping("/survival/siguiente")
    public ModelAndView siguientePreguntaSurvival(HttpSession session) {
        EstadoSurvival estado = obtenerEstadoSurvival(session);

        if (estado.usuario == null) {
            return new ModelAndView("redirect:/login");
        }
        if (estado.gestorPreguntas == null) {
            return new ModelAndView("redirect:/home");
        }
        if (estado.vidas <= 0) {
            return finalizarSurvival(session, estado.usuario, estado.puntajeTotal, 
                                   estado.correctas, estado.erradas, estado.vidas);
        }
        boolean debeAvanzar = Boolean.TRUE.equals(estado.respondida);
        
        if (debeAvanzar) {
            estado.gestorPreguntas.marcarPreguntaComoRespondida();
        }

        Preguntas pregunta = estado.gestorPreguntas.obtenerSiguientePregunta(servicioSurvival);
        
        if (pregunta == null) {
            if (estado.gestorPreguntas.cargarNuevasPreguntas(servicioSurvival)) {
                pregunta = estado.gestorPreguntas.obtenerSiguientePregunta(servicioSurvival);
            }
            
            if (pregunta == null) {
                return finalizarSurvival(session, estado.usuario, estado.puntajeTotal, 
                                       estado.correctas, estado.erradas, estado.vidas);
            }
        }

        actualizarSessionParaNuevaPregunta(session, estado);
        List<Preguntas> todasLasPreguntas = obtenerTodasLasPreguntasParaVista(estado.gestorPreguntas);
        Cuestionario cuestionario = crearCuestionarioDummy(todasLasPreguntas);

        return prepararVistaSurvival(cuestionario, pregunta, estado.timer, 
                                    false, null, estado.usuario, estado.puntajeTotal, 
                                    estado.correctas, estado.erradas, estado.vidas, 
                                    estado.dificultadActual, false, session);
    }

    private EstadoSurvival obtenerEstadoSurvival(HttpSession session) {
        EstadoSurvival estado = new EstadoSurvival();
        estado.gestorPreguntas = (GestorPreguntasSurvival) session.getAttribute("gestorPreguntas");
        estado.puntajeTotal = obtenerValorConDefault(session, "survivalPuntaje", 0);
        estado.correctas = obtenerValorConDefault(session, "survivalCorrectas", 0);
        estado.erradas = obtenerValorConDefault(session, "survivalErradas", 0);
        estado.vidas = obtenerValorConDefault(session, "survivalVidas", VIDAS_INICIALES);
        estado.dificultadActual = obtenerValorConDefault(session, "survivalDificultad", DIFICULTAD_EASY);
        estado.usuario = (Usuario) session.getAttribute("usuario");
        estado.timer = (TimerPregunta) session.getAttribute("timer");
        estado.respondida = (Boolean) session.getAttribute("respondida");
        return estado;
    }

    @SuppressWarnings("unchecked")
    private <T> T obtenerValorConDefault(HttpSession session, String key, T defaultValue) {
        T valor = (T) session.getAttribute(key);
        return valor != null ? valor : defaultValue;
    }

    private void actualizarSessionParaNuevaPregunta(HttpSession session, EstadoSurvival estado) {
        session.setAttribute("gestorPreguntas", estado.gestorPreguntas);
        session.setAttribute("survivalDificultad", estado.dificultadActual);
        session.setAttribute("survivalPuntaje", estado.puntajeTotal);
        session.setAttribute("survivalCorrectas", estado.correctas);
        session.setAttribute("survivalErradas", estado.erradas);
        session.setAttribute("survivalVidas", estado.vidas);
        
        if (estado.timer == null) {
            estado.timer = new TimerPregunta(TIEMPO_PREGUNTA_SEGUNDOS);
        } else {
            estado.timer.reiniciar();
        }
        session.setAttribute("timer", estado.timer);
        session.setAttribute("respondida", false);
    }

    private List<Preguntas> obtenerTodasLasPreguntasParaVista(GestorPreguntasSurvival gestor) {
        List<Preguntas> preguntas = new java.util.ArrayList<>();
        Preguntas preguntaActual = gestor.getPreguntaActual(servicioSurvival);
        if (preguntaActual != null) {
            preguntas.add(preguntaActual);
        }
        return preguntas;
    }

    private Cuestionario crearCuestionarioDummy(List<Preguntas> preguntas) {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setId(ID_CUESTIONARIO_DUMMY);
        cuestionario.setNombre(NOMBRE_CUESTIONARIO_SURVIVAL);
        cuestionario.setPreguntas(preguntas);
        return cuestionario;
    }

    private static class EstadoSurvival {
        GestorPreguntasSurvival gestorPreguntas;
        Integer puntajeTotal;
        Integer correctas;
        Integer erradas;
        Integer vidas;
        String dificultadActual;
        Usuario usuario;
        TimerPregunta timer;
        Boolean respondida;
    }

    @RequestMapping("/survival/validar")
    public ModelAndView validarPreguntaSurvival(@RequestParam String respuesta,
                                               @RequestParam(required = false) String tiempoAgotado,
                                               HttpSession session) {
        EstadoSurvival estado = obtenerEstadoSurvival(session);

        if (estado.gestorPreguntas == null) {
            return new ModelAndView("redirect:/home");
        }

        Preguntas pregunta = estado.gestorPreguntas.getPreguntaActual(servicioSurvival);
        
        if (pregunta == null) {
            return new ModelAndView("redirect:/home");
        }
        boolean esCorrecta = validarRespuesta(pregunta, respuesta, tiempoAgotado);
        
        actualizarEstadoDespuesDeValidacion(estado, esCorrecta, session);
        
        if (estado.vidas <= 0) {
            return finalizarSurvival(session, estado.usuario, estado.puntajeTotal, 
                                   estado.correctas, estado.erradas, estado.vidas);
        }

        List<Preguntas> todasLasPreguntas = obtenerTodasLasPreguntasParaVista(estado.gestorPreguntas);
        Cuestionario cuestionario = crearCuestionarioDummy(todasLasPreguntas);
        boolean tiempoAgotadoFlag = "true".equals(tiempoAgotado);

        return prepararVistaSurvival(cuestionario, pregunta, estado.timer, true, 
                                    esCorrecta, estado.usuario, estado.puntajeTotal, 
                                    estado.correctas, estado.erradas, estado.vidas, 
                                    estado.dificultadActual, tiempoAgotadoFlag, session);
    }

    private boolean validarRespuesta(Preguntas pregunta, String respuesta, String tiempoAgotado) {
        if ("true".equals(tiempoAgotado)) {
            return false;
        }
        return pregunta.getRespuestaCorrecta().equals(respuesta.trim());
    }

    private void actualizarEstadoDespuesDeValidacion(EstadoSurvival estado, boolean esCorrecta, 
                                                     HttpSession session) {
        if (esCorrecta) {
            procesarRespuestaCorrecta(estado, session);
        } else {
            procesarRespuestaIncorrecta(estado);
        }
        
        estado.gestorPreguntas.marcarPreguntaComoRespondida();
        
        session.setAttribute("gestorPreguntas", estado.gestorPreguntas);
        session.setAttribute("survivalPuntaje", estado.puntajeTotal);
        session.setAttribute("survivalCorrectas", estado.correctas);
        session.setAttribute("survivalErradas", estado.erradas);
        session.setAttribute("survivalVidas", estado.vidas);
        session.setAttribute("survivalDificultad", estado.dificultadActual);
        session.setAttribute("respondida", true);
    }

    private void procesarRespuestaCorrecta(EstadoSurvival estado, HttpSession session) {
        int multiplicador = servicioSurvival.calcularMultiplicadorSurvival(estado.dificultadActual);
        int tiempoBonus = calcularTiempoBonus(estado.timer);
        int puntosGanados = (PUNTAJE_BASE + tiempoBonus) * multiplicador;
        
        estado.puntajeTotal += puntosGanados;
        estado.correctas++;
        
        int monedasGanadas = calcularMonedasGanadas(puntosGanados);
        
        Integer monedasGanadasTotales = (Integer) session.getAttribute("survivalMonedasGanadas");
        if (monedasGanadasTotales == null) {
            monedasGanadasTotales = 0;
        }
        monedasGanadasTotales += monedasGanadas;
        session.setAttribute("survivalMonedasGanadas", monedasGanadasTotales);

        Integer puntajeGanado = (Integer) session.getAttribute("puntajeGanado");
        if (puntajeGanado == null) {
            puntajeGanado = 0;
        }
        puntajeGanado += puntosGanados;
        session.setAttribute("puntajeGanado", puntajeGanado);

        Usuario usuarioActualizado = servicioSurvival.actualizarPuntajeYMonedas(
                estado.usuario.getId(), puntosGanados);
        
        if (usuarioActualizado != null) {
            estado.usuario = usuarioActualizado;
            session.setAttribute("usuario", usuarioActualizado);
        }
        
        String nuevaDificultad = servicioSurvival.obtenerDificultadSurvival(estado.correctas);
        if (!nuevaDificultad.equals(estado.dificultadActual)) {
            estado.dificultadActual = nuevaDificultad;
            estado.gestorPreguntas.actualizarDificultad(nuevaDificultad);
        }
    }

    private void procesarRespuestaIncorrecta(EstadoSurvival estado) {
        estado.erradas++;
        estado.vidas--;
    }

    private int calcularTiempoBonus(TimerPregunta timer) {
        if (timer == null) {
            return 0;
        }
        return timer.segundosRestantes().intValue() * BONIFICACION_TIEMPO;
    }

    private ModelAndView finalizarSurvival(HttpSession session, Usuario usuario, 
                                          Integer puntajeTotal, Integer correctas, 
                                          Integer erradas, Integer vidas) {
        Integer monedasGanadas = (Integer) session.getAttribute("survivalMonedasGanadas");
        if (monedasGanadas == null) {
            monedasGanadas = calcularMonedasGanadas(puntajeTotal);
        }
        
        Integer puntajeGanado = (Integer) session.getAttribute("puntajeGanado");
        if (puntajeGanado == null) {
            puntajeGanado = puntajeTotal != null ? puntajeTotal : 0;
        }
        
        limpiarSessionSurvival(session);

        ModelMap model = new ModelMap();
        model.put("survivalVidas", vidas);
        model.put("survivalPuntaje", puntajeGanado);
        model.put("survivalCorrectas", correctas);
        model.put("survivalErradas", erradas);
        model.put("monedas", monedasGanadas);
        model.put("usuario", usuario);

        return new ModelAndView("final_survival", model);
    }

    private int calcularMonedasGanadas(Integer puntajeTotal) {
        return (int) Math.floor(puntajeTotal * FACTOR_CONVERSION_MONEDAS);
    }

    private void limpiarSessionSurvival(HttpSession session) {
        GestorPreguntasSurvival gestor = (GestorPreguntasSurvival) session.getAttribute("gestorPreguntas");
        if (gestor != null) {
            gestor.limpiarPreguntas();
        }

        session.removeAttribute("survivalPuntaje");
        session.removeAttribute("survivalCorrectas");
        session.removeAttribute("survivalErradas");
        session.removeAttribute("survivalVidas");
        session.removeAttribute("survivalMonedasGanadas");
        session.removeAttribute("puntajeGanado");
        session.removeAttribute("survivalDificultad");
        session.removeAttribute("timer");
        session.removeAttribute("respondida");
    }

    private ModelAndView prepararVistaSurvival(Cuestionario cuestionario, Preguntas pregunta,
                                              TimerPregunta timer, Boolean respondida,
                                              Boolean esCorrecta, Usuario usuario, Integer puntajeTotal,
                                              Integer correctas, Integer erradas, Integer vidas,
                                              String dificultadActual, Boolean tiempoAgotado, 
                                              HttpSession session) {
        if (pregunta == null) {
            return new ModelAndView("redirect:/home");
        }

        List<String> opciones = crearOpcionesMezcladas(pregunta);
        Integer monedasGanadas = (Integer) session.getAttribute("survivalMonedasGanadas");
        Integer puntajeGanado = (Integer) session.getAttribute("puntajeGanado");

        ModelMap model = crearModeloVista(cuestionario, pregunta, opciones, respondida,
                                         esCorrecta, usuario, puntajeTotal, correctas, erradas,
                                         vidas, dificultadActual, tiempoAgotado, timer, monedasGanadas, puntajeGanado);

        return new ModelAndView("pregunta_survival", model);
    }

    private List<String> crearOpcionesMezcladas(Preguntas pregunta) {
        List<String> opciones = Arrays.asList(
                pregunta.getRespuestaCorrecta(),
                pregunta.getRespuestaIncorrecta1(),
                pregunta.getRespuestaIncorrecta2(),
                pregunta.getRespuestaIncorrecta3()
        );
        Collections.shuffle(opciones);
        return opciones;
    }

    private ModelMap crearModeloVista(Cuestionario cuestionario, Preguntas pregunta,
                                     List<String> opciones, Boolean respondida, Boolean esCorrecta,
                                     Usuario usuario, Integer puntajeTotal, Integer correctas,
                                     Integer erradas, Integer vidas, String dificultadActual,
                                     Boolean tiempoAgotado, TimerPregunta timer, Integer monedasGanadas, Integer puntajeGanado) {
        ModelMap model = new ModelMap();
        model.put("cuestionario", cuestionario);
        model.put("pregunta", pregunta);
        model.put("opciones", opciones);
        model.put("respondida", respondida);
        model.put("esCorrecta", esCorrecta);
        model.put("usuario", usuario);
        model.put("puntajeTotal", puntajeGanado != null ? puntajeGanado : puntajeTotal);
        model.put("survivalCorrectas", correctas);
        model.put("survivalErradas", erradas);
        model.put("tiempoRestante", timer.segundosRestantes());
        model.put("idCuestionario", cuestionario.getId());
        model.put("survivalVidas", vidas);
        model.put("dificultadActual", dificultadActual);
        model.put("tiempoAgotado", tiempoAgotado != null ? tiempoAgotado : false);
        model.put("monedas", monedasGanadas);
        return model;
    }
}


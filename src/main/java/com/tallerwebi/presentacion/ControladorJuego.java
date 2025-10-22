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
@SessionAttributes({"idCuestionario", "indicePregunta", "puntajeTotal", "preguntasCorrectas", "preguntasErradas", "usuario"})
public class ControladorJuego {
    private ServicioJuego servicioJuego;
    private ServicioPregunta servicioPregunta;

    @Autowired
    public ControladorJuego(ServicioJuego servicioJuego, ServicioPregunta servicioPregunta) {
        this.servicioJuego = servicioJuego;
        this.servicioPregunta = servicioPregunta;
    }

    @RequestMapping("/iniciar")
    public ModelAndView iniciarPorFormulario(@RequestParam("idCuestionario") Long idCuestionario, HttpSession session) {
        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);

        if (cuestionario == null || cuestionario.getPreguntas().isEmpty()) {
            ModelMap errorModel = new ModelMap();
            errorModel.put("error", "No se encontró un cuestionario válido");
            return new ModelAndView("vista-error-cuestionario", errorModel);
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        session.setAttribute("indicePregunta", 0);
        session.setAttribute("puntajeTotal", 0);
        session.setAttribute("preguntasCorrectas", 0);
        session.setAttribute("preguntasErradas", 0);
        session.setAttribute("idCuestionario", idCuestionario);
        session.setAttribute("usuario", usuario);

        TimerPregunta timer = new TimerPregunta(10);
        session.setAttribute("timer", timer);
        session.setAttribute("respondida", false);

        servicioJuego.reiniciarPuntaje();

        return prepararVista(cuestionario, 0, timer, false, null, usuario, 0, 0, 0);
    }

    @RequestMapping("/siguiente")
    public ModelAndView siguientePregunta(HttpSession session) {
        Integer indicePregunta = (Integer) session.getAttribute("indicePregunta");
        Integer puntajeTotal = (Integer) session.getAttribute("puntajeTotal");
        Integer preguntasCorrectas = (Integer) session.getAttribute("preguntasCorrectas");
        Integer preguntasErradas = (Integer) session.getAttribute("preguntasErradas");
        Long idCuestionario = (Long) session.getAttribute("idCuestionario");
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TimerPregunta timer = (TimerPregunta) session.getAttribute("timer");

        if (indicePregunta == null) {indicePregunta = 0;}
        if (puntajeTotal == null) {puntajeTotal = 0;}
        if (preguntasCorrectas == null) {preguntasCorrectas = 0;}
        if (preguntasErradas == null) {preguntasErradas = 0;}

        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);
        int nuevoIndice = indicePregunta + 1;

        if (timer == null) {
            timer = new TimerPregunta(10);
        } else {
            timer.reiniciar();
        }
        session.setAttribute("timer", timer);

        if (nuevoIndice < cuestionario.getPreguntas().size()) {
            session.setAttribute("indicePregunta", nuevoIndice);
            return prepararVista(cuestionario, nuevoIndice, timer, false, null, usuario,
                    puntajeTotal, preguntasCorrectas, preguntasErradas);
        } else {
            servicioJuego.actualizarPuntajeYCrearHistorial(usuario, cuestionario, preguntasCorrectas, preguntasErradas);

            session.removeAttribute("puntajeTotal");
            session.removeAttribute("preguntasCorrectas");
            session.removeAttribute("preguntasErradas");
            session.removeAttribute("indicePregunta");
            session.removeAttribute("idCuestionario");

            ModelMap model = new ModelMap();
            model.put("puntajeTotal", puntajeTotal);
            model.put("preguntasCorrectas", preguntasCorrectas);
            model.put("preguntasErradas", preguntasErradas);
            model.put("cuestionario", cuestionario);
            model.put("usuario", usuario);

            return new ModelAndView("final_partida", model);
        }
    }

    @RequestMapping("/juego/{idCuestionario}/validar")
    public ModelAndView validarPregunta(@PathVariable("idCuestionario") Long idCuestionario,
                                        @RequestParam Long idPregunta,
                                        @RequestParam String respuesta,
                                        HttpSession session) {
        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);
        Preguntas pregunta = servicioPregunta.obtenerPorId(idPregunta);
        TimerPregunta timer = (TimerPregunta) session.getAttribute("timer");

        if (timer == null || timer.tiempoAgotado()) {
            ModelMap errorModel = new ModelMap();
            errorModel.put("mensaje", "Tiempo agotado");
            errorModel.put("idCuestionario", idCuestionario);
            return new ModelAndView("tiempo-agotado", errorModel);
        }

        Integer indicePregunta = (Integer) session.getAttribute("indicePregunta");
        Integer puntajeTotal = (Integer) session.getAttribute("puntajeTotal");
        Integer preguntasCorrectas = (Integer) session.getAttribute("preguntasCorrectas");
        Integer preguntasErradas = (Integer) session.getAttribute("preguntasErradas");
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (indicePregunta == null) indicePregunta = 0;
        if (puntajeTotal == null) puntajeTotal = 0;
        if (preguntasCorrectas == null) preguntasCorrectas = 0;
        if (preguntasErradas == null) preguntasErradas = 0;

        boolean esCorrecta = servicioJuego.validarRespuesta(respuesta, idPregunta);
        puntajeTotal = servicioJuego.obtenerPuntaje(idPregunta, respuesta, timer);

        if (esCorrecta) preguntasCorrectas++;
        else preguntasErradas++;

        session.setAttribute("indicePregunta", indicePregunta);
        session.setAttribute("puntajeTotal", puntajeTotal);
        session.setAttribute("preguntasCorrectas", preguntasCorrectas);
        session.setAttribute("preguntasErradas", preguntasErradas);
        session.setAttribute("usuario", usuario);
        session.setAttribute("respondida", true);

        return prepararVista(cuestionario, indicePregunta, timer, true, esCorrecta, usuario,
                puntajeTotal, preguntasCorrectas, preguntasErradas);
    }

    @RequestMapping("/tiempo-agotado")
    public ModelAndView tiempoAgotado(@SessionAttribute("idCuestionario") Long idCuestionario) {
        ModelMap model = new ModelMap();
        model.put("mensaje", "Se acabó el tiempo de la pregunta");
        model.put("idCuestionario", idCuestionario);
        return new ModelAndView("tiempo-agotado", model);
    }

    private ModelAndView prepararVista(Cuestionario cuestionario,
                                       Integer indice,
                                       TimerPregunta timer,
                                       Boolean respondida,
                                       Boolean esCorrecta,
                                       Usuario usuario,
                                       Integer puntajeTotal,
                                       Integer preguntasCorrectas,
                                       Integer preguntasErradas) {

        ModelMap model = new ModelMap();
        Preguntas pregunta = servicioJuego.obtenerPregunta(cuestionario, indice);
        List<String> opciones = Arrays.asList(
                pregunta.getRespuestaCorrecta(),
                pregunta.getRespuestaIncorrecta1(),
                pregunta.getRespuestaIncorrecta2(),
                pregunta.getRespuestaIncorrecta3()
        );
        Collections.shuffle(opciones);

        model.put("cuestionario", cuestionario);
        model.put("pregunta", pregunta);
        model.put("indicePregunta", indice);
        model.put("opciones", opciones);
        model.put("respondida", respondida);
        model.put("esCorrecta", esCorrecta);
        model.put("usuario", usuario);
        model.put("puntajeTotal", puntajeTotal);
        model.put("preguntasCorrectas", preguntasCorrectas);
        model.put("preguntasErradas", preguntasErradas);
        model.put("tiempoRestante", timer.segundosRestantes());
        model.put("idCuestionario", cuestionario.getId());

        return new ModelAndView("pregunta", model);
    }
}
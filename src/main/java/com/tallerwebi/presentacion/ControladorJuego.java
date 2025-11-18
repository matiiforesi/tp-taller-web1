package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes({"idCuestionario", "indicePregunta", "puntajeTotal", "preguntasCorrectas", "preguntasErradas", "vidasRestantes", "usuario"})
public class ControladorJuego {

    private final ServicioJuego servicioJuego;
    private final ServicioPregunta servicioPregunta;
    private final ServicioCuestionario servicioCuestionario;
    private final ServicioCompra servicioCompra;

    @Autowired
    public ControladorJuego(ServicioJuego servicioJuego, ServicioPregunta servicioPregunta, ServicioCuestionario servicioCuestionario, ServicioCompra servicioCompra) {
        this.servicioJuego = servicioJuego;
        this.servicioPregunta = servicioPregunta;
        this.servicioCuestionario = servicioCuestionario;
        this.servicioCompra = servicioCompra;
    }

    @RequestMapping("/iniciar")
    public ModelAndView iniciarPorFormulario(@RequestParam("idCuestionario") Long idCuestionario, HttpSession session) {
        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);
        List<Preguntas> preguntasMezcladas = new ArrayList<>(cuestionario.getPreguntas());
        Collections.shuffle(preguntasMezcladas);
        cuestionario.setPreguntas(preguntasMezcladas);

        if (cuestionario == null || cuestionario.getPreguntas().isEmpty()) {
            ModelMap errorModel = new ModelMap();
            errorModel.put("error", "No se encontró un cuestionario válido");
            return new ModelAndView("vista-error-cuestionario", errorModel);
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        servicioJuego.reiniciarPuntaje();
        servicioJuego.inicializarVidas(cuestionario);

        session.setAttribute("preguntasMezcladas", preguntasMezcladas);
        session.setAttribute("indicePregunta", 0);
        session.setAttribute("puntajeTotal", 0);
        session.setAttribute("preguntasCorrectas", 0);
        session.setAttribute("preguntasErradas", 0);
        session.setAttribute("vidasRestantes", cuestionario.getVidas());
        session.setAttribute("idCuestionario", idCuestionario);
        session.setAttribute("usuario", usuario);

        TimerPregunta timer = new TimerPregunta(10);
        session.setAttribute("timer", timer);
        session.setAttribute("respondida", false);

        return prepararVista(cuestionario, 0, timer, false, null, usuario, 0, 0, 0, cuestionario.getVidas(), false, session);
    }

    @RequestMapping("/siguiente")
    public ModelAndView siguientePregunta(HttpSession session) {
        Integer indicePregunta = (Integer) session.getAttribute("indicePregunta");
        Integer puntajeTotal = (Integer) session.getAttribute("puntajeTotal");
        Integer preguntasCorrectas = (Integer) session.getAttribute("preguntasCorrectas");
        Integer preguntasErradas = (Integer) session.getAttribute("preguntasErradas");
        Integer vidasRestantes = (Integer) session.getAttribute("vidasRestantes");
        Long idCuestionario = (Long) session.getAttribute("idCuestionario");
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        TimerPregunta timer = (TimerPregunta) session.getAttribute("timer");
        Boolean respondida = (Boolean) session.getAttribute("respondida");

        if (indicePregunta == null) indicePregunta = 0;
        if (puntajeTotal == null) puntajeTotal = 0;
        if (preguntasCorrectas == null) preguntasCorrectas = 0;
        if (preguntasErradas == null) preguntasErradas = 0;
        if (vidasRestantes == null) vidasRestantes = servicioJuego.obtenerCuestionario(idCuestionario).getVidas();

        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);
        List<Preguntas> preguntasMezcladas = (List<Preguntas>) session.getAttribute("preguntasMezcladas");
        cuestionario.setPreguntas(preguntasMezcladas);

        /*if (timer == null) timer = new TimerPregunta(10);
        else timer.reiniciar();
        session.setAttribute("timer", timer);*/


        if (Boolean.TRUE.equals(respondida)) {
            int nuevoIndice = indicePregunta + 1;
            session.setAttribute("indicePregunta", nuevoIndice);
            session.setAttribute("respondida", false);
            if(timer!=null)timer.reiniciar();
            session.setAttribute("timer", timer);

            if (vidasRestantes <= 0 || nuevoIndice >= cuestionario.getPreguntas().size()) {
                Integer puntajeTotalSesion = (Integer) session.getAttribute("puntajeTotal");
                int monedasCuestionario = (int) Math.floor(puntajeTotalSesion * 0.1);

                servicioJuego.registrarIntento(usuario.getId(), cuestionario.getId(), puntajeTotalSesion);
                servicioJuego.actualizarPuntajeYCrearHistorial(usuario, cuestionario, preguntasCorrectas, preguntasErradas, puntajeTotalSesion);
                servicioJuego.asignarMonedas(usuario, puntajeTotalSesion);

                session.setAttribute("usuario", usuario);

                // limpiar sesión
                session.removeAttribute("puntajeTotal");
                session.removeAttribute("preguntasCorrectas");
                session.removeAttribute("preguntasErradas");
                session.removeAttribute("indicePregunta");
                session.removeAttribute("idCuestionario");
                session.removeAttribute("trampaActivada");

                ModelMap model = new ModelMap();
                model.put("puntajeTotal", puntajeTotalSesion);
                model.put("preguntasCorrectas", preguntasCorrectas);
                model.put("preguntasErradas", preguntasErradas);
                model.put("monedasCuestionario", monedasCuestionario);
                model.put("cuestionario", cuestionario);
                model.put("usuario", usuario);

                return new ModelAndView("final_partida", model);
            }


            return prepararVista(cuestionario, nuevoIndice, timer, false, null, usuario,
                    puntajeTotal, preguntasCorrectas, preguntasErradas, vidasRestantes, false, session);
        }
        session.setAttribute("timer", timer);

        return prepararVista(cuestionario, indicePregunta, timer, false, null, usuario,
                puntajeTotal, preguntasCorrectas, preguntasErradas, vidasRestantes, false, session);
    }


    @RequestMapping("/juego/{idCuestionario}/validar")
    public ModelAndView validarPregunta(@PathVariable("idCuestionario") Long idCuestionario,
                                        @RequestParam Long idPregunta,
                                        @RequestParam String respuesta,
                                        @RequestParam(required = false) String tiempoAgotado,
                                        @RequestParam(required = false) String trampaActivada,
                                        HttpSession session) {
        Cuestionario cuestionario = servicioJuego.obtenerCuestionario(idCuestionario);
        List<Preguntas> preguntasMezcladas = (List<Preguntas>) session.getAttribute("preguntasMezcladas");
        cuestionario.setPreguntas(preguntasMezcladas);
        TimerPregunta timer = (TimerPregunta) session.getAttribute("timer");
        TIPO_ITEMS trampa = (trampaActivada != null && !trampaActivada.isEmpty())
                ? TIPO_ITEMS.valueOf(trampaActivada)
                : null;

        if ("true".equals(tiempoAgotado)) {
            Integer indicePregunta = (Integer) session.getAttribute("indicePregunta");
            Integer puntajeTotal = (Integer) session.getAttribute("puntajeTotal");
            Integer preguntasErradas = (Integer) session.getAttribute("preguntasErradas");
            Integer preguntasCorrectas = (Integer) session.getAttribute("preguntasCorrectas");
            Integer vidasRestantes = (Integer) session.getAttribute("vidasRestantes");
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (indicePregunta == null) indicePregunta = 0;
            if (puntajeTotal == null) puntajeTotal = 0;
            if (preguntasErradas == null) preguntasErradas = 0;
            if (preguntasCorrectas == null) preguntasCorrectas = 0;
            if (vidasRestantes == null) vidasRestantes = cuestionario.getVidas();

            vidasRestantes--;
            preguntasErradas++;

            session.setAttribute("vidasRestantes", vidasRestantes);
            session.setAttribute("preguntasErradas", preguntasErradas);
            session.setAttribute("puntajeTotal", puntajeTotal);
            session.setAttribute("respondida", true);
            session.setAttribute("trampaUsada", trampaActivada);

            if (vidasRestantes <= 0) {
                int monedasCuestionario = (int) Math.floor(puntajeTotal * 0.1);

                servicioJuego.registrarIntento(usuario.getId(), idCuestionario, puntajeTotal);
                servicioJuego.actualizarPuntajeYCrearHistorial(usuario, cuestionario, preguntasCorrectas, preguntasErradas, puntajeTotal);
                servicioJuego.asignarMonedas(usuario, puntajeTotal);

                usuario.setPuntaje(usuario.getPuntaje() + puntajeTotal);
                session.setAttribute("usuario", usuario);

                ModelMap model = new ModelMap();
                model.put("mensaje", "Te quedaste sin vidas");
                model.put("puntajeTotal", puntajeTotal);
                model.put("preguntasCorrectas", preguntasCorrectas);
                model.put("preguntasErradas", preguntasErradas);
                model.put("vidasRestantes", 0);
                model.put("monedasCuestionario", monedasCuestionario);
                model.put("cuestionario", cuestionario);
                model.put("usuario", usuario);

                session.invalidate();
                return new ModelAndView("final_partida", model);
            }
//            session.setAttribute("vidasRestantes", vidasRestantes);
//            session.setAttribute("preguntasErradas", preguntasErradas);
//            session.setAttribute("puntajeTotal", puntajeTotal);
//            session.setAttribute("respondida", false);

            timer.reiniciar();
            session.setAttribute("timer", timer);

            return prepararVista(cuestionario, indicePregunta, timer, true, false, usuario,
                    puntajeTotal, preguntasCorrectas, preguntasErradas, vidasRestantes, true, session);
        }

        Integer indicePregunta = (Integer) session.getAttribute("indicePregunta");
        Integer puntajeTotal = (Integer) session.getAttribute("puntajeTotal");
        Integer preguntasCorrectas = (Integer) session.getAttribute("preguntasCorrectas");
        Integer preguntasErradas = (Integer) session.getAttribute("preguntasErradas");
        Integer vidasRestantes = (Integer) session.getAttribute("vidasRestantes");
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (indicePregunta == null) indicePregunta = 0;
        if (puntajeTotal == null) puntajeTotal = 0;
        if (preguntasCorrectas == null) preguntasCorrectas = 0;
        if (preguntasErradas == null) preguntasErradas = 0;
        if (vidasRestantes == null) vidasRestantes = cuestionario.getVidas();

        boolean esCorrecta = servicioJuego.validarRespuesta(respuesta, idPregunta);

        puntajeTotal = servicioJuego.obtenerPuntajeConTrampa(idPregunta, respuesta, timer, usuario.getId(), trampa);
        servicioJuego.setPuntajeTotal(puntajeTotal);

        Integer puntajePenalizado = servicioJuego.calcularPenalizacion(usuario.getId(), cuestionario.getId(), puntajeTotal);
        puntajeTotal = puntajePenalizado;

        if (esCorrecta) preguntasCorrectas++;
        else {
            preguntasErradas++;
            vidasRestantes--;
        }

        session.setAttribute("indicePregunta", indicePregunta);
        session.setAttribute("puntajeTotal", puntajeTotal);
        session.setAttribute("preguntasCorrectas", preguntasCorrectas);
        session.setAttribute("preguntasErradas", preguntasErradas);
        session.setAttribute("vidasRestantes", vidasRestantes);
        session.setAttribute("respondida", true);
        session.setAttribute("trampaUsada", trampaActivada);

        if (vidasRestantes <= 0) {
            int monedasCuestionario = (int) Math.floor(puntajeTotal * 0.1);

            servicioJuego.registrarIntento(usuario.getId(), idCuestionario, puntajeTotal);
            servicioJuego.actualizarPuntajeYCrearHistorial(usuario, cuestionario, preguntasCorrectas, preguntasErradas, puntajePenalizado);
            servicioJuego.asignarMonedas(usuario, puntajeTotal);

            usuario.setPuntaje(usuario.getPuntaje() + puntajePenalizado);
            session.setAttribute("usuario", usuario);

            ModelMap model = new ModelMap();
            model.put("mensaje", "Te quedaste sin vidas");
            model.put("puntajeTotal", puntajePenalizado);
            model.put("preguntasCorrectas", preguntasCorrectas);
            model.put("preguntasErradas", preguntasErradas);
            model.put("vidasRestantes", 0);
            model.put("monedasCuestionario", monedasCuestionario);
            model.put("cuestionario", cuestionario);
            model.put("usuario", usuario);

            session.invalidate();
            return new ModelAndView("final_partida", model);
        }
        return prepararVista(cuestionario, indicePregunta, timer, true, esCorrecta, usuario,
                puntajeTotal, preguntasCorrectas, preguntasErradas, vidasRestantes, false, session);
    }

//    @RequestMapping("/tiempo-agotado")
//    public ModelAndView tiempoAgotado(@SessionAttribute("idCuestionario") Long idCuestionario) {
//        ModelMap model = new ModelMap();
//        model.put("mensaje", "Se acabo el tiempo de la pregunta");
//        model.put("idCuestionario", idCuestionario);
//        return new ModelAndView("tiempo-agotado", model);
//    }

    @RequestMapping(value = "/juego/opciones-filtradas", method = RequestMethod.POST)
    @ResponseBody
    public List<String> obtenerOpcionesFiltradas(@RequestParam Long idPregunta,
                                                 @RequestParam Long idUsuario,
                                                 @RequestParam String trampaActivada) {
        TIPO_ITEMS trampa = TIPO_ITEMS.valueOf(trampaActivada);
        Preguntas pregunta = servicioPregunta.obtenerPorId(idPregunta);
        return servicioJuego.obtenerOpcionesFiltradas(pregunta, idUsuario, trampa);
    }

    private ModelAndView prepararVista(Cuestionario cuestionario,
                                       Integer indice,
                                       TimerPregunta timer,
                                       Boolean respondida,
                                       Boolean esCorrecta,
                                       Usuario usuario,
                                       Integer puntajeTotal,
                                       Integer preguntasCorrectas,
                                       Integer preguntasErradas,
                                       Integer vidasRestantes,
                                       Boolean tiempoAgotado,
                                       HttpSession session) {

        ModelMap model = new ModelMap();
        Preguntas pregunta = servicioJuego.obtenerPregunta(cuestionario, indice);
        List<Item>ayudasDisponibles=servicioJuego.obtenerTrampasDisponibles(usuario.getId());
        /*List<String> opciones = Arrays.asList(
                pregunta.getRespuestaCorrecta(),
                pregunta.getRespuestaIncorrecta1(),
                pregunta.getRespuestaIncorrecta2(),
                pregunta.getRespuestaIncorrecta3()
        );
        Collections.shuffle(opciones);*/

        String trampaStr = (String) session.getAttribute("trampaUsada");
        TIPO_ITEMS trampaActivada = (trampaStr != null && !trampaStr.isEmpty())
                ? TIPO_ITEMS.valueOf(trampaStr)
                : null;

        List<String> opciones = servicioJuego.obtenerOpcionesFiltradas(pregunta, usuario.getId(), trampaActivada);
        model.put("trampaUsada", trampaStr);
        session.removeAttribute("trampaUsada");

        Long duplicar_puntaje=servicioCompra.contarComprasPorUsuarioYTipo(usuario.getId(), TIPO_ITEMS.DUPLICAR_PUNTAJE);
        Long eliminar_incorrectas=servicioCompra.contarComprasPorUsuarioYTipo(usuario.getId(), TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS);

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
        model.put("vidasRestantes", vidasRestantes);
        model.put("tiempoAgotado", tiempoAgotado);
        model.put("ayudasDisponibles", ayudasDisponibles);
        model.put("duplicar_puntaje", duplicar_puntaje);
        model.put("eliminar_incorrectas", eliminar_incorrectas);

        int monedasCuestionario = (int) Math.floor(puntajeTotal * 0.1);
        model.put("monedas", monedasCuestionario);

        return new ModelAndView("pregunta", model);
    }
}

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

/*
* que se pueda ver la vista,
* que se pueda ver la pregunta con las respuestas,
*
* */
@Controller
@SessionAttributes({"idCuestionario", "puntajeTotal"})
public class ControladorJuego {


    private ServicioJuego servicioJuego;
    private ServicioPregunta servicioPregunta;

    @Autowired
    public ControladorJuego(ServicioJuego servicioJuego,ServicioPregunta servicioPregunta) {
        this.servicioJuego = servicioJuego;
        this.servicioPregunta = servicioPregunta;
    }

    @RequestMapping("/iniciar/{idCuestionario}")
    public ModelAndView iniciar(@PathVariable("idCuestionario") Long idCuestionario){
        ModelMap model = new ModelMap();
        Cuestionario cuestionario= servicioJuego.obtenerCuestionario(idCuestionario);

        if(cuestionario==null){
            model.put("error","No se encontro ningun cuestionario");
            return new ModelAndView("pregunta",model);
        }

        Preguntas pregunta= servicioJuego.obtenerPregunta(cuestionario,0);
        List<String>opciones= Arrays.asList(pregunta.getRespuestaCorrecta(),pregunta.getRespuestaIncorrecta1(),pregunta.getRespuestaIncorrecta2(),pregunta.getRespuestaIncorrecta3());
        Collections.shuffle(opciones);
        servicioJuego.reiniciarPuntaje();

        model.put("cuestionario",cuestionario);
        model.put("pregunta",pregunta);
        model.put("opciones",opciones);
        model.put("idCuestionario",idCuestionario);
        model.put("indicePregunta",0);
        model.put("respondida",false);
        model.put("esCorrecta",null);
        model.put("puntajeTotal",0);

        return new ModelAndView("pregunta",model);
    }

    @RequestMapping("/iniciar")
    public ModelAndView iniciarPorFormulario(@RequestParam("idCuestionario") Long idCuestionario, HttpSession sesion){


            Integer indicePregunta=0;
            sesion.setAttribute("indicePregunta",indicePregunta);
            TimerPregunta timer= new TimerPregunta(10);
            sesion.setAttribute("timer",timer);

            Boolean respondida=(Boolean)sesion.getAttribute("respondida");
            if(respondida==null){
                respondida=false;
            }
            sesion.setAttribute("respondida",respondida);

       // sesion.setAttribute("indicePregunta",0);
        ModelMap model = new ModelMap();
        Cuestionario cuestionario= servicioJuego.obtenerCuestionario(idCuestionario);

        if(cuestionario==null){
            model.put("error","No se encontro ningun cuestionario");
            return new ModelAndView("vista-error-cuestionario",model);
        }

        Preguntas pregunta= servicioJuego.obtenerPregunta(cuestionario,0);
        List<String>opciones= Arrays.asList(pregunta.getRespuestaCorrecta(),pregunta.getRespuestaIncorrecta1(),pregunta.getRespuestaIncorrecta2(),pregunta.getRespuestaIncorrecta3());
        Collections.shuffle(opciones);
        servicioJuego.reiniciarPuntaje();

        model.put("cuestionario",cuestionario);
        model.put("pregunta",pregunta);
        model.put("opciones",opciones);
        model.put("idCuestionario",idCuestionario);
        model.put("indicePregunta",0);
        model.put("respondida",false);
        model.put("esCorrecta",null);
        model.put("puntajeTotal",0);
        model.put("tiempoRestante",timer.segundosRestantes());

        return new ModelAndView("pregunta",model);
    }

    @RequestMapping("/siguiente")
    public ModelAndView siguientePregunta(@SessionAttribute("idCuestionario") Long idCuestionario, HttpSession sesion){
        ModelMap model = new ModelMap();
        Cuestionario cuestionario=servicioJuego.obtenerCuestionario(idCuestionario);
        Integer indiceActual=(Integer)sesion.getAttribute("indicePregunta");
        TimerPregunta timer=(TimerPregunta)sesion.getAttribute("timer");
        Boolean respondida=(Boolean)sesion.getAttribute("respondida");

        //Si la pregunta no fue respondida se mantiene el indice actual
        if(respondida==null || !respondida){

           return prepararVista(cuestionario,indiceActual,timer,false,null);
        }
        //Si si fue respondida se avanza a la siguiente y cambia el estado de respondida
        Integer nuevoIndice= indiceActual+1;
        sesion.setAttribute("indicePregunta",nuevoIndice);
        sesion.setAttribute("respondida",false);
        if(timer!=null){
            timer.reiniciar();
        }else{
            timer=new TimerPregunta(10);
            sesion.setAttribute("timer",timer);
        }

        if(nuevoIndice<cuestionario.getPreguntas().size()){
            return prepararVista(cuestionario,nuevoIndice,timer,false,null);
        }
        return new ModelAndView("final_partida",model);
    }

    @RequestMapping("/juego/{idCuestionario}/validar")
    public ModelAndView validarPregunta(@PathVariable("idCuestionario") Long idCuestionario,@RequestParam Long idPregunta,@RequestParam String respuesta,@SessionAttribute("puntajeTotal") Integer puntajeTotal, HttpSession sesion){
            ModelMap model = new ModelMap();
            Cuestionario cuestionario=servicioJuego.obtenerCuestionario(idCuestionario);
            Preguntas pregunta= servicioPregunta.obtenerPorId(idPregunta);
            boolean esCorrecta=servicioJuego.validarRespuesta(respuesta,idPregunta);
            puntajeTotal=servicioJuego.obtenerPuntaje(idPregunta,respuesta);

            TimerPregunta timer= (TimerPregunta) sesion.getAttribute("timer");
            Integer indicePregunta=(Integer)sesion.getAttribute("indicePregunta");
            if(timer==null || timer.tiempoAgotado()){
                model.put("mensaje","Tiempo agotado");
                return new ModelAndView("tiempo-agotado",model);
            }
            sesion.setAttribute("respondida",true);

            model.put("pregunta",pregunta);
            model.put("indicePregunta",indicePregunta);
            model.put("esCorrecta",esCorrecta);
            model.put("respondida",true);
            model.put("puntajeTotal",puntajeTotal);

            return new ModelAndView("pregunta",model);
    }

    @RequestMapping("/tiempo-agotado")
    public ModelAndView tiempoAgotado(@SessionAttribute("idCuestionario") Long idCuestionario) {
        ModelMap model = new ModelMap();
        model.put("mensaje", "Se acabó el tiempo de la pregunta.");
        model.put("idCuestionario", idCuestionario); // si querés reiniciar
        return new ModelAndView("tiempo-agotado", model);
    }

    private ModelAndView prepararVista(Cuestionario cuestionario,Integer indice, TimerPregunta timer,Boolean respondida,Boolean esCorrecta){
        ModelMap model = new ModelMap();
        Preguntas pregunta=servicioJuego.obtenerPregunta(cuestionario,indice);
        List<String>opciones=Arrays.asList(pregunta.getRespuestaCorrecta(),pregunta.getRespuestaIncorrecta1(),pregunta.getRespuestaIncorrecta2(),pregunta.getRespuestaIncorrecta3());
        Collections.shuffle(opciones);
        model.put("cuestionario",cuestionario);
        model.put("pregunta",pregunta);
        model.put("indicePregunta",indice);
        model.put("opciones",opciones);
        model.put("respondida",respondida);
        model.put("esCorrecta",null);
        model.put("tiempoRestante",timer.segundosRestantes());
        return new ModelAndView("pregunta",model);

    }
}

package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
@SessionAttributes({"idCuestionario", "indicePregunta"})
public class ControladorJuego {

    @Autowired
    private ServicioJuego servicioJuego;

    @Autowired
    public ControladorJuego(ServicioJuego servicioJuego){
        this.servicioJuego = servicioJuego;
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
        System.out.println("Pregunta obtenida: " + pregunta);

        model.put("cuestionario",cuestionario);
        model.put("pregunta",pregunta);
        model.put("opciones",opciones);
        model.put("idCuestionario",idCuestionario);
        model.put("indicePregunta",0);

        return new ModelAndView("pregunta",model);
    }

    @RequestMapping("/iniciar")
    public ModelAndView iniciarPorFormulario(@RequestParam("idCuestionario") Long idCuestionario){
        ModelMap model = new ModelMap();
        Cuestionario cuestionario= servicioJuego.obtenerCuestionario(idCuestionario);

        if(cuestionario==null){
            model.put("error","No se encontro ningun cuestionario");
            return new ModelAndView("pregunta",model);
        }

        Preguntas pregunta= servicioJuego.obtenerPregunta(cuestionario,0);
        List<String>opciones= Arrays.asList(pregunta.getRespuestaCorrecta(),pregunta.getRespuestaIncorrecta1(),pregunta.getRespuestaIncorrecta2(),pregunta.getRespuestaIncorrecta3());
        Collections.shuffle(opciones);
        System.out.println("Pregunta obtenida: " + pregunta);

        model.put("cuestionario",cuestionario);
        model.put("pregunta",pregunta);
        model.put("opciones",opciones);
        model.put("idCuestionario",idCuestionario);
        model.put("indicePregunta",0);

        return new ModelAndView("pregunta",model);
    }

    @RequestMapping("/siguiente")
    public ModelAndView siguientePregunta(@SessionAttribute("indicePregunta") Integer indicePregunta, @SessionAttribute("idCuestionario") Long idCuestionario){
        ModelMap model = new ModelMap();
        Integer nuevoIndice= indicePregunta+1;
        Cuestionario cuestionario=servicioJuego.obtenerCuestionario(idCuestionario);

        if(nuevoIndice<cuestionario.getPreguntas().size()){
            model.put("cuestionario",cuestionario);
            Preguntas pregunta= servicioJuego.obtenerPregunta(cuestionario,nuevoIndice);
            model.put("pregunta", pregunta);
            List<String>opciones= Arrays.asList(pregunta.getRespuestaCorrecta(),pregunta.getRespuestaIncorrecta1(),pregunta.getRespuestaIncorrecta2(),pregunta.getRespuestaIncorrecta3());
            Collections.shuffle(opciones);
            model.put("opciones",opciones);
            model.put("indicePregunta",nuevoIndice);
            return new ModelAndView("pregunta",model);
        }
        return new ModelAndView("home");
    }
}

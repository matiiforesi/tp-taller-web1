package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.Preguntas;
import com.tallerwebi.dominio.RepositorioCuestionario;
import com.tallerwebi.dominio.ServicioCuestionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/*
* que se pueda ver la vista,
* que se pueda ver la pregunta con las respuestas,
*
* */
@Controller
public class ControladorJuego {

    @Autowired
    private ServicioCuestionario servicioCuestionario;

    @Autowired
    public ControladorJuego(ServicioCuestionario servicioCuestionario){
        this.servicioCuestionario=servicioCuestionario;
    }

    @RequestMapping("/iniciar/{idCuestionario}")
    public ModelAndView iniciar(@PathVariable("idCuestionario") Long idCuestionario){
        ModelMap model = new ModelMap();
        Cuestionario cuestionario= servicioCuestionario.buscar(idCuestionario);

        if(cuestionario==null){
            model.put("error","No se encontro ningun cuestionario");
            return new ModelAndView("pregunta",model);
        }

        Preguntas pregunta= cuestionario.getPreguntas().get(0);

        model.put("pregunta",pregunta);

        return new ModelAndView("pregunta",model);
    }
}

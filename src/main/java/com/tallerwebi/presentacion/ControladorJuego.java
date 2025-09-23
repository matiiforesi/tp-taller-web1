package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.Preguntas;
import com.tallerwebi.dominio.RepositorioCuestionario;
import com.tallerwebi.dominio.ServicioCuestionario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/*
* que se pueda ver la vista,
* que se pueda ver la pregunta con las respuestas,
*
* */
@Controller
public class ControladorJuego {

    private ServicioCuestionario servicioCuestionario;

    public ControladorJuego(){}
    public ControladorJuego(ServicioCuestionario servicioCuestionario){
        this.servicioCuestionario=servicioCuestionario;
    }

    @RequestMapping("/iniciar/{idCuestionario}")
    public ModelAndView iniciar(@RequestParam Long idCuestionario){
        ModelMap model = new ModelMap();
        Cuestionario cuestionario= servicioCuestionario.buscar(idCuestionario);
        Preguntas pregunta= cuestionario.getPreguntas().get(0);
        model.put("preguntas",pregunta);
        return new ModelAndView("pregunta",model);
    }
}

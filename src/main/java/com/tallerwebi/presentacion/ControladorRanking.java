package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ControladorRanking {

    private ServicioRanking servicioRanking;

    @Autowired
    public ControladorRanking(ServicioRanking servicioRanking) {
        this.servicioRanking = servicioRanking;
    }

    @RequestMapping("/ranking")
    public ModelAndView mostrarRankingGeneral() {
        List<Usuario> ranking = servicioRanking.obtenerRankingGeneral();
        ModelMap model = new ModelMap();
        model.addAttribute("rankingGeneral", ranking);
        return new ModelAndView("ranking", model);
    }

    @RequestMapping("/rankingCuestionario")
    public ModelAndView mostrarRankingCuestionario(
            @RequestParam(value = "nombreCuestionario", required = false) String nombreCuestionario,
            @RequestParam(value = "idCuestionario", required = false) Long idCuestionario) {

        List<Object[]> ranking;
//        List<HistorialCuestionario> ranking;

        if (nombreCuestionario != null) {
            ranking = servicioRanking.obtenerRankingCuestionarioAgregadoPorNombre(nombreCuestionario);
        } else if (idCuestionario != null) {
            ranking = servicioRanking.obtenerRankingCuestionarioAgregadoPorId(idCuestionario);
        } else {
            ranking = List.of();
        }

//        if (idCuestionario != null) {
//            ranking = servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario);
//            if (!ranking.isEmpty()) {
//                nombreCuestionario = ranking.get(0).getNombreCuestionario();
//            }
//        } else if (nombreCuestionario != null) {
//            ranking = servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario);
//        } else {
//            ranking = List.of();
//        }

        ModelMap model = new ModelMap();
        model.addAttribute("rankingCuestionario", ranking);
        model.addAttribute("nombreCuestionario", nombreCuestionario);
        model.addAttribute("idCuestionario", idCuestionario);

        return new ModelAndView("ranking", model);
    }
}

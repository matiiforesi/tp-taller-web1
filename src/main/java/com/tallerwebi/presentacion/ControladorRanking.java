package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.HistorialCuestionario;
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

//        if (ranking.isEmpty()) {
//            Usuario usuario1 = new Usuario();
//            Usuario usuario2 = new Usuario();
//            Usuario usuario3 = new Usuario();
//            usuario1.setNombre("Mateo");
//            usuario2.setNombre("Juan");
//            usuario3.setNombre("Matias");
//            usuario1.setPuntaje(100L);
//            usuario2.setPuntaje(70L);
//            usuario3.setPuntaje(50L);
//            ranking = List.of(usuario1, usuario2, usuario3);
//        }

        ModelMap model = new ModelMap();
        model.addAttribute("rankingGeneral", ranking);
        return new ModelAndView("ranking", model);
    }

    @RequestMapping("/rankingCuestionario")
    public ModelAndView mostrarRankingCuestionario(
            @RequestParam(value = "nombreCuestionario", required = false) String nombreCuestionario,
            @RequestParam(value = "idCuestionario", required = false) Long idCuestionario) {

        List<HistorialCuestionario> ranking;

        if (idCuestionario != null) {
            ranking = servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario);
            if (!ranking.isEmpty()) {
                nombreCuestionario = ranking.get(0).getNombreCuestionario();
            }
        } else if (nombreCuestionario != null) {
            ranking = servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario);
        } else {
            ranking = List.of();
        }

//        if (ranking.isEmpty()) {
//            HistorialCuestionario h1 = new HistorialCuestionario();
//            HistorialCuestionario h2 = new HistorialCuestionario();
//            HistorialCuestionario h3 = new HistorialCuestionario();
//            h1.setNombreUsuario("Mateo");
//            h2.setNombreUsuario("Juan");
//            h3.setNombreUsuario("Matias");
//            h1.setId(1L);
//            h2.setId(1L);
//            h3.setId(1L);
//            h1.setNombreCuestionario("Historia");
//            h2.setNombreCuestionario("Historia");
//            h3.setNombreCuestionario("Historia");
//            h1.setPuntaje(100L);
//            h2.setPuntaje(70L);
//            h3.setPuntaje(50L);
//            h1.setPreguntasCorrectas(10);
//            h2.setPreguntasCorrectas(7);
//            h3.setPreguntasCorrectas(5);
//            h1.setPreguntasErradas(0);
//            h2.setPreguntasErradas(3);
//            h3.setPreguntasErradas(5);
//            ranking = List.of(h1, h2, h3);
//        }

        ModelMap model = new ModelMap();
        model.addAttribute("rankingCuestionario", ranking);
        model.addAttribute("idCuestionario", idCuestionario);
        model.addAttribute("nombreCuestionario", nombreCuestionario);

        return new ModelAndView("ranking", model);
    }
}

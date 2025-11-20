package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class ControladorRanking {

    private final ServicioRanking servicioRanking;

    @Autowired
    public ControladorRanking(ServicioRanking servicioRanking) {
        this.servicioRanking = servicioRanking;
    }

    @RequestMapping("/ranking")
    public ModelAndView mostrarRankingGeneral(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        List<RankingGeneralDTO> rankingGeneral = servicioRanking.obtenerRankingGeneral();

        ModelMap model = new ModelMap();
        model.addAttribute("rankingGeneral", rankingGeneral);

        model.addAttribute("nombre", usuario.getNombre());
        model.addAttribute("puntaje", usuario.getPuntaje());
        model.addAttribute("monedas", usuario.getMonedas());

        return new ModelAndView("ranking", model);
    }

    @RequestMapping("/rankingCuestionario")
    public ModelAndView mostrarRankingCuestionario(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        List<RankingCuestionarioDTO> ranking = Collections.emptyList();
        String nombreCuestionario = null;
        Long idCuestionario = null;

        if (busqueda != null && !busqueda.isBlank()) {
            busqueda = busqueda.trim();
            try {
                // Si es nro, busca por ID
                idCuestionario = Long.parseLong(busqueda);
                ranking = servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario);
            } catch (NumberFormatException e) {
                // Si no es nro, busca por nombre
                nombreCuestionario = busqueda;
                ranking = servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario);
            }
        }

        if (nombreCuestionario == null && !ranking.isEmpty()) {
            nombreCuestionario = ranking.get(0).getNombreCuestionario();
        }

        ModelMap model = new ModelMap();
        model.addAttribute("rankingCuestionario", ranking);
        model.addAttribute("nombreCuestionario", nombreCuestionario);
        model.addAttribute("idCuestionario", idCuestionario);

        model.addAttribute("nombre", usuario.getNombre());
        model.addAttribute("puntaje", usuario.getPuntaje());
        model.addAttribute("monedas", usuario.getMonedas());

        return new ModelAndView("ranking", model);
    }
}

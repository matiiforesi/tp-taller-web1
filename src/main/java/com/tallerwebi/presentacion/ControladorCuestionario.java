package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.ServicioCuestionario;
import com.tallerwebi.dominio.ServicioTrivia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControladorCuestionario {

    private final ServicioCuestionario servicioCuestionario;
    private final ServicioTrivia servicioTrivia;

    @Autowired
    public ControladorCuestionario(ServicioCuestionario servicioCuestionario, ServicioTrivia servicioTrivia) {
        this.servicioCuestionario = servicioCuestionario;
        this.servicioTrivia = servicioTrivia;
    }

    @GetMapping("/cuestionario/list")
    public String listTrivias(Model model) {
        model.addAttribute("trivias", servicioCuestionario.buscarTodo());

        return "cuestionario_list";
    }

    @GetMapping("/cuestionario/new")
    public String mostrarForm(Model model) {
        model.addAttribute("cuestionario", new Cuestionario());

        var respuestaCategorias = servicioTrivia.obtenerCategorias();
        if (respuestaCategorias != null && respuestaCategorias.getTriviaCategories() != null) {
            model.addAttribute("triviaCategories", respuestaCategorias.getTriviaCategories());
        }

        return "cuestionario_form";
    }

    @PostMapping("/cuestionario/new")
    public String createTrivia(
            @ModelAttribute Cuestionario cuestionario,
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(defaultValue = "25") int category,
            @RequestParam(defaultValue = "easy") String difficulty) {
        servicioCuestionario.crearCuestionario(
                cuestionario.getNombre(),
                cuestionario.getDescripcion(),
                amount,
                category,
                difficulty
        );

        return "redirect:/cuestionario/list";
    }
}

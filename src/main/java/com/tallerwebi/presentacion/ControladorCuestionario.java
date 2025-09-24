package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.ServicioCuestionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControladorCuestionario {

    private final ServicioCuestionario servicioCuestionario;

    @Autowired
    public ControladorCuestionario(ServicioCuestionario servicioCuestionario) {
        this.servicioCuestionario = servicioCuestionario;
    }

    @GetMapping("/cuestionario/list")
    public String listTrivias(Model model) {
        model.addAttribute("trivias", servicioCuestionario.buscarTodo());
        return "cuestionario_list";
    }

    @GetMapping("/cuestionario/new")
    public String mostrarForm(Model model) {
        model.addAttribute("cuestionario", new Cuestionario());
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

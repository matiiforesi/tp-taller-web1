package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorTienda {

    private final ServicioCompra servicioCompra;
    private final ServicioItem servicioItem;
    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ControladorTienda(ServicioCompra servicioCompra, ServicioItem servicioItem, RepositorioUsuario repositorioUsuario) {
        this.servicioCompra = servicioCompra;
        this.servicioItem = servicioItem;
        this.repositorioUsuario = repositorioUsuario;
    }

    @RequestMapping("/tienda")
    public ModelAndView mostrarTienda(HttpSession session) {
        ModelMap model = new ModelMap();
        List<Item> items = servicioItem.obtenerTodos();

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return new ModelAndView("redirect:/login");
        }

        Long idUsuario = usuario.getId();
        session.setAttribute("idUsuario", idUsuario);

        Long duplicar_puntaje = servicioCompra.contarComprasPorUsuarioYTipo(idUsuario, TIPO_ITEMS.DUPLICAR_PUNTAJE);
        Long eliminar_incorrectas = servicioCompra.contarComprasPorUsuarioYTipo(idUsuario, TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS);

        model.put("items", items);
        model.put("usuario", usuario);
        model.put("duplicar_puntaje", duplicar_puntaje);
        model.put("eliminar_incorrectas", eliminar_incorrectas);

        model.put("nombre", usuario.getNombre());
        model.put("puntaje", usuario.getPuntaje());
        model.put("monedas", usuario.getMonedas());

        return new ModelAndView("tienda", model);
    }

    @RequestMapping("/comprar")
    public ModelAndView mostrarCompra(HttpSession session,
                                      @RequestParam Long idUsuario,
                                      @RequestParam Long idItem,
                                      RedirectAttributes redirectAttributes) {
        Boolean exito = servicioCompra.comprarItem(idUsuario, idItem);
        Usuario usuarioActualizado = servicioCompra.obtenerUsuarioActualizado(idUsuario);

        session.setAttribute("usuario", usuarioActualizado);

        if (exito) {
            redirectAttributes.addFlashAttribute("mensaje", "¡Compra exitosa!");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Monedas insuficientes");
        }
        return new ModelAndView("redirect:/tienda");
    }
}

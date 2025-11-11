package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
        List<Item> items= servicioItem.obtenerTodos();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.put("items", items);
        model.put("usuario", usuario);

        return new  ModelAndView("tienda",model);
    }
    @RequestMapping("/comprar")
    public ModelAndView mostrarCompra(HttpSession session, @RequestParam Long idUsuario,@RequestParam Long idItem) {
        ModelMap model = new ModelMap();
        /*Usuario usuarioActualizado= servicioCompra.obtenerUsuarioActualizado(idUsuario);
        session.setAttribute("usuario",usuarioActualizado);*/
       // Usuario usuario = (Usuario) session.getAttribute("usuario");
        Boolean exito=servicioCompra.comprarItem(idUsuario,idItem);
        if(exito){
            model.put("mensaje","¡Compra exitosa!");
        }else{
            model.put("mensaje","No tienes suficientes monedas");
        }
        return new ModelAndView("redirect:/tienda",model);
    }
}

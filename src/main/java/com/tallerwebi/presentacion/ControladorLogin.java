package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorLogin {

    private ServicioLogin servicioLogin;
    @Autowired
    private ServicioCuestionario servicioCuestionario;
    @Autowired
    private ServicioAdmin servicioAdmin;
    @Autowired
    private ServicioDificultad servicioDificultad;

    @Autowired
    public ControladorLogin(ServicioLogin servicioLogin) {
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin() {
        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLogin datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        Usuario usuarioBuscado = servicioLogin.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
        if (usuarioBuscado != null) {
            request.getSession().setAttribute("usuario", usuarioBuscado);
            request.getSession().setAttribute("ROL", usuarioBuscado.getRol());

            System.out.println("Usuario logueado: " + usuarioBuscado.getNombre());
            return new ModelAndView("redirect:/home");
        } else {
            model.put("error", "Usuario o clave incorrecta");
        }

        return new ModelAndView("login", model);
    }


    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario) {
        ModelMap model = new ModelMap();

        try {
            servicioLogin.registrar(usuario);
        } catch (UsuarioExistente e) {
            model.put("error", "El usuario ya existe");
            return new ModelAndView("nuevo-usuario", model);
        } catch (Exception e) {
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("nuevo-usuario", model);
        }

        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();
        model.put("usuario", new Usuario());
        return new ModelAndView("nuevo-usuario", model);
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome(
            HttpServletRequest request,
            @RequestParam(required = false) String dificultad,
            @RequestParam(required = false) String categoria) {

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute("usuario") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelMap model = new ModelMap();
        Usuario usuarioEncontrado = (Usuario) request.getSession().getAttribute("usuario");
        String rol = usuarioEncontrado.getRol();
        Integer cantidadUsuarios = servicioAdmin.contarUsuarios();
        Integer cantidadCuestionarios = servicioAdmin.contarCuestionarios();

        List<Dificultad> dificultades = servicioDificultad.obtenerTodas();
        List<String> categorias = servicioCuestionario.obtenerTodasLasCategorias();

        List<Cuestionario> cuestionariosSugeridos = servicioCuestionario.obtenerCuestionariosSugeridos(6);

        List<Cuestionario> cuestionariosFiltrados = servicioCuestionario.obtenerCuestionariosFiltrados(request, dificultad, categoria);

        model.put("usuario", usuarioEncontrado);
        model.put("cuestionarios", cuestionariosSugeridos);
        model.put("cuestionariosFiltrados", cuestionariosFiltrados);
        model.put("rol", rol);
        model.put("nombre", usuarioEncontrado.getNombre());
        model.put("puntaje", usuarioEncontrado.getPuntaje());
        model.put("cantidadUsuarios", cantidadUsuarios);
        model.put("cantidadCuestionarios", cantidadCuestionarios);
        model.put("dificultades", dificultades);
        model.put("categorias", categorias);
        model.put("monedas", usuarioEncontrado.getMonedas());

        return new ModelAndView("home", model);
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio() {
        return new ModelAndView("redirect:/login");
    }
}

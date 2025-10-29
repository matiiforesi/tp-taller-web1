package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorJuegoTest {

    private RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
    private RepositorioHistorial repoHistorial = mock(RepositorioHistorial.class);
    private RepositorioIntento repoIntento = mock(RepositorioIntento.class);
    private ServicioCuestionario servCuestionario = mock(ServicioCuestionario.class);
    private ServicioPregunta servPregunta = mock(ServicioPregunta.class);
    private ServicioDificultad servDificultad = mock(ServicioDificultad.class);

    private ServicioJuegoImpl servJuego = new ServicioJuegoImpl(repoUsuario, repoHistorial, repoIntento, servCuestionario, servPregunta, servDificultad);

    private ControladorJuego controladorJuego = new ControladorJuego(servJuego, servPregunta, servCuestionario);

    private Preguntas pregunta;
    private Cuestionario cuestionario = new Cuestionario();

    @BeforeEach
    public void setUp() {
        pregunta = new Preguntas();
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        Dificultad dificultad = new Dificultad();
        dificultad.setNombre("Facil");
        pregunta.setDificultad(dificultad);
        pregunta.setRespuestaCorrecta("25 de Mayo");
        pregunta.setRespuestaIncorrecta1("23 de Abril");
        pregunta.setRespuestaIncorrecta2("24 de Junio");
        pregunta.setRespuestaIncorrecta3("25 de Julio");

        cuestionario = new Cuestionario();
        cuestionario.setId(3L);
        cuestionario.setPreguntas(Arrays.asList(pregunta, pregunta));
    }

    @Test
    public void queDevuelvaLaPreguntaConSusOpciones() {
        //preparacion
        givenCuestionario();
        //ejecucion
        ModelAndView vista = whenCuestionarioDevuelvaLaPregunta();
        //validacion
        thenCuestionario(vista);
    }

    @Test
    public void siNoEncuentraElCuestionarioMuestraError() {
        Cuestionario ejem = new Cuestionario();
        HttpSession sesion = mock(HttpSession.class);
        ejem.setId(1L);
        ModelAndView mav = controladorJuego.iniciarPorFormulario(1L, sesion);
        assertEquals("vista-error-cuestionario", mav.getViewName());
    }

    public void givenCuestionario() {
        when(servJuego.obtenerCuestionario(3L)).thenReturn(cuestionario);
    }

    public ModelAndView whenCuestionarioDevuelvaLaPregunta() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("usuario")).thenReturn(usuario);

        return controladorJuego.iniciarPorFormulario(3L, session);
    }

    public void thenCuestionario(ModelAndView vista) {
        assertThat(vista.getViewName(), equalToIgnoringCase("pregunta"));
        Preguntas model = (Preguntas) vista.getModel().get("pregunta");

        assertThat(model.getEnunciado(), equalToIgnoringCase("¿Cuando fue la revolucion de Mayo?"));
        assertThat(model.getRespuestaCorrecta(), equalToIgnoringCase("25 de Mayo"));
        assertThat(model.getRespuestaIncorrecta1(), equalToIgnoringCase("23 de Abril"));
        assertThat(model.getRespuestaIncorrecta2(), equalToIgnoringCase("24 de Junio"));
        assertThat(model.getRespuestaIncorrecta3(), equalToIgnoringCase("25 de Julio"));
    }

    @Test
    public void queUsuarioSeAgregueAlSessionAlIniciarCuestionario() {
        givenCuestionario();

        HttpSession session = mock(HttpSession.class);
        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView mav = whenCuestionarioIniciaConUsuario(3L, session);
        thenUsuarioEstaEnSession(session, usuario);
        assertThat(mav.getViewName(), equalToIgnoringCase("pregunta"));
    }

    public ModelAndView whenCuestionarioIniciaConUsuario(Long id, HttpSession session) {
        return controladorJuego.iniciarPorFormulario(id, session);
    }

    public void thenUsuarioEstaEnSession(HttpSession session, Usuario usuario) {
        verify(session, times(1)).getAttribute("usuario");
    }

    @Test
    public void siElCuestionarioNoTienePreguntasMuestraError() {
        givenCuestionarioVacio();

        HttpSession session = mock(HttpSession.class);
        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView mav = controladorJuego.iniciarPorFormulario(4L, session);
        assertEquals("vista-error-cuestionario", mav.getViewName());
    }

    public void givenCuestionarioVacio() {
        Cuestionario cuestionarioVacio = new Cuestionario();
        cuestionarioVacio.setId(4L);
        when(servJuego.obtenerCuestionario(4L)).thenReturn(cuestionarioVacio);
    }

    @Test
    public void siNoHayUnUsuarioEnSessionParaIniciarCuestionarioRedirijeALogin() {
        givenCuestionario();

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView mav = controladorJuego.iniciarPorFormulario(3L, session);
        assertEquals("redirect:/login", mav.getViewName());
    }
}

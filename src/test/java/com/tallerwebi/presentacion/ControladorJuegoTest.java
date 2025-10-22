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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControladorJuegoTest {

    private RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
    private RepositorioHistorial repoHistorial = mock(RepositorioHistorial.class);
    private ServicioCuestionario servCuestionario = mock(ServicioCuestionario.class);
    private ServicioPregunta servPregunta = mock(ServicioPregunta.class);
    private ServicioDificultad servDificultad = mock(ServicioDificultad.class);

    private ServicioJuegoImpl servJuego = new ServicioJuegoImpl(repoUsuario, repoHistorial, servCuestionario, servPregunta, servDificultad);

    private ControladorJuego controladorJuego = new ControladorJuego(servJuego, servPregunta);

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
        pregunta.setRespuestaCorrecta("25 de mayo");
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
    public void siNoEncuentraElCuestionario() {

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
        assertThat(model.getRespuestaCorrecta(), equalToIgnoringCase("25 de mayo"));
        assertThat(model.getRespuestaIncorrecta1(), equalToIgnoringCase("23 de Abril"));
        assertThat(model.getRespuestaIncorrecta2(), equalToIgnoringCase("24 de Junio"));
        assertThat(model.getRespuestaIncorrecta3(), equalToIgnoringCase("25 de Julio"));
    }

}

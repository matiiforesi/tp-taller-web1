package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorJuegoTest {

    private ServicioCuestionario servicioMock= mock(ServicioCuestionario.class);
    private ServicioPregunta servicioPregunta= mock(ServicioPregunta.class);
    private ServicioJuegoImpl servicioJuego= new ServicioJuegoImpl(servicioMock,servicioPregunta);
    private ControladorJuego controladorJuego = new ControladorJuego(servicioJuego,servicioPregunta);
    private Preguntas pregunta;
    private Cuestionario cuestionario=new Cuestionario();

    @BeforeEach
    public void setUp(){
        pregunta= new Preguntas();
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        pregunta.setDificultad("Facil");
        pregunta.setRespuestaCorrecta("25 de mayo");
        pregunta.setRespuestaIncorrecta1("23 de Abril");
        pregunta.setRespuestaIncorrecta2("24 de Junio");
        pregunta.setRespuestaIncorrecta3("25 de Julio");

        cuestionario= new Cuestionario();
        cuestionario.setId(3L);
        cuestionario.setPreguntas(Arrays.asList(pregunta,pregunta));
    }

    @Test
    public void queDevuelvaLaPreguntaConSusOpciones(){
        //preparacion
       givenCuestionario();
        //ejecucion
        ModelAndView vista=whenCuestionarioDevuelvaLaPregunta();
        //validacion
        thenCuestionario(vista);
    }

    @Test
    public void siNoEncuentraElCuestionario(){

        Cuestionario ejem=new Cuestionario();
        HttpSession sesion= mock(HttpSession.class);
        ejem.setId(1L);
        ModelAndView mav= controladorJuego.iniciarPorFormulario(1L, sesion);
        assertEquals("vista-error-cuestionario",mav.getViewName());
    }


    public void givenCuestionario(){
        Mockito.when(servicioJuego.obtenerCuestionario(3L)).thenReturn(cuestionario);
    }
    public ModelAndView whenCuestionarioDevuelvaLaPregunta(){
        return controladorJuego.iniciar(3L);
    }

    public void thenCuestionario(ModelAndView vista){
        //Devuelve la vista y el modelo
        assertThat(vista.getViewName(), equalToIgnoringCase("pregunta"));
        Preguntas model=(Preguntas)vista.getModel().get("pregunta");

        assertThat(model.getEnunciado(),equalToIgnoringCase("¿Cuando fue la revolucion de Mayo?"));
        assertThat(model.getRespuestaCorrecta(),equalToIgnoringCase("25 de mayo"));
        assertThat(model.getRespuestaIncorrecta1(),equalToIgnoringCase("23 de Abril"));
        assertThat(model.getRespuestaIncorrecta2(),equalToIgnoringCase("24 de Junio"));
        assertThat(model.getRespuestaIncorrecta3(),equalToIgnoringCase("25 de Julio"));
    }

}

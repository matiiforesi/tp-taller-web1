package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

public class ControladorJuegoTest {

    private ServicioCuestionario servicioMock= mock(ServicioCuestionario.class);
    private ServicioJuegoImpl servicioJuego= new ServicioJuegoImpl(servicioMock);
    private ControladorJuego controladorJuego = new ControladorJuego(servicioJuego);

    @Test
    public void queDevuelvaLaPreguntaConSusOpciones(){
        //preparacion
       givenCuestionario();
        //ejecucion
        ModelAndView vista=whenCuestionarioDevuelvaLaPregunta();
        //validacion
        thenCuestionario(vista);
    }
    public void givenCuestionario(){
        Preguntas pregunta = new Preguntas();
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        pregunta.setDificultad("Facil");
        pregunta.setRespuestaCorrecta("25 de mayo");
        pregunta.setRespuestaIncorrecta1("23 de Abril");
        pregunta.setRespuestaIncorrecta2("24 de Junio");
        pregunta.setRespuestaIncorrecta3("25 de Julio");

        Cuestionario ejemplo= new Cuestionario();
        ejemplo.setPreguntas(Arrays.asList(pregunta));
        Mockito.when(servicioJuego.obtenerCuestionario(3L)).thenReturn(ejemplo);

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

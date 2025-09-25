package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ServicioJuegoTest {
/* Que se pueda validar la respuesta
* Que Te devuelva si es incorrecta*/

    private ServicioCuestionario serv= mock(ServicioCuestionario.class);
    private ServicioJuegoImpl servicioJuego= new ServicioJuegoImpl(serv);
    @Test
    public void queSeValideLaRespuestaSiEsCorrecta(){

        givenCreacionPreguntas();
        Boolean obtenido= whenSeValideLaRespuestaSiEsCorrecta();
        thenValideLaRespuesta(obtenido);
    }
    @Test
    public void queSeValideLaRespuestaSiEsIncorrecta(){
        givenCreacionPreguntas();
        Boolean obtenido=whenSeValideLaRespuestaIncorrecta();
        thenSeValideRespuestaIncorrecta(obtenido);
    }
    @Test
    public void queAcumulePuntos(){

        givenCreacionPreguntas();
        Integer obtenido=whenAcumulaPuntaje("25 de mayo");
        thenAcumulaPuntaje(200,obtenido);
    }

    @Test
    public void queNoSumePuntos(){
        givenCreacionPreguntas();
        Integer obtenido= whenAcumulaPuntaje("23 de abril");
        thenAcumulaPuntaje(0,obtenido);
    }
    private void thenAcumulaPuntaje(Integer esperado,Integer puntaje){
        assertEquals(esperado,puntaje);
    }

    private Integer whenAcumulaPuntaje(String respuesta){
            return servicioJuego.obtenerPuntaje(2L, 1L, respuesta);
    }

    private Boolean whenSeValideLaRespuestaIncorrecta() {
        return servicioJuego.validarRespuesta("23 de Abril",2L);
    }
    private void thenSeValideRespuestaIncorrecta(Boolean obtenido) {
        assertEquals(Boolean.FALSE, obtenido);
    }

    private void givenCreacionPreguntas(){
        Preguntas pregunta = new Preguntas();
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        pregunta.setDificultad("Facil");
        pregunta.setRespuestaCorrecta("25 de mayo");
        pregunta.setRespuestaIncorrecta1("23 de Abril");
        pregunta.setRespuestaIncorrecta2("24 de Junio");
        pregunta.setRespuestaIncorrecta3("25 de Julio");

        Cuestionario c=new Cuestionario();
        c.setPreguntas(Arrays.asList(pregunta));

        Mockito.when(serv.buscar(2L)).thenReturn(c);
    }

    private Boolean whenSeValideLaRespuestaSiEsCorrecta(){
        return servicioJuego.validarRespuesta("25 de mayo",2L);
    }

    private void thenValideLaRespuesta(Boolean respuesta){
        assertEquals(Boolean.TRUE,respuesta);
    }
}

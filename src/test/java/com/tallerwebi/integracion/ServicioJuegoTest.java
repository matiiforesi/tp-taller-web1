package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ServicioJuegoTest {
    // Que se pueda validar la respuesta
    // Que te devuelva si es incorrecta
    private RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
    private RepositorioHistorial repoHistorial = mock(RepositorioHistorial.class);
    private ServicioCuestionario servCuestionario = mock(ServicioCuestionario.class);
    private ServicioPregunta servPregunta = mock(ServicioPregunta.class);
    private ServicioDificultad servDificultad = mock(ServicioDificultad.class);

    private ServicioJuego servicioJuego = new ServicioJuegoImpl(repoUsuario, repoHistorial, servCuestionario, servPregunta, servDificultad);

    @Test
    public void queSeValideLaRespuestaSiEsCorrecta() {
        givenCreacionPreguntas();
        Boolean obtenido = whenSeValideLaRespuestaSiEsCorrecta();
        thenValideLaRespuesta(obtenido);
    }

    @Test
    public void queSeValideLaRespuestaSiEsIncorrecta() {
        givenCreacionPreguntas();
        Boolean obtenido = whenSeValideLaRespuestaIncorrecta();
        thenSeValideRespuestaIncorrecta(obtenido);
    }

    @Test
    public void queObtengaPuntos() {
        givenCreacionPreguntas();
        Integer obtenido = whenAcumulaPuntaje("25 de mayo");
        thenAcumulaPuntaje(600, obtenido);
    }

    @Test
    public void queNoSumePuntos() {
        givenCreacionPreguntas();
        Integer obtenido = whenAcumulaPuntaje("23 de abril");
        thenAcumulaPuntaje(0, obtenido);
    }

    @Test
    public void queObtengaElCuestionario() {
        Cuestionario cuestionarioMock = new Cuestionario();
        cuestionarioMock.setId(1L);

        Mockito.when(servCuestionario.buscar(1L)).thenReturn(cuestionarioMock);
        Cuestionario resultado = servicioJuego.obtenerCuestionario(1L);

        assertEquals(cuestionarioMock, resultado);
    }

    @Test
    public void queObtengaLaPregunta() {
        Preguntas pregunta_uno = new Preguntas();
        pregunta_uno.setId(1L);

        Preguntas pregunta_dos = new Preguntas();
        pregunta_dos.setId(2L);

        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setPreguntas(List.of(pregunta_uno, pregunta_dos));

        Preguntas obtenido = servicioJuego.obtenerPregunta(cuestionario, 1);

        assertEquals(2L, obtenido.getId());
    }

    private void thenAcumulaPuntaje(Integer esperado, Integer puntaje) {
        assertEquals(esperado, puntaje);
    }

    private Integer whenAcumulaPuntaje(String respuesta) {
        TimerPregunta timer = new TimerPregunta(10);
        if (whenSeValideLaRespuestaSiEsCorrecta()) {
            return servicioJuego.obtenerPuntaje(1L, respuesta, timer);
        }
        return 0;
    }

    private Boolean whenSeValideLaRespuestaIncorrecta() {
        return servicioJuego.validarRespuesta("23 de Abril", 1L);
    }

    private void thenSeValideRespuestaIncorrecta(Boolean obtenido) {
        assertEquals(Boolean.FALSE, obtenido);
    }

    private void givenCreacionPreguntas() {
        Preguntas pregunta = new Preguntas();
        pregunta.setId(1L);
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        Dificultad dificultad = new Dificultad();
        dificultad.setNombre("Dificil");
        pregunta.setDificultad(dificultad);
        pregunta.setRespuestaCorrecta("25 de mayo");
        pregunta.setRespuestaIncorrecta1("23 de Abril");
        pregunta.setRespuestaIncorrecta2("24 de Junio");
        pregunta.setRespuestaIncorrecta3("25 de Julio");

       /* Cuestionario c=new Cuestionario();
        c.setPreguntas(Arrays.asList(pregunta));

        Mockito.when(serv.buscar(2L)).thenReturn(c);*/
        Mockito.when(servPregunta.obtenerPorId(1L)).thenReturn(pregunta);
        Mockito.when(servDificultad.calcularMultiplicador(Mockito.any())).thenReturn(3);
    }

    private Boolean whenSeValideLaRespuestaSiEsCorrecta() {
        return servicioJuego.validarRespuesta("25 de mayo", 1L);
    }

    private void thenValideLaRespuesta(Boolean respuesta) {
        assertEquals(Boolean.TRUE, respuesta);
    }
}

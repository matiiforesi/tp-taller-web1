package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ServicioJuegoTest {

    // Que se pueda validar la respuesta
    // Que te devuelva si es incorrecta

    private RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
    private RepositorioHistorial repoHistorial = mock(RepositorioHistorial.class);
    private RepositorioIntento repoIntento = mock(RepositorioIntento.class);
    private ServicioCuestionario servCuestionario = mock(ServicioCuestionario.class);
    private ServicioPregunta servPregunta = mock(ServicioPregunta.class);
    private ServicioDificultad servDificultad = mock(ServicioDificultad.class);

    private ServicioJuego servicioJuego = new ServicioJuegoImpl(repoUsuario, repoHistorial, repoIntento, servCuestionario, servPregunta, servDificultad);

    private RepositorioCuestionario repoCuestionario = mock(RepositorioCuestionario.class);

    @Test
    public void queSeValideLaRespuestaSiEsCorrecta() {
        givenCreacionPreguntas();
        Boolean obtenido = whenSeValideLaRespuestaSiEsCorrecta();
        thenValideLaRespuesta(obtenido);
    }

    private void givenCreacionPreguntas() {
        Preguntas pregunta = new Preguntas();
        pregunta.setId(1L);
        pregunta.setEnunciado("¿Cuando fue la revolucion de Mayo?");
        pregunta.setCategoria("Historia");
        Dificultad dificultad = new Dificultad();
        dificultad.setNombre("Dificil");
        pregunta.setDificultad(dificultad);
        pregunta.setRespuestaCorrecta("25 de Mayo");
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
        return servicioJuego.validarRespuesta("25 de Mayo", 1L);
    }

    private void thenValideLaRespuesta(Boolean respuesta) {
        assertEquals(Boolean.TRUE, respuesta);
    }

    @Test
    public void queSeValideLaRespuestaSiEsIncorrecta() {
        givenCreacionPreguntas();
        Boolean obtenido = whenSeValideLaRespuestaIncorrecta();
        thenSeValideRespuestaIncorrecta(obtenido);
    }

    private Boolean whenSeValideLaRespuestaIncorrecta() {
        return servicioJuego.validarRespuesta("23 de Abril", 1L);
    }

    private void thenSeValideRespuestaIncorrecta(Boolean obtenido) {
        assertEquals(Boolean.FALSE, obtenido);
    }

    @Test
    public void queObtengaPuntos() {
        givenCreacionPreguntas();
        Integer obtenido = whenAcumulaPuntaje("25 de Mayo");
        thenAcumulaPuntaje(600, obtenido);
    }

    @Test
    public void queNoSumePuntos() {
        givenCreacionPreguntas();
        Integer obtenido = whenAcumulaPuntaje("23 de Abril");
        thenAcumulaPuntaje(0, obtenido);
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

    @Test
    public void siContestaMalPierdeVidas() {
        Cuestionario cuestionarioMock = givenCuestionarioConVidas(3);
        Preguntas pregunta = givenPreguntaConRespuesta("Correcta");
        TimerPregunta timer = new TimerPregunta(10);

        when(servPregunta.obtenerPorId(3L)).thenReturn(pregunta);
        servicioJuego.inicializarVidas(cuestionarioMock);

        Integer puntajeTotal = servicioJuego.obtenerPuntaje(3L, "Incorrecta", timer);

        assertEquals(0, puntajeTotal);
        assertEquals(2, getVidasRestantes(servicioJuego));
    }

    public Cuestionario givenCuestionarioConVidas(int vidas) {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setVidas(vidas);
        return cuestionario;
    }

    public Preguntas givenPreguntaConRespuesta(String respuestaCorrecta) {
        Preguntas pregunta = new Preguntas();
        pregunta.setRespuestaCorrecta(respuestaCorrecta);
        return pregunta;
    }

    private int getVidasRestantes(ServicioJuego servicioJuego) {
        try {
            Field campo = ServicioJuegoImpl.class.getDeclaredField("vidasRestantes");
            campo.setAccessible(true);
            return (int) campo.get(servicioJuego);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void queRegistreUnIntentoPorUsuarioYCuestionario() {
        Usuario usuario = givenUsuarioConId(1L);
        Cuestionario cuestionario = givenCuestionarioConId(2L);
        int puntajePartida = 500;

        when(repoUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(repoCuestionario.buscar(2L)).thenReturn(cuestionario);
        when(repoIntento.contarIntentos(1L, 2L)).thenReturn(0);

        Integer puntajePenalizado = servicioJuego.registrarIntento(1L, 2L, puntajePartida);

        assertEquals(puntajePartida, puntajePenalizado);
        verify(repoIntento, times(1)).guardar(any(IntentoCuestionario.class));
    }

    public Usuario givenUsuarioConId(Long id){
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setPuntaje(0L);
        usuario.setNombre("Prueba Usuario");
        return usuario;
    }

    private Cuestionario givenCuestionarioConId(Long id){
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setId(id);
        cuestionario.setNombre("Prueba Cuestionario");
        return cuestionario;
    }
}

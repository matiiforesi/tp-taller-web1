package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ControladorSurvivalTest {

    private ControladorSurvival controladorSurvival;
    private ServicioSurvival servicioSurvivalMock;
    private HttpSession sessionMock;
    private Usuario usuarioMock;
    private List<Preguntas> preguntasMock;
    private Map<String, Object> sessionAttributes;

    @BeforeEach
    public void setUp() {
        servicioSurvivalMock = mock(ServicioSurvival.class);
        controladorSurvival = new ControladorSurvival(servicioSurvivalMock);
        sessionMock = mock(HttpSession.class);
        sessionAttributes = new HashMap<>();

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("test@unlam.edu.ar");
        usuarioMock.setNombre("Test User");
        usuarioMock.setPuntaje(0L);
        usuarioMock.setMonedas(0L);

        preguntasMock = crearPreguntasMock(10);

        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object value = invocation.getArgument(1);
            sessionAttributes.put(key, value);
            return null;
        }).when(sessionMock).setAttribute(anyString(), any());

        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return sessionAttributes.get(key);
        }).when(sessionMock).getAttribute(anyString());

        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            sessionAttributes.remove(key);
            return null;
        }).when(sessionMock).removeAttribute(anyString());
    }

    @Test
    public void queIniciarSurvivalRedirijaALoginSiNoHayUsuario() {
        sessionAttributes.put("usuario", null);
        ModelAndView mav = controladorSurvival.iniciarSurvival(sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queIniciarSurvivalRedirijaAHomeSiNoHayPreguntas() {
        sessionAttributes.put("usuario", usuarioMock);
        when(servicioSurvivalMock.obtenerPreguntasSurvival(anyString(), anyInt()))
                .thenReturn(new ArrayList<>());
        ModelAndView mav = controladorSurvival.iniciarSurvival(sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queIniciarSurvivalInicieElJuegoCorrectamente() {
        sessionAttributes.put("usuario", usuarioMock);
        when(servicioSurvivalMock.obtenerPreguntasSurvival(anyString(), anyInt()))
                .thenReturn(preguntasMock.subList(0, 5));

        ModelAndView mav = controladorSurvival.iniciarSurvival(sessionMock);

        assertThat(mav.getViewName(), equalTo("pregunta_survival"));
        assertThat(sessionAttributes.get("survivalVidas"), equalTo(3));
        assertThat(sessionAttributes.get("survivalPuntaje"), equalTo(0));
        assertThat(sessionAttributes.get("survivalCorrectas"), equalTo(0));
        assertThat(sessionAttributes.get("survivalErradas"), equalTo(0));
        assertThat(sessionAttributes.get("survivalMonedasGanadas"), equalTo(0));
        assertThat(sessionAttributes.get("puntajeGanado"), equalTo(0));
        assertNotNull(sessionAttributes.get("gestorPreguntas"));
    }

    @Test
    public void queSiguientePreguntaSurvivalRedirijaALoginSiNoHayUsuario() {
        sessionAttributes.put("usuario", null);
        ModelAndView mav = controladorSurvival.siguientePreguntaSurvival(sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queSiguientePreguntaSurvivalRedirijaAHomeSiNoHayGestor() {
        sessionAttributes.put("usuario", usuarioMock);
        sessionAttributes.put("gestorPreguntas", null);
        ModelAndView mav = controladorSurvival.siguientePreguntaSurvival(sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queSiguientePreguntaSurvivalFinaliceSiNoHayVidas() {
        sessionAttributes.put("usuario", usuarioMock);
        GestorPreguntasSurvival gestor = new GestorPreguntasSurvival();
        gestor.inicializar(preguntasMock.subList(0, 5), "easy");
        sessionAttributes.put("gestorPreguntas", gestor);
        sessionAttributes.put("survivalVidas", 0);
        sessionAttributes.put("survivalPuntaje", 100);
        sessionAttributes.put("survivalCorrectas", 1);
        sessionAttributes.put("survivalErradas", 0);

        ModelAndView mav = controladorSurvival.siguientePreguntaSurvival(sessionMock);

        assertThat(mav.getViewName(), equalTo("final_survival"));
    }

    @Test
    public void queSiguientePreguntaSurvivalMuestreLaSiguientePregunta() {
        configurarJuegoActivo();
        ModelAndView mav = controladorSurvival.siguientePreguntaSurvival(sessionMock);
        assertThat(mav.getViewName(), equalTo("pregunta_survival"));
        assertNotNull(mav.getModel().get("pregunta"));
    }

    @Test
    public void queValidarPreguntaSurvivalRedirijaAHomeSiNoHayGestor() {
        sessionAttributes.put("gestorPreguntas", null);
        ModelAndView mav = controladorSurvival.validarPreguntaSurvival("respuesta", null, sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queValidarPreguntaSurvivalRedirijaAHomeSiNoHayPregunta() {
        GestorPreguntasSurvival gestor = new GestorPreguntasSurvival();
        sessionAttributes.put("gestorPreguntas", gestor);
        ModelAndView mav = controladorSurvival.validarPreguntaSurvival("respuesta", null, sessionMock);
        assertThat(mav.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queValidarPreguntaSurvivalIncrementeCorrectasYActualicePuntajeSiEsCorrecta() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaCorrecta = pregunta.getRespuestaCorrecta();

        when(servicioSurvivalMock.calcularMultiplicadorSurvival(anyString())).thenReturn(1);
        when(servicioSurvivalMock.obtenerDificultadSurvival(anyInt())).thenReturn("easy");
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPuntaje(100L);
        usuarioActualizado.setMonedas(10L);
        when(servicioSurvivalMock.actualizarPuntajeYMonedas(anyLong(), anyInt()))
                .thenReturn(usuarioActualizado);

        ModelAndView mav = controladorSurvival.validarPreguntaSurvival(respuestaCorrecta, null, sessionMock);

        assertThat(mav.getViewName(), equalTo("pregunta_survival"));
        assertThat(mav.getModel().get("esCorrecta"), equalTo(true));
        assertThat(sessionAttributes.get("survivalCorrectas"), equalTo(1));
        assertThat(sessionAttributes.get("survivalPuntaje"), notNullValue());
        assertThat(sessionAttributes.get("puntajeGanado"), notNullValue());
        assertThat(sessionAttributes.get("survivalMonedasGanadas"), notNullValue());
    }

    @Test
    public void queValidarPreguntaSurvivalIncrementeErradasYDecrementeVidasSiEsIncorrecta() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaIncorrecta = "Respuesta Incorrecta";

        ModelAndView mav = controladorSurvival.validarPreguntaSurvival(respuestaIncorrecta, null, sessionMock);

        assertThat(mav.getViewName(), equalTo("pregunta_survival"));
        assertThat(mav.getModel().get("esCorrecta"), equalTo(false));
        assertThat(sessionAttributes.get("survivalErradas"), equalTo(1));
        assertThat(sessionAttributes.get("survivalVidas"), equalTo(2));
    }

    @Test
    public void queValidarPreguntaSurvivalMuestreTiempoAgotadoSiSeAgotoElTiempo() {
        configurarJuegoActivo();

        ModelAndView mav = controladorSurvival.validarPreguntaSurvival("cualquier respuesta", "true", sessionMock);

        assertThat(mav.getViewName(), equalTo("pregunta_survival"));
        assertThat(mav.getModel().get("esCorrecta"), equalTo(false));
        assertThat(mav.getModel().get("tiempoAgotado"), equalTo(true));
        assertThat(sessionAttributes.get("survivalErradas"), equalTo(1));
        assertThat(sessionAttributes.get("survivalVidas"), equalTo(2));
    }

    @Test
    public void queValidarPreguntaSurvivalFinaliceElJuegoSiNoQuedanVidas() {
        configurarJuegoActivo();
        sessionAttributes.put("survivalVidas", 1);
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaIncorrecta = "Respuesta Incorrecta";

        ModelAndView mav = controladorSurvival.validarPreguntaSurvival(respuestaIncorrecta, null, sessionMock);

        assertThat(mav.getViewName(), equalTo("final_survival"));
    }

    @Test
    public void quePuntajeSeAcumuleCorrectamenteEnCadaRespuestaCorrecta() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaCorrecta = pregunta.getRespuestaCorrecta();

        when(servicioSurvivalMock.calcularMultiplicadorSurvival(anyString())).thenReturn(1);
        when(servicioSurvivalMock.obtenerDificultadSurvival(anyInt())).thenReturn("easy");
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPuntaje(100L);
        usuarioActualizado.setMonedas(10L);
        when(servicioSurvivalMock.actualizarPuntajeYMonedas(anyLong(), anyInt()))
                .thenReturn(usuarioActualizado);

        controladorSurvival.validarPreguntaSurvival(respuestaCorrecta, null, sessionMock);
        Integer puntajeGanado1 = (Integer) sessionAttributes.get("puntajeGanado");

        controladorSurvival.siguientePreguntaSurvival(sessionMock);
        Preguntas pregunta2 = preguntasMock.get(1);
        controladorSurvival.validarPreguntaSurvival(pregunta2.getRespuestaCorrecta(), null, sessionMock);
        Integer puntajeGanado2 = (Integer) sessionAttributes.get("puntajeGanado");

        assertThat(puntajeGanado2, equalTo(puntajeGanado1));
    }

    @Test
    public void queMonedasSeAcumulenCorrectamenteEnCadaRespuestaCorrecta() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaCorrecta = pregunta.getRespuestaCorrecta();

        when(servicioSurvivalMock.calcularMultiplicadorSurvival(anyString())).thenReturn(1);
        when(servicioSurvivalMock.obtenerDificultadSurvival(anyInt())).thenReturn("easy");
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPuntaje(100L);
        usuarioActualizado.setMonedas(10L);
        when(servicioSurvivalMock.actualizarPuntajeYMonedas(anyLong(), anyInt()))
                .thenReturn(usuarioActualizado);

        controladorSurvival.validarPreguntaSurvival(respuestaCorrecta, null, sessionMock);
        Integer monedas1 = (Integer) sessionAttributes.get("survivalMonedasGanadas");

        controladorSurvival.siguientePreguntaSurvival(sessionMock);
        Preguntas pregunta2 = preguntasMock.get(1);
        controladorSurvival.validarPreguntaSurvival(pregunta2.getRespuestaCorrecta(), null, sessionMock);
        Integer monedas2 = (Integer) sessionAttributes.get("survivalMonedasGanadas");

        assertThat(monedas2, equalTo(monedas1));
    }

    @Test
    public void queElMultiplicadorSeApliqueCorrectamenteSegunLaDificultad() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaCorrecta = pregunta.getRespuestaCorrecta();

        when(servicioSurvivalMock.calcularMultiplicadorSurvival("easy")).thenReturn(1);
        when(servicioSurvivalMock.calcularMultiplicadorSurvival("medium")).thenReturn(2);
        when(servicioSurvivalMock.obtenerDificultadSurvival(anyInt())).thenReturn("easy");
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPuntaje(100L);
        usuarioActualizado.setMonedas(10L);
        when(servicioSurvivalMock.actualizarPuntajeYMonedas(anyLong(), anyInt()))
                .thenReturn(usuarioActualizado);

        controladorSurvival.validarPreguntaSurvival(respuestaCorrecta, null, sessionMock);

        verify(servicioSurvivalMock, atLeastOnce()).calcularMultiplicadorSurvival(anyString());
    }

    @Test
    public void queElUsuarioSeActualiceEnLaSesionDespuesDeUnaRespuestaCorrecta() {
        configurarJuegoActivo();
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaCorrecta = pregunta.getRespuestaCorrecta();

        when(servicioSurvivalMock.calcularMultiplicadorSurvival(anyString())).thenReturn(1);
        when(servicioSurvivalMock.obtenerDificultadSurvival(anyInt())).thenReturn("easy");
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setPuntaje(500L);
        usuarioActualizado.setMonedas(50L);
        when(servicioSurvivalMock.actualizarPuntajeYMonedas(anyLong(), anyInt()))
                .thenReturn(usuarioActualizado);

        controladorSurvival.validarPreguntaSurvival(respuestaCorrecta, null, sessionMock);

        Usuario usuarioEnSesion = (Usuario) sessionAttributes.get("usuario");
        assertNotNull(usuarioEnSesion);
        assertThat(usuarioEnSesion.getPuntaje(), equalTo(500L));
        assertThat(usuarioEnSesion.getMonedas(), equalTo(50L));
    }

    @Test
    public void queElJuegoFinaliceYMuestreResultadosCuandoSePierdenTodasLasVidas() {
        configurarJuegoActivo();
        sessionAttributes.put("survivalVidas", 1);
        sessionAttributes.put("survivalMonedasGanadas", 50);
        sessionAttributes.put("puntajeGanado", 500);
        sessionAttributes.put("survivalCorrectas", 5);
        sessionAttributes.put("survivalErradas", 2);
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaIncorrecta = "Respuesta Incorrecta";

        ModelAndView mav = controladorSurvival.validarPreguntaSurvival(respuestaIncorrecta, null, sessionMock);

        assertThat(mav.getViewName(), equalTo("final_survival"));
        assertThat(mav.getModel().get("survivalPuntaje"), notNullValue());
        assertThat(mav.getModel().get("survivalCorrectas"), notNullValue());
        assertThat(mav.getModel().get("survivalErradas"), notNullValue());
        assertThat(mav.getModel().get("monedas"), notNullValue());
    }

    @Test
    public void queLaSesionSeLimpieCuandoElJuegoFinaliza() {
        configurarJuegoActivo();
        sessionAttributes.put("survivalVidas", 1);
        sessionAttributes.put("survivalMonedasGanadas", 50);
        sessionAttributes.put("puntajeGanado", 500);
        Preguntas pregunta = preguntasMock.get(0);
        String respuestaIncorrecta = "Respuesta Incorrecta";

        controladorSurvival.validarPreguntaSurvival(respuestaIncorrecta, null, sessionMock);

        assertNull(sessionAttributes.get("survivalPuntaje"));
        assertNull(sessionAttributes.get("survivalCorrectas"));
        assertNull(sessionAttributes.get("survivalErradas"));
        assertNull(sessionAttributes.get("survivalVidas"));
        assertNull(sessionAttributes.get("survivalMonedasGanadas"));
        assertNull(sessionAttributes.get("puntajeGanado"));
    }

    private void configurarJuegoActivo() {
        sessionAttributes.put("usuario", usuarioMock);
        GestorPreguntasSurvival gestor = new GestorPreguntasSurvival();
        gestor.inicializar(preguntasMock.subList(0, 5), "easy");
        sessionAttributes.put("gestorPreguntas", gestor);
        sessionAttributes.put("survivalVidas", 3);
        sessionAttributes.put("survivalPuntaje", 0);
        sessionAttributes.put("survivalCorrectas", 0);
        sessionAttributes.put("survivalErradas", 0);
        sessionAttributes.put("survivalMonedasGanadas", 0);
        sessionAttributes.put("puntajeGanado", 0);
        sessionAttributes.put("survivalDificultad", "easy");
        sessionAttributes.put("timer", new TimerPregunta(10));
        sessionAttributes.put("respondida", false);

        when(servicioSurvivalMock.obtenerPreguntasSurvival(anyString(), anyInt()))
                .thenReturn(preguntasMock.subList(5, 10));
    }

    private List<Preguntas> crearPreguntasMock(int cantidad) {
        List<Preguntas> preguntas = new ArrayList<>();
        Dificultad dificultadEasy = new Dificultad();
        dificultadEasy.setNombre("Easy");

        for (int i = 0; i < cantidad; i++) {
            Preguntas pregunta = new Preguntas();
            pregunta.setId((long) (i + 1));
            pregunta.setEnunciado("Pregunta " + (i + 1));
            pregunta.setCategoria("General Knowledge");
            pregunta.setDificultad(dificultadEasy);
            pregunta.setRespuestaCorrecta("Respuesta Correcta " + (i + 1));
            pregunta.setRespuestaIncorrecta1("Incorrecta 1 " + (i + 1));
            pregunta.setRespuestaIncorrecta2("Incorrecta 2 " + (i + 1));
            pregunta.setRespuestaIncorrecta3("Incorrecta 3 " + (i + 1));
            preguntas.add(pregunta);
        }

        return preguntas;
    }
}

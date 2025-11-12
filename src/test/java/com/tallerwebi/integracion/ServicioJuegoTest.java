package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private ServicioConfigJuego servConfigJuego = mock(ServicioConfigJuego.class);
    private RepositorioCompraItem repositorioCompraItem = mock(RepositorioCompraItem.class);

    private ServicioJuego servicioJuego = new ServicioJuegoImpl(repositorioCompraItem,repoUsuario, repoHistorial, repoIntento, servCuestionario, servPregunta, servDificultad, servConfigJuego);

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
        Mockito.when(servConfigJuego.getInt("puntaje.base", 100)).thenReturn(100);
        Mockito.when(servConfigJuego.getInt("bonificacion.tiempo", 10)).thenReturn(10);
        Mockito.when(servConfigJuego.getInt("penalizacion.vida", 1)).thenReturn(1);

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
    @Test
    public void queDupliqueElPuntajeSiTieneTrampaDuplicarPuntaje(){
        Preguntas pregunta = new Preguntas();
        pregunta.setId(1L);
        pregunta.setRespuestaCorrecta("Correcta");
        Dificultad dificultad = new Dificultad();
        dificultad.setNombre("Media");
        pregunta.setDificultad(dificultad);

        Item trampa=new Item();
        trampa.setTipoItem(TIPO_ITEMS.DUPLICAR_PUNTAJE);

        CompraItem compra=new CompraItem();
        compra.setItem(trampa);
        compra.setUsado(false);

        when(servPregunta.obtenerPorId(1L)).thenReturn(pregunta);
        when(servDificultad.calcularMultiplicador(dificultad)).thenReturn(2);
        when(servConfigJuego.getInt("puntaje.base", 100)).thenReturn(100);
        when(servConfigJuego.getInt("bonificacion.tiempo", 10)).thenReturn(10);
        when(servConfigJuego.getInt("penalizacion.vida", 1)).thenReturn(1);
        when(repositorioCompraItem.obtenerComprasPorUsuario(1L)).thenReturn(List.of(compra));

        TimerPregunta timer = new TimerPregunta(5);

        Integer puntaje= servicioJuego.obtenerPuntajeConTrampa(1L,"Correcta",timer,1L,trampa.getTipoItem());

        Integer esperado=(100+50)*2*2;
        assertEquals(esperado,puntaje);
        assertTrue(compra.getUsado());
    }
    
    @Test
    public void queSeEliminenDosOpcionesIncorrectasSiTieneTrampa() {
        Preguntas pregunta = new Preguntas();
        pregunta.setRespuestaCorrecta("Correcta");
        pregunta.setRespuestaIncorrecta1("Incorrecta1");
        pregunta.setRespuestaIncorrecta2("Incorrecta2");
        pregunta.setRespuestaIncorrecta3("Incorrecta3");

        Item trampa = new Item();
        trampa.setTipoItem(TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS);

        CompraItem compra = new CompraItem();
        compra.setItem(trampa);
        compra.setUsado(false);

        when(repositorioCompraItem.obtenerComprasPorUsuario(1L)).thenReturn(List.of(compra));

        List<String> opciones = servicioJuego.obtenerOpcionesFiltradas(pregunta, 1L, TIPO_ITEMS.ELIMINAR_DOS_INCORRECTAS);

        assertEquals(2, opciones.size());
        assertTrue(opciones.contains("Correcta"));
        assertTrue(compra.getUsado());
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
        Mockito.when(servConfigJuego.getInt("puntaje.base", 100)).thenReturn(100);
        Mockito.when(servConfigJuego.getInt("bonificacion.tiempo", 10)).thenReturn(10);
        Mockito.when(servConfigJuego.getInt("penalizacion.vida", 1)).thenReturn(1);
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

    @Test
    public void queUseDificultadIndividualDeCadaPreguntaEnCuestionarioMulti() {
        Preguntas preguntaEasy = new Preguntas();
        preguntaEasy.setId(1L);
        preguntaEasy.setRespuestaCorrecta("Easy answer");
        Dificultad dificultadEasy = new Dificultad();
        dificultadEasy.setNombre("Easy");
        preguntaEasy.setDificultad(dificultadEasy);

        Preguntas preguntaMedium = new Preguntas();
        preguntaMedium.setId(2L);
        preguntaMedium.setRespuestaCorrecta("Medium answer");
        Dificultad dificultadMedium = new Dificultad();
        dificultadMedium.setNombre("Medium");
        preguntaMedium.setDificultad(dificultadMedium);

        Preguntas preguntaHard = new Preguntas();
        preguntaHard.setId(3L);
        preguntaHard.setRespuestaCorrecta("Hard answer");
        Dificultad dificultadHard = new Dificultad();
        dificultadHard.setNombre("Hard");
        preguntaHard.setDificultad(dificultadHard);

        when(servPregunta.obtenerPorId(1L)).thenReturn(preguntaEasy);
        when(servPregunta.obtenerPorId(2L)).thenReturn(preguntaMedium);
        when(servPregunta.obtenerPorId(3L)).thenReturn(preguntaHard);

        when(servDificultad.calcularMultiplicador(dificultadEasy)).thenReturn(1);
        when(servDificultad.calcularMultiplicador(dificultadMedium)).thenReturn(2);
        when(servDificultad.calcularMultiplicador(dificultadHard)).thenReturn(3);

        when(servConfigJuego.getInt("puntaje.base", 100)).thenReturn(100);
        when(servConfigJuego.getInt("bonificacion.tiempo", 10)).thenReturn(10);
        when(servConfigJuego.getInt("penalizacion.vida", 1)).thenReturn(1);

        TimerPregunta timer = new TimerPregunta(10);

        Integer puntajeEasy = servicioJuego.obtenerPuntaje(1L, "Easy answer", timer);
        servicioJuego.reiniciarPuntaje();

        Integer puntajeMedium = servicioJuego.obtenerPuntaje(2L, "Medium answer", timer);
        servicioJuego.reiniciarPuntaje();

        Integer puntajeHard = servicioJuego.obtenerPuntaje(3L, "Hard answer", timer);

        assertEquals((100 + (10 * 10)) * 1, puntajeEasy);
        assertEquals((100 + (10 * 10)) * 2, puntajeMedium);
        assertEquals((100 + (10 * 10)) * 3, puntajeHard);

        verify(servDificultad, times(1)).calcularMultiplicador(dificultadEasy);
        verify(servDificultad, times(1)).calcularMultiplicador(dificultadMedium);
        verify(servDificultad, times(1)).calcularMultiplicador(dificultadHard);
    }
}

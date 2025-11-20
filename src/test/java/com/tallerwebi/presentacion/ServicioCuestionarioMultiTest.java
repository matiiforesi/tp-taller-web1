package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ServicioCuestionarioMultiTest {

    private ServicioCuestionario servicioCuestionario;
    private RepositorioCuestionario repositorioCuestionarioMock;
    private ServicioTrivia servicioTriviaMock;
    private ServicioDificultad servicioDificultadMock;

    @BeforeEach
    public void init() {
        repositorioCuestionarioMock = mock(RepositorioCuestionario.class);
        servicioTriviaMock = mock(ServicioTrivia.class);
        servicioDificultadMock = mock(ServicioDificultad.class);
        servicioCuestionario = new ServicioCuestionarioImpl(repositorioCuestionarioMock);

        ReflectionTestUtils.setField(servicioCuestionario, "servicioTrivia", servicioTriviaMock);
        ReflectionTestUtils.setField(servicioCuestionario, "servicioDificultad", servicioDificultadMock);
    }

    @Test
    public void debeAsignarCuatroVidasACuestionarioMulti() {
        Cuestionario cuestionario = new Cuestionario();
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("Multi");
        cuestionario.setDificultad(dificultadMulti);

        servicioCuestionario.asignarVidasSegunDificultad(cuestionario);

        assertThat(cuestionario.getVidas(), is(4));
    }

    @Test
    public void debeAsignarCuatroVidasACuestionarioMultiCaseInsensitive() {
        Cuestionario cuestionario = new Cuestionario();
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("multi"); // lowercase
        cuestionario.setDificultad(dificultadMulti);

        servicioCuestionario.asignarVidasSegunDificultad(cuestionario);

        assertThat(cuestionario.getVidas(), is(4));
    }

    @Test
    public void debeCrearCuestionarioConDificultadMulti() {
        RespuestaTrivia respuestaMulti = new RespuestaTrivia();
        PreguntaTrivia preguntaEasy = new PreguntaTrivia();
        preguntaEasy.setQuestion("Easy question");
        preguntaEasy.setCorrect_answer("Easy answer");
        preguntaEasy.setCategory("Geography");
        preguntaEasy.setDifficulty("easy");
        preguntaEasy.setIncorrect_answers(Arrays.asList("Wrong1", "Wrong2", "Wrong3"));

        PreguntaTrivia preguntaMedium = new PreguntaTrivia();
        preguntaMedium.setQuestion("Medium question");
        preguntaMedium.setCorrect_answer("Medium answer");
        preguntaMedium.setCategory("Geography");
        preguntaMedium.setDifficulty("medium");
        preguntaMedium.setIncorrect_answers(Arrays.asList("Wrong1", "Wrong2", "Wrong3"));

        PreguntaTrivia preguntaHard = new PreguntaTrivia();
        preguntaHard.setQuestion("Hard question");
        preguntaHard.setCorrect_answer("Hard answer");
        preguntaHard.setCategory("Geography");
        preguntaHard.setDifficulty("hard");
        preguntaHard.setIncorrect_answers(Arrays.asList("Wrong1", "Wrong2", "Wrong3"));

        respuestaMulti.setResults(Arrays.asList(preguntaEasy, preguntaMedium, preguntaHard));

        when(servicioTriviaMock.buscarPreguntas(anyInt(), anyInt(), eq("multi")))
                .thenReturn(respuestaMulti);

        Dificultad dificultadEasy = new Dificultad();
        dificultadEasy.setNombre("Easy");
        Dificultad dificultadMedium = new Dificultad();
        dificultadMedium.setNombre("Medium");
        Dificultad dificultadHard = new Dificultad();
        dificultadHard.setNombre("Hard");
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("Multi");

        when(servicioDificultadMock.obtenerPorNombre("Easy")).thenReturn(dificultadEasy);
        when(servicioDificultadMock.obtenerPorNombre("Medium")).thenReturn(dificultadMedium);
        when(servicioDificultadMock.obtenerPorNombre("Hard")).thenReturn(dificultadHard);
        when(servicioDificultadMock.obtenerPorNombre("Multi")).thenReturn(dificultadMulti);

        servicioCuestionario.crearCuestionario("Trivia Multi", "Descripción", 5, 22, "multi");

        var cuestionarioCaptor = ArgumentCaptor.forClass(Cuestionario.class);
        verify(repositorioCuestionarioMock, times(1)).guardar(cuestionarioCaptor.capture());

        Cuestionario cuestionarioGuardado = cuestionarioCaptor.getValue();
        assertThat(cuestionarioGuardado.getNombre(), is("Trivia Multi"));
        assertThat(cuestionarioGuardado.getDescripcion(), is("Descripción"));
        assertThat(cuestionarioGuardado.getDificultad(), is(notNullValue()));
        assertThat(cuestionarioGuardado.getDificultad().getNombre(), is("Multi"));
        assertThat(cuestionarioGuardado.getPreguntas(), is(notNullValue()));
        assertThat(cuestionarioGuardado.getPreguntas().size(), is(3));

        List<Preguntas> preguntas = cuestionarioGuardado.getPreguntas();
        assertThat(preguntas.get(0).getDificultad().getNombre(), is("Easy"));
        assertThat(preguntas.get(1).getDificultad().getNombre(), is("Medium"));
        assertThat(preguntas.get(2).getDificultad().getNombre(), is("Hard"));
    }
}

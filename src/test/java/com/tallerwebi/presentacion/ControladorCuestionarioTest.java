package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ControladorCuestionarioTest {

    private ControladorCuestionario controladorCuestionario;
    private ServicioCuestionario servicioCuestionarioMock;
    private ServicioTrivia servicioTriviaMock;

    @BeforeEach
    public void init() {
        servicioCuestionarioMock = mock(ServicioCuestionario.class);
        servicioTriviaMock = mock(ServicioTrivia.class);
        controladorCuestionario = new ControladorCuestionario(servicioCuestionarioMock, servicioTriviaMock);
    }

    @Test
    public void debeRetornarListaDeCuestionarios() {
        List<Cuestionario> cuestionarios = Arrays.asList(
                crearCuestionarioMock("Historia", "Preguntas de historia"),
                crearCuestionarioMock("Geografia", "Preguntas de geografía")
        );
        when(servicioCuestionarioMock.buscarTodo()).thenReturn(cuestionarios);
        Model model = new ExtendedModelMap();

        String viewName = controladorCuestionario.listTrivias(model);

        assertThat(viewName, equalToIgnoringCase("cuestionario_list"));
        assertThat(model.getAttribute("trivias"), is(cuestionarios));
        verify(servicioCuestionarioMock, times(1)).buscarTodo();
    }

    @Test
    public void debeMostrarFormularioConCategorias() {
        RespuestaCategorias respuestaCategorias = new RespuestaCategorias();
        TriviaCategory categoria1 = new TriviaCategory();
        categoria1.setId(9);
        categoria1.setName("General Knowledge");
        
        TriviaCategory categoria2 = new TriviaCategory();
        categoria2.setId(22);
        categoria2.setName("Geography");
        
        respuestaCategorias.setTriviaCategories(Arrays.asList(categoria1, categoria2));
        when(servicioTriviaMock.obtenerCategorias()).thenReturn(respuestaCategorias);
        Model model = new ExtendedModelMap();

        String viewName = controladorCuestionario.mostrarForm(model);

        assertThat(viewName, equalToIgnoringCase("cuestionario_form"));
        assertThat(model.getAttribute("cuestionario"), is(notNullValue()));
        assertThat(model.getAttribute("triviaCategories"), is(respuestaCategorias.getTriviaCategories()));
        verify(servicioTriviaMock, times(1)).obtenerCategorias();
    }

    @Test
    public void debeMostrarFormularioCuandoNoHayCategorias() {
        when(servicioTriviaMock.obtenerCategorias()).thenReturn(null);
        Model model = new ExtendedModelMap();

        String viewName = controladorCuestionario.mostrarForm(model);

        assertThat(viewName, equalToIgnoringCase("cuestionario_form"));
        assertThat(model.getAttribute("cuestionario"), is(notNullValue()));
        verify(servicioTriviaMock, times(1)).obtenerCategorias();
    }

    @Test
    public void debeCrearCuestionarioYRedirigirALista() {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setNombre("Historia Argentina");
        cuestionario.setDescripcion("Preguntas sobre historia argentina");

        doNothing().when(servicioCuestionarioMock).crearCuestionario(
                anyString(), anyString(), anyInt(), anyInt(), anyString()
        );

        String viewName = controladorCuestionario.createTrivia(cuestionario, 5, 25, "medium");

        assertThat(viewName, equalToIgnoringCase("redirect:/cuestionario/list"));
        verify(servicioCuestionarioMock, times(1)).crearCuestionario(
                "Historia Argentina", "Preguntas sobre historia argentina", 5, 25, "medium"
        );
    }

    @Test
    public void debeCrearCuestionarioConValoresPorDefecto() {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setNombre("Geografia");
        cuestionario.setDescripcion("Preguntas de geografía");

        doNothing().when(servicioCuestionarioMock).crearCuestionario(
                anyString(), anyString(), anyInt(), anyInt(), anyString()
        );

        String viewName = controladorCuestionario.createTrivia(
                cuestionario, 5, 25, "easy"
        );
        assertThat(viewName, equalToIgnoringCase("redirect:/cuestionario/list"));
        verify(servicioCuestionarioMock, times(1)).crearCuestionario(
                "Geografia", "Preguntas de geografía", 5, 25, "easy"
        );
    }

    @Test
    public void debeManejarRespuestaCategoriasConListaVacia() {
        RespuestaCategorias respuestaCategorias = new RespuestaCategorias();
        respuestaCategorias.setTriviaCategories(List.of());
        when(servicioTriviaMock.obtenerCategorias()).thenReturn(respuestaCategorias);
        Model model = new ExtendedModelMap();

        String viewName = controladorCuestionario.mostrarForm(model);

        assertThat(viewName, equalToIgnoringCase("cuestionario_form"));
        assertThat(model.getAttribute("cuestionario"), is(notNullValue()));
        verify(servicioTriviaMock, times(1)).obtenerCategorias();
    }

    @Test
    public void debeCrearCuestionarioConDificultadMulti() {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setNombre("Trivia Multi Dificultad");
        cuestionario.setDescripcion("Preguntas de todas las dificultades");

        doNothing().when(servicioCuestionarioMock).crearCuestionario(
                anyString(), anyString(), anyInt(), anyInt(), anyString()
        );

        String viewName = controladorCuestionario.createTrivia(cuestionario, 10, 25, "multi");

        assertThat(viewName, equalToIgnoringCase("redirect:/cuestionario/list"));
        verify(servicioCuestionarioMock, times(1)).crearCuestionario(
                "Trivia Multi Dificultad", "Preguntas de todas las dificultades", 10, 25, "multi"
        );
    }

    @Test
    public void debeCrearCuestionarioConDificultadMultiCaseInsensitive() {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setNombre("Trivia Multi");
        cuestionario.setDescripcion("Preguntas variadas");

        doNothing().when(servicioCuestionarioMock).crearCuestionario(
                anyString(), anyString(), anyInt(), anyInt(), anyString()
        );

        String viewName = controladorCuestionario.createTrivia(cuestionario, 5, 22, "MULTI");

        assertThat(viewName, equalToIgnoringCase("redirect:/cuestionario/list"));
        verify(servicioCuestionarioMock, times(1)).crearCuestionario(
                "Trivia Multi", "Preguntas variadas", 5, 22, "MULTI"
        );
    }

    private Cuestionario crearCuestionarioMock(String nombre, String descripcion) {
        Cuestionario cuestionario = new Cuestionario();
        cuestionario.setNombre(nombre);
        cuestionario.setDescripcion(descripcion);
        return cuestionario;
    }
}


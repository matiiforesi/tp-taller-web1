package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.HistorialCuestionario;
import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ControladorRankingTest {

    @Mock
    private ServicioRanking servicioRanking;

    private ControladorRanking controladorRanking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controladorRanking = new ControladorRanking(servicioRanking);
    }

    // Mostrar ordenado
    // Filtrar por cuestionario
    // Si no hay jugadores se muestra vacio

    @Test
    public void queElRankingSeMuestreOrdenado() {
        givenRankingGeneralConUsuarios();
        ModelAndView mav = whenMostrarRankingGeneral();
        thenRankingGeneralOrdenado(mav);
    }

    private void givenRankingGeneralConUsuarios() {
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        usuario1.setNombre("Mateo");
        usuario1.setPuntaje(80L);
        usuario2.setNombre("Juan");
        usuario2.setPuntaje(110L);

        when(servicioRanking.rankingGeneral()).thenReturn(Arrays.asList(usuario2, usuario1));
    }

    private ModelAndView whenMostrarRankingGeneral() {
        return controladorRanking.mostrarRankingGeneral();
    }

    private void thenRankingGeneralOrdenado(ModelAndView mav) {
        assertThat(mav.getViewName(), equalTo("ranking"));
        List<Usuario> ranking = (List<Usuario>) mav.getModel().get("rankingGeneral");
        assertThat(ranking, hasSize(2));
        assertThat(ranking.get(0).getPuntaje(), greaterThanOrEqualTo(ranking.get(1).getPuntaje()));
        assertThat(ranking.get(0).getNombre(), equalTo("Juan"));
        assertThat(ranking.get(1).getNombre(), equalTo("Mateo"));
    }

    @Test
    public void filtrarElRankingPorCuestionario() {
        HistorialCuestionario h1 = new HistorialCuestionario();
        h1.setNombreCuestionario("Historia");
        h1.setNombreUsuario("Matias");
        h1.setPuntaje(70L);
        h1.setPreguntasCorrectas(7);
        h1.setPreguntasErradas(3);

        givenRankingCuestionario("Historia", Collections.singletonList(h1));
        ModelAndView mav = whenMostrarRankingCuestionario("Historia");
        thenRankingCuestionarioFiltrado(mav);
    }

    private void givenRankingCuestionario(String nombreCuestionario, List<HistorialCuestionario> resultados) {
        when(servicioRanking.rankingCuestionario(nombreCuestionario)).thenReturn(resultados);
    }

    private ModelAndView whenMostrarRankingCuestionario(String nombreCuestionario) {
        return controladorRanking.mostrarRankingCuestionario(nombreCuestionario);
    }

    private void thenRankingCuestionarioFiltrado(ModelAndView mav) {
        assertThat(mav.getViewName(), equalTo("ranking"));
        List<HistorialCuestionario> ranking =
                (List<HistorialCuestionario>) mav.getModel().get("rankingCuestionario");

        assertThat(ranking, hasSize(1));
        HistorialCuestionario h = ranking.get(0);
        assertThat(h.getNombreCuestionario(), equalTo("Historia"));
        assertThat(h.getNombreUsuario(), equalTo("Matias"));
        assertThat(h.getPuntaje(), equalTo(70L));
        assertThat(h.getPreguntasCorrectas(), equalTo(7));
        assertThat(h.getPreguntasErradas(), equalTo(3));
    }

    @Test
    public void siNoHayJugadoresElCuestionarioSeMuestraVacio() {
        givenRankingCuestionario("Geografia", Collections.emptyList());
        ModelAndView mav = whenMostrarRankingCuestionario("Geografia");
        thenRankingVacio(mav);
    }

    private void thenRankingVacio(ModelAndView mav) {
        assertThat(mav.getViewName(), equalTo("ranking"));
        List<HistorialCuestionario> ranking =
                (List<HistorialCuestionario>) mav.getModel().get("rankingCuestionario");
        assertThat(ranking, is(empty()));
    }
}

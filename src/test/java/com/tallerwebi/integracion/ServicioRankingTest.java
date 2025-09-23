package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class ServicioRankingTest {

    @Mock
    private RepositorioHistorial repositorioHistorial;

    @InjectMocks
    private ServicioRankingImpl servicioRanking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queElRankingEsteOrdenado() {
        givenRankingGeneralConUsuarios();
        List<Usuario> resultado = whenTraerRankingGeneral();
        thenRankingGeneralOrdenado(resultado);
    }

    private void givenRankingGeneralConUsuarios() {
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        usuario1.setNombre("Matias");
        usuario2.setNombre("Damian");
        usuario1.setPuntaje(50L);
        usuario2.setPuntaje(100L);

        when(repositorioHistorial.obtenerRankingGeneral()).thenReturn(Arrays.asList(usuario2, usuario1));
    }

    private List<Usuario> whenTraerRankingGeneral() {
        return servicioRanking.rankingGeneral();
    }

    private void thenRankingGeneralOrdenado(List<Usuario> resultado) {
        assertThat(resultado, hasSize(2));
        assertThat(resultado.get(0).getPuntaje(), greaterThanOrEqualTo(resultado.get(1).getPuntaje()));
        assertThat(resultado.get(0).getNombre(), equalTo("Damian"));
        assertThat(resultado.get(1).getNombre(), equalTo("Matias"));
    }

    @Test
    public void queSePuedaFiltrarPorCuestionario() {
        HistorialCuestionario h1 = new HistorialCuestionario();
        h1.setNombreUsuario("Matias");
        h1.setPuntaje(70L);

        givenRankingCuestionario("Historia", Collections.singletonList(h1));
        List<HistorialCuestionario> resultado = whenTraerRankingCuestionario("Historia");
        thenRankingCuestionarioFiltrado(resultado, 1, "Matias", 70L);
    }

    private void givenRankingCuestionario(String nombreCuestionario, List<HistorialCuestionario> resultados) {
        when(repositorioHistorial.obtenerRankingCuestionario(nombreCuestionario)).thenReturn(resultados);
    }

    private List<HistorialCuestionario> whenTraerRankingCuestionario(String nombreCuestionario) {
        return servicioRanking.rankingCuestionario(nombreCuestionario);
    }

    private void thenRankingCuestionarioFiltrado(List<HistorialCuestionario> resultado, int size, String nombreUsuario, Long puntaje) {
        assertThat(resultado, hasSize(size));
        if (size > 0) {
            HistorialCuestionario h = resultado.get(0);
            assertThat(h.getNombreUsuario(), equalTo(nombreUsuario));
            assertThat(h.getPuntaje(), equalTo(puntaje));
        }
    }

    @Test
    public void siNoHayJugadoresElRankingCuestionarioEstaVacio() {
        givenRankingCuestionario("Geografia", Collections.emptyList());
        List<HistorialCuestionario> resultado = whenTraerRankingCuestionario("Geografia");
        thenRankingCuestionarioFiltrado(resultado, 0, null, null);
    }
}

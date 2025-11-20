package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ServicioRankingTest {

    private RepositorioHistorial repoHistorial = mock(RepositorioHistorial.class);
    private RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);

    private ServicioRankingImpl servicioRanking = new ServicioRankingImpl(repoUsuario, repoHistorial);

    @BeforeEach
    void setUp() {Mockito.reset(repoHistorial);}

    @Test
    public void queSeLlameAlRepoAlObtenerRankingGeneral() {
        givenRepoConRankingGeneralVacio();
        whenObtenerRankingGeneral();
        thenSeLlamaABuscarRankingGeneral();
    }

    private void givenRepoConRankingGeneralVacio() {
        when(repoHistorial.buscarRankingGeneral()).thenReturn(Collections.emptyList());
    }

    private List<RankingGeneralDTO> whenObtenerRankingGeneral() {return servicioRanking.obtenerRankingGeneral();}

    private void thenSeLlamaABuscarRankingGeneral() {
        verify(repoHistorial, times(1)).buscarRankingGeneral();
    }

    @Test
    public void queSeLlameAlRepoAlFiltrarPorNombre() {
        String nombreCuestionario = "Historia";

        givenRepoConRankingPorNombreVacio(nombreCuestionario);
        whenObtenerRankingCuestionarioPorNombre(nombreCuestionario);
        thenSeLlamaABuscarRankingCuestionarioPorNombre(nombreCuestionario);
    }

    private void givenRepoConRankingPorNombreVacio(String nombreCuestionario) {
        when(repoHistorial.buscarRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.emptyList());
    }

    private void whenObtenerRankingCuestionarioPorNombre(String nombreCuestionario) {
        servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario);
    }

    private void thenSeLlamaABuscarRankingCuestionarioPorNombre(String nombreCuestionario) {
        verify(repoHistorial, times(1)).buscarRankingCuestionarioPorNombre(nombreCuestionario);
    }

    @Test
    public void queSeLlameAlRepoAlFiltrarRankingPorId() {
        Long idCuestionario = 1L;

        givenRepoConRankingCuestionarioPorIdVacio(idCuestionario);
        whenObtenerRankingCuestionarioPorId(idCuestionario);
        thenSeLlamaABuscarRankingCuestionarioPorId(idCuestionario);
    }

    private void givenRepoConRankingCuestionarioPorIdVacio(Long idCuestionario) {
        when(repoHistorial.buscarRankingCuestionarioPorId(idCuestionario)).thenReturn(Collections.emptyList());
    }

    private void whenObtenerRankingCuestionarioPorId(Long idCuestionario) {
        servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario);
    }

    private void thenSeLlamaABuscarRankingCuestionarioPorId(Long idCuestionario) {
        verify(repoHistorial, times(1)).buscarRankingCuestionarioPorId(idCuestionario);
    }

    @Test
    public void queDevuelvaListVacioSiNoHayIntentosRegistrados() {
        givenRepoSinIntentosRegistrados();
        List<RankingGeneralDTO> ranking = whenObtenerRankingGeneral();
        thenRankingVacio(ranking);
    }

    private void givenRepoSinIntentosRegistrados() {
        when(repoHistorial.buscarRankingGeneral()).thenReturn(Collections.emptyList());
    }

    private List<RankingGeneralDTO> whenObtieneRankingGeneral() {return servicioRanking.obtenerRankingGeneral();}

    private void thenRankingVacio(List<RankingGeneralDTO> ranking) {assertEquals(0, ranking.size());}
}

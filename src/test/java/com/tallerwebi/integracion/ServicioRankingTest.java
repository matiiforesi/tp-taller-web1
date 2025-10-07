package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class ServicioRankingTest {

    private RepositorioHistorial repositorioHistorial = mock(RepositorioHistorial.class);
    private ServicioRankingImpl servicioRanking = new ServicioRankingImpl(mock(RepositorioUsuario.class), repositorioHistorial);

    @BeforeEach
    void setUp() {
        Mockito.reset(repositorioHistorial);
    }

    @Test
    public void queSeLlameAlRepoAlObtenerRankingGeneral() {
        givenRepoConRankingGeneralVacio();
        whenObtenerRankingGeneral();
        thenSeLlamaABuscarRankingGeneral();
    }

    private void givenRepoConRankingGeneralVacio() {
        when(repositorioHistorial.buscarRankingGeneral()).thenReturn(Collections.emptyList());
    }

    private void whenObtenerRankingGeneral() {
        servicioRanking.obtenerRankingGeneral();
    }

    private void thenSeLlamaABuscarRankingGeneral() {
        verify(repositorioHistorial, times(1)).buscarRankingGeneral();
    }

    @Test
    public void queSeLlameAlRepoAlFiltrarPorNombre() {
        String nombreCuestionario = "Historia";

        givenRepoConRankingPorNombreVacio(nombreCuestionario);
        whenObtenerRankingCuestionarioPorNombre(nombreCuestionario);
        thenSeLlamaABuscarRankingCuestionarioPorNombre(nombreCuestionario);
    }

    private void givenRepoConRankingPorNombreVacio(String nombreCuestionario) {
        when(repositorioHistorial.buscarRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.emptyList());
    }

    private void whenObtenerRankingCuestionarioPorNombre(String nombreCuestionario) {
        servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario);
    }

    private void thenSeLlamaABuscarRankingCuestionarioPorNombre(String nombreCuestionario) {
        verify(repositorioHistorial, times(1)).buscarRankingCuestionarioPorNombre(nombreCuestionario);
    }

    @Test
    public void queSeLlameAlRepoAlFiltrarRankingPorId() {
        Long idCuestionario = 1L;

        givenRepoConRankingCuestionarioPorIdVacio(idCuestionario);
        whenObtenerRankingCuestionarioPorId(idCuestionario);
        thenSeLlamaABuscarRankingCuestionarioPorId(idCuestionario);
    }

    private void givenRepoConRankingCuestionarioPorIdVacio(Long idCuestionario) {
        when(repositorioHistorial.buscarRankingCuestionarioPorId(idCuestionario)).thenReturn(Collections.emptyList());
    }

    private void whenObtenerRankingCuestionarioPorId(Long idCuestionario) {
        servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario);
    }

    private void thenSeLlamaABuscarRankingCuestionarioPorId(Long idCuestionario) {
        verify(repositorioHistorial, times(1)).buscarRankingCuestionarioPorId(idCuestionario);
    }
}

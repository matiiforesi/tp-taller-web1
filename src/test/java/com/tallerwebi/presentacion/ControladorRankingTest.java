package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.HistorialCuestionario;
import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControladorRankingTest {

    private ServicioRanking servicioRanking = mock(ServicioRanking.class);
    private ControladorRanking controladorRanking = new ControladorRanking(servicioRanking);

    @BeforeEach
    void setUp() {
        Mockito.reset(servicioRanking);
    }

    // Obtener ranking general
    // Obtener ranking por filtrado (nombre o id)
    // Caso sin jugadores

    @Test
    public void queSeLlameAlServicioYDevuelvaRankingGeneral() {
        givenServicioRankingGeneral();
        ModelAndView mav = whenObtieneRankingGeneral();
        thenSeRenderizaVistaRankingGeneral(mav);
    }

    private void givenServicioRankingGeneral() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        u1.setNombre("Mateo");
        u2.setNombre("Juan");
        // u1.setPuntaje(80L)
        // u2.setPuntaje(110L)
        when(servicioRanking.obtenerRankingGeneral()).thenReturn(Arrays.asList(u2, u1));
    }

    private ModelAndView whenObtieneRankingGeneral() {
        return controladorRanking.mostrarRankingGeneral();
    }

    private void thenSeRenderizaVistaRankingGeneral(ModelAndView mav) {
        verify(servicioRanking, times(1)).obtenerRankingGeneral();
        // assertEquals("ranking", mav.getViewName());
        List<Usuario> lista = (List<Usuario>) mav.getModel().get("rankingGeneral");
        assertEquals(2, lista.size());
    }

    @Test
    public void queSeLlameAlServicioYDevuelvaRankingFiltradoPorNombre() {
        String nombreCuestionario = "Historia";
        givenRankingFiltradoPorNombre(nombreCuestionario);
        ModelAndView mav = whenObtieneNombreCuestionario(nombreCuestionario);
        thenRenderizaVistaRankingFiltradoPorNombre(mav, nombreCuestionario);
    }

    private void givenRankingFiltradoPorNombre(String nombreCuestionario) {
        HistorialCuestionario h1 = new HistorialCuestionario();
        h1.setNombreUsuario("Matias");
        h1.setPuntaje(70L);
        // h1.setPreguntasCorrectas(7);
        // h1.setPreguntasErradas(3);
        when(servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.singletonList(h1));
    }

    private ModelAndView whenObtieneNombreCuestionario(String nombreCuestionario) {
        return controladorRanking.mostrarRankingCuestionario(nombreCuestionario, null);
    }

    private void thenRenderizaVistaRankingFiltradoPorNombre(ModelAndView mav, String nombreCuestionario) {
        verify(servicioRanking, times(1)).obtenerRankingCuestionarioPorNombre(nombreCuestionario);
        // assertEquals("ranking", mav.getViewName());
        List<HistorialCuestionario> lista = (List<HistorialCuestionario>) mav.getModel().get("rankingCuestionario");
        assertEquals(1, lista.size());
        assertEquals("Matias", lista.get(0).getNombreUsuario());
        assertEquals(nombreCuestionario, mav.getModel().get("nombreCuestionario"));
    }

    @Test
    public void queSeLlameAlServicioYDevuelvaRankingFiltradoPorId() {
        Long idCuestionario = 1L;

        givenRankingFiltradoPorId(idCuestionario);
        ModelAndView mav = whenObtieneIdCuestionario(idCuestionario);
        thenSeRenderizaVistaRankingFiltradoPorId(mav, idCuestionario);
    }

    private void givenRankingFiltradoPorId(Long idCuestionario) {
        HistorialCuestionario h1 = new HistorialCuestionario();
        h1.setNombreUsuario("Damian");
        h1.setNombreCuestionario("Deportes");
        h1.setPuntaje(40L);

        when(servicioRanking.obtenerRankingCuestionarioPorId(idCuestionario))
                .thenReturn(Collections.singletonList(h1));
    }

    private ModelAndView whenObtieneIdCuestionario(Long idCuestionario) {
        return controladorRanking.mostrarRankingCuestionario(null, idCuestionario);
    }

    private void thenSeRenderizaVistaRankingFiltradoPorId(ModelAndView mav, Long idCuestionario) {
        verify(servicioRanking, times(1)).obtenerRankingCuestionarioPorId(idCuestionario);
        List<HistorialCuestionario> lista = (List<HistorialCuestionario>) mav.getModel().get("rankingCuestionario");
        assertEquals(1, lista.size());
        assertEquals("Damian", lista.get(0).getNombreUsuario());
        assertEquals("Deportes", mav.getModel().get("nombreCuestionario"));
        assertEquals(idCuestionario, mav.getModel().get("idCuestionario"));
    }

    @Test
    public void siNoHayJugadoresElModeloEsteVacio() {
        String nombreCuestionario = "Geografía";

        givenServicioSinJugadores(nombreCuestionario);
        ModelAndView mav = whenObtieneNombreCuestionario(nombreCuestionario);
        thenElModeloDeRankingEstaVacio(mav, nombreCuestionario);
    }

    private void givenServicioSinJugadores(String nombreCuestionario) {
        when(servicioRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.emptyList());
    }

    private void thenElModeloDeRankingEstaVacio(ModelAndView mav, String nombreCuestionario) {
        verify(servicioRanking, times(1)).obtenerRankingCuestionarioPorNombre(nombreCuestionario);
        List<HistorialCuestionario> lista = (List<HistorialCuestionario>) mav.getModel().get("rankingCuestionario");
        assertTrue(lista.isEmpty());
    }
}

package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioRanking;
import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;
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

    private ServicioRanking servRanking = mock(ServicioRanking.class);

    private ControladorRanking controladorRanking = new ControladorRanking(servRanking);

    @BeforeEach
    void setUp() {
        Mockito.reset(servRanking);
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
        RankingGeneralDTO r1 = new RankingGeneralDTO(2L, "Juan", 110L);
        RankingGeneralDTO r2 = new RankingGeneralDTO(1L, "Mateo", 80L);
        when(servRanking.obtenerRankingGeneral()).thenReturn(Arrays.asList(r1, r2));
    }

    private ModelAndView whenObtieneRankingGeneral() {
        return controladorRanking.mostrarRankingGeneral();
    }

    private void thenSeRenderizaVistaRankingGeneral(ModelAndView mav) {
        verify(servRanking, times(1)).obtenerRankingGeneral();
        List<RankingGeneralDTO> lista = (List<RankingGeneralDTO>) mav.getModel().get("rankingGeneral");
        assertEquals(2, lista.size());
        assertEquals("Juan", lista.get(0).getNombreUsuario());
        assertEquals(110L, lista.get(0).getPuntajeTotal());
    }

    @Test
    public void queSeLlameAlServicioYDevuelvaRankingFiltradoPorNombre() {
        String nombreCuestionario = "Historia";
        givenRankingFiltradoPorNombre(nombreCuestionario);
        ModelAndView mav = whenObtieneNombreCuestionario(nombreCuestionario);
        thenRenderizaVistaRankingFiltradoPorNombre(mav, nombreCuestionario);
    }

    private void givenRankingFiltradoPorNombre(String nombreCuestionario) {
        RankingCuestionarioDTO dto = new RankingCuestionarioDTO(1L, "Matias", 70L, 7L, 3L, 2L, "");
        when(servRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.singletonList(dto));
    }

    private ModelAndView whenObtieneNombreCuestionario(String nombreCuestionario) {
        return controladorRanking.mostrarRankingCuestionario(nombreCuestionario, null);
    }

    private void thenRenderizaVistaRankingFiltradoPorNombre(ModelAndView mav, String nombreCuestionario) {
        verify(servRanking, times(1)).obtenerRankingCuestionarioPorNombre(nombreCuestionario);
        List<RankingCuestionarioDTO> lista = (List<RankingCuestionarioDTO>) mav.getModel().get("rankingCuestionario");
        assertEquals(1, lista.size());
        RankingCuestionarioDTO dto = lista.get(0);
        assertEquals("Matias", dto.getNombreUsuario());
        assertEquals(70L, dto.getPuntajeTotal());
        assertEquals(7L, dto.getPreguntasCorrectas());
        assertEquals(3L, dto.getPreguntasErradas());
        assertEquals(2L, dto.getNumeroIntentos());
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
        RankingCuestionarioDTO dto = new RankingCuestionarioDTO(1L, "Damian", 40L, 4L, 6L, 1L, "");
        when(servRanking.obtenerRankingCuestionarioPorId(idCuestionario))
                .thenReturn(Collections.singletonList(dto));
    }

    private ModelAndView whenObtieneIdCuestionario(Long idCuestionario) {
        return controladorRanking.mostrarRankingCuestionario(null, idCuestionario);
    }

    private void thenSeRenderizaVistaRankingFiltradoPorId(ModelAndView mav, Long idCuestionario) {
        verify(servRanking, times(1)).obtenerRankingCuestionarioPorId(idCuestionario);
        List<RankingCuestionarioDTO> lista = (List<RankingCuestionarioDTO>) mav.getModel().get("rankingCuestionario");
        assertEquals(1, lista.size());
        RankingCuestionarioDTO dto = lista.get(0);
        assertEquals("Damian", dto.getNombreUsuario());
        assertEquals(40L, dto.getPuntajeTotal());
        assertEquals(4L, dto.getPreguntasCorrectas());
        assertEquals(6L, dto.getPreguntasErradas());
        assertEquals(1L, dto.getNumeroIntentos());
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
        when(servRanking.obtenerRankingCuestionarioPorNombre(nombreCuestionario))
                .thenReturn(Collections.emptyList());
    }

    private void thenElModeloDeRankingEstaVacio(ModelAndView mav, String nombreCuestionario) {
        verify(servRanking, times(1)).obtenerRankingCuestionarioPorNombre(nombreCuestionario);
        List<RankingCuestionarioDTO> lista = (List<RankingCuestionarioDTO>) mav.getModel().get("rankingCuestionario");
        assertTrue(lista.isEmpty());
    }
}

package com.tallerwebi.integracion;
import com.tallerwebi.dominio.*;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class ControladorCuestionarioTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public ServicioTrivia servicioTrivia() {
            ServicioTrivia mock = mock(ServicioTrivia.class);

            RespuestaTrivia respuesta = new RespuestaTrivia();
            PreguntaTrivia pregunta = new PreguntaTrivia();
            pregunta.setQuestion("¿Cuál es la capital de Francia?");
            pregunta.setCorrect_answer("París");
            pregunta.setCategory("Geography");
            pregunta.setDifficulty("medium");
            pregunta.setIncorrect_answers(asList("Londres", "Madrid", "Roma"));
            respuesta.setResults(List.of(pregunta));

            when(mock.buscarPreguntas(org.mockito.ArgumentMatchers.anyInt(), 
                                    org.mockito.ArgumentMatchers.anyInt(), 
                                    org.mockito.ArgumentMatchers.anyString()))
                    .thenReturn(respuesta);
            return mock;
        }
    }

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ServicioDificultad servicioDificultad;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        setupDificultades();
    }

    private void setupDificultades() {
        Dificultad easy = new Dificultad();
        easy.setNombre("Easy");
        servicioDificultad.guardar(easy);

        Dificultad medium = new Dificultad();
        medium.setNombre("Medium");
        servicioDificultad.guardar(medium);

        Dificultad hard = new Dificultad();
        hard.setNombre("Hard");
        servicioDificultad.guardar(hard);
    }

    @Test
    public void debeRetornarListaDeCuestionarios() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/cuestionario/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("cuestionario_list"));
        assertThat(mv.getModel().containsKey("trivias"), is(true));
    }

    @Test
    public void debeMostrarFormularioNuevoCuestionario() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/cuestionario/new"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("cuestionario_form"));
    }
    @Test
    public void debeCrearCuestionarioYRedirigirALista() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/cuestionario/new")
                        .param("nombre", "Historia Argentina")
                        .param("descripcion", "Preguntas sobre historia argentina")
                        .param("amount", "5")
                        .param("category", "25")
                        .param("difficulty", "medium"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/cuestionario/list"));
    }


    // TODO - FIX limpiar BD antes/despues tests
//    @Test
//    public void debeCrearCuestionarioConDatosValidos() throws Exception {
//        // Given: Dificultad entities are already set up in @BeforeEach
//
//        // When: Creating a questionnaire
//        MvcResult result = this.mockMvc.perform(post("/cuestionario/new")
//                        .param("nombre", "Geografía Mundial")
//                        .param("descripcion", "Preguntas de geografía")
//                        .param("amount", "3")
//                        .param("category", "22")
//                        .param("difficulty", "easy"))
//                .andExpect(status().is3xxRedirection())
//                .andReturn();
//
//        // Then: Should redirect to list
//        ModelAndView mv = result.getModelAndView();
//        assert mv != null;
//        assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/cuestionario/list"));
//    }
}

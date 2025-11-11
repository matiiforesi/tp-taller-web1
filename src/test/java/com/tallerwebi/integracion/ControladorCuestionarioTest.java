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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class, ControladorCuestionarioTest.TestConfig.class})
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

            RespuestaTrivia respuestaMulti = new RespuestaTrivia();
            PreguntaTrivia preguntaEasy = new PreguntaTrivia();
            preguntaEasy.setQuestion("Easy question");
            preguntaEasy.setCorrect_answer("Easy answer");
            preguntaEasy.setCategory("Geography");
            preguntaEasy.setDifficulty("easy");
            preguntaEasy.setIncorrect_answers(asList("Wrong1", "Wrong2", "Wrong3"));

            PreguntaTrivia preguntaMedium = new PreguntaTrivia();
            preguntaMedium.setQuestion("Medium question");
            preguntaMedium.setCorrect_answer("Medium answer");
            preguntaMedium.setCategory("Geography");
            preguntaMedium.setDifficulty("medium");
            preguntaMedium.setIncorrect_answers(asList("Wrong1", "Wrong2", "Wrong3"));

            PreguntaTrivia preguntaHard = new PreguntaTrivia();
            preguntaHard.setQuestion("Hard question");
            preguntaHard.setCorrect_answer("Hard answer");
            preguntaHard.setCategory("Geography");
            preguntaHard.setDifficulty("hard");
            preguntaHard.setIncorrect_answers(asList("Wrong1", "Wrong2", "Wrong3"));

            respuestaMulti.setResults(asList(preguntaEasy, preguntaMedium, preguntaHard));

            when(mock.buscarPreguntas(org.mockito.ArgumentMatchers.anyInt(),
                    org.mockito.ArgumentMatchers.anyInt(),
                    org.mockito.ArgumentMatchers.argThat(arg -> 
                        arg != null && !arg.equalsIgnoreCase("multi"))))
                    .thenReturn(respuesta);

            when(mock.buscarPreguntas(org.mockito.ArgumentMatchers.anyInt(),
                    org.mockito.ArgumentMatchers.anyInt(),
                    org.mockito.ArgumentMatchers.argThat(arg -> 
                        arg != null && arg.equalsIgnoreCase("multi"))))
                    .thenReturn(respuestaMulti);

            RespuestaCategorias respuestaCategorias = new RespuestaCategorias();
            TriviaCategory categoria1 = new TriviaCategory();
            categoria1.setId(9);
            categoria1.setName("General Knowledge");
            
            TriviaCategory categoria2 = new TriviaCategory();
            categoria2.setId(22);
            categoria2.setName("Geography");
            
            TriviaCategory categoria3 = new TriviaCategory();
            categoria3.setId(23);
            categoria3.setName("History");
            
            respuestaCategorias.setTriviaCategories(asList(categoria1, categoria2, categoria3));

            when(mock.obtenerCategorias()).thenReturn(respuestaCategorias);

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

        Dificultad multi = new Dificultad();
        multi.setNombre("Multi");
        servicioDificultad.guardar(multi);
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
        assertThat(mv.getModel().containsKey("cuestionario"), is(true));
    }

    @Test
    public void debeMostrarFormularioConCategorias() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/cuestionario/new"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("cuestionario_form"));
        assertThat(mv.getModel().containsKey("triviaCategories"), is(true));
        assertThat(mv.getModel().get("triviaCategories"), is(notNullValue()));
        
        @SuppressWarnings("unchecked")
        List<TriviaCategory> categorias = (List<TriviaCategory>) mv.getModel().get("triviaCategories");
        assertThat(categorias.size(), is(3));
        assertThat(categorias.get(0).getName(), equalToIgnoringCase("General Knowledge"));
        assertThat(categorias.get(1).getName(), equalToIgnoringCase("Geography"));
        assertThat(categorias.get(2).getName(), equalToIgnoringCase("History"));
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

    @Test
    public void debeCrearCuestionarioConDificultadMulti() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/cuestionario/new")
                        .param("nombre", "Trivia Multi Dificultad")
                        .param("descripcion", "Preguntas de todas las dificultades")
                        .param("amount", "10")
                        .param("category", "22")
                        .param("difficulty", "multi"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/cuestionario/list"));
    }

    @Test
    public void debeCrearCuestionarioConDificultadMultiCaseInsensitive() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/cuestionario/new")
                        .param("nombre", "Trivia Multi")
                        .param("descripcion", "Preguntas variadas")
                        .param("amount", "5")
                        .param("category", "25")
                        .param("difficulty", "MULTI"))
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

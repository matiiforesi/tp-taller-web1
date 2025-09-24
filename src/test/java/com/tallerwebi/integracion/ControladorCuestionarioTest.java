package com.tallerwebi.integracion;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class ControladorCuestionarioTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
                        .param("nombre", "Historia")
                        .param("descripcion", "Preguntas de historia")
                        .param("cantidadPreguntas", "5")
                        .param("categoria", "General")
                        .param("dificultad", "medium"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        ModelAndView mv = result.getModelAndView();
        assert mv != null;
        assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/cuestionario/list"));
    }

    // TODO - test assert database
}

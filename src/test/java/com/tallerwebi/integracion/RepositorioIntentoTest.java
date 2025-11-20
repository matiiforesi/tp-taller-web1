package com.tallerwebi.integracion;

import com.tallerwebi.dominio.IntentoCuestionario;
import org.hibernate.Session;
import com.tallerwebi.infraestructura.RepositorioIntentoCuestionarioImpl;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RepositorioIntentoTest {

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session session = mock(Session.class);
    private Criteria criteria = mock(Criteria.class);

    @Autowired
    private RepositorioIntentoCuestionarioImpl repositorioIntentoCuestionario = new RepositorioIntentoCuestionarioImpl(sessionFactory);


    @Test
    public void queSeBusqueIntentoConUsuarioYCuestionario() {
        IntentoCuestionario intento = givenIntentoCuestionario();
        IntentoCuestionario resultado = whenIntentoCuestionario();
        thenIntentoCuestionario(intento, resultado);
    }

    private void thenIntentoCuestionario(IntentoCuestionario esperado, IntentoCuestionario intentoCuestionario) {
        assertEquals(esperado, intentoCuestionario);
    }

    private IntentoCuestionario whenIntentoCuestionario() {
        return repositorioIntentoCuestionario.buscarPorUsuarioYCuestionario(1L, 2L);
    }

    public IntentoCuestionario givenIntentoCuestionario() {
        IntentoCuestionario intentoCuestionario = new IntentoCuestionario();
        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createCriteria(IntentoCuestionario.class)).thenReturn(criteria);
        Mockito.when(criteria.createAlias(anyString(), anyString())).thenReturn(criteria);
        Mockito.when(criteria.add(any())).thenReturn(criteria);
        Mockito.when(criteria.uniqueResult()).thenReturn(intentoCuestionario);

        return intentoCuestionario;
    }
}

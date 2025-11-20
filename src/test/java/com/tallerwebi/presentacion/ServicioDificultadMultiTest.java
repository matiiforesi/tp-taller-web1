package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Dificultad;
import com.tallerwebi.dominio.RepositorioDificultad;
import com.tallerwebi.dominio.ServicioDificultad;
import com.tallerwebi.dominio.ServicioDificultadImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ServicioDificultadMultiTest {

    private ServicioDificultad servicioDificultad;
    private RepositorioDificultad repositorioDificultadMock;

    @BeforeEach
    public void init() {
        repositorioDificultadMock = mock(RepositorioDificultad.class);
        servicioDificultad = new ServicioDificultadImpl(repositorioDificultadMock);
    }

    @Test
    public void debeCalcularMultiplicadorMultiComoDos() {
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("Multi");
        dificultadMulti.setMultiplicadorDificultad(null);

        int multiplicador = servicioDificultad.calcularMultiplicador(dificultadMulti);

        assertThat(multiplicador, is(2));
    }

    @Test
    public void debeCalcularMultiplicadorMultiCaseInsensitive() {
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("multi"); // lowercase
        dificultadMulti.setMultiplicadorDificultad(null);

        int multiplicador = servicioDificultad.calcularMultiplicador(dificultadMulti);

        assertThat(multiplicador, is(2));
    }

    @Test
    public void debeUsarMultiplicadorExplicitoSiEstaDefinido() {
        Dificultad dificultadMulti = new Dificultad();
        dificultadMulti.setNombre("Multi");
        dificultadMulti.setMultiplicadorDificultad(5);

        int multiplicador = servicioDificultad.calcularMultiplicador(dificultadMulti);

        assertThat(multiplicador, is(5));
    }
}

package com.tallerwebi.integracion;

import com.tallerwebi.dominio.RepositorioCuestionario;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioAdmin;
import com.tallerwebi.dominio.ServicioAdminImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicioAdminTest {

    private RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
    private RepositorioCuestionario repositorioCuestionario = mock(RepositorioCuestionario.class);
    private ServicioAdmin servicioAdmin = new ServicioAdminImpl(repositorioUsuario, repositorioCuestionario);

    @Test
    public void queDevuelvaLaCantidadDeUsuarios() {
        givenContarCantidadUsuarios();
        Integer obtenido = whenContarCantidadUsuarios();
        thenContarCantidadUsuarios(5, obtenido);

    }

    @Test
    public void queDevuelvaLaCantidadDeCuestionarios() {
        givenContarCantidadCuestionarios();
        Integer obtenido = whenContarCantidadCuestionarios();
        thenContarCantidadCuestionarios(5, obtenido);
    }

    private void thenContarCantidadCuestionarios(int i, Integer obtenido) {assertEquals(i, obtenido);}

    private Integer whenContarCantidadCuestionarios() {return servicioAdmin.contarCuestionarios();}

    private void givenContarCantidadCuestionarios() {when(servicioAdmin.contarCuestionarios()).thenReturn(5);}

    private void thenContarCantidadUsuarios(Integer esperado, Integer obtenido) {assertEquals(esperado, obtenido);}

    private Integer whenContarCantidadUsuarios() {return servicioAdmin.contarUsuarios();}

    private void givenContarCantidadUsuarios() {when(servicioAdmin.contarUsuarios()).thenReturn(5);}
}

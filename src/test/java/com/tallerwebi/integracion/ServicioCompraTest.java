package com.tallerwebi.integracion;

import com.tallerwebi.dominio.*;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicioCompraTest {


    private RepositorioUsuario repoUsuario=mock(RepositorioUsuario.class);
    private RepositorioItem repoItem=mock(RepositorioItem.class);
    private RepositorioCompraItem repoCompraItem=mock(RepositorioCompraItem.class);
    private ServicioCompra servicioCompra = new ServicioCompraImpl(repoUsuario,repoItem,repoCompraItem);
    private Usuario usuario= new Usuario();
    private Item item= new Item();

    @Test
    public void queSePuedaComprarItem(){

       givenCreacionUsuarioEItem();
       Boolean obtenido= whenSeCompre();
       thenUsuarioCompro(obtenido);
    }
    @Test
    public void queNoSeEfectueLaCompraSiElUsuarioNoTieneMonedasSuficientes(){
        usuario.setId(1L);
        usuario.setMonedas(90L);

        item.setId(1L);
        item.setPrecio(100L);

        Boolean obtenido= whenSeCompre();

        assertEquals(90,usuario.getMonedas());
        assertFalse(obtenido);
    }

    private void thenUsuarioCompro(Boolean obtenido) {
        assertTrue(obtenido);
        assertEquals(1000L,usuario.getPuntaje());
        assertEquals(0,usuario.getMonedas());
    }

    private Boolean whenSeCompre() {
        when(repoUsuario.buscarPorId(1L)).thenReturn(usuario);
        when(repoItem.obtenerPorId(1L)).thenReturn(item);
        return servicioCompra.comprarItem(1L,1L);
    }

    private void givenCreacionUsuarioEItem() {

        usuario.setId(1L);
        usuario.setNombre("Mateo");
        usuario.setMonedas(100L);
        usuario.setPuntaje(1000L);


        item.setId(1L);
        item.setTipoItem(TIPO_ITEMS.DUPLICAR_PUNTAJE);
        item.setPrecio(100L);
    }

}

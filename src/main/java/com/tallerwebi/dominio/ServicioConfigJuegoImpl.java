package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.RepositorioConfigJuegoImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ServicioConfigJuegoImpl implements ServicioConfigJuego {
    private final RepositorioConfigJuego repositorioConfigJuego;

    public  ServicioConfigJuegoImpl(RepositorioConfigJuego repositorioConfigJuego) {
        this.repositorioConfigJuego = repositorioConfigJuego;
    }
    @Override
    public String getValor(String clave, String valorPorDefecto) {
        return this.repositorioConfigJuego.getValor(clave, valorPorDefecto);
    }

    @Override
    public Integer getInt(String clave, Integer valorPorDefecto) {
        return this.repositorioConfigJuego.getInt(clave, valorPorDefecto);
    }
    @Override
    public void save(ConfiguracionJuego configuracionJuego) {
        this.repositorioConfigJuego.save(configuracionJuego);
    }
}

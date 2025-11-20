package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.ConfiguracionJuego;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import com.tallerwebi.dominio.RepositorioConfigJuego;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioConfigJuegoImpl implements RepositorioConfigJuego {
    private SessionFactory sessionFactory;

    public RepositorioConfigJuegoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String getValor(String clave, String valorPorDefecto) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RepositorioConfigJuego.class);
        ConfiguracionJuego config = (ConfiguracionJuego) criteria.add(Restrictions.eq("clave", clave)).uniqueResult();

        return (config != null) ? config.getValor() : valorPorDefecto;
    }

    @Override
    public Integer getInt(String clave, Integer valorPorDefecto) {
        String valor = getValor(clave, String.valueOf(valorPorDefecto));
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    @Override
    public void save(ConfiguracionJuego configuracionJuego) {
        sessionFactory.getCurrentSession().save(configuracionJuego);
    }
}

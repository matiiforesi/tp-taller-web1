package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.CompraItem;
import com.tallerwebi.dominio.RepositorioCompraItem;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioCompraItemImpl implements RepositorioCompraItem {

    private SessionFactory sessionFactory;

    public RepositorioCompraItemImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(CompraItem compra) {
        this.sessionFactory.getCurrentSession().save(compra);
    }
}

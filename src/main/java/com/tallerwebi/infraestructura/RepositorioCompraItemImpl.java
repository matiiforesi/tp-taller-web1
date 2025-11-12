package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.CompraItem;
import com.tallerwebi.dominio.RepositorioCompraItem;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<CompraItem> obtenerComprasPorUsuario(Long idUsuario) {
        return sessionFactory.getCurrentSession().createCriteria(CompraItem.class)
                .add(Restrictions.eq("usuario.id",idUsuario)).list();
    }

    @Override
    public void modificar(CompraItem compra) {
        sessionFactory.getCurrentSession().update(compra);
    }
}

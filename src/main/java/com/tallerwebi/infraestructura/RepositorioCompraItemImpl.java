package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.CompraItem;
import com.tallerwebi.dominio.RepositorioCompraItem;
import com.tallerwebi.dominio.TIPO_ITEMS;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
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

    @Override
    public Long contarComprasPorUsuarioYTipo(Long idUsuario, TIPO_ITEMS tipo) {
        Long resultado= (Long)sessionFactory.getCurrentSession().createCriteria(CompraItem.class,"compra")
                .createAlias("compra.item","item")
                .add(Restrictions.eq("compra.usuario.id",idUsuario))
                .add(Restrictions.eq("item.tipoItem",tipo))
                .add(Restrictions.eq("compra.usado",false))
                .setProjection(Projections.rowCount()).uniqueResult();
        return resultado!=null ? resultado : 0L;
    }
}

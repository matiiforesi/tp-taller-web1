package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Item;
import com.tallerwebi.dominio.RepositorioItem;
import jdk.jfr.consumer.RecordedStackTrace;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioItemImpl implements RepositorioItem {

    private SessionFactory sessionFactory;
    public RepositorioItemImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public void guardar(Item item) {
        this.sessionFactory.getCurrentSession().save(item);
    }

    @Override
    public void actualizar(Item item) {
        this.sessionFactory.getCurrentSession().update(item);
    }

    @Override
    public Item obtenerPorId(Long id) {
        return (Item)this.sessionFactory.getCurrentSession().createCriteria(Item.class)
                .add(Restrictions.eq("id",id)).uniqueResult();
    }

    @Override
    public List<Item> obtenerTodos() {
        return (List<Item>) sessionFactory.getCurrentSession().createCriteria(Item.class)
                .list();
    }
}

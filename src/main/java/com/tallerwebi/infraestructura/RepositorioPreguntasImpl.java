package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Preguntas;
import com.tallerwebi.dominio.RepositorioPreguntas;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioPreguntasImpl implements RepositorioPreguntas {

    private SessionFactory sessionFactory;

    public RepositorioPreguntasImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    @Override
    public Preguntas buscarPregunta(Long idPregunta) {
       final Session session = sessionFactory.getCurrentSession();
        return (Preguntas) session.createCriteria(Preguntas.class)
                .add(Restrictions.eq("id", idPregunta))
                .uniqueResult();
    }

    @Override
    public void guardar(Preguntas pregunta) {
       sessionFactory.getCurrentSession().save(pregunta);
    }

    @Override
    public void modificar(Preguntas pregunta) {
        sessionFactory.getCurrentSession().update(pregunta);
    }

    @Override
    public Preguntas buscarPorCategoria(String categoria){
        final Session session = sessionFactory.getCurrentSession();
        return (Preguntas) session.createCriteria(Preguntas.class);
    }
}

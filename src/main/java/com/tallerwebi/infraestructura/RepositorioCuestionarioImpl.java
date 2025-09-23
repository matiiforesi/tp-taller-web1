package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.RepositorioCuestionario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioCuestionario")
public class RepositorioCuestionarioImpl implements RepositorioCuestionario {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioCuestionarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public void guardar(Cuestionario cuestionario) {
        sessionFactory.getCurrentSession().save(cuestionario);
    }

    @Override
    public Cuestionario buscar(Long id) {

        final Session session = sessionFactory.getCurrentSession();
        return (Cuestionario) session.createCriteria(Cuestionario.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
    }

    @Override
    public void modificar(Cuestionario cuestionario) {
        sessionFactory.getCurrentSession().update(cuestionario);
    }

    @Override
    public Cuestionario buscarPorCategoria(String categoria) {

        final Session session = sessionFactory.getCurrentSession();
        return (Cuestionario) session.createCriteria(Cuestionario.class)     //con esto te devuelve la pregunta que coincida con la categoria de los param
                .add(Restrictions.eq("categoria", categoria)).uniqueResult();
    }

    @Override
    public Cuestionario buscarPorDificultad(String dificultad) {
        final Session session = sessionFactory.getCurrentSession();
        return (Cuestionario) session.createCriteria(Cuestionario.class)
                .add(Restrictions.eq("dificultad", dificultad))
                .uniqueResult();
    }


//    void guardar(Cuestionario cuestionario);
//    Cuestionario buscar(Integer id);
//    void modificar(Cuestionario cuestionario);
//    Cuestionario buscarPorCategoria(String categoria);
//    Cuestionario buscarPorDificultad(String dificultad);



}

package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.RepositorioCuestionario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("repositorioCuestionario")
public class RepositorioCuestionarioImpl implements RepositorioCuestionario {

    private SessionFactory sessionFactory;

    @Override
    public void guardar(Cuestionario cuestionario) {

    }

    @Override
    public Cuestionario buscar(Integer id) {
        return null;
    }

    @Override
    public void modificar(Cuestionario cuestionario) {

    }

    @Override
    public Cuestionario buscarPorCategoria(String categoria) {
        return null;
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

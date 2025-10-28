package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Cuestionario;
import com.tallerwebi.dominio.RepositorioCuestionario;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

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
    public List<Cuestionario> buscarPorCategoria(String categoria) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Cuestionario.class)
                .add(Restrictions.eq("categoria", categoria))
                .list();
    }

    @Override
    public List<Cuestionario> buscarPorDificultad(String dificultad) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Cuestionario.class)
                .createAlias("dificultad", "dif")
                .add(Restrictions.eq("dif.nombre", dificultad))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public List<Cuestionario> buscarTodo(){
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Cuestionario", Cuestionario.class).list();
        }
    }

    @Override
    public Integer contarCuestionarios(){
        Long cantidad=(Long)sessionFactory.getCurrentSession()
                .createQuery("select count(*) from Cuestionario").uniqueResult();
        return cantidad!=null?cantidad.intValue():0;
    }

    @Override
    public List<Cuestionario> filtrarPorDificultadYCategoria(String dificultad, String categoria) {
        final Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Cuestionario.class);

        if (dificultad != null && !dificultad.isEmpty()) {
            criteria.createAlias("dificultad", "dif")
                    .add(Restrictions.eq("dif.nombre", dificultad));
        }

        if (categoria != null && !categoria.isEmpty()) {
            criteria.add(Restrictions.eq("categoria", categoria));
        }

        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public List<String> obtenerTodasLasCategorias() {
        final Session session = sessionFactory.getCurrentSession();
        List<String> categorias = session.createQuery("SELECT DISTINCT categoria FROM Cuestionario ORDER BY categoria").list();
        return categorias;
    }
}

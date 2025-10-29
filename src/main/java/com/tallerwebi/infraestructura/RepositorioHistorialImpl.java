package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.HistorialCuestionario;
import com.tallerwebi.dominio.RepositorioHistorial;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioHistorialImpl implements RepositorioHistorial {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioHistorialImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(HistorialCuestionario historialCuestionario) {
        sessionFactory.getCurrentSession().save(historialCuestionario);
    }

    @Override
    public HistorialCuestionario buscar(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return (HistorialCuestionario) session.createCriteria(HistorialCuestionario.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
    }

    @Override
    public void modificar(HistorialCuestionario historialCuestionario) {
        sessionFactory.getCurrentSession().update(historialCuestionario);
    }

    @Override
    public List<Usuario> buscarRankingGeneral() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "select u " +
                        "from Usuario u " +
                        "join HistorialCuestionario h on h.jugador.id = u.id " +
                        "group by u.id " +
                        "order by sum (h.puntaje) desc",
                Usuario.class).list();
    }

    @Override
    public List<HistorialCuestionario> buscarRankingCuestionarioPorNombre(String nombreCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "from HistorialCuestionario h " +
                        "where h.nombreCuestionario = :nombreCuestionario " +
                        "order by h.puntaje desc",
                HistorialCuestionario.class
        ).setParameter("nombreCuestionario", nombreCuestionario).list();
    }

    @Override
    public List<HistorialCuestionario> buscarRankingCuestionarioPorId(Long idCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "from HistorialCuestionario h " +
                        "where h.idCuestionario = :idCuestionario " +
                        "order by h.puntaje desc",
                HistorialCuestionario.class
        ).setParameter("idCuestionario", idCuestionario).list();
    }

    @Override
    public List<Object[]> buscarRankingCuestionarioAgregadoPorId(Long idCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "select h.jugador.id, h.nombreUsuario, sum(h.puntaje), sum(h.preguntasCorrectas), sum(h.preguntasErradas), count(h) " +
                        "from HistorialCuestionario h " +
                        "where h.idCuestionario = :idCuestionario " +
                        "group by h.jugador.id, h.nombreUsuario " +
                        "order by sum(h.puntaje) desc", Object[].class)
                .setParameter("idCuestionario", idCuestionario).list();
    }

    @Override
    public List<Object[]> buscarRankingCuestionarioAgregadoPorNombre(String nombreCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "select h.jugador.id, h.nombreUsuario, sum(h.puntaje), sum(h.preguntasCorrectas), sum(h.preguntasErradas), count(h) " +
                                "from HistorialCuestionario h " +
                                "where h.nombreCuestionario = :nombreCuestionario " +
                                "group by h.jugador.id, h.nombreUsuario " +
                                "order by sum(h.puntaje) desc", Object[].class)
                .setParameter("nombreCuestionario", nombreCuestionario).list();
    }
}

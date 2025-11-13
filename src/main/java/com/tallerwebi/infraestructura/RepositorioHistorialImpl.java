package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.HistorialCuestionario;
import com.tallerwebi.dominio.RepositorioHistorial;
import com.tallerwebi.dominio.dto.RankingCuestionarioDTO;
import com.tallerwebi.dominio.dto.RankingGeneralDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<RankingGeneralDTO> buscarRankingGeneral() {
        final Session session = sessionFactory.getCurrentSession();
//        return session.createCriteria(HistorialCuestionario.class, "h")
//                .createAlias("h.jugador", "u")
//                .setProjection(Projections.projectionList()
//                        .add(Projections.groupProperty("u.id"))
//                        .add(Projections.groupProperty("u.nombre"))
//                        .add(Projections.sum("h.puntaje").as("puntajeTotal"))
//                )
//                .addOrder(Order.desc("puntajeTotal"))
//                .list();

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = session.createCriteria(HistorialCuestionario.class, "h")
                .createAlias("h.jugador", "u")
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("u.id"))
                        .add(Projections.groupProperty("u.nombre"))
                        .add(Projections.sum("h.puntaje").as("puntajeTotal"))
                )
                .addOrder(Order.desc("puntajeTotal"))
                .list();

        return resultados.stream()
                .map(RankingGeneralDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RankingCuestionarioDTO> buscarRankingCuestionarioPorId(Long idCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
//        return session.createCriteria(HistorialCuestionario.class, "h")
//                .add(Restrictions.eq("idCuestionario", idCuestionario))
//                .setProjection(Projections.projectionList()
//                        .add(Projections.groupProperty("jugador.id"))
//                        .add(Projections.groupProperty("nombreUsuario"))
//                        .add(Projections.sum("puntaje").as("puntajeTotal"))
//                        .add(Projections.sum("preguntasCorrectas"))
//                        .add(Projections.sum("preguntasErradas"))
//                        .add(Projections.count("id"))
//                ).addOrder(Order.desc("puntajeTotal")).list();

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = session.createCriteria(HistorialCuestionario.class, "h")
                .add(Restrictions.eq("idCuestionario", idCuestionario))
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("jugador.id"))
                        .add(Projections.groupProperty("nombreUsuario"))
                        .add(Projections.sum("puntaje").as("puntajeTotal"))
                        .add(Projections.sum("preguntasCorrectas"))
                        .add(Projections.sum("preguntasErradas"))
                        .add(Projections.count("id"))
                ).addOrder(Order.desc("puntajeTotal"))
                .list();

        return resultados.stream()
                .map(RankingCuestionarioDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RankingCuestionarioDTO> buscarRankingCuestionarioPorNombre(String nombreCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
//        return session.createCriteria(HistorialCuestionario.class, "h")
//                .add(Restrictions.eq("nombreCuestionario", nombreCuestionario))
//                .setProjection(Projections.projectionList()
//                        .add(Projections.groupProperty("jugador.id"))
//                        .add(Projections.groupProperty("nombreUsuario"))
//                        .add(Projections.sum("puntaje").as("puntajeTotal"))
//                        .add(Projections.sum("preguntasCorrectas"))
//                        .add(Projections.sum("preguntasErradas"))
//                        .add(Projections.count("id"))
//                ).addOrder(Order.desc("puntajeTotal")).list();

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = session.createCriteria(HistorialCuestionario.class, "h")
                .add(Restrictions.eq("nombreCuestionario", nombreCuestionario))
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("jugador.id"))
                        .add(Projections.groupProperty("nombreUsuario"))
                        .add(Projections.sum("puntaje").as("puntajeTotal"))
                        .add(Projections.sum("preguntasCorrectas"))
                        .add(Projections.sum("preguntasErradas"))
                        .add(Projections.count("id"))
                ).addOrder(Order.desc("puntajeTotal"))
                .list();

        return resultados.stream()
                .map(RankingCuestionarioDTO::new)
                .collect(Collectors.toList());
    }
}

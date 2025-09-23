package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.HistorialCuestionario;
import com.tallerwebi.dominio.RepositorioHistorial;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioHistorialImpl implements RepositorioHistorial {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Usuario> obtenerRankingGeneral() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "select u " +
                        "from Usuario u " +
                        "join HistorialCuestionario h on h.jugador.id = u.id " +
                        "group by u.id " +
                        "order by sum (h.puntaje) desc",
                Usuario.class
        ).list();
    }

    @Override
    public List<HistorialCuestionario> obtenerRankingCuestionario(String nombreCuestionario) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "from HistorialCuestionario h " +
                        "where h.nombreCuestionario = :nombreCuestionario " +
                        "order by h.puntaje desc",
                HistorialCuestionario.class
        ).setParameter("nombreCuestionario", nombreCuestionario).list();
    }
}

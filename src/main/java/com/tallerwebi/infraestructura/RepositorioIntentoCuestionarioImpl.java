package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.IntentoCuestionario;
import com.tallerwebi.dominio.RepositorioIntento;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioIntentoCuestionarioImpl implements RepositorioIntento {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioIntentoCuestionarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public IntentoCuestionario buscarPorUsuarioYCuestionario(Long idUsuario, Long idCuestionario) {
        return (IntentoCuestionario) sessionFactory.getCurrentSession().createCriteria(IntentoCuestionario.class)
                .createAlias("usuario", "u")
                .createAlias("cuestionario", "c")
                .add(Restrictions.eq("u.id", idUsuario))
                .add(Restrictions.eq("c.id", idCuestionario))
                .uniqueResult();
    }

    @Override
    public void guardar(IntentoCuestionario intentoCuestionario) {
        sessionFactory.getCurrentSession().save(intentoCuestionario);
    }

    @Override
    public void actualizar(IntentoCuestionario intentoCuestionario) {
        sessionFactory.getCurrentSession().update(intentoCuestionario);
    }

    @Override
    public Integer contarIntentos(Long idUsuario, Long idCuestionario) {
        Long cantidad = (Long) sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(i) FROM IntentoCuestionario i " +
                        "WHERE i.usuario.id = :idUsuario AND i.cuestionario.id = :idCuestionario")
                .setParameter("idUsuario", idUsuario)
                .setParameter("idCuestionario", idCuestionario)
                .uniqueResult();

        return cantidad != null ? cantidad.intValue() : 0;
    }
}

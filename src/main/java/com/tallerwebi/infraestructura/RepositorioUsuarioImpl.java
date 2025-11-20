package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String email, String password) {
        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
    }

    @Override
    public void guardar(Usuario usuario) {sessionFactory.getCurrentSession().save(usuario);}

    @Override
    public Usuario buscar(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {sessionFactory.getCurrentSession().update(usuario);}

    @Override
    public Usuario buscarPorId(Long idUsuario) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class).add(Restrictions.eq("id", idUsuario)).uniqueResult();
    }

    @Override
    public Integer contarUsuarios() {
        Long cantidad = (Long) sessionFactory.getCurrentSession().
                createQuery("SELECT COUNT(u) FROM Usuario u").uniqueResult();
        return cantidad != null ? cantidad.intValue() : 0;
    }

    @Override
    public void actualizarMonedas(Long idUsuario, Long nuevasMonedas) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE Usuario SET monedas = :monedas WHERE id = :id")
                .setParameter("monedas", nuevasMonedas)
                .setParameter("id", idUsuario)
                .executeUpdate();
    }
}

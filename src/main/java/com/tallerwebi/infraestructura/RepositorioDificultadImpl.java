package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Dificultad;
import com.tallerwebi.dominio.RepositorioDificultad;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioDificultadImpl implements RepositorioDificultad {

	private SessionFactory sessionFactory;

	@Autowired
	public RepositorioDificultadImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Dificultad buscarPorNombre(String nombre) {
		final Session session = sessionFactory.getCurrentSession();
		return (Dificultad) session.createCriteria(Dificultad.class)
				.add(Restrictions.eq("nombre", nombre))
				.uniqueResult();
	}

	@Override
	public Dificultad buscarPorId(Long id) {
		final Session session = sessionFactory.getCurrentSession();
		return (Dificultad) session.createCriteria(Dificultad.class)
				.add(Restrictions.eq("id", id))
				.uniqueResult();
	}

	@Override
	public void guardar(Dificultad dificultad) {
		sessionFactory.getCurrentSession().save(dificultad);
	}

	@Override
	public void modificar(Dificultad dificultad) {
		sessionFactory.getCurrentSession().update(dificultad);
	}
}



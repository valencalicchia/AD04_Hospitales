package Helpers;

import java.util.List;

import org.hibernate.Session;

import Dtos.NuevasSalas;

public class NuevasSalasHelper {
    private final Session session;

    public NuevasSalasHelper(Session session) {
        this.session = session;
    }

    public List<NuevasSalas> getAll() {
        return getEntities("from NuevasSalas", NuevasSalas.class);
    }

    private <T> List<T> getEntities(String hql, Class<T> entityClass) {
        return session.createQuery(hql, entityClass).getResultList();
    }
}

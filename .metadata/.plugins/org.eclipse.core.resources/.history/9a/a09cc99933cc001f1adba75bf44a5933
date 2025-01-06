package Helpers;

import org.hibernate.Session;
import org.hibernate.Transaction;

import Dtos.Sala;
import Dtos.SalaId;

import java.util.List;

public class SalaHelper {


    private final Session session;

    public SalaHelper(Session session) {
        this.session = session;
    }

    public List<Sala> getAll() {
        return getEntities("from Sala s order by s.id.hospitalCod, s.id.salaCod", Sala.class);
    }

    public boolean existSala(SalaId salaId) {
        return session.find(Sala.class, salaId) != null;
    }

    public Sala insertSala(Sala sala) {
        session.persist(sala);
        return sala;
    }


    private <T> List<T> getEntities(String hql, Class<T> entityClass) {
        return session.createQuery(hql, entityClass).getResultList();
    }


    @FunctionalInterface
    private interface TransactionAction<T> {
        T execute(Session session);
    }
	
}

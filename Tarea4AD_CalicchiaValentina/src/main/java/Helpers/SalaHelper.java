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
        return session.createQuery("from Sala s order by s.id.hospitalCod, s.id.salaCod", Sala.class)
                      .getResultList();
    }

    public boolean existSala(SalaId salaId) {
        return session.find(Sala.class, salaId) != null;
    }

    public Sala insertSala(Sala sala) {
        session.persist(sala);
        return sala;
    }

    public List<Object[]> obtenerPlantillaPorSala(int hospitalCod, int salaCod) {
        String hql = """
            SELECT p.apellido, p.salario 
            FROM Plantilla p 
            WHERE p.sala.id.hospitalCod = :hospitalCod AND p.sala.id.salaCod = :salaCod 
            ORDER BY p.apellido DESC
        """;

        return session.createQuery(hql, Object[].class)
                      .setParameter("hospitalCod", hospitalCod)
                      .setParameter("salaCod", salaCod)
                      .getResultList();
    }

    public Double calcularSalarioMedio(int hospitalCod, int salaCod) {
        String hql = """
            SELECT AVG(p.salario) 
            FROM Plantilla p 
            WHERE p.sala.id.hospitalCod = :hospitalCod AND p.sala.id.salaCod = :salaCod
        """;

        return session.createQuery(hql, Double.class)
                      .setParameter("hospitalCod", hospitalCod)
                      .setParameter("salaCod", salaCod)
                      .uniqueResult();
    }
}
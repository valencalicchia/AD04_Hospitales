package Helpers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import Dtos.Hospitales;

public class HospitalesHelper {
    private final Session session;

    public HospitalesHelper(Session session) {
        this.session = session;
    }

    public List<Hospitales> getAll() {
        return session.createQuery("from Hospitales", Hospitales.class).getResultList();
    }

    public boolean existingHospital(byte hospitalCod) {
        Hospitales hospital = session.find(Hospitales.class, hospitalCod);
        return hospital != null;
    }

    public Map<Byte, String> traerIdAndNombre() {
        return getAll().stream()
                .collect(Collectors.toMap(Hospitales::getHospitalCod, Hospitales::getNombre));
    }

    public short numDoctoresHospital(byte hospitalCod) {
        return countEntitiesByHospitalCod("SELECT count(d) FROM Doctor d JOIN d.hospitaleses h WHERE h.hospitalCod = :hospitalCod", hospitalCod);
    }

    public short numPacientesHospital(byte hospitalCod) {
        return countEntitiesByHospitalCod("SELECT count(e) FROM Enfermo e JOIN e.ocupacions o WHERE o.hospitales.hospitalCod = :hospitalCod", hospitalCod);
    }

    public short numPlantillaHospital(byte hospitalCod) {
        return countEntitiesByHospitalCod("SELECT count(d) FROM Plantilla d JOIN d.hospitales h WHERE h.hospitalCod = :hospitalCod", hospitalCod);
    }

    public short numSalasHospital(byte hospitalCod) {
        return countEntitiesByHospitalCod("SELECT count(d) FROM Sala d JOIN d.hospitales h WHERE h.hospitalCod = :hospitalCod", hospitalCod);
    }


    private short countEntitiesByHospitalCod(String hql, byte hospitalCod) {
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("hospitalCod", hospitalCod);
        Long result = query.uniqueResult();
        return (result != null) ? result.shortValue() : 0;
    }
}

package Helpers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import Dtos.Hospitales;
import Dtos.Sala;

public class HospitalesHelper {
	Session session;

	public HospitalesHelper(Session session) {
		this.session = session;
	}

	public List<Hospitales> getAll() {
		return session.createQuery("from Hospitales", Hospitales.class).getResultList();
	}

	public boolean existingHospital(byte hospitalCod) {
		Hospitales hospital = session.find(Hospitales.class, hospitalCod);
		if (hospital == null)
			return false;
		return true;
	}

	public Map<Byte, String> traerIdAndNombre() {
		return getAll().stream().collect(Collectors.toMap(Hospitales::getHospitalCod, Hospitales::getNombre));
	}

	public short numDoctoresHospital(byte hospitalCod) {
		String hql = "SELECT count(d) FROM Doctor d JOIN d.hospitaleses h WHERE h.hospitalCod = :hospitalCod";
		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("hospitalCod", hospitalCod);
		Long result = query.uniqueResult();
		return result != null ? result.shortValue() : 0; 
	}

	public short numPacientesHospital(byte hospitalCod) {
		String hql = "SELECT count(e) FROM Enfermo e JOIN e.ocupacions o WHERE o.hospitales.hospitalCod = :hospitalCod";
		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("hospitalCod", hospitalCod);
		Long result = query.uniqueResult();
		return result != null ? result.shortValue() : 0; 
	}

	public short numPlantillaHospital(byte hospitalCod) {
		String hql = "SELECT count(d) FROM Plantilla d JOIN d.hospitales h WHERE h.hospitalCod = :hospitalCod";
		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("hospitalCod", hospitalCod);
		Long result = query.uniqueResult();
		return result != null ? result.shortValue() : 0; 
	}

	public short numSalasHospital(byte hospitalCod) {
		String hql = "SELECT count(d) FROM Sala d JOIN d.hospitales h WHERE h.hospitalCod = :hospitalCod";
		Query<Long> query = session.createQuery(hql, Long.class);
		query.setParameter("hospitalCod", hospitalCod);
		Long result = query.uniqueResult();
		return result != null ? result.shortValue() : 0; 
	}

	public List<Sala> getSalasHospital(int hospitalCod) {

		return session.createQuery("FROM Sala s WHERE s.hospitales.hospitalCod = :hospitalCod ORDER BY s.id.salaCod",
				Sala.class).setParameter("hospitalCod", hospitalCod).list();

	}
}

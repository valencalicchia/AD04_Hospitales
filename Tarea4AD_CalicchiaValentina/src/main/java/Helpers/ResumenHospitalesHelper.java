package Helpers;

import org.hibernate.Session;
import org.hibernate.Transaction;

import Dtos.ResumenHospitales;

import java.util.List;

public class ResumenHospitalesHelper {
	  private final Session session;

	    public ResumenHospitalesHelper(Session session) {
	        this.session = session;
	    }

	    public List<ResumenHospitales> getAll() {
	        return getEntities("from ResumenHospitales", ResumenHospitales.class);
	    }

		public void create(ResumenHospitales resumen) {
			if (resumen.getHospitales() != null) {
				session.persist(resumen);
			}

		}

	    public boolean existsResumen(byte hospitalCod) {
	        ResumenHospitales resumen = session.find(ResumenHospitales.class, hospitalCod);
	        return resumen != null && resumen.getHospitales() != null;
	    }


	    private <T> List<T> getEntities(String hql, Class<T> entityClass) {
	        return session.createQuery(hql, entityClass).getResultList();
	    }

		public void update(ResumenHospitales resumen) {
			ResumenHospitales resumenBbdd = session.find(ResumenHospitales.class, resumen.getHospitales().getHospitalCod());

			if (resumenBbdd != null) {
				resumenBbdd.setNumDoctores(resumen.getNumDoctores());
				resumenBbdd.setNumSalas(resumen.getNumSalas());
				resumenBbdd.setNumEnfermos(resumen.getNumEnfermos());
				resumenBbdd.setNumPlantilla(resumen.getNumPlantilla());
				session.merge(resumenBbdd);
			}

		}
}

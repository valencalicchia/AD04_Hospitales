
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import Dtos.*;
import Helpers.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Ejercicio2 {

		private static final Logger logger = Logger.getLogger(Ejercicio2.class.getName());


		public static void main(String[] args) {
			SessionFactory factory = HibernateHelper.getSessionFactory();
			System.out.println("Ejercicio 2 – Insertando-Actualizando Resumen Hospitales");
			System.out.println("Bajo Tabero Álvaro");
			System.out.println("========================================================");
			try (Session session = factory.openSession()) {
				ResumenHospitalesHelper resumenDao = new ResumenHospitalesHelper(session);
				HospitalesHelper hospitalDao = new HospitalesHelper(session);
				obtenerResumenBbdd(hospitalDao, resumenDao, session);
				System.out.println();
				listarSalas(session);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error general de la aplicación.", e);
			}
		}


		private static void obtenerResumenBbdd(HospitalesHelper hospitalDao, ResumenHospitalesHelper resumenDao,
				Session session) {
			try {
				session.beginTransaction();

				hospitalDao.getAll().forEach(h -> {
					actualizarResumen(h, hospitalDao, resumenDao, session);
				});

				session.getTransaction().commit();

			} catch (Exception e) {
				session.getTransaction().rollback();
			}
		}

		private static void listarSalas(Session session) {
			ResumenHospitalesHelper resumenDao = new ResumenHospitalesHelper(session);
			List<ResumenHospitales> resumenes = resumenDao.getAll().stream()
					.sorted((r1, r2) -> Byte.compare(r1.getHospitalCod(), r2.getHospitalCod()))
					.collect(Collectors.toList());

			System.out.println("=".repeat(70));
			System.out.println("LISTADO DE SALAS");
			System.out.println("=".repeat(70));
			System.out.printf("%-25s %-10s %10s %10s %10s%n", "HOSPITAL", "DOCTORES", "ENFERMOS", "PLANTILLA", "SALAS");
			System.out.println("-".repeat(70));

			int idActual = 0;
			for (ResumenHospitales re : resumenes) {
				if (idActual != re.getHospitalCod()) {
					idActual = re.getHospitalCod();
					if (idActual != 0) {
						System.out.println();
					}
				}

				String hospital = String.format("(%d) %s", re.getHospitalCod(), re.getHospitales().getNombre());
				String doctores = String.format("%5d", re.getNumDoctores());
				String enfermos = String.format("%10s", re.getNumEnfermos());
				String salas = String.format("%10s", re.getNumSalas());
				String plantilla = String.format("%10s", re.getNumPlantilla());

				enfermos = String.format("%-10s", enfermos).replace(' ', ' ');
				salas = String.format("%-10s", salas).replace(' ', ' ');
				plantilla = String.format("%-10s", plantilla).replace(' ', ' ');

				System.out.printf("%-25s %-7s %7s %7s %7s%n", hospital, doctores, enfermos, plantilla, salas);
			}
		}

		private static void actualizarResumen(Hospitales hospitales, HospitalesHelper hospitalDao,
				ResumenHospitalesHelper resumenDao, Session session) {

			if (resumenDao.existResumen(hospitales.getHospitalCod())) {
				System.out.println("Hospital Cod(" + hospitales.getHospitalCod() + ") YA EXISTE, se actualiza...");
				resumenDao.update(createResumen(hospitalDao, hospitales));
			} else {
				System.out.println("Hospital Cod(" + hospitales.getHospitalCod() + ") AÑADIDO...");
				resumenDao.create(createResumen(hospitalDao, hospitales));
			}
		}

		private static ResumenHospitales createResumen(HospitalesHelper hospitalDao, Hospitales hospitales) {

			Short numDoctores = hospitalDao.numDoctoresHospital(hospitales.getHospitalCod());
			Short numSalas = hospitalDao.numSalasHospital(hospitales.getHospitalCod());
			Short numEnfermos = hospitalDao.numPacientesHospital(hospitales.getHospitalCod());
			Short numPlantilla = hospitalDao.numPlantillaHospital(hospitales.getHospitalCod());

			ResumenHospitales resumen = new ResumenHospitales(hospitales, numDoctores, numSalas, numEnfermos, numPlantilla);

			return resumen;
		}
}

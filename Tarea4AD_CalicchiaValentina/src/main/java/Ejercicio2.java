
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
    
    private static final String SEPARADOR_IGUALES = "=".repeat(61);
    private static final String AUTOR = "Calicchia Valentina Alessandra";
    
    public static void main(String[] args) {
		
	    try (SessionFactory factory = HibernateHelper.getSessionFactory();
	    		Session session = factory.openSession()) {
	        System.out.println("Ejercicio 2 – Insertando-Actualizando Resumen Hospitales");
	        
	        System.out.println(SEPARADOR_IGUALES);
	        System.out.println(AUTOR);
	        System.out.println(SEPARADOR_IGUALES);
	           
	        resumenHospitales(session);
	
	       }catch (Exception e) {
	            logger.log(Level.SEVERE, "Error general de la aplicación.", e);
	       } finally {
	           HibernateHelper.shutdown();
	       }
    }
    
    private static void resumenHospitales(Session session) {
        ResumenHospitalesHelper resumenHelper = new ResumenHospitalesHelper(session);
        HospitalesHelper hospitalHelper = new HospitalesHelper(session);
        getResumenes(hospitalHelper, resumenHelper, session);
        System.out.println();
        listarResumenHospitales(session);
    }


    private static void getResumenes(HospitalesHelper hospitalHelper, ResumenHospitalesHelper resumenHelper,
                                           Session session) {
        try {
            session.beginTransaction();

            hospitalHelper.getAll().forEach(hospital -> {
                if (resumenHelper.existsResumen(hospital.getHospitalCod())) {
                    System.out.println("Hospital Cod(" + hospital.getHospitalCod() + ") YA EXISTE, se actualiza...");
                    ResumenHospitales resumenActual = session.find(ResumenHospitales.class, hospital.getHospitalCod());
                    actualizarResumen(resumenActual, hospitalHelper, hospital);
                    resumenHelper.update(resumenActual);
                } else {
                    System.out.println("Hospital Cod(" + hospital.getHospitalCod() + ") AÑADIDO...");
                    ResumenHospitales nuevoResumen = crearResumen(hospitalHelper, hospital);
                    resumenHelper.create(nuevoResumen);
                }
            });

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.log(Level.SEVERE, "Error durante la transacción.", e);
        }
    }

    private static void listarResumenHospitales(Session session) {
        ResumenHospitalesHelper resumenHelper = new ResumenHospitalesHelper(session);
        List<ResumenHospitales> resumenes = resumenHelper.getAll().stream()
                .sorted((r1, r2) -> Byte.compare(r1.getHospitalCod(), r2.getHospitalCod()))
                .collect(Collectors.toList());

        String separador = "=".repeat(70);
        System.out.println(separador);
        System.out.println("LISTADO DE RESUMEN HOSPITALES");
        System.out.println(separador);
        System.out.printf("%-25s %-10s %10s %10s %10s%n", "HOSPITAL", "DOCTORES", "ENFERMOS", "PLANTILLA", "SALAS");
        System.out.println("-".repeat(70));

        resumenes.forEach(resumen -> {
            System.out.printf("(%d) %-20s %10d %10d %10d %10d%n",
                    resumen.getHospitalCod(),
                    resumen.getHospitales().getNombre(),
                    resumen.getNumDoctores(),
                    resumen.getNumEnfermos(),
                    resumen.getNumPlantilla(),
                    resumen.getNumSalas());
        });
    }

    private static void actualizarResumen(ResumenHospitales resumen, HospitalesHelper hospitalHelper, Hospitales hospital) {
        resumen.setNumDoctores((short) (resumen.getNumDoctores() + hospitalHelper.numDoctoresHospital(hospital.getHospitalCod())));
        resumen.setNumSalas((short) (resumen.getNumSalas() + hospitalHelper.numSalasHospital(hospital.getHospitalCod())));
        resumen.setNumEnfermos((short) (resumen.getNumEnfermos() + hospitalHelper.numPacientesHospital(hospital.getHospitalCod())));
        resumen.setNumPlantilla((short) (resumen.getNumPlantilla() + hospitalHelper.numPlantillaHospital(hospital.getHospitalCod())));
    }

    private static ResumenHospitales crearResumen(HospitalesHelper hospitalHelper, Hospitales hospital) {
        short numDoctores = hospitalHelper.numDoctoresHospital(hospital.getHospitalCod());
        short numSalas = hospitalHelper.numSalasHospital(hospital.getHospitalCod());
        short numEnfermos = hospitalHelper.numPacientesHospital(hospital.getHospitalCod());
        short numPlantilla = hospitalHelper.numPlantillaHospital(hospital.getHospitalCod());

        return new ResumenHospitales(hospital, numDoctores, numSalas, numEnfermos, numPlantilla);
    }
}

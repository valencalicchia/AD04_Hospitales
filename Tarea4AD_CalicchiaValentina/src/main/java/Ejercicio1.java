import org.hibernate.Session;
import org.hibernate.SessionFactory;

import Dtos.*;
import Helpers.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Ejercicio1 {

    private static final Logger logger = Logger.getLogger(Ejercicio1.class.getName());
	
    private static final String SEPARADOR_IGUALES = "=".repeat(61);
    private static final String AUTOR = "Calicchia Valentina Alessandra";
    
    public static void main(String[] args) {
		
	    try (SessionFactory factory = HibernateHelper.getSessionFactory();
	    		Session session = factory.openSession()) {
	    	System.out.println("Ejercicio 1 - Insertar Nuevas Salas");
	        System.out.println(AUTOR);
	        System.out.println(SEPARADOR_IGUALES);
	           
	        procesarNuevasSalas(session);
	
	       }catch (Exception e) {
	            logger.log(Level.SEVERE, "Error general de la aplicación.", e);
	       } finally {
	           HibernateHelper.shutdown();
	       }
    }

    private static void listarSalas(Session session) {
        SalaHelper salaHelper = new SalaHelper(session);
        HospitalesHelper hospitalesHelper = new HospitalesHelper(session);

        List<Sala> salas = salaHelper.getAll();
        Map<Byte, String> hospitalMap = hospitalesHelper.traerIdAndNombre();
        
        imprimirEncabezadoSalas();
        imprimirSalas(salas, hospitalMap);
    }

    private static void imprimirEncabezadoSalas() {
        String separator = "=".repeat(85);
        System.out.println(separator);
        System.out.println("LISTADO DE SALAS");
        System.out.println(separator);
        System.out.printf("%-25s %-25s %10s %10s %10s%n", "HOSPITAL", "SALA", "CAMAS", "OCUPACIÓN", "PLANTILLA");
        System.out.println(separator);
    }

    private static void imprimirSalas(List<Sala> salas, Map<Byte, String> hospitalMap) {
        byte currentHospitalCod = 0;
        for (Sala sala : salas) {
            if (currentHospitalCod != sala.getId().getHospitalCod()) {
                if (currentHospitalCod != -1) {
                    System.out.println();
                }
                currentHospitalCod = sala.getId().getHospitalCod();
            }
            imprimirSala(sala, hospitalMap.get(currentHospitalCod));
        }
    }

    private static void imprimirSala(Sala sala, String hospitalName) {
        String hospital = String.format("(%d) %s", sala.getId().getHospitalCod(), hospitalName);
        String salaInfo = String.format("(%d) %s", sala.getId().getSalaCod(), sala.getNombre());
        System.out.printf("%-25s %-25s %10d %10d %10d%n",
                hospital, salaInfo, sala.getNumCama(), sala.getOcupacions().size(), sala.getPlantillas().size());
    }

    private static void guardarNuevasSalas(List<Sala> salas, Session session) {
        session.beginTransaction();
        SalaHelper salaHelper = new SalaHelper(session);
        HospitalesHelper hospitalesHelper = new HospitalesHelper(session);

        for (Sala sala : salas) {
        	System.out.println("Insertando (" + sala.getId().getHospitalCod() + ", " + sala.getId().getSalaCod() + ", "
					+ sala.getNombre() + ", " + sala.getNumCama() + ")");
            if (validarSala(sala, salaHelper, hospitalesHelper)) {
                salaHelper.insertSala(sala);
                System.out.printf("Sala(%d, %d) AÑADIDA...%n", sala.getId().getHospitalCod(), sala.getId().getSalaCod());
            }
            System.out.println();
        }
        session.getTransaction().commit();
        listarSalas(session);
    }

    private static boolean validarSala(Sala sala, SalaHelper salaHelper, HospitalesHelper hospitalesHelper) {
        String error = "";
        if (salaHelper.existSala(sala.getId())) {
        	error = String.format("Sala(%d, %d) YA EXISTE", sala.getId().getHospitalCod(), sala.getId().getSalaCod());
        }
        if (!hospitalesHelper.existingHospital(sala.getId().getHospitalCod())) {
        	error = String.format("Código de hospital %d no existe", sala.getId().getHospitalCod());
        }

        if (error != "") {
            System.out.println(error +", No se insertará...");
            return false;
        }
        return true;
    }

    private static void transformarNuevasSalas(List<NuevasSalas> nuevasSalas, Session session) {
        List<Sala> salas = nuevasSalas.stream()
                .map(Ejercicio1::crearSalaDesdeNuevaSala)
                .toList();
        
        guardarNuevasSalas(salas, session);
    }

    private static Sala crearSalaDesdeNuevaSala(NuevasSalas nuevaSala) {
        return new Sala(
                new SalaId(nuevaSala.getId().getHospitalCod(), nuevaSala.getId().getSalaCod()),
                null, nuevaSala.getId().getNombre(), nuevaSala.getId().getNumCama(), null, null
        );
    }

    private static void procesarNuevasSalas(Session session) {
        NuevasSalasHelper nuevasSalasHelper = new NuevasSalasHelper(session);
        List<NuevasSalas> nuevasSalas = nuevasSalasHelper.getAll();
        transformarNuevasSalas(nuevasSalas, session);
    }
}

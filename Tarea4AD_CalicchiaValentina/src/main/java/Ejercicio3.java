
import Dtos.*;
import Helpers.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Ejercicio3 {

	private static final Logger logger = Logger.getLogger(Ejercicio2.class.getName());
	
    private static final String SEPARADOR_IGUALES = "=".repeat(61);
    private static final String SEPARADOR_GUIONES = "-".repeat(61);
    private static final String SEPARADOR_SALAS = " ====  ====================  ===============  ========";
    private static final String AUTOR = "Calicchia Valentina Alessandra";


    
    public static void main(String[] args) {
		
	    try (SessionFactory factory = HibernateHelper.getSessionFactory();
	    		Session session = factory.openSession()) {
	    	
	    	System.out.println("Ejercicio 3 - LISTADO DE HOSPITALES");
	        
	        System.out.println(SEPARADOR_IGUALES);
	        System.out.println(AUTOR);
	        System.out.println(SEPARADOR_IGUALES);
	      
	        listadoHospitales(session);
	
	       }catch (Exception e) {
	            logger.log(Level.SEVERE, "Error general de la aplicación.", e);
	       } finally {
	           HibernateHelper.shutdown();
	       }
    }
    
    private static void listadoHospitales(Session session) {
    	  HospitalesHelper hospitalesHelper = new HospitalesHelper(session);
          SalaHelper salaHelper = new SalaHelper(session);

          List<Hospitales> hospitales = hospitalesHelper.getAll();
          for (Hospitales hospital : hospitales) {
              mostrarDatosHospital(hospital, hospitalesHelper, salaHelper);
          }
    }
    

    private static void mostrarDatosHospital(Hospitales hospital, HospitalesHelper hospitalesHelper, SalaHelper salaHelper) {
        System.out.printf("%nCOD-HOSPITAL: %d   NOMBRE: %s%n", hospital.getHospitalCod(), hospital.getNombre());
        System.out.printf("DIRECCIÓN: %s    Número de camas del hospital: %d%n", hospital.getDireccion(), hospital.getNumCama());
        System.out.println(SEPARADOR_GUIONES);

        try {
            List<Sala> salas = hospitalesHelper.obtenerSalasHospital(hospital.getHospitalCod());
            double totalSalarioHospital = 0.0;
            int totalSalas = 0;

            for (Sala sala : salas) {
                mostrarDatosSala(sala, salaHelper);

                Double salarioMedioSala = salaHelper.calcularSalarioMedio(sala.getId().getHospitalCod(), sala.getId().getSalaCod());
                if (salarioMedioSala != null) {
                    totalSalarioHospital += salarioMedioSala;
                    totalSalas++;
                }
            }

            if (totalSalas > 0) {
                System.out.printf("%nSalario Medio del Hospital:   %.2f%n", totalSalarioHospital / totalSalas);
            } else {
                System.out.println("%nNo hay salas con plantilla en este hospital.");
            }

        } catch (Exception e) {
            System.err.println("Error al mostrar datos del hospital: " + e.getMessage());
        }
    }

    private static void mostrarDatosSala(Sala sala, SalaHelper salaHelper) {
        System.out.printf("%n SALA  NOMBRE                APELLIDO          SALARIO %n");
        System.out.println(SEPARADOR_SALAS);

        try {
            List<Object[]> plantilla = salaHelper.obtenerPlantillaPorSala(sala.getId().getHospitalCod(), sala.getId().getSalaCod());
            if (plantilla.isEmpty()) {
                System.out.printf("    %d  %-20s  LA SALA NO TIENE PLANTILLA%n", sala.getId().getSalaCod(), sala.getNombre());
            } else {
                double totalSalario = 0.0;
                for (Object[] empleado : plantilla) {
                    String apellido = (String) empleado[0];
                    Long salario = (Long) empleado[1];
                    totalSalario += salario;

                    System.out.printf("                             %-15s  %8d%n", apellido, salario.intValue());
                }

                System.out.println("                             ---------------  --------");
                System.out.printf("                             Salario medio:   %8.2f%n", totalSalario / plantilla.size());
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar datos de la sala: " + e.getMessage());
        }
    }
}

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import Helpers.*;

public class Ejercicio4 {

    private static final String SEPARADOR_IGUALES = "=".repeat(61);
    private static final String AUTOR = "Calicchia Valentina Alessandra";
    
    private static final Logger logger = Logger.getLogger(Ejercicio4.class.getName());


    public static void main(String[] args) {

        try (SessionFactory factory = HibernateHelper.getSessionFactory();
             Session session = factory.openSession()) {
            System.out.println("Ejercicio 4 - CONSULTAS");
            System.out.println(SEPARADOR_IGUALES);
            System.out.println(AUTOR);
            System.out.println(SEPARADOR_IGUALES);
        	
            consulta1(session);
            consulta2(session);
            consulta3(session);
            consulta4(session);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error general de la aplicación.", e);
        } finally {
            HibernateHelper.shutdown();
        }
    }

    public static void consulta1(Session session) {
        System.out.println("Consulta 1:");

        String consulta = """
                SELECT h.id, h.nombre, COUNT(p), AVG(p.salario)
                FROM Hospitales h JOIN h.plantillas p
                GROUP BY h.id, h.nombre
                """;

        System.out.println(consulta);

        try {
            Query<Object[]> query = session.createQuery(consulta, Object[].class);
            for (Object[] resultado : query.getResultList()) {
                byte hospitalCod = (Byte) resultado[0];
                String nombre = (String) resultado[1];
                Long numPlantilla = (Long) resultado[2];
                Double salarioMed = (Double) resultado[3];

                System.out.printf("Hospital: %d, Nombre: %s, Nº Empleados: %d, Salario medio: %.2f%n",
                        hospitalCod, nombre, numPlantilla, salarioMed);
            }
        } catch (Exception e) {
            System.err.println("Error en consulta 1: " + e.getMessage());
        }
        
        System.out.println();
    }

    public static void consulta2(Session session) {
        System.out.println("Consulta 2:");


        String consulta = """
                SELECT h.id, h.nombre, r.numPlantilla
                FROM Hospitales h JOIN h.resumenHospitales r
                WHERE r.numPlantilla = (SELECT MAX(r2.numPlantilla) FROM ResumenHospitales r2)
                """;

        System.out.println(consulta);

        try {
            Query<Object[]> query = session.createQuery(consulta, Object[].class);
            Object[] maxPlantilla = query.uniqueResult();

            if (maxPlantilla != null) {
                byte hospitalCod = (Byte) maxPlantilla[0];
                String nombre = (String) maxPlantilla[1];
                Short numPlantilla = (Short) maxPlantilla[2];

                System.out.printf("Código: %d, Nombre: %s, Nº Empleados: %d%n", hospitalCod, nombre, numPlantilla);
            } 
        } catch (Exception e) {
            System.err.println("Error en consulta 2: " + e.getMessage());
        }
        
        System.out.println();
    }

    public static void consulta3(Session session) {
        System.out.println("Consulta 3:");

        String consulta = "SELECT h.nombre FROM Hospitales h WHERE h.plantillas IS EMPTY";

        System.out.println(consulta);

        try {
            Query<String> query = session.createQuery(consulta, String.class);
            for (String nombre : query.getResultList()) {
                System.out.println("Nombre: " + nombre);
            }
        } catch (Exception e) {
            System.err.println("Error en consulta 3: " + e.getMessage());
        }
        System.out.println();
    }

    public static void consulta4(Session session) {
        System.out.println("Consulta 4:");

        String consulta = """
                SELECT h.nombre, s.nombre, e.apellido, o.cama
                FROM Hospitales h 
                JOIN h.salas s 
                JOIN s.ocupacions o 
                JOIN o.enfermo e
                ORDER BY h.nombre
                """;

        System.out.println(consulta);

        try {
            Query<Object[]> query = session.createQuery(consulta, Object[].class);
            for (Object[] resultado : query.getResultList()) {
                String hospitalNombre = (String) resultado[0];
                String salaNombre = (String) resultado[1];
                String apellidoEnfermo = (String) resultado[2];
                Short cama = (Short) resultado[3];

                System.out.printf("Hospital: %s, Sala: %s, Apellido: %s, Cama: %d%n",
                        hospitalNombre, salaNombre, apellidoEnfermo, cama);
            }
        } catch (Exception e) {
            System.err.println("Error en consulta 4: " + e.getMessage());
        }
    }
}


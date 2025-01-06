
import Dtos.*;
import Helpers.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Ejercicio3 {

	public static void main(String[] args) {
		SessionFactory factory = HibernateHelper.getSessionFactory();
		Session session = factory.openSession();
		crearEncabezado();
		listarHospitales(session);
	}

	private static void listarHospitales(Session session) {
		HospitalesHelper hospitalDao = new HospitalesHelper(session);
		List<Hospitales> hospitales = hospitalDao.getAll(
				).stream()
				.sorted((h1,h2)-> Byte.compare(h1.getHospitalCod(), h2.getHospitalCod()))
				.toList();
		
		for(Hospitales hospital: hospitales) {
			imprimirHospital(hospital);
		}
	}
	

	private static void imprimirHospital(Hospitales hospital) {
	    String id = "" + hospital.getHospitalCod();
	    String nombre = hospital.getNombre();
	    String direccion = hospital.getDireccion();
	    AtomicInteger totalNumeroCamas = new AtomicInteger(0);
	    hospital.getSalas().forEach(sala -> totalNumeroCamas.addAndGet(sala.getNumCama()));

	    System.out.println("COD-HOSPITAL: " + id + " NOMBRE: " + nombre);
	    System.out.println("DIRECCIÓN: " + direccion + " Número de camas del hospital: " + totalNumeroCamas);
	    System.out.println("-------------------------------------------------------------");
	    hospital.getSalas().forEach(s->imprimirSala(s));
	    
	}
	
	private static void imprimirSala(Sala sala) {
		System.out.println("SALA  NOMBRE               APELLIDO         SALARIO"); 
		System.out.println("====  ==================== ===============  ========");
		System.out.println(" "+sala.getId().getSalaCod() + "   " + sala.getNombre()+"  ");
		
	}


	private static void crearEncabezado() {
		System.out.println("Ejercicio 3 - LISTADO DE HOSPITALES");
		System.out.println("=============================================================");
		System.out.println("Calicchia Valentina Alessandra");
		System.out.println("=============================================================");	
	}
}

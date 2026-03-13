package CONTROLADOR;

import java.sql.Connection;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Si dejaste el código del profe tal cual (con el Scanner):
        Scanner lector = new Scanner(System.in);
        Connection miConexion = GestorBBDD.conectarBaseDatos(lector);
        
        // Si lo hardcodeaste como te sugerí (sin Scanner):
        // Connection miConexion = BBDD.conectarBaseDatos();
        
        if (miConexion != null) {
            System.out.println("🎉 ¡LA CONEXIÓN FUNCIONA PERFECTAMENTE!");
            GestorBBDD.cerrar(miConexion); // Cerramos al terminar por ser educados con el servidor
        } else {
            System.out.println("💥 Algo ha fallado. Revisa la consola.");
        }
    }
}

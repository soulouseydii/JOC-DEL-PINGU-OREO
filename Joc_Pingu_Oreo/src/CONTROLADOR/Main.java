package CONTROLADOR;

import java.sql.Connection;
import java.util.Scanner;
import MODELO.Partida; // Asegúrate de importar tu modelo

public class Main {

    public static void main(String[] args) {
        Scanner lector = new Scanner(System.in);
        
        System.out.println("=== BIENVENIDO A PINGUINOS AL AGUA ===");
        
        // 1. Intentar la conexión (Lo que ya tienes)
        Connection miConexion = GestorBBDD.conectarBaseDatos(lector);
        
        if (miConexion != null) {
            // 2. INICIALIZACIÓN DEL JUEGO
            // Aquí es donde creas el objeto que gestiona todo
            GestorPartida gp = new GestorPartida(); 
            
            // Si tu GestorPartida necesita la conexión para guardar/cargar:
            // gp.setConexion(miConexion); 

            System.out.println("\n--- Iniciando nueva partida ---");
            // Aquí llamarías al método que configura el tablero y jugadores
            // gp.prepararPartidaInicial(); 

            // 3. BUCLE DE PRUEBA (Simulamos 5 turnos para ver si funciona)
            for (int i = 0; i < 5; i++) {
                gp.ejecutarTurnoCompleto();
                
                System.out.println("Presiona ENTER para el siguiente turno...");
                lector.nextLine(); 
            }

            // 4. CIERRE
            GestorBBDD.cerrar(miConexion);
            System.out.println("Conexión cerrada. Fin del programa.");
            
        } else {
            System.out.println("💥 No se puede jugar sin conexión a la base de datos.");
        }
        
        lector.close();
    }
}
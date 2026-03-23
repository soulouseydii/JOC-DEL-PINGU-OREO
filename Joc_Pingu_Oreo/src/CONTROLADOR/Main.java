package CONTROLADOR;

import javafx.application.Application;
import VISTA.PantallaMenu;

public class Main {

    public static void main(String[] args) {
        Main aplicacion = new Main();
        aplicacion.jugar(args);
    }

    public void jugar(String[] args) {
        System.out.println("--- Iniciando el Juego de los Pinguinos ---");
        
        // Configuración del Gestor de Base de Datos
        GestorBBDD gestorBD = new GestorBBDD();
        gestorBD.setUrlBBDD("jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2");
        gestorBD.setUsername("DW2526_GR01_PINGU"); 
        gestorBD.setPassword("AJBPNSS"); 
        
        // Comprobar que la conexion a Oracle funciona antes de arrancar el juego
        gestorBD.testConexion();
        
        // Arrancar la interfaz gráfica de JavaFX
        System.out.println("Cargando la interfaz grafica...");
        Application.launch(PantallaMenu.class, args);
        
        System.out.println("Juego cerrado. ¡Hasta pronto!");
    }
}

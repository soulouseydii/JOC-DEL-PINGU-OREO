package CONTROLADOR;

import javafx.application.Application;
import VISTA.PantallaInicio;

public class Main {

    // Credenciales de la base de datos Oracle (accesibles desde cualquier clase)
    public static String DB_URL = "jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2";
    public static String DB_USER = "DW2526_GR01_PINGU";
    public static String DB_PASS = "AJBPNSS";

    public static void main(String[] args) {
        Main aplicacion = new Main();
        aplicacion.jugar(args);
    }

    public void jugar(String[] args) {
        System.out.println("--- INICIANDO JUEGO DEL PINGUINO OREO ---");
        
        // Configuración del Gestor de Base de Datos
        GestorBBDD gestorBD = new GestorBBDD();
        gestorBD.setUrlBBDD("jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2");
        gestorBD.setUsername("DW2526_GR01_PINGU"); 
        gestorBD.setPassword("AJBPNSS"); 
        
        // Comprobar que la conexion a Oracle funciona antes de arrancar el juego
        gestorBD.testConexion();
        
        // Crear la tabla USUARIOS si no existe (necesaria para login/registro)
        gestorBD.crearTablaUsuarios();
        
        // Arrancar la interfaz gráfica de JavaFX
        System.out.println("Cargando la interfaz grafica...");
        Application.launch(PantallaInicio.class, args);
        
        System.out.println("Juego cerrado. ¡Hasta pronto!");
    }
}

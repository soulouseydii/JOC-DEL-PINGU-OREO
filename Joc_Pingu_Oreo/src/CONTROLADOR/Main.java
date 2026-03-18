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
        
        // 1. Configuración del Gestor de Base de Datos
        GestorBBDD gestorBD = new GestorBBDD();
        gestorBD.setUrlBBDD("jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2");
        gestorBD.setUsername("usuario_clase");
        gestorBD.setPassword("password_clase");
        
        System.out.println("Conexion a base de datos configurada y lista.");
        
        // 2. Arrancar la interfaz gráfica de JavaFX
        System.out.println("Cargando la interfaz grafica...");
        Application.launch(PantallaMenu.class, args);
        
        // Lo que pongas a partir de aquí solo se ejecutará cuando cierres la ventana del juego
        System.out.println("Juego cerrado. ¡Hasta pronto!");
    }
}

package CONTROLADOR;

import MODELO.Partida;

public class Main {

    public static void main(String[] args) {
        // Se instancia la aplicacion para iniciar el proceso
        Main aplicacion = new Main();
        aplicacion.jugar();
    }

    public void jugar() {
        System.out.println("--- Iniciando el Juego de los Pinguinos ---");
        
        // Configuracion del Gestor de Base de Datos
        GestorBBDD gestorBD = new GestorBBDD();
        gestorBD.setUrlBBDD("jdbc:oracle:thin:@//192.168.3.26:1521/XEPDB2");
        gestorBD.setUsername("usuario_clase");
        gestorBD.setPassword("password_clase");
        System.out.println("Conexion a base de datos configurada.");

        // Inicializacion de componentes del juego
        Partida partidaActual = new Partida();
        GestorPartida gestorPartida = new GestorPartida();

        System.out.println("Componentes cargados. El juego esta listo para comenzar.");
        
    }
}

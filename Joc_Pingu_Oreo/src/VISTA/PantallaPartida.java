package VISTA;

import CONTROLADOR.GestorPartida;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PantallaPartida {

    private GestorPartida gestorPartida;

    // Métodos del diagrama UML adaptados a JavaFX

    public void iniciarPartida() {
        System.out.println("Iniciando una nueva partida...");
        gestorPartida = new GestorPartida();
        // Aquí conectaremos con el modelo
    }

    public void cargarPartida() {
        System.out.println("Cargando partida guardada...");
    }

    public void guardarPartida() {
        System.out.println("Guardando partida actual...");
    }

    public void refrescarPartida() {
        System.out.println("Actualizando las imágenes del tablero...");
    }

    // ACCIONES DE LOS BOTONES (@FXML)
    
    @FXML
    public void botonTirarDado(ActionEvent event) {
        System.out.println("¡Has hecho clic en tirar el dado!");
        // Aquí irá la animación de mover la ficha que os dio el profe
    }

    @FXML
    public void botonUsarObjeto(ActionEvent event) {
        System.out.println("Has usado un objeto del inventario.");
    }

    @FXML
    public void botonFinalizarTurno(ActionEvent event) {
        System.out.println("Pasando el turno al siguiente jugador...");
    }
}

package VISTA;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PantallaMenu extends Application {

    // ESTE MÉTODO ES OBLIGATORIO PARA QUE NO DE ERROR
    @Override
    public void start(Stage primaryStage) {
        // Por ahora lo dejamos vacío, solo para que el error rojo desaparezca
        primaryStage.setTitle("Menú Pingu Oreo");
        primaryStage.show();
    }

    public void menu() {
        System.out.println("Pantalla de menú cargada.");
    }

    @FXML
    public void botonNuevaPartida(ActionEvent event) {
        System.out.println("Cargando la pantalla de la partida...");
    }

    @FXML
    public void botonCargarPartida(ActionEvent event) {
        int idPartida = 1;
        System.out.println("Cargando la partida con ID: " + idPartida);
    }

    @FXML
    public void botonSalir(ActionEvent event) {
        System.out.println("Saliendo del juego...");
        System.exit(0);
    }
}

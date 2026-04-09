package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PantallaCargarPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ListView<String> listaPartidas;
    @FXML private Button btnCargar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;
    @FXML private VBox overlayConfirmacion;

    private ObservableList<String> partidas;

    @FXML
    private void initialize() {
        System.out.println("PantallaCargarPartida Controller initialized");
        
        // Cargar datos dummy
        partidas = FXCollections.observableArrayList(
            "Partida 1 - Nivel 3",
            "Partida 2 - Nivel 5",
            "Partida 3 - Nivel 1"
        );
        listaPartidas.setItems(partidas);
    }

    @FXML
    private void handleCargar(ActionEvent event) {
        String seleccionada = listaPartidas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            System.out.println("Cargar partida seleccionada: " + seleccionada);
            // TODO: Lógica para pasar a la pantalla de juego con la partida cargada
        } else {
            System.out.println("Por favor, selecciona una partida para cargar.");
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        String seleccionada = listaPartidas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            overlayConfirmacion.setVisible(true);
        } else {
            System.out.println("Por favor, selecciona una partida para eliminar.");
        }
    }

    @FXML
    private void confirmarBorrado(ActionEvent event) {
        String seleccionada = listaPartidas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            System.out.println("Partida borrada: " + seleccionada);
            partidas.remove(seleccionada);
        }
        overlayConfirmacion.setVisible(false);
    }

    @FXML
    private void cancelarBorrado(ActionEvent event) {
        overlayConfirmacion.setVisible(false);
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inicio Pingu Oreo");
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // LÓGICA DE LOS BOTONES DE LA BARRA DE TÍTULO
    // ==========================================

    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void maximizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onTitleBarPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onTitleBarDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}

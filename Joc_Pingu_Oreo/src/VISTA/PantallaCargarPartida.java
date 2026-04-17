package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import java.util.ArrayList;
import CONTROLADOR.GestorPartida;
import CONTROLADOR.GestorBBDD;
import MODELO.Partida;
import CONTROLADOR.GestorAnimacionesVistas;

public class PantallaCargarPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ListView<String> listaPartidas;
    @FXML private Button btnCargar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;
    @FXML private VBox panelCargar;
    @FXML private VBox overlayConfirmacion;

    private ObservableList<String> partidas;

    // Guardamos los IDs reales de la base de datos para saber cual cargar/eliminar
    private ArrayList<Integer> idsPartidas = new ArrayList<>();

    private GestorBBDD gestorBBDD;

    @FXML
    private void initialize() {
        System.out.println("PantallaCargarPartida Controller initialized");

        gestorBBDD = new GestorBBDD();
        cargarListaDesdeOracle();

        // Aplicar animación en cascada al componente central al entrar
        javafx.application.Platform.runLater(() -> GestorAnimacionesVistas.animarEntradaCascada(panelCargar));
    }

    private void cargarListaDesdeOracle() {
        partidas = FXCollections.observableArrayList();
        idsPartidas.clear();

        ArrayList<String[]> listaDB = gestorBBDD.listarPartidas();

        if (listaDB.isEmpty()) {
            partidas.add("(No hay partidas guardadas)");
        } else {
            for (String[] info : listaDB) {
                idsPartidas.add(Integer.parseInt(info[0]));
                partidas.add(info[1]);
            }
        }

        listaPartidas.setItems(partidas);
    }

    @FXML
    private void handleCargar(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado < 0 || idsPartidas.isEmpty()) {
            System.out.println("Por favor, selecciona una partida para cargar.");
            return;
        }

        int idPartida = idsPartidas.get(indiceSeleccionado);
        System.out.println("Cargando partida con ID: " + idPartida);

        // Cargar la partida completa desde Oracle
        Partida partidaCargada = gestorBBDD.cargarBBDD(idPartida);

        if (partidaCargada == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/RESOURCES/PantallaMenu.css").toExternalForm());
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la partida desde la base de datos.");
            alert.showAndWait();
            return;
        }

        // Abrir PantallaJuego con la partida cargada
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
            Parent root = loader.load();

            PantallaJuego controller = loader.getController();
            controller.cargarPartidaGuardada(partidaCargada);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Juego Pingu Oreo - Partida Cargada");
            stage.setMaximized(false);
            stage.setMaximized(true);

        } catch (Exception e) {
            System.out.println("Error al abrir PantallaJuego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado < 0 || idsPartidas.isEmpty()) {
            System.out.println("Por favor, selecciona una partida para eliminar.");
            return;
        }

        GestorAnimacionesVistas.animarAparicionOverlay(overlayConfirmacion);
    }

    @FXML
    private void confirmarBorrado(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado >= 0 && indiceSeleccionado < idsPartidas.size()) {
            int idPartida = idsPartidas.get(indiceSeleccionado);

            boolean eliminada = gestorBBDD.eliminarPartida(idPartida);

            if (eliminada) {
                System.out.println("Partida ID " + idPartida + " eliminada.");
                // Refrescar la lista
                cargarListaDesdeOracle();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/RESOURCES/PantallaMenu.css").toExternalForm());
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo eliminar la partida.");
                alert.showAndWait();
            }
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

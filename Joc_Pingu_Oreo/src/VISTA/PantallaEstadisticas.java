package VISTA;

import CONTROLADOR.GestorEstadisticas;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PantallaEstadisticas {

    @FXML private Label lblRecord;
    @FXML private Label lblMedia;
    @FXML private Label lblEstado;
    @FXML private Label lblConexion;

    @FXML private ListView<String> listJugadoresRecord;
    @FXML private ListView<String> listEncimaMed;
    @FXML private ListView<String> listRanking;

    @FXML private ProgressIndicator progressRecord;
    @FXML private ProgressIndicator progressMedia;
    @FXML private ProgressIndicator progressRecord2;
    @FXML private ProgressIndicator progressMedia2;
    @FXML private ProgressIndicator progressRanking;

    private final GestorEstadisticas gestor = new GestorEstadisticas();

    @FXML
    private void initialize() {
        // Aplicamos efectos de sonido a los botones cuando la pantalla este lista
        lblRecord.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
            }
        });
        cargarTodosLosDatos();
    }

    @FXML
    private void handleRefrescar(ActionEvent event) {
        cargarTodosLosDatos();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("El Juego del Pingüino Oreo");
        } catch (Exception e) {
            System.out.println("Error al volver al menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Carga los datos en un hilo secundario para no bloquear la interfaz
    private void cargarTodosLosDatos() {
        setLoadingState(true);
        if (lblEstado != null) lblEstado.setText("Conectando con Oracle...");

        Thread thread = new Thread(() -> {
            boolean conectado = gestor.testConexion();

            if (!conectado) {
                Platform.runLater(() -> {
                    setLoadingState(false);
                    lblConexion.setText("❌  Sin conexión con la base de datos");
                    lblConexion.setStyle("-fx-text-fill: #ff6b6b;");
                    lblEstado.setText("Error de conexión. Comprueba que Oracle esté activo.");
                    lblRecord.setText("—");
                    lblMedia.setText("—");
                    mostrarErrorEnListas();
                });
            } else {

            // Llamamos a las funciones y procedimientos PL/SQL
            int maxRec                      = gestor.obtenerMaxPartidasGanadas();
            double media                    = gestor.obtenerMediaPartidasGanadas();
            ArrayList<String[]> recJugadors = gestor.obtenerJugadoresConRecord();
            ArrayList<String[]> sobreMed    = gestor.obtenerJugadoresPorEncimaMedia();
            ArrayList<String[]> ranking     = gestor.obtenerRankingCompleto();

            // Actualizamos la UI en el hilo principal de JavaFX
            Platform.runLater(() -> {
                setLoadingState(false);
                lblConexion.setText("✔  Conectado a Oracle (XEPDB2)");
                lblConexion.setStyle("-fx-text-fill: #5ecfff;");
                lblEstado.setText("Datos actualizados correctamente.");

                // Tab 1 - Record
                lblRecord.setText(maxRec >= 0 ? String.valueOf(maxRec) : "Error");

                // Tab 2 - Jugadores con record
                ObservableList<String> itemsRecord = FXCollections.observableArrayList();
                if (recJugadors.isEmpty()) {
                    itemsRecord.add("No hay datos disponibles");
                } else {
                    for (String[] j : recJugadors)
                        itemsRecord.add(j[0] + "   -   " + j[1] + " victorias");
                }
                listJugadoresRecord.setItems(itemsRecord);

                // Tab 3 - Media
                lblMedia.setText(media >= 0 ? String.format("%.2f", media) : "Error");

                // Tab 4 - Por encima de la media
                ObservableList<String> itemsMed = FXCollections.observableArrayList();
                if (sobreMed.isEmpty()) {
                    itemsMed.add("Ningún jugador por encima de la media");
                } else {
                    for (String[] j : sobreMed)
                        itemsMed.add(j[0] + "   -   " + j[1] + " victorias");
                }
                listEncimaMed.setItems(itemsMed);

                // Tab 5 - Ranking
                ObservableList<String> itemsRanking = FXCollections.observableArrayList();
                if (ranking.isEmpty()) {
                    itemsRanking.add("No hay partidas registradas");
                } else {
                    for (String[] j : ranking) {
                        int pos = Integer.parseInt(j[0]);
                        itemsRanking.add("#" + pos + "   " + j[1] + "   -   " + j[2] + " victorias");
                    }
                }
                listRanking.setItems(itemsRanking);
            });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    // Muestra u oculta los indicadores de carga
    private void setLoadingState(boolean loading) {
        if (progressRecord  != null) progressRecord.setVisible(loading);
        if (progressMedia   != null) progressMedia.setVisible(loading);
        if (progressRecord2 != null) progressRecord2.setVisible(loading);
        if (progressMedia2  != null) progressMedia2.setVisible(loading);
        if (progressRanking != null) progressRanking.setVisible(loading);
    }

    // Muestra error en todas las listas si no hay conexion
    private void mostrarErrorEnListas() {
        ObservableList<String> err = FXCollections.observableArrayList("Error al conectar con la base de datos");
        if (listJugadoresRecord != null) listJugadoresRecord.setItems(err);
        if (listEncimaMed       != null) listEncimaMed.setItems(err);
        if (listRanking         != null) listRanking.setItems(err);
    }
}

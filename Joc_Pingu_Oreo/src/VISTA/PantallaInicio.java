package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import javafx.application.Application;


public class PantallaInicio extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private Button btnNuevaPartida;
    @FXML private Button btnCargarPartida;
    @FXML private Button btnOpciones;
    @FXML private Button btnCreditos;
    @FXML private Button btnSalir;

    @Override
    public void start(Stage primaryStage) {
        try {
            String rutaFxml = "/RESOURCES/PantallaInicio.fxml"; 
            java.net.URL url = getClass().getResource(rutaFxml);
            
            if (url == null) {
                System.out.println("No encuentra el FXML en la ruta: " + rutaFxml);
                primaryStage.setTitle("Fallo FXML - Inicio");
                primaryStage.show();
                return;
            }

            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle("Inicio Pingu Oreo");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
            System.out.println("El diseño fxml de inicio se ha cargado.");

        } catch (Exception e) {
            System.out.println("No se ha podido cargar el diseño fxml de inicio");
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        System.out.println("PantallaInicio Controller initialized");
    }

    @FXML
    private void handleNuevaPartida(ActionEvent event) {
        System.out.println("Nueva Partida clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaConfiguracionPartida.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Configuración Pingu Oreo");
            // Forzar re-maximización al cambiar de escena
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            System.out.println("No se pudo cargar PantallaConfiguracionPartida.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCargarPartida(ActionEvent event) {
        System.out.println("Cargar Partida clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaCargarPartida.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cargar Partida - Pingu Oreo");
            // Forzar re-maximización al cambiar de escena
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            System.out.println("No se pudo cargar PantallaCargarPartida.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpciones(ActionEvent event) {
        System.out.println("Opciones clicked");
        // TODO: Implementar menú opciones
    }

    @FXML
    private void handleCreditos(ActionEvent event) {
        System.out.println("Créditos clicked");
        // TODO: Implementar pantalla de créditos
    }

    @FXML
    private void handleSalir(ActionEvent event) {
        System.out.println("Saliendo del juego...");
        System.exit(0);
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
        System.out.println("Cerrando el juego...");
        System.exit(0);
    }

    // ==========================================
    // LÓGICA PARA ARRASTRAR LA VENTANA
    // ==========================================

    @FXML
    private void onTitleBarPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onTitleBarDragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if(stage.isMaximized()) {
            stage.setMaximized(false);
        }
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}

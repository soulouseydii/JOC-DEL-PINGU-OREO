package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.StageStyle;
import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;


public class PantallaInicio extends Application {


    @FXML private Button btnNuevaPartida;
    @FXML private Button btnCargarPartida;
    @FXML private Button btnOpciones;
    @FXML private Button btnCreditos;
    @FXML private Button btnSalir;

    // Elementos del menú de opciones
    @FXML private VBox paneOpciones;
    @FXML private CheckBox chkMusica;
    @FXML private Slider sliderMusica;
    @FXML private CheckBox chkSonido;
    @FXML private Slider sliderSonido;
    @FXML private CheckBox chkAnimaciones;
    @FXML private CheckBox chkPantallaCompleta;

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
            try {
                primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/imagenes/icono_oreo.png")));
            } catch (Exception e) {
                System.out.println("No se pudo cargar el icono del juego");
            }
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
        
        // Cerrar la app al pulsar ESC
        btnSalir.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        System.out.println("ESC pulsado - Saliendo del juego...");
                        System.exit(0);
                    }
                });
            }
        });
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
        paneOpciones.setVisible(true);
    }

    @FXML
    private void cerrarOpciones(ActionEvent event) {
        paneOpciones.setVisible(false);
    }

    @FXML
    private void handleChkMusica(ActionEvent event) {
        // TODO: Implement music system later
        // TODO: Apply changes in real time later
        // TODO: Save settings persistently later
    }

    @FXML
    private void handleSliderMusica(MouseEvent event) {
        // TODO: Implement music system later
        // TODO: Apply changes in real time later
        // TODO: Save settings persistently later
    }

    @FXML
    private void handleChkSonido(ActionEvent event) {
        // TODO: Implement sound effects system later
        // TODO: Apply changes in real time later
        // TODO: Save settings persistently later
    }

    @FXML
    private void handleSliderSonido(MouseEvent event) {
        // TODO: Implement sound effects system later
        // TODO: Apply changes in real time later
        // TODO: Save settings persistently later
    }

    @FXML
    private void handleChkAnimaciones(ActionEvent event) {
        // TODO: Implement animations system later
        // TODO: Apply changes in real time later
        // TODO: Save settings persistently later
    }

    @FXML
    private void handleChkPantallaCompleta(ActionEvent event) {
        Stage stage = (Stage) paneOpciones.getScene().getWindow();
        stage.setMaximized(chkPantallaCompleta.isSelected());
        // TODO: Save settings persistently later
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


}

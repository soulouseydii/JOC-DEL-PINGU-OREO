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
import CONTROLADOR.GestorAudio;


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
        
        // Inicializar Gestor de Audio
        GestorAudio gestorAudio = GestorAudio.getInstance();
        gestorAudio.playMusicaFondo();
        
        // Aplicar efectos visuales y sonoros a los botones cuando la escena esté lista
        btnSalir.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());

                // Cerrar la app al pulsar ESC
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        System.out.println("ESC pulsado - Saliendo del juego...");
                        System.exit(0);
                    }
                });
            }
        });
        
        // Sincronizar UI de Opciones de Inicio
        if(chkMusica != null) chkMusica.setSelected(gestorAudio.isMusicaHabilitada());
        if(sliderMusica != null) {
            sliderMusica.setValue(50); // Default placeholder, you could store volume globally later
        }

        if(chkSonido != null) chkSonido.setSelected(gestorAudio.isSonidoHabilitado());
        if(sliderSonido != null) {
            sliderSonido.setValue(100);
        }
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
        boolean habilitada = chkMusica.isSelected();
        GestorAudio.getInstance().setMusicaHabilitada(habilitada);
    }

    @FXML
    private void handleSliderMusica(MouseEvent event) {
        if (sliderMusica != null) {
            // Volume ranges from 0 to 1 in MediaPlayer, and Slider is maybe 0 to 100
            double sliderVal = sliderMusica.getValue();
            GestorAudio.getInstance().setVolumenMusica(sliderVal / 100.0);
        }
    }

    @FXML
    private void handleChkSonido(ActionEvent event) {
        boolean habilitado = chkSonido.isSelected();
        GestorAudio.getInstance().setSonidosHabilitados(habilitado);
    }

    @FXML
    private void handleSliderSonido(MouseEvent event) {
        if (sliderSonido != null) {
            double sliderVal = sliderSonido.getValue();
            GestorAudio.getInstance().setVolumenSonidos(sliderVal / 100.0);
        }
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

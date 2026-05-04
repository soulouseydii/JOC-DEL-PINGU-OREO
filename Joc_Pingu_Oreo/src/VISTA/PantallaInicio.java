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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import CONTROLADOR.GestorAudio;
import CONTROLADOR.GestorAnimacionesVistas;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.Random;
import java.util.ArrayList;
import CONTROLADOR.GestorBBDD;

public class PantallaInicio extends Application {


    @FXML private Button btnNuevaPartida;
    @FXML private Button btnCargarPartida;
    @FXML private Button btnOpciones;
    @FXML private Button btnCreditos;
    @FXML private Button btnSalir;

    @FXML private AnchorPane sceneRoot;

    // Menú principal
    @FXML private HBox menuPrincipal;

    // Elementos del menú de opciones
    @FXML private StackPane paneOpciones;
    @FXML private CheckBox chkMusica;
    @FXML private Slider sliderMusica;
    @FXML private CheckBox chkSonido;
    @FXML private Slider sliderSonido;

    @FXML private CheckBox chkPantallaCompleta;

    @FXML private Label lblRanking1;
    @FXML private Label lblRanking2;
    @FXML private Label lblRanking3;
    @FXML private Label lblVictorias1;
    @FXML private Label lblVictorias2;
    @FXML private Label lblVictorias3;

    private GestorBBDD gestorBBDD = new GestorBBDD();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Configurar ventana sin bordes y maximizada
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle("Inicio Pingu Oreo");
            try {
                primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/imagenes/icono_oreo.png")));
            } catch (Exception e) {
                System.out.println("No se pudo cargar el icono del juego");
            }

            // Crear y mostrar la pantalla de carga primero
            PantallaCarga pantallaCarga = new PantallaCarga(() -> {
                // Este callback se ejecuta cuando la carga termina
                cargarMenuPrincipal(primaryStage);
            });

            Scene escenaCarga = pantallaCarga.crearEscena();
            primaryStage.setScene(escenaCarga);
            primaryStage.setMaximized(true);
            primaryStage.show();

            System.out.println("Pantalla de carga mostrada.");

        } catch (Exception e) {
            System.out.println("Error al iniciar la pantalla de carga");
            e.printStackTrace();
            // Fallback: intentar cargar directamente el menú principal
            cargarMenuPrincipal(primaryStage);
        }
    }

    /**
     * Carga el menú principal desde el FXML y lo muestra en el Stage.
     * Se llama automáticamente cuando la pantalla de carga finaliza.
     */
    private void cargarMenuPrincipal(Stage stage) {
        try {
            String rutaFxml = "/RESOURCES/PantallaInicio.fxml"; 
            java.net.URL url = getClass().getResource(rutaFxml);
            
            if (url != null) {
                Parent root = FXMLLoader.load(url);

                // Reutilizar la escena existente para mantener el estado maximizado
                Scene escenaActual = stage.getScene();
                if (escenaActual != null) {
                    escenaActual.setRoot(root);
                } else {
                    stage.setScene(new Scene(root));
                }

                // Forzar que el stage se mantenga maximizado
                stage.setMaximized(true);
                
                System.out.println("El diseño fxml de inicio se ha cargado.");
            } else {
                System.out.println("No encuentra el FXML en la ruta: " + rutaFxml);
            }

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
        
        crearNieveProcedural();
        
        // Aplicar efectos visuales y sonoros a los botones cuando la escena esté lista
        btnSalir.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
                
                // Animar menú en cascada al entrar
                GestorAnimacionesVistas.animarEntradaCascada(menuPrincipal);

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

        cargarRanking();
    }

    private void cargarRanking() {
        ArrayList<String[]> ranking = gestorBBDD.obtenerRanking();
        
        // Reset default labels
        lblRanking1.setText("---"); lblVictorias1.setText("");
        lblRanking2.setText("---"); lblVictorias2.setText("");
        lblRanking3.setText("---"); lblVictorias3.setText("");

        if (ranking.size() >= 1) {
            lblRanking1.setText(ranking.get(0)[0]);
            lblVictorias1.setText(ranking.get(0)[1] + " v");
        }
        if (ranking.size() >= 2) {
            lblRanking2.setText(ranking.get(1)[0]);
            lblVictorias2.setText(ranking.get(1)[1] + " v");
        }
        if (ranking.size() >= 3) {
            lblRanking3.setText(ranking.get(2)[0]);
            lblVictorias3.setText(ranking.get(2)[1] + " v");
        }
    }

    private void crearNieveProcedural() {
        if (sceneRoot != null) {
            Random random = new Random();
            int count = 18; // 18 snowflakes as in the HTML
            
            for (int i = 0; i < count; i++) {
                Label copo = new Label("·");
                
                // Random size: 4px to 10px
                double size = 4 + random.nextDouble() * 6;
                copo.setStyle("-fx-font-size: " + size + "px;");
                
                // Random color
                int r = 180 + random.nextInt(40);
                int g = 210 + random.nextInt(30);
                int b = 255;
                double a = 0.2 + random.nextDouble() * 0.4;
                copo.setTextFill(Color.rgb(r, g, b, a));
                
                copo.setMouseTransparent(true);
                
                // Add behind UI elements (aurora is index 1-4, background is 0)
                sceneRoot.getChildren().add(5, copo); 
                
                // Random X position bound to window width
                double xPercent = random.nextDouble();
                copo.translateXProperty().bind(sceneRoot.widthProperty().multiply(xPercent));
                copo.setLayoutY(-20);
                
                // Animation down
                double durationSeconds = 6 + random.nextDouble() * 10;
                TranslateTransition transition = new TranslateTransition(Duration.seconds(durationSeconds), copo);
                transition.setFromY(0);
                transition.setToY(1500); // Ensures it goes off screen on full HD monitors
                transition.setCycleCount(TranslateTransition.INDEFINITE);
                
                // Delay or playFrom to scatter them immediately
                transition.playFrom(Duration.seconds(random.nextDouble() * durationSeconds));
            }
        }
    }

    @FXML
    private void handleNuevaPartida(ActionEvent event) {
        System.out.println("Nueva Partida clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaConfiguracionPartida.fxml"));
            Parent root = loader.load();
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Configuración Pingu Oreo");
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
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Cargar Partida - Pingu Oreo");
        } catch (Exception e) {
            System.out.println("No se pudo cargar PantallaCargarPartida.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpciones(ActionEvent event) {
        System.out.println("Opciones clicked");
        menuPrincipal.setVisible(false);
        paneOpciones.setVisible(true);
        GestorAnimacionesVistas.animarAparicionOverlay(paneOpciones);
    }

    @FXML
    private void cerrarOpciones(ActionEvent event) {
        paneOpciones.setVisible(false);
        menuPrincipal.setVisible(true);
        GestorAnimacionesVistas.animarEntradaCascada(menuPrincipal);
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
    private void handleChkPantallaCompleta(ActionEvent event) {
        Stage stage = (Stage) paneOpciones.getScene().getWindow();
        stage.setMaximized(chkPantallaCompleta.isSelected());
    }

    @FXML
    private void handleCreditos(ActionEvent event) {
        System.out.println("Créditos clicked");
    }

    @FXML
    private void handleSalir(ActionEvent event) {
        System.out.println("Saliendo del juego...");
        System.exit(0);
    }


}

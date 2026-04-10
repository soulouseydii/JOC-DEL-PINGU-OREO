package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import MODELO.*;

public class PantallaConfiguracionPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ComboBox<Integer> comboNumJugadores;
    @FXML private HBox contenedorJugadores;
    @FXML private Button btnEmpezar;

    private int numJugadores = 2; // Default
    
    // Arrays de skins disponibles
    private static final String[] SKINS_PINGUINO = {"Pingu Azul", "Pingu Rojo", "Pingu Verde", "Pingu Amarillo"};
    private static final String[] SKINS_FOCA = {"Foca Azul", "Foca Roja", "Foca Verde", "Foca Amarilla"};

    // Referencia de las skins que ya han sido confirmadas por jugadores "listos"
    private java.util.Set<String> skinsSeleccionadas = new java.util.HashSet<>();

    // Clase interna para manejar cada tarjeta de jugador de forma independiente 
    private class TarjetaJugador {
        VBox root;
        ComboBox<String> comboTipo;
        VBox cajaLogin;
        TextField txtUsuario;
        PasswordField txtPassword;
        Button btnListo;
        Label lblEstado;
        boolean estaListo = false;
        
        // Elementos Carousel
        VBox cajaCarousel;
        Button btnLeft;
        Button btnRight;
        Label lblSkinActual;
        int currentSkinIndex = 0;
        String currentSkinType = "Pinguino"; // Pinguino o Foca

        public TarjetaJugador(int numero) {
            root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-border-color: #2c3e50; -fx-border-radius: 10;");
            root.setPrefWidth(200);

            Label lblTitulo = new Label("Jugador " + numero);
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("Humano (Pingüino)", "CPU (Foca)");
            comboTipo.setValue("Humano (Pingüino)");

            cajaLogin = new VBox(5);
            cajaLogin.setAlignment(Pos.CENTER);

            txtUsuario = new TextField();
            txtUsuario.setPromptText("Usuario...");
            txtUsuario.getStyleClass().add("field");

            txtPassword = new PasswordField();
            txtPassword.setPromptText("Contraseña...");
            txtPassword.getStyleClass().add("field");

            btnListo = new Button("Listo");
            btnListo.getStyleClass().add("primary");
            btnListo.setMaxWidth(Double.MAX_VALUE);

            lblEstado = new Label("Esperando login...");
            lblEstado.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

            cajaLogin.getChildren().addAll(new Label("Usuario:"), txtUsuario, new Label("Contraseña:"), txtPassword, btnListo);

            // Setup Carousel UI
            cajaCarousel = new VBox(5);
            cajaCarousel.setAlignment(Pos.CENTER);
            HBox carouselNav = new HBox(10);
            carouselNav.setAlignment(Pos.CENTER);
            btnLeft = new Button("<");
            btnLeft.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
            btnRight = new Button(">");
            btnRight.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");
            
            lblSkinActual = new Label("");
            lblSkinActual.setStyle("-fx-font-weight: bold; -fx-padding: 5; -fx-background-color: #e0e0e0; -fx-background-radius: 5; -fx-min-width: 100; -fx-alignment: center;");
            
            carouselNav.getChildren().addAll(btnLeft, lblSkinActual, btnRight);
            cajaCarousel.getChildren().addAll(new Label("Skin:"), carouselNav);

            btnLeft.setOnAction(e -> cambiarSkin(-1));
            btnRight.setOnAction(e -> cambiarSkin(1));

            comboTipo.setOnAction(e -> {
                if (estaListo) {
                    skinsSeleccionadas.remove(getCurrentSkin());
                }
                
                if (comboTipo.getValue().equals("CPU (Foca)")) {
                    currentSkinType = "Foca";
                    currentSkinIndex = 0;
                    cajaLogin.setVisible(false);
                    cajaLogin.setManaged(false);
                    lblEstado.setText("CPU Lista");
                    lblEstado.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    estaListo = true;
                    btnLeft.setDisable(false); // No bloquear para Foca
                    btnRight.setDisable(false); // No bloquear para Foca
                } else {
                    currentSkinType = "Pinguino";
                    currentSkinIndex = 0;
                    cajaLogin.setVisible(true);
                    cajaLogin.setManaged(true);
                    lblEstado.setText("Esperando login...");
                    lblEstado.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    estaListo = false;
                    
                    // Resetear el boton y los campos
                    txtUsuario.setDisable(false);
                    txtPassword.setDisable(false);
                    btnListo.setDisable(false);
                    btnLeft.setDisable(false);
                    btnRight.setDisable(false);
                }
                
                actualizarSkinLibre();
                
                if (estaListo) {
                    skinsSeleccionadas.add(getCurrentSkin());
                    notificarNuevasSkins();
                }
                
                // Aplicar efectos visuales y sonoros a los botones fijos cuando la escena esté lista
                btnEmpezar.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
                    }
                });
                
                verificarTodosListos();
            });

            btnListo.setOnAction(e -> {
                if (!txtUsuario.getText().isEmpty() && !txtPassword.getText().isEmpty()) {
                    // Simulación de validación exitosa (Login)
                    lblEstado.setText("¡Jugador Listo!");
                    lblEstado.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    estaListo = true;
                    // Bloquear campos para que no se modifiquen tras el login
                    txtUsuario.setDisable(true);
                    txtPassword.setDisable(true);
                    btnListo.setDisable(true);
                    btnLeft.setDisable(true);
                    btnRight.setDisable(true);
                    comboTipo.setDisable(true);
                    
                    // Registrar skin ocupada
                    skinsSeleccionadas.add(getCurrentSkin());
                    notificarNuevasSkins();
                    
                    verificarTodosListos();
                } else {
                    lblEstado.setText("¡Faltan credenciales!");
                }
            });

            root.getChildren().addAll(lblTitulo, new Label("Tipo:"), comboTipo, cajaCarousel, cajaLogin, lblEstado);
            actualizarSkinLibre(); // Inicializar
        }
        
        private String[] getSkinArray() {
            return currentSkinType.equals("Pinguino") ? SKINS_PINGUINO : SKINS_FOCA;
        }

        public String getCurrentSkin() {
            return getSkinArray()[currentSkinIndex];
        }

        private void animarCambioSkin(String nuevaSkin) {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100), lblSkinActual);
            st.setToX(0);
            st.setOnFinished(e -> {
                lblSkinActual.setText(nuevaSkin);
                javafx.animation.ScaleTransition st2 = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(100), lblSkinActual);
                st2.setToX(1.0);
                st2.play();
            });
            st.play();
        }

        private void cambiarSkin(int delta) {
            String oldSkin = getCurrentSkin();
            String[] skins = getSkinArray();
            int n = skins.length;
            int offset = currentSkinIndex;
            
            for (int i = 0; i < n; i++) {
                offset = (offset + delta + n) % n;
                if (!skinsSeleccionadas.contains(skins[offset])) {
                    if (estaListo) {
                        skinsSeleccionadas.remove(oldSkin);
                        skinsSeleccionadas.add(skins[offset]);
                        notificarNuevasSkins();
                    }
                    currentSkinIndex = offset;
                    animarCambioSkin(skins[currentSkinIndex]);
                    return;
                }
            }
        }

        public void actualizarSkinLibre() {
            if (estaListo) return; // Si ya estaba listo, no cambia
            String skin = getCurrentSkin();
            if (skinsSeleccionadas.contains(skin)) {
                cambiarSkin(1); // Mover al siguiente libre
            } else {
                animarCambioSkin(skin);
            }
        }
    }
    
    private void notificarNuevasSkins() {
        for (TarjetaJugador tj : tarjetasActivas) {
            if (!tj.estaListo) {
                tj.actualizarSkinLibre();
            }
        }
    }

    private List<TarjetaJugador> tarjetasActivas = new ArrayList<>();

    @FXML
    private void initialize() {
        System.out.println("PantallaConfiguracionPartida Controller initialized");
        comboNumJugadores.getItems().addAll(2, 3, 4);
        comboNumJugadores.setValue(2);
        
        // Aplicar efectos visuales y sonoros a los botones fijos cuando la escena esté lista
        btnEmpezar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
            }
        });
        
        generarTarjetas();
    }

    @FXML
    private void onNumJugadoresChanged() {
        numJugadores = comboNumJugadores.getValue();
        generarTarjetas();
        verificarTodosListos();
    }

    private void generarTarjetas() {
        skinsSeleccionadas.clear();
        contenedorJugadores.getChildren().clear();
        tarjetasActivas.clear();

        for (int i = 1; i <= numJugadores; i++) {
            TarjetaJugador tj = new TarjetaJugador(i);
            tarjetasActivas.add(tj);
            contenedorJugadores.getChildren().add(tj.root);
        }
    }

    private void verificarTodosListos() {
        boolean todosListos = true;
        for (TarjetaJugador tj : tarjetasActivas) {
            if (!tj.estaListo) {
                todosListos = false;
                break;
            }
        }
        btnEmpezar.setDisable(!todosListos);
    }

    @FXML
    private void handleEmpezar(ActionEvent event) {
        System.out.println("Partida iniciada con " + numJugadores + " jugadores.");
        
        ArrayList<Jugador> jugadoresConfigurados = new ArrayList<>();
        
        for (int i = 0; i < numJugadores; i++) {
            TarjetaJugador tj = tarjetasActivas.get(i);
            String tipo = tj.comboTipo.getValue();
            String color = tj.getCurrentSkin(); // Color ahora pasa a ser el nombre real de la skin elegida
            
            if (tipo.equals("CPU (Foca)")) {
                jugadoresConfigurados.add(new Foca("Jugador " + (i + 1) + " (Foca)", color, 0));
            } else {
                Inventario inv = new Inventario();
                inv.getlista().add(new Dado("Normal"));
                String nombre = tj.txtUsuario.getText();
                if (nombre == null || nombre.trim().isEmpty()) {
                    nombre = "Jugador " + (i + 1);
                }
                jugadoresConfigurados.add(new Pinguino(nombre, color, 0, inv));
            }
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
            Parent root = loader.load();
            
            PantallaJuego controller = loader.getController();
            controller.iniciarConJugadores(jugadoresConfigurados);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Juego Pingu Oreo");
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            System.out.println("No se ha podido iniciar el juego 'PantallaJuego.fxml'");
            e.printStackTrace();
        }
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
    // LÓGICA DE LA BARRA DE TÍTULO
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
        if(stage.isMaximized()) stage.setMaximized(false);
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}

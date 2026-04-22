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
import CONTROLADOR.GestorAnimacionesVistas;

public class PantallaConfiguracionPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ComboBox<Integer> comboNumJugadores;
    @FXML private javafx.scene.control.TextField txtNombrePartida;
    @FXML private Label lblErrorNombre;
    @FXML private HBox contenedorJugadores;
    @FXML private Button btnEmpezar;

    private int numJugadores = 2; 
    
    private static final String[] SKINS_PINGUINO = {"pinguino.png", "pinguino_cool.png", "pinguino_corredor.png", "pinguino_corredor2.png", "pinguino_oreo.png"};
    private static final String[] SKINS_FOCA = {"foca_default.png", "foca_oreo.png", "foca_pirata.png", "foca_rey.png", "foca_robot.png"};

    private java.util.Set<String> skinsSeleccionadas = new java.util.HashSet<>();
    private List<TarjetaJugador> tarjetasActivas = new ArrayList<>();

    private boolean isSkinUnica(String skin) {
        return !skin.equals("pinguino.png") && !skin.equals("foca_default.png");
    }

    private class TarjetaJugador {
        StackPane root;
        VBox content;
        javafx.scene.image.ImageView bgImage;
        Label lblSelloListo;
        ComboBox<String> comboTipo;
        VBox cajaLogin;
        TextField txtUsuario;
        PasswordField txtPassword;
        Button btnListo;
        Label lblEstado;
        boolean estaListo = false;
        
        VBox cajaCarousel;
        Button btnLeft;
        Button btnRight;
        Label lblSkinActual;
        int currentSkinIndex = 0;
        String currentSkinType = "Pinguino";

        public TarjetaJugador(int numero) {
            root = new StackPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(8,20,50,0.97), rgba(5,50,85,0.97)); -fx-background-radius: 18; -fx-border-color: rgba(130,210,255,0.45); -fx-border-radius: 18; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0.15, 0, 8);");
            root.setPrefWidth(220);

            bgImage = new javafx.scene.image.ImageView();
            bgImage.setOpacity(0.12);
            bgImage.setFitWidth(400);
            bgImage.setFitHeight(400);
            bgImage.setPreserveRatio(true);

            StackPane bgContainer = new StackPane(bgImage);
            bgContainer.setPrefSize(0, 0);
            bgContainer.setMinSize(0, 0);
            bgContainer.setMaxSize(0, 0);
            bgContainer.setMouseTransparent(true);

            javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle();
            clipRect.widthProperty().bind(root.widthProperty());
            clipRect.heightProperty().bind(root.heightProperty());
            clipRect.setArcWidth(36);
            clipRect.setArcHeight(36);
            root.setClip(clipRect);

            content = new VBox(10);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(18));

            Label lblTitulo = new Label("Jugador " + numero);
            lblTitulo.setStyle("-fx-font-weight: 900; -fx-font-size: 17px; -fx-text-fill: #d0f0ff;");

            comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("Humano (Pingüino)", "CPU (Foca)");
            comboTipo.setValue("Humano (Pingüino)");
            comboTipo.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.35); -fx-text-fill: #d0f0ff;");
            comboTipo.setMaxWidth(Double.MAX_VALUE);

            cajaLogin = new VBox(5);
            cajaLogin.setAlignment(Pos.CENTER);

            txtUsuario = new TextField();
            txtUsuario.setPromptText("Usuario...");
            txtUsuario.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-text-fill: #d0f0ff;");

            txtPassword = new PasswordField();
            txtPassword.setPromptText("Contraseña...");
            txtPassword.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-text-fill: #d0f0ff;");

            btnListo = new Button("✓  Listo");
            btnListo.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(30,100,200,0.6), rgba(10,60,140,0.8)); -fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.6); -fx-text-fill: #d0f0ff; -fx-font-weight: 900; -fx-cursor: hand;");
            btnListo.setMaxWidth(Double.MAX_VALUE);

            lblEstado = new Label("Esperando login...");
            lblEstado.setStyle("-fx-text-fill: rgba(255,100,100,0.9); -fx-font-weight: bold;");

            cajaLogin.getChildren().addAll(new Label("Usuario:"), txtUsuario, new Label("Contraseña:"), txtPassword);
            for (Node n : cajaLogin.getChildren()) {
                if (n instanceof Label) ((Label) n).setStyle("-fx-text-fill: rgba(130,210,255,0.7); -fx-font-size: 11px;");
            }

            cajaCarousel = new VBox(6);
            cajaCarousel.setAlignment(Pos.CENTER);
            cajaCarousel.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.15); -fx-padding: 8;");
            HBox carouselNav = new HBox(10);
            carouselNav.setAlignment(Pos.CENTER);
            btnLeft = new Button("‹");
            btnLeft.setStyle("-fx-cursor: hand; -fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #d0f0ff; -fx-background-color: transparent;");
            btnRight = new Button("›");
            btnRight.setStyle("-fx-cursor: hand; -fx-font-weight: 900; -fx-font-size: 18px; -fx-text-fill: #d0f0ff; -fx-background-color: transparent;");
            
            lblSkinActual = new Label("");
            lblSkinActual.setStyle("-fx-font-weight: bold; -fx-background-color: rgba(100,180,255,0.15); -fx-background-radius: 6; -fx-text-fill: #c8eeff; -fx-min-width: 140; -fx-min-height: 120; -fx-alignment: center;");
            
            carouselNav.getChildren().addAll(btnLeft, lblSkinActual, btnRight);
            cajaCarousel.getChildren().addAll(new Label("SKIN:"), carouselNav);

            btnLeft.setOnAction(e -> cambiarSkin(-1));
            btnRight.setOnAction(e -> cambiarSkin(1));

            comboTipo.setOnAction(e -> {
                boolean isCpu = comboTipo.getValue().equals("CPU (Foca)");
                cajaLogin.setVisible(!isCpu);
                cajaLogin.setManaged(!isCpu);
                currentSkinType = isCpu ? "Foca" : "Pinguino";
                currentSkinIndex = 0;
                actualizarSkinDisplay();
                if (isCpu) {
                    estaListo = true;
                    btnListo.setDisable(true);
                    lblEstado.setText("CPU LISTA");
                    lblEstado.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                } else {
                    estaListo = false;
                    btnListo.setDisable(false);
                    lblEstado.setText("Esperando login...");
                    lblEstado.setStyle("-fx-text-fill: rgba(255,100,100,0.9); -fx-font-weight: bold;");
                }
                verificarTodosListos();
            });

            btnListo.setOnAction(e -> {
                String user = txtUsuario.getText();
                String pass = txtPassword.getText();
                if (user != null && !user.trim().isEmpty() && pass != null && !pass.trim().isEmpty()) {
                    estaListo = true;
                    btnListo.setDisable(true);
                    txtUsuario.setDisable(true);
                    txtPassword.setDisable(true);
                    comboTipo.setDisable(true);
                    
                    // Registrar skin ocupada
                    if (isSkinUnica(getCurrentSkin())) {
                        skinsSeleccionadas.add(getCurrentSkin());
                    }
                    lblSelloListo.setVisible(true);
                    
                    // Animación de impacto (asomarse desde grande a normal)
                    javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(300), lblSelloListo);
                    st.setFromX(2.5);
                    st.setFromY(2.5);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                    
                    notificarNuevasSkins();
                    cajaCarousel.setDisable(true);
                    lblEstado.setText("✓ LISTO");
                    lblEstado.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    skinsSeleccionadas.add(getCurrentSkin());
                    verificarTodosListos();
                } else {
                    lblEstado.setText("Faltan datos");
                }
            });

            lblSelloListo = new Label("LISTO");
            lblSelloListo.setStyle("-fx-font-size: 55px; -fx-font-weight: 900; -fx-text-fill: rgba(100, 255, 130, 0.9); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 12, 0.6, 0, 0); -fx-background-color: rgba(100, 255, 130, 0.12); -fx-background-radius: 12; -fx-padding: 0 15; -fx-border-color: rgba(100, 255, 130, 0.25); -fx-border-width: 2; -fx-border-radius: 12;");
            lblSelloListo.setRotate(-35);
            lblSelloListo.setStyle("-fx-text-fill: rgba(34, 197, 94, 0.5); -fx-font-size: 40px; -fx-font-weight: 900; -fx-rotate: -20; -fx-border-color: rgba(34, 197, 94, 0.5); -fx-border-width: 4; -fx-padding: 5 15;");
            lblSelloListo.setVisible(false);

            content.getChildren().addAll(lblTitulo, comboTipo, cajaCarousel, cajaLogin, btnListo, lblEstado);
            root.getChildren().addAll(bgContainer, content, lblSelloListo);
            
            actualizarSkinDisplay();
        }

        private void cambiarSkin(int dir) {
            String[] skins = currentSkinType.equals("Pinguino") ? SKINS_PINGUINO : SKINS_FOCA;
            currentSkinIndex = (currentSkinIndex + dir + skins.length) % skins.length;
            actualizarSkinDisplay();
        }

        private void actualizarSkinDisplay() {
            String skin = getCurrentSkin();
            lblSkinActual.setGraphic(new javafx.scene.image.ImageView(new javafx.scene.image.Image("/imagenes/" + skin, 100, 100, true, true)));
            bgImage.setImage(new javafx.scene.image.Image("/imagenes/" + skin));
        }

        public String getCurrentSkin() {
            String[] skins = currentSkinType.equals("Pinguino") ? SKINS_PINGUINO : SKINS_FOCA;
            return skins[currentSkinIndex];
        }
    }

    public void initialize() {
        comboNumJugadores.getItems().addAll(2, 3, 4);
        comboNumJugadores.setValue(2);
        
        btnEmpezar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        handleVolver(null);
                    }
                });
            }
        });

        txtNombrePartida.textProperty().addListener((obs, oldVal, newVal) -> {
            verificarTodosListos();
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
        GestorAnimacionesVistas.animarEntradaCascada(contenedorJugadores);
    }

    private void verificarTodosListos() {
        boolean nameEmpty = txtNombrePartida.getText().trim().isEmpty();
        lblErrorNombre.setVisible(nameEmpty);
        lblErrorNombre.setManaged(nameEmpty);
        
        boolean todosListos = true;
        for (TarjetaJugador tj : tarjetasActivas) {
            if (!tj.estaListo) {
                todosListos = false;
                break;
            }
        }
        btnEmpezar.setDisable(nameEmpty || !todosListos);
    }

    @FXML
    private void handleEmpezar(ActionEvent event) {
        ArrayList<Jugador> jugadoresConfigurados = new ArrayList<>();
        for (int i = 0; i < numJugadores; i++) {
            TarjetaJugador tj = tarjetasActivas.get(i);
            String tipo = tj.comboTipo.getValue();
            String color = tj.getCurrentSkin(); 
            
            if (tipo.equals("CPU (Foca)")) {
                jugadoresConfigurados.add(new Foca("Jugador " + (i + 1) + " (Foca)", color, 0));
            } else {
                Inventario inv = new Inventario();
                inv.getlista().add(new Dado("Normal"));
                String nombre = tj.txtUsuario.getText();
                if (nombre == null || nombre.trim().isEmpty()) nombre = "Jugador " + (i + 1);
                jugadoresConfigurados.add(new Pinguino(nombre, color, 0, inv));
            }
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
            Parent root = loader.load();
            PantallaJuego controller = loader.getController();
            controller.iniciarConJugadores(jugadoresConfigurados);
            controller.setNombrePartida(txtNombrePartida.getText().trim());
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
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
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}

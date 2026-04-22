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

    private int numJugadores = 2; // Default
    
    // Arrays de skins disponibles
    private static final String[] SKINS_PINGUINO = {"pinguino.png", "pinguino_cool.png", "pinguino_corredor.png", "pinguino_corredor2.png", "pinguino_oreo.png"};
    private static final String[] SKINS_FOCA = {"foca_default.png", "foca_oreo.png", "foca_pirata.png", "foca_rey.png", "foca_robot.png"};

    // Referencia de las skins que ya han sido confirmadas por jugadores "listos"
    private java.util.Set<String> skinsSeleccionadas = new java.util.HashSet<>();

    private boolean isSkinUnica(String skin) {
        return !skin.equals("pinguino.png") && !skin.equals("foca_default.png");
    }

    // Clase interna para manejar cada tarjeta de jugador de forma independiente 
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
        
        // Elementos Carousel
        VBox cajaCarousel;
        Button btnLeft;
        Button btnRight;
        Label lblSkinActual;
        int currentSkinIndex = 0;
        String currentSkinType = "Pinguino"; // Pinguino o Foca

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
            lblTitulo.setStyle("-fx-font-weight: 900; -fx-font-size: 17px; -fx-text-fill: #d0f0ff; -fx-effect: dropshadow(gaussian, rgba(100,200,255,0.4), 8, 0.3, 0, 0);");

            comboTipo = new ComboBox<>();
            comboTipo.getItems().addAll("Humano (Pingüino)", "CPU (Foca)");
            comboTipo.setValue("Humano (Pingüino)");
            comboTipo.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.35); -fx-border-radius: 8; -fx-border-width: 1; -fx-text-fill: #d0f0ff;");
            comboTipo.setMaxWidth(Double.MAX_VALUE);

            cajaLogin = new VBox(5);
            cajaLogin.setAlignment(Pos.CENTER);

            txtUsuario = new TextField();
            txtUsuario.setPromptText("Usuario...");
            txtUsuario.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-border-radius: 8; -fx-border-width: 1.5; -fx-text-fill: #d0f0ff; -fx-prompt-text-fill: rgba(160,210,255,0.35); -fx-padding: 7 10;");

            txtPassword = new PasswordField();
            txtPassword.setPromptText("Contraseña...");
            txtPassword.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-border-radius: 8; -fx-border-width: 1.5; -fx-text-fill: #d0f0ff; -fx-prompt-text-fill: rgba(160,210,255,0.35); -fx-padding: 7 10;");

            btnListo = new Button("✓  Listo");
            btnListo.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(30,100,200,0.6), rgba(10,60,140,0.8)); -fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.6); -fx-border-radius: 10; -fx-border-width: 1.5; -fx-text-fill: #d0f0ff; -fx-font-weight: 900; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 16;");
            btnListo.setMaxWidth(Double.MAX_VALUE);

            lblEstado = new Label("Esperando login...");
            lblEstado.setStyle("-fx-text-fill: rgba(255,100,100,0.9); -fx-font-weight: bold; -fx-font-size: 12px;");

            Label lblUsuario = new Label("Usuario:");
            lblUsuario.setStyle("-fx-text-fill: rgba(130,210,255,0.7); -fx-font-size: 11px; -fx-font-weight: 900;");
            Label lblPass = new Label("Contraseña:");
            lblPass.setStyle("-fx-text-fill: rgba(130,210,255,0.7); -fx-font-size: 11px; -fx-font-weight: 900;");
            cajaLogin.getChildren().addAll(lblUsuario, txtUsuario, lblPass, txtPassword);

            // Setup Carousel UI
            cajaCarousel = new VBox(6);
            cajaCarousel.setAlignment(Pos.CENTER);
            cajaCarousel.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.15); -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 8;");
            HBox carouselNav = new HBox(10);
            carouselNav.setAlignment(Pos.CENTER);
            btnLeft = new Button("‹");
            btnLeft.setStyle("-fx-cursor: hand; -fx-font-weight: 900; -fx-font-size: 18px; -fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-border-radius: 8; -fx-text-fill: #d0f0ff; -fx-padding: 4 12;");
            btnRight = new Button("›");
            btnRight.setStyle("-fx-cursor: hand; -fx-font-weight: 900; -fx-font-size: 18px; -fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; -fx-border-color: rgba(130,210,255,0.3); -fx-border-radius: 8; -fx-text-fill: #d0f0ff; -fx-padding: 4 12;");
            
            lblSkinActual = new Label("");
            lblSkinActual.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-color: rgba(100,180,255,0.15); -fx-background-radius: 6; -fx-border-color: rgba(130,210,255,0.25); -fx-border-radius: 6; -fx-border-width: 1; -fx-text-fill: #c8eeff; -fx-font-size: 12px; -fx-min-width: 140; -fx-min-height: 120; -fx-alignment: center;");
            
            Label lblSkinHeader = new Label("SKIN:");
            lblSkinHeader.setStyle("-fx-text-fill: rgba(130,210,255,0.7); -fx-font-size: 11px; -fx-font-weight: 900;");
            carouselNav.getChildren().addAll(btnLeft, lblSkinActual, btnRight);
            cajaCarousel.getChildren().addAll(lblSkinHeader, carouselNav);

            btnLeft.setOnAction(e -> cambiarSkin(-1));
            btnRight.setOnAction(e -> cambiarSkin(1));

            comboTipo.setOnAction(e -> {
                if (estaListo) {
                    skinsSeleccionadas.remove(getCurrentSkin());
                }
                
                estaListo = false;
                btnListo.setText("Listo");
                btnListo.setDisable(false);
                lblSelloListo.setVisible(false);
                
                if (comboTipo.getValue().equals("CPU (Foca)")) {
                    currentSkinType = "Foca";
                    currentSkinIndex = 0;
                    cajaLogin.setVisible(false);
                    cajaLogin.setManaged(false);
                    lblEstado.setText("CPU Esperando...");
                    lblEstado.setStyle("-fx-text-fill: rgba(255,180,80,0.9); -fx-font-weight: bold; -fx-font-size: 12px;");
                } else {
                    currentSkinType = "Pinguino";
                    currentSkinIndex = 0;
                    cajaLogin.setVisible(true);
                    cajaLogin.setManaged(true);
                    lblEstado.setText("Esperando login...");
                    lblEstado.setStyle("-fx-text-fill: rgba(255,100,100,0.9); -fx-font-weight: bold; -fx-font-size: 12px;");
                    
                    // Resetear campos
                    txtUsuario.setDisable(false);
                    txtPassword.setDisable(false);
                }
                
                // Asegurar que los botones de skin estén activos al cambiar tipo
                btnLeft.setDisable(false);
                btnRight.setDisable(false);
                
                actualizarSkinLibre();
                
                // Aplicar efectos visuales y sonoros a los botones fijos cuando la escena esté lista
                btnEmpezar.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
                    }
                });
                
                verificarTodosListos();
            });

            btnListo.setOnAction(e -> {
                if (estaListo) {
                    // DESMARCAR LISTO (Toggle OFF)
                    estaListo = false;
                    btnListo.setText("✓  Listo");
                    btnListo.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(30,100,200,0.6), rgba(10,60,140,0.8)); -fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.6); -fx-border-radius: 10; -fx-border-width: 1.5; -fx-text-fill: #d0f0ff; -fx-font-weight: 900; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 16;");
                    lblEstado.setText(currentSkinType.equals("Foca") ? "CPU Esperando..." : "Esperando login...");
                    lblEstado.setStyle("-fx-text-fill: rgba(255,100,100,0.9); -fx-font-weight: bold; -fx-font-size: 12px;");
                    
                    // Desbloquear todo
                    txtUsuario.setDisable(false);
                    txtPassword.setDisable(false);
                    btnLeft.setDisable(false);
                    btnRight.setDisable(false);
                    comboTipo.setDisable(false);
                    
                    // Liberar skin
                    skinsSeleccionadas.remove(getCurrentSkin());
                    lblSelloListo.setVisible(false);
                    notificarNuevasSkins();
                    verificarTodosListos();
                } else {
                    // MARCAR LISTO (Toggle ON)
                    if (currentSkinType.equals("Pinguino")) {
                        if (txtUsuario.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                            lblEstado.setText("¡Faltan credenciales!");
                            return;
                        }
                    }
                    
                    estaListo = true;
                    btnListo.setText("✎  Modificar");
                    btnListo.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(10,80,30,0.7), rgba(5,60,20,0.85)); -fx-background-radius: 10; -fx-border-color: rgba(100,210,130,0.6); -fx-border-radius: 10; -fx-border-width: 1.5; -fx-text-fill: #b0ffc0; -fx-font-weight: 900; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 16;");
                    lblEstado.setText("✓  ¡Jugador Listo!");
                    lblEstado.setStyle("-fx-text-fill: rgba(100,255,130,0.9); -fx-font-weight: bold; -fx-font-size: 12px;");
                    
                    // Bloquear todo
                    txtUsuario.setDisable(true);
                    txtPassword.setDisable(true);
                    btnLeft.setDisable(true);
                    btnRight.setDisable(true);
                    comboTipo.setDisable(true);
                    
                    // Registrar skin ocupada
                    if (isSkinUnica(getCurrentSkin())) {
                        skinsSeleccionadas.add(getCurrentSkin());
                    }
                    lblSelloListo.setVisible(true);
                    notificarNuevasSkins();
                    verificarTodosListos();
                }
            });

            lblSelloListo = new Label("LISTO");
            lblSelloListo.setStyle("-fx-font-size: 45px; -fx-font-weight: 900; -fx-text-fill: rgba(100, 255, 130, 0.35); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.5, 0, 0);");
            lblSelloListo.setRotate(-35);
            lblSelloListo.setVisible(false);
            lblSelloListo.setMouseTransparent(true);

            Label lblTipoHeader = new Label("TIPO:");
            lblTipoHeader.setStyle("-fx-text-fill: rgba(130,210,255,0.7); -fx-font-size: 11px; -fx-font-weight: 900;");
            content.getChildren().addAll(lblTitulo, lblTipoHeader, comboTipo, cajaCarousel, cajaLogin, btnListo, lblEstado);
            root.getChildren().addAll(bgContainer, content, lblSelloListo);
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
                if (nuevaSkin.endsWith(".png")) {
                    try {
                        String ruta = currentSkinType.equals("Pinguino") ? "/imagenes/pinguino/" + nuevaSkin : "/imagenes/foca/" + nuevaSkin;
                        javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream(ruta));
                        javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(img);
                        iv.setFitWidth(100);
                        iv.setFitHeight(100);
                        iv.setPreserveRatio(true);
                        lblSkinActual.setGraphic(iv);
                        lblSkinActual.setText("");
                        
                        bgImage.setImage(img);
                    } catch (Exception ex) {
                        lblSkinActual.setText(nuevaSkin);
                        lblSkinActual.setGraphic(null);
                        bgImage.setImage(null);
                    }
                } else {
                    lblSkinActual.setText(nuevaSkin);
                    lblSkinActual.setGraphic(null);
                    bgImage.setImage(null);
                }

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
                if (!skinsSeleccionadas.contains(skins[offset]) || !isSkinUnica(skins[offset])) {
                    if (estaListo) {
                        skinsSeleccionadas.remove(oldSkin);
                        if (isSkinUnica(skins[offset])) {
                            skinsSeleccionadas.add(skins[offset]);
                        }
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
            if (skinsSeleccionadas.contains(skin) && isSkinUnica(skin)) {
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

        // Escuchar cambios en el nombre de la partida para validar
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
        
        // Efecto cascada de entrada para las tarjetas
        GestorAnimacionesVistas.animarEntradaCascada(contenedorJugadores);
    }

    private void verificarTodosListos() {
        boolean nameEmpty = txtNombrePartida.getText().trim().isEmpty();
        
        // Mostrar/Ocultar error
        lblErrorNombre.setVisible(nameEmpty);
        lblErrorNombre.setManaged(nameEmpty);
        
        boolean todosListos = true;
        for (TarjetaJugador tj : tarjetasActivas) {
            if (!tj.estaListo) {
                todosListos = false;
                break;
            }
        }
        
        // El botón solo se habilita si hay nombre Y todos están listos
        btnEmpezar.setDisable(nameEmpty || !todosListos);
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
            
            String nombrePartida = txtNombrePartida.getText().trim();
            
            PantallaJuego controller = loader.getController();
            controller.iniciarConJugadores(jugadoresConfigurados);
            controller.setNombrePartida(nombrePartida);
            
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

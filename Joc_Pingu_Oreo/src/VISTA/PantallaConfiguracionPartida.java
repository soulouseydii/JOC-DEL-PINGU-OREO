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

public class PantallaConfiguracionPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ComboBox<Integer> comboNumJugadores;
    @FXML private HBox contenedorJugadores;
    @FXML private Button btnEmpezar;

    private int numJugadores = 2; // Default

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

            comboTipo.setOnAction(e -> {
                if (comboTipo.getValue().equals("CPU (Foca)")) {
                    cajaLogin.setVisible(false);
                    cajaLogin.setManaged(false);
                    lblEstado.setText("CPU Lista");
                    lblEstado.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    estaListo = true;
                } else {
                    cajaLogin.setVisible(true);
                    cajaLogin.setManaged(true);
                    lblEstado.setText("Esperando login...");
                    lblEstado.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    estaListo = false;
                    
                    // Resetear el boton y los campos
                    txtUsuario.setDisable(false);
                    txtPassword.setDisable(false);
                    btnListo.setDisable(false);
                }
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
                    
                    verificarTodosListos();
                } else {
                    lblEstado.setText("¡Faltan credenciales!");
                }
            });

            root.getChildren().addAll(lblTitulo, new Label("Tipo:"), comboTipo, cajaLogin, lblEstado);
        }
    }

    private List<TarjetaJugador> tarjetasActivas = new ArrayList<>();

    @FXML
    private void initialize() {
        System.out.println("PantallaConfiguracionPartida Controller initialized");
        comboNumJugadores.getItems().addAll(2, 3, 4);
        comboNumJugadores.setValue(2);
        
        generarTarjetas();
    }

    @FXML
    private void onNumJugadoresChanged() {
        numJugadores = comboNumJugadores.getValue();
        generarTarjetas();
        verificarTodosListos();
    }

    private void generarTarjetas() {
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
            Parent root = loader.load();
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

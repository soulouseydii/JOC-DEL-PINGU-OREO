package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class PantallaMenu extends Application {
	
    @FXML private MenuItem newGame;
    @FXML private MenuItem saveGame;
    @FXML private MenuItem loadGame;
    @FXML private MenuItem quitGame;

    @FXML private TextField userField;
    @FXML private PasswordField passField;

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. IMPORTANTE: Pon el nombre EXACTO de tu archivo FXML (respetando mayúsculas/minúsculas)
            String rutaFxml = "/RESOURCES/PantallaMenu.fxml"; 
            
            java.net.URL url = getClass().getResource(rutaFxml);
            
            // 2. Comprobamos si Java encuentra el archivo
            if (url == null) {
                System.out.println("No encuentra el FXML en la ruta: " + rutaFxml);
                primaryStage.setTitle("Ventana Vacía (Fallo FXML)");
                primaryStage.show();
                return; // Detenemos la carga para que no pete el programa
            }

            // 3. Si lo encuentra, montamos la escena
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(url);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // 4. Mostramos la ventana con tu diseño
            primaryStage.setTitle("Menú Pingu Oreo");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("El diseño fxml se ha cargado.");

        } catch (Exception e) {
            System.out.println("No se ha podido cargar el diseño fxml");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void initialize() {
        // This method is called automatically after the FXML is loaded
        // You can set initial values or add listeners here
        System.out.println("pantallaPrincipalController initialized");
    }

    @FXML
    private void handleNewGame() {
        System.out.println("New Game clicked");
        // TODO
    }

    @FXML
    private void handleSaveGame() {
        System.out.println("Save Game clicked");
        // TODO
    }

    @FXML
    private void handleLoadGame() {
        System.out.println("Load Game clicked");
        // TODO
    }

    @FXML
    private void handleQuitGame() {
        System.out.println("Quit Game clicked");
        // TODO
        System.exit(0);
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = userField.getText();
        String password = passField.getText();

        System.out.println("Login pressed: " + username + " / " + password);

        // Basic check (just for demo, replace with real login logic)
        if (!username.isEmpty() && !password.isEmpty()) {
            try {
            	FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
                Parent pantallaJuegoRoot = loader.load();
                Scene pantallaJuegoScene = new Scene(pantallaJuegoRoot);

                // Get the current stage using the event
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(pantallaJuegoScene);
                stage.setTitle("Pantalla de Juego");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please. Enter user and password.");
        }
    }


    @FXML
    private void handleRegister() {
        System.out.println("Register pressed");
        // TODO
    }
}
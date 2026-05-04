package VISTA;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.ArrayList;
import CONTROLADOR.GestorPartida;
import CONTROLADOR.GestorBBDD;
import MODELO.Partida;
import CONTROLADOR.GestorAnimacionesVistas;

public class PantallaCargarPartida {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML private ListView<String> listaPartidas;
    @FXML private Button btnCargar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;
    @FXML private VBox panelCargar;
    @FXML private VBox overlayConfirmacion;

    private ObservableList<String> partidas;

    // Guardamos los IDs reales de la base de datos para saber cual cargar/eliminar
    private ArrayList<Integer> idsPartidas = new ArrayList<>();

    private GestorBBDD gestorBBDD;

    // --- Overlay de autenticación para cargar partida ---
    private StackPane overlayAuth;
    private VBox authContent;
    private ArrayList<PasswordField> camposPassword = new ArrayList<>();
    private ArrayList<String> nombresAPedir = new ArrayList<>();
    private Label lblAuthGlobalError;
    private int idPartidaSeleccionada = -1;
    private boolean modoEliminar = false; // false = cargar, true = eliminar

    @FXML
    private void initialize() {
        System.out.println("PantallaCargarPartida Controller initialized");

        gestorBBDD = new GestorBBDD();
        cargarListaDesdeOracle();

        // Aplicar animación en cascada al componente central al entrar y configurar ESC
        javafx.application.Platform.runLater(() -> {
            GestorAnimacionesVistas.animarEntradaCascada(panelCargar);

            // Crear el overlay de autenticación dinámicamente y añadirlo al StackPane padre
            crearOverlayAutenticacion();

            if (panelCargar.getScene() != null) {
                panelCargar.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                        if (overlayAuth != null && overlayAuth.isVisible()) {
                            overlayAuth.setVisible(false);
                        } else if (overlayConfirmacion.isVisible()) {
                            cancelarBorrado(null);
                        } else {
                            handleVolver(null);
                        }
                    }
                });
            }
        });
    }

    /**
     * Crea dinámicamente el overlay de autenticación y lo inyecta en el StackPane
     * que contiene el panelCargar y overlayConfirmacion.
     */
    private void crearOverlayAutenticacion() {
        overlayAuth = new StackPane();
        overlayAuth.setVisible(false);
        overlayAuth.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        overlayAuth.setAlignment(Pos.CENTER);

        // Inyectarlo como hermano del panelCargar dentro del StackPane padre
        if (panelCargar.getParent() instanceof StackPane) {
            ((StackPane) panelCargar.getParent()).getChildren().add(overlayAuth);
        }
    }

    /**
     * Construye el contenido del overlay de autenticación con un campo de contraseña
     * por cada jugador pingüino de la partida seleccionada.
     * @param titulo Título del overlay (ej. "CARGAR PARTIDA" o "ELIMINAR PARTIDA")
     */
    private void construirFormularioAuth(ArrayList<String> nombresPinguinos, String titulo) {
        overlayAuth.getChildren().clear();
        camposPassword.clear();
        nombresAPedir = nombresPinguinos;

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(480);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(8,20,50,0.97), rgba(5,50,85,0.97)); " +
                "-fx-background-radius: 18; -fx-border-color: rgba(130,210,255,0.45); " +
                "-fx-border-radius: 18; -fx-border-width: 1.5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 20, 0.15, 0, 8);");
        card.setPadding(new Insets(36, 40, 36, 40));

        // Icono y título
        String icono = modoEliminar ? "\u26A0" : "\uD83D\uDD12";
        String iconoColor = modoEliminar ? "rgba(255,100,100,0.6)" : "rgba(0,200,255,0.6)";
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 42px; -fx-effect: dropshadow(gaussian, " + iconoColor + ", 15, 0.5, 0, 0);");

        Label lblTitulo = new Label(titulo);
        String tituloColor = modoEliminar ? "#ffd0d0" : "#d0f0ff";
        lblTitulo.setStyle("-fx-text-fill: " + tituloColor + "; -fx-font-size: 18px; -fx-font-weight: 900;");

        Label lblSubtitulo = new Label("Introduce la contraseña de cada jugador");
        lblSubtitulo.setStyle("-fx-text-fill: rgba(200,230,255,0.7); -fx-font-size: 13px;");

        // Separador
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setMaxWidth(350);
        sep.setStyle("-fx-background-color: rgba(94,207,255,0.2);");

        card.getChildren().addAll(lblIcono, lblTitulo, lblSubtitulo, sep);

        // Campo de contraseña por cada pingüino
        for (String nombre : nombresPinguinos) {
            VBox filaJugador = new VBox(4);
            filaJugador.setAlignment(Pos.CENTER_LEFT);

            Label lblNombre = new Label("\uD83D\uDC27  " + nombre);
            lblNombre.setStyle("-fx-text-fill: #c8eeff; -fx-font-weight: bold; -fx-font-size: 14px;");

            PasswordField campoPass = new PasswordField();
            campoPass.setPromptText("Contraseña de " + nombre + "...");
            campoPass.setStyle("-fx-background-color: rgba(255,255,255,0.07); -fx-background-radius: 8; " +
                    "-fx-border-color: rgba(130,210,255,0.3); -fx-text-fill: white; -fx-prompt-text-fill: rgba(200,230,255,0.4);");
            campoPass.setMaxWidth(Double.MAX_VALUE);

            filaJugador.getChildren().addAll(lblNombre, campoPass);
            card.getChildren().add(filaJugador);
            camposPassword.add(campoPass);
        }

        // Label error global
        lblAuthGlobalError = new Label("");
        lblAuthGlobalError.setStyle("-fx-text-fill: #ff6e6e; -fx-font-size: 12px; -fx-font-weight: bold;");
        card.getChildren().add(lblAuthGlobalError);

        // Botones
        HBox botones = new HBox(16);
        botones.setAlignment(Pos.CENTER);

        Button btnCancelarAuth = new Button("C A N C E L A R");
        btnCancelarAuth.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 10; " +
                "-fx-border-color: rgba(130,210,255,0.4); -fx-border-radius: 10; -fx-text-fill: #d0f0ff; " +
                "-fx-font-weight: 900; -fx-cursor: hand; -fx-padding: 10 24;");
        btnCancelarAuth.setOnAction(e -> overlayAuth.setVisible(false));

        String btnTexto = modoEliminar ? "\uD83D\uDDD1  E L I M I N A R" : "▶  V E R I F I C A R";
        String btnEstilo = modoEliminar
                ? "-fx-background-color: linear-gradient(to bottom right, rgba(200,50,50,0.6), rgba(140,20,20,0.8)); " +
                  "-fx-background-radius: 10; -fx-border-color: rgba(255,130,130,0.6); -fx-border-radius: 10; " +
                  "-fx-text-fill: #ffd0d0; -fx-font-weight: 900; -fx-cursor: hand; -fx-padding: 10 24;"
                : "-fx-background-color: linear-gradient(to bottom right, rgba(30,100,200,0.6), rgba(10,60,140,0.8)); " +
                  "-fx-background-radius: 10; -fx-border-color: rgba(130,210,255,0.6); -fx-border-radius: 10; " +
                  "-fx-text-fill: #d0f0ff; -fx-font-weight: 900; -fx-cursor: hand; -fx-padding: 10 24;";

        Button btnAccionAuth = new Button(btnTexto);
        btnAccionAuth.setStyle(btnEstilo);
        btnAccionAuth.setOnAction(e -> verificarCredencialesYEjecutar());

        botones.getChildren().addAll(btnCancelarAuth, btnAccionAuth);
        card.getChildren().add(botones);

        // ScrollPane por si hay muchos jugadores
        ScrollPane scroll = new ScrollPane(card);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setMaxHeight(600);
        scroll.setMaxWidth(520);

        overlayAuth.getChildren().add(scroll);
    }

    /**
     * Verifica todas las contraseñas introducidas contra la BBDD.
     * Si todas son correctas, ejecuta la acción (cargar o eliminar según modoEliminar).
     */
    private void verificarCredencialesYEjecutar() {
        StringBuilder errores = new StringBuilder();
        boolean todoOK = true;

        for (int i = 0; i < nombresAPedir.size(); i++) {
            String nombre = nombresAPedir.get(i);
            String pass = camposPassword.get(i).getText();

            if (pass == null || pass.trim().isEmpty()) {
                errores.append("Falta contraseña de ").append(nombre).append("\n");
                todoOK = false;
                camposPassword.get(i).setStyle("-fx-background-color: rgba(255,80,80,0.15); -fx-background-radius: 8; " +
                        "-fx-border-color: rgba(255,100,100,0.6); -fx-text-fill: white;");
            } else if (!gestorBBDD.iniciarSesion(nombre, pass.trim())) {
                errores.append("❌ Contraseña incorrecta para ").append(nombre).append("\n");
                todoOK = false;
                camposPassword.get(i).setStyle("-fx-background-color: rgba(255,80,80,0.15); -fx-background-radius: 8; " +
                        "-fx-border-color: rgba(255,100,100,0.6); -fx-text-fill: white;");
            } else {
                // OK - estilo verde
                camposPassword.get(i).setStyle("-fx-background-color: rgba(34,197,94,0.15); -fx-background-radius: 8; " +
                        "-fx-border-color: rgba(34,197,94,0.6); -fx-text-fill: white;");
            }
        }

        if (!todoOK) {
            lblAuthGlobalError.setText(errores.toString().trim());
            return;
        }

        // ¡Todos verificados! Cerrar overlay y ejecutar la acción
        overlayAuth.setVisible(false);
        if (modoEliminar) {
            eliminarPartidaReal(idPartidaSeleccionada);
        } else {
            cargarPartidaReal(idPartidaSeleccionada);
        }
    }

    private void cargarListaDesdeOracle() {
        partidas = FXCollections.observableArrayList();
        idsPartidas.clear();

        ArrayList<String[]> listaDB = gestorBBDD.listarPartidas();

        if (listaDB.isEmpty()) {
            partidas.add("(No hay partidas guardadas)");
        } else {
            for (String[] info : listaDB) {
                idsPartidas.add(Integer.parseInt(info[0]));
                partidas.add(info[1]);
            }
        }

        listaPartidas.setItems(partidas);
    }

    @FXML
    private void handleCargar(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado < 0 || idsPartidas.isEmpty()) {
            System.out.println("Por favor, selecciona una partida para cargar.");
            return;
        }

        int idPartida = idsPartidas.get(indiceSeleccionado);
        System.out.println("Preparando carga de partida con ID: " + idPartida);

        // Obtener los nombres de los pingüinos que jugaban esa partida
        ArrayList<String> nombresPinguinos = gestorBBDD.obtenerNombresPinguinosPartida(idPartida);

        if (nombresPinguinos.isEmpty()) {
            // Si no hay pingüinos (solo CPUs), cargar directamente
            cargarPartidaReal(idPartida);
        } else {
            // Mostrar overlay de autenticación para CARGAR
            modoEliminar = false;
            idPartidaSeleccionada = idPartida;
            construirFormularioAuth(nombresPinguinos, "AUTENTICACIÓN PARA CARGAR");
            overlayAuth.setVisible(true);
        }
    }

    /**
     * Carga la partida desde Oracle y abre PantallaJuego.
     * Se llama después de que todos los jugadores se hayan autenticado correctamente.
     */
    private void cargarPartidaReal(int idPartida) {
        Partida partidaCargada = gestorBBDD.cargarBBDD(idPartida);

        if (partidaCargada == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/RESOURCES/PantallaMenu.css").toExternalForm());
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la partida desde la base de datos.");
            alert.showAndWait();
            return;
        }

        // Abrir PantallaJuego con la partida cargada
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaJuego.fxml"));
            Parent root = loader.load();

            PantallaJuego controller = loader.getController();
            controller.cargarPartidaGuardada(partidaCargada);

            Scene scene = panelCargar.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Juego Pingu Oreo - Partida Cargada");

        } catch (Exception e) {
            System.out.println("Error al abrir PantallaJuego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado < 0 || idsPartidas.isEmpty()) {
            System.out.println("Por favor, selecciona una partida para eliminar.");
            return;
        }

        int idPartida = idsPartidas.get(indiceSeleccionado);

        // Obtener los pingüinos de esa partida para pedir autenticación
        ArrayList<String> nombresPinguinos = gestorBBDD.obtenerNombresPinguinosPartida(idPartida);

        if (nombresPinguinos.isEmpty()) {
            // Si no hay pingüinos, mostrar confirmación clásica
            GestorAnimacionesVistas.animarAparicionOverlay(overlayConfirmacion);
        } else {
            // Mostrar overlay de autenticación para ELIMINAR
            modoEliminar = true;
            idPartidaSeleccionada = idPartida;
            construirFormularioAuth(nombresPinguinos, "AUTENTICACIÓN PARA ELIMINAR");
            overlayAuth.setVisible(true);
        }
    }

    /**
     * Elimina la partida de la BBDD después de una autenticación exitosa.
     */
    private void eliminarPartidaReal(int idPartida) {
        boolean eliminada = gestorBBDD.eliminarPartida(idPartida);

        if (eliminada) {
            System.out.println("Partida ID " + idPartida + " eliminada.");
            cargarListaDesdeOracle();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/RESOURCES/PantallaMenu.css").toExternalForm());
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo eliminar la partida.");
            alert.showAndWait();
        }
    }

    @FXML
    private void confirmarBorrado(ActionEvent event) {
        int indiceSeleccionado = listaPartidas.getSelectionModel().getSelectedIndex();

        if (indiceSeleccionado >= 0 && indiceSeleccionado < idsPartidas.size()) {
            int idPartida = idsPartidas.get(indiceSeleccionado);
            eliminarPartidaReal(idPartida);
        }

        overlayConfirmacion.setVisible(false);
    }

    @FXML
    private void cancelarBorrado(ActionEvent event) {
        overlayConfirmacion.setVisible(false);
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
            Parent root = loader.load();
            Scene scene = panelCargar.getScene();
            scene.setRoot(root);
            
            Stage stage = (Stage) scene.getWindow();
            stage.setTitle("Inicio Pingu Oreo");
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
        if (stage.isMaximized()) {
            stage.setMaximized(false);
        }
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}

package VISTA;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.PathTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.RotateTransition;
import javafx.animation.FadeTransition;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurveTo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

import CONTROLADOR.GestorPartida;
import MODELO.*;

public class PantallaJuego {


	// Botón dado
	@FXML private Button dado;
	
	// Textos UI
	@FXML private Text dadoResultText;
	
	// Inventario UI
	@FXML private Label inventarioTitulo;
	@FXML private Text peces_t;
	@FXML private Text nieve_t;
	@FXML private Text moto_t;
	@FXML private Button btnPeces;
	@FXML private Button btnNieve;
	@FXML private Button btnMoto;
	@FXML private Button btnSettings;
	@FXML private Button btnDadoRapido;
	@FXML private Button btnDadoLento;
	@FXML private Label lblCantRapido;
	@FXML private Label lblCantLento;
	
	// Log de eventos
	@FXML private TextArea eventosLog;

	// Contenedores para Overlay
	@FXML private BorderPane mainContainer;
	@FXML private VBox menuOverlay;
	@FXML private VBox optionsSubmenu;
	@FXML private CheckBox checkMusica;
	@FXML private Slider sliderVolumen;
	@FXML private CheckBox checkSonidos;
	@FXML private Slider sliderSonidos;
	@FXML private CheckBox chkPantallaCompleta;
	@FXML private VBox confirmationOverlay;
	@FXML private Label confirmationTitle;
	@FXML private Label confirmationMessage;

	// Snowball Fight overlays
	@FXML private StackPane snowballFightImageOverlay;
	@FXML private ImageView snowballFightImage;
	@FXML private VBox snowballDecisionOverlay;
	@FXML private Label snowballDecisionMessage;

	// Tablero y fichas
	@FXML private GridPane tablero;
	@FXML private Circle P1;
	@FXML private Circle P2;
	@FXML private Circle P3;
	@FXML private Circle P4;

	private GestorPartida gestorPartida;
	private int[] posiciones = new int[] {-1, -1, -1, -1};
	private boolean isPaused = false;
	private PauseTransition cpuPauseTransition;
	private List<Animation> currentAnimations = new ArrayList<>();
	private static final int COLUMNS = 10;
	private static final int ROWS = 5;
	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";

	private enum ConfirmAction { BACK_TO_MENU, EXIT_GAME }
	private ConfirmAction pendingAction;

	// PvP snowball fight state
	private Pinguino pvpAtacante;
	private Pinguino pvpDefensor;
	private int pvpIndiceAtacante;

	// Imágenes de las casillas
	private Image imgNormal;
	private Image imgAgujero;
	private Image imgTrineo;
	private Image imgSueloQuebradizo;
	private Image imgOso;
	private Image imgEvento;

	// =========================================
	//  INICIALIZACIÓN
	// =========================================

	@FXML
	private void initialize() {
		gestorPartida = new GestorPartida();
		
		try {
			imgNormal = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_normal.png"));
			imgAgujero = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_agujero.png"));
			imgTrineo = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_trineo.png"));
			imgSueloQuebradizo = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_SueloQuebradizo.png"));
			imgOso = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_oso.png"));
			imgEvento = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_evento.png"));
		} catch (Exception e) {
			System.out.println("Error al cargar imágenes de las casillas: " + e.getMessage());
		}
		
		// Sincronizar audio con GestorAudio
		CONTROLADOR.GestorAudio gestorAudio = CONTROLADOR.GestorAudio.getInstance();
		if (checkMusica != null) {
			checkMusica.setSelected(gestorAudio.isMusicaHabilitada());
			checkMusica.setOnAction(e -> gestorAudio.setMusicaHabilitada(checkMusica.isSelected()));
		}
		if (sliderVolumen != null) {
			sliderVolumen.setValue(50.0);
			sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
				gestorAudio.setVolumenMusica(newVal.doubleValue() / 100.0);
			});
		}
		if (checkSonidos != null) {
			checkSonidos.setSelected(gestorAudio.isSonidoHabilitado());
			checkSonidos.setOnAction(e -> gestorAudio.setSonidosHabilitados(checkSonidos.isSelected()));
		}
		if (sliderSonidos != null) {
			sliderSonidos.setValue(100.0);
			sliderSonidos.valueProperty().addListener((obs, oldVal, newVal) -> {
				gestorAudio.setVolumenSonidos(newVal.doubleValue() / 100.0);
			});
		}

		// Tooltip Opciones (1s de retardo)
		Tooltip tooltip = new Tooltip("Opciones");
		tooltip.setShowDelay(Duration.seconds(1));
		btnSettings.setTooltip(tooltip);
		
		// Clipping bounds del tablero principal
		javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle();
		clipRect.widthProperty().bind(tablero.widthProperty());
		clipRect.heightProperty().bind(tablero.heightProperty());
		tablero.setClip(clipRect);
		
		// Aplicar efectos visuales y sonoros a los botones cuando la escena esté lista
		tablero.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				CONTROLADOR.GestorAudio.aplicarEfectosATodosLosBotones(newScene.getRoot());
			}
		});
	}

	public void iniciarConJugadores(ArrayList<Jugador> jugadores) {
		gestorPartida.nuevaPartida(jugadores);
		mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
		
		// Ocultar fichas que no participen visualmente
		P1.setVisible(false);
		P2.setVisible(false);
		P3.setVisible(false);
		P4.setVisible(false);
		
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			getFicha(i).setVisible(true);
		}
		
		actualizarTodasLasFichas();
		
		actualizarInventarioUI();
		
		Jugador j1 = gestorPartida.getPartida().getJugadorActual();
		agregarEvento("🎮 ¡El juego ha comenzado! Turno de " + j1.getNombre());
		
		if (j1 instanceof Foca) {
			dado.setDisable(true);
			btnMoto.setDisable(true);
			btnPeces.setDisable(true);
			btnNieve.setDisable(true);
			cpuPauseTransition = new PauseTransition(Duration.millis(1500));
			cpuPauseTransition.setOnFinished(e -> {
				currentAnimations.remove(cpuPauseTransition);
				if (!isPaused) handleDado(null);
			});
			currentAnimations.add(cpuPauseTransition);
			cpuPauseTransition.play();
		}
	}

	@FXML
	private void handleDadoRapido(ActionEvent event) {
		if (isPaused) return;
		Jugador j = gestorPartida.getPartida().getJugadorActual();
		j.getInventario().usarDadoRapido();
		ejecutarAnimacionDado(new Dado("Rapido"), btnDadoRapido);
	}

	@FXML
	private void handleDadoLento(ActionEvent event) {
		if (isPaused) return;
		Jugador j = gestorPartida.getPartida().getJugadorActual();
		j.getInventario().usarDadoLento();
		ejecutarAnimacionDado(new Dado("Lento"), btnDadoLento);
	}

	private void ejecutarAnimacionDado(Dado dadoEspecial, Button botonAAnimar) {
		dado.setDisable(true);
		btnDadoRapido.setDisable(true);
		btnDadoLento.setDisable(true);
		btnMoto.setDisable(true);
		btnPeces.setDisable(true);
		btnNieve.setDisable(true);

		// 1. Animación visual del botón (vibración)
		RotateTransition rt = new RotateTransition(Duration.millis(80), botonAAnimar);
		rt.setFromAngle(-15);
		rt.setToAngle(15);
		rt.setCycleCount(10);
		rt.setAutoReverse(true);

		// 2. Animación de cambio de números en el texto
		Timeline timeline = new Timeline();
		for (int i = 0; i < 10; i++) {
			final int tempNum = (int)(Math.random() * (dadoEspecial != null ? (dadoEspecial.getMax() - dadoEspecial.getMin() + 1) : 6)) + (dadoEspecial != null ? dadoEspecial.getMin() : 1);
			KeyFrame kf = new KeyFrame(Duration.millis(i * 100), e -> {
				dadoResultText.setText("Girando... " + tempNum);
			});
			timeline.getKeyFrames().add(kf);
		}

		timeline.setOnFinished(e -> {
			rt.stop();
			botonAAnimar.setRotate(0);
			currentAnimations.remove(rt);
			currentAnimations.remove(timeline);
			continuarTurnoTrasAnimacion(dadoEspecial);
		});

		currentAnimations.add(rt);
		currentAnimations.add(timeline);
		rt.play();
		timeline.play();
	}

	// =========================================
	//  LOG DE EVENTOS
	// =========================================

	private void agregarEvento(String mensaje) {
		if (eventosLog.getText().isEmpty()) {
			eventosLog.setText(mensaje);
		} else {
			eventosLog.appendText("\n" + mensaje);
		}
		eventosLog.setScrollTop(Double.MAX_VALUE);
		eventosLog.positionCaret(eventosLog.getText().length());
	}

	// =========================================
	//  INVENTARIO UI
	// =========================================
	
	private void actualizarInventarioUI() {
		Jugador jActual = gestorPartida.getPartida().getJugadorActual();
		
		// Título del inventario con el nombre del jugador
		inventarioTitulo.setText("Inventario - " + jActual.getNombre());
		
		if (jActual instanceof Pinguino) {
			Pinguino p = (Pinguino) jActual;
			
			int cantPeces = p.contarItem("Pez");
			int cantBolas = p.contarItem("Bola de Nieve");
			int cantMotos = p.contarItem("Moto de Nieve");
			
			peces_t.setText("Peces: " + cantPeces);
			nieve_t.setText("Bolas de nieve: " + cantBolas);
			moto_t.setText("Moto de Nieve: " + cantMotos);
			
			btnMoto.setDisable(cantMotos <= 0);
			btnPeces.setDisable(cantPeces <= 0);
			btnNieve.setDisable(cantBolas <= 0);
			
			// Dados especiales
			int cantRapidos = jActual.getInventario().getDadosRapidos();
			int cantLentos = jActual.getInventario().getDadosLentos();
			
			lblCantRapido.setText("Rápido: " + cantRapidos);
			lblCantLento.setText("Lento: " + cantLentos);
			
			btnDadoRapido.setDisable(cantRapidos <= 0);
			btnDadoLento.setDisable(cantLentos <= 0);
		} else {
			peces_t.setText("Peces: -");
			nieve_t.setText("Bolas de nieve: -");
			moto_t.setText("Moto de Nieve: -");
			btnMoto.setDisable(true);
			btnPeces.setDisable(true);
			btnNieve.setDisable(true);
			btnDadoRapido.setDisable(true);
			btnDadoLento.setDisable(true);
			lblCantRapido.setText("Rápido: -");
			lblCantLento.setText("Lento: -");
		}
	}

	// =========================================
	//  TABLERO Y COORDENADAS
	// =========================================

	private int[] getCoordenadas(int posicion) {
		int posReal = Math.max(0, Math.min(posicion, 49));
		int fila = posReal / COLUMNS;
		int col;
		if (fila % 2 == 0) {
			col = posReal % COLUMNS;
		} else {
			col = (COLUMNS - 1) - (posReal % COLUMNS);
		}
		return new int[]{col, fila};
	}

	private Image getImagenParaCasilla(Casilla casilla) {
		String tipo = casilla.getClass().getSimpleName();
		switch (tipo) {
			case "Agujero": return imgAgujero != null ? imgAgujero : imgNormal;
			case "Trineo": return imgTrineo != null ? imgTrineo : imgNormal;
			case "SueloQuebradizo": return imgSueloQuebradizo != null ? imgSueloQuebradizo : imgNormal;
			case "Oso": return imgOso != null ? imgOso : imgNormal;
			case "Evento": return imgEvento != null ? imgEvento : imgNormal;
			case "Normal":
			default: return imgNormal;
		}
	}

	private void mostrarTiposDeCasillasEnTablero(Tablero t) {
		tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

		for (int i = 0; i < 50 && i < t.getListaCasillas().size(); i++) {
			Casilla casilla = t.getListaCasillas().get(i);
			
			StackPane celdaContainer = new StackPane();
			celdaContainer.setUserData(TAG_CASILLA_TEXT);
			
			Image img = getImagenParaCasilla(casilla);
			if (img != null) {
				ImageView imgView = new ImageView(img);
				imgView.setPreserveRatio(true);
				// Permitir que el contenedor se encoja rompiendo la dependencia del tamaño de la imagen original
				celdaContainer.setMinSize(0, 0);
				
				imgView.fitWidthProperty().bind(celdaContainer.widthProperty().multiply(1.00));
				imgView.fitHeightProperty().bind(celdaContainer.heightProperty().multiply(1.00));
				celdaContainer.getChildren().add(imgView);
			}

			if (i == 0) {
				Text texto = new Text("INICIO");
				texto.getStyleClass().add("cell-title");
				celdaContainer.getChildren().add(texto);
			} else if (i == 49) {
				Text texto = new Text("FIN");
				texto.getStyleClass().add("cell-title");
				celdaContainer.getChildren().add(texto);
			}

			int[] coords = getCoordenadas(i);
			GridPane.setColumnIndex(celdaContainer, coords[0]);
			GridPane.setRowIndex(celdaContainer, coords[1]);
			GridPane.setHalignment(celdaContainer, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(celdaContainer, javafx.geometry.VPos.CENTER);
			tablero.getChildren().add(celdaContainer);
		}

		// Asegurar que los jugadores estén siempre por encima de las casillas
		P1.toFront();
		P2.toFront();
		P3.toFront();
		P4.toFront();
	}

	// =========================================
	//  MENÚ GUARDADO / CARGA (Desde Pause)
	// =========================================

	/**
	 * Guarda la partida directamente en la BD.
	 * Si ya fue guardada antes (tiene ID), actualiza.
	 * Si es nueva, inserta.
	 * No cambia de pantalla. Muestra alerta de confirmacion.
	 */
	@FXML
	private void handleSaveGame() {
		if (gestorPartida.getPartida() == null) {
			agregarEvento("⚠️ No hay partida activa para guardar.");
			return;
		}

		int idGuardado = gestorPartida.guardarPartida();

		if (idGuardado > 0) {
			agregarEvento("═══════════════════════");
			agregarEvento("💾 Partida guardada correctamente! (ID: " + idGuardado + ")");
			agregarEvento("═══════════════════════");

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			try {
				alert.getDialogPane().getStylesheets().add(
					getClass().getResource("/RESOURCES/PantallaJuego.css").toExternalForm());
				alert.getDialogPane().getStyleClass().add("custom-alert");
			} catch (Exception ignored) {}
			alert.setTitle("Partida Guardada");
			alert.setHeaderText(null);
			alert.setContentText("Partida guardada correctamente.\nID: " + idGuardado);
			alert.showAndWait();
		} else {
			agregarEvento("❌ Error al guardar la partida.");

			Alert alert = new Alert(Alert.AlertType.ERROR);
			try {
				alert.getDialogPane().getStylesheets().add(
					getClass().getResource("/RESOURCES/PantallaJuego.css").toExternalForm());
				alert.getDialogPane().getStyleClass().add("custom-alert");
			} catch (Exception ignored) {}
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("No se pudo guardar la partida.\nRevisa la conexion a la base de datos.");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleLoadGame() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaCargarPartida.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			Stage stage = (Stage) tablero.getScene().getWindow();
			stage.setScene(scene);
			stage.setTitle("Cargar Partida");
			stage.setMaximized(false);
			stage.setMaximized(true);
		} catch (Exception e) {
			System.out.println("Error al abrir pantalla de cargar partida: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// =========================================
	//  HANDLER CONFIGURACIÓN (Overlay)
	// =========================================

	@FXML
	private void handleSettingsClick(ActionEvent event) {
		isPaused = true;
		
		for (Animation a : currentAnimations) {
			if (a.getStatus() == Animation.Status.RUNNING) {
				a.pause();
			}
		}
		
		menuOverlay.setVisible(true);
		mainContainer.setDisable(true);
		if (!mainContainer.getStyleClass().contains("main-container-disabled")) {
			mainContainer.getStyleClass().add("main-container-disabled");
		}
	}

	@FXML
	private void handleResume(ActionEvent event) {
		reanudarJuego();
	}

	@FXML
	private void handleMenuSave(ActionEvent event) {
		// Guardar directo y reanudar
		handleSaveGame();
		reanudarJuego();
	}

	/**
	 * Despausa el juego y cierra el overlay de menu.
	 */
	private void reanudarJuego() {
		isPaused = false;
		menuOverlay.setVisible(false);
		mainContainer.setDisable(false);
		mainContainer.getStyleClass().remove("main-container-disabled");
		for (Animation a : currentAnimations) {
			if (a.getStatus() == Animation.Status.PAUSED) {
				a.play();
			}
		}
	}

	@FXML
	private void handleMenuBackToMenu(ActionEvent event) {
		pendingAction = ConfirmAction.BACK_TO_MENU;
		confirmationTitle.setText("VOLVER AL MENÚ");
		confirmationMessage.setText("¿Quieres guardar la partida antes de volver al menú principal?");
		
		menuOverlay.setVisible(false);
		confirmationOverlay.setVisible(true);
	}

	@FXML
	private void handleMenuExit(ActionEvent event) {
		pendingAction = ConfirmAction.EXIT_GAME;
		confirmationTitle.setText("SALIR DEL JUEGO");
		confirmationMessage.setText("¿Quieres guardar la partida antes de cerrar el juego?");
		
		menuOverlay.setVisible(false);
		confirmationOverlay.setVisible(true);
	}

	@FXML
	private void handleConfirmSave(ActionEvent event) {
		// Guardar la partida
		handleSaveGame();
		confirmationOverlay.setVisible(false);
		
		// Ejecutar la accion pendiente
		if (pendingAction == ConfirmAction.BACK_TO_MENU) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(scene);
				stage.setMaximized(false);
				stage.setMaximized(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (pendingAction == ConfirmAction.EXIT_GAME) {
			Platform.exit();
		}
	}

	@FXML
	private void handleConfirmDiscard(ActionEvent event) {
		confirmationOverlay.setVisible(false);
		if (pendingAction == ConfirmAction.BACK_TO_MENU) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
				Parent root = loader.load();
				Scene scene = new Scene(root);
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				stage.setScene(scene);
				stage.setMaximized(false);
				stage.setMaximized(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (pendingAction == ConfirmAction.EXIT_GAME) {
			Platform.exit();
		}
	}

	@FXML
	private void handleConfirmCancel(ActionEvent event) {
		confirmationOverlay.setVisible(false);
		menuOverlay.setVisible(true);
	}


	@FXML
	private void handleMenuOptions(ActionEvent event) {
		menuOverlay.setVisible(false);
		optionsSubmenu.setVisible(true);
	}

	@FXML
	private void handleBackFromOptions(ActionEvent event) {
		optionsSubmenu.setVisible(false);
		menuOverlay.setVisible(true);
	}

	@FXML
	private void handleChkPantallaCompleta(ActionEvent event) {
		Stage stage = (Stage) optionsSubmenu.getScene().getWindow();
		stage.setMaximized(chkPantallaCompleta.isSelected());
	}


	// =========================================
	//  HANDLER DEL DADO — ANIMACIÓN EN 2 PASOS
	// =========================================
	
	@FXML
	private void handleDado(ActionEvent event) {
		if (isPaused) return;
		ejecutarAnimacionDado(null, dado);
	}

	private void continuarTurnoTrasAnimacion(Dado dadoEspecial) {
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		int indiceActual = gestorPartida.getPartida().getJugadorActualIndice();

		gestorPartida.ejecutarTurnoCompleto(dadoEspecial);
		
		int posNueva = jugadorActual.getPosicion();
		int resultadoDado = gestorPartida.getUltimoResultadoDado();
		int casillaPisada = gestorPartida.getUltimaCasillaPisada();
		
		dadoResultText.setText(jugadorActual.getNombre() + " ha sacado: " + resultadoDado);
		registrarEventosCasilla(jugadorActual, casillaPisada, resultadoDado);
		
		// Animación del movimiento del jugador (saltos parabólicos)
		int posVisualCasilla = Math.max(0, Math.min(casillaPisada, 49));
		int posVisualFinal = Math.max(0, Math.min(posNueva, 49));
		
		if (casillaPisada >= 0 && posVisualCasilla != posVisualFinal) {
			// HAY EFECTO de casilla (retroceso o avance): animación en 2 pasos
			animarMovimiento(indiceActual, posVisualCasilla, () -> {
				PauseTransition pausaEfecto = new PauseTransition(Duration.millis(400));
				pausaEfecto.setOnFinished(pausaEvt -> {
					currentAnimations.remove(pausaEfecto);
					animarMovimiento(indiceActual, posVisualFinal, () -> {
						actualizarTodasLasFichas();
						comprobarPvPYFinalizar(jugadorActual, indiceActual);
					});
				});
				currentAnimations.add(pausaEfecto);
				pausaEfecto.play();
			});
		} else {
			// Movimiento normal directo a la casilla final
			animarMovimiento(indiceActual, posVisualFinal, () -> {
				actualizarTodasLasFichas();
				comprobarPvPYFinalizar(jugadorActual, indiceActual);
			});
		}
	}

	// =========================================
	//  GUERRA DE BOLAS DE NIEVE (PvP)
	// =========================================

	/**
	 * Después de que la animación de movimiento termina, comprueba si
	 * el jugador actual (Pingüino) ha caído en la misma casilla que otro Pingüino.
	 * Si es así, lanza la secuencia visual de guerra de bolas de nieve.
	 * Si no, finaliza el turno normalmente.
	 */
	private void comprobarPvPYFinalizar(Jugador jugadorActual, int indiceActual) {
		if (jugadorActual instanceof Pinguino) {
			Pinguino atacante = (Pinguino) jugadorActual;
			
			// En la primera casilla (inicio) no hay guerra de bolas de nieve
			if (atacante.getPosicion() == 0) {
				finalizarTurnoVisual();
				return;
			}
			
			Pinguino defensor = gestorPartida.detectarPvP(atacante);

			if (defensor != null) {
				// Guardar estado PvP para los handlers de los botones
				pvpAtacante = atacante;
				pvpDefensor = defensor;
				pvpIndiceAtacante = indiceActual;

				agregarEvento("═══════════════════════");
				agregarEvento("⚔️ ¡" + atacante.getNombre() + " y " + defensor.getNombre() + " se encuentran en la casilla " + atacante.getPosicion() + "!");

				// Lanzar la secuencia visual
				mostrarEventoGuerraBolasNieve();
				return;
			}
		}
		// No hay PvP → finalizar turno normalmente
		finalizarTurnoVisual();
	}

	/**
	 * Muestra la imagen guerra_bolas_nieve.png en el centro de la pantalla
	 * con una animación de fade-in + zoom. Después de ~1.5s, muestra el diálogo de decisión.
	 */
	private void mostrarEventoGuerraBolasNieve() {
		// Preparar la imagen para la animación
		snowballFightImage.setOpacity(0);
		snowballFightImage.setScaleX(0.7);
		snowballFightImage.setScaleY(0.7);
		snowballFightImageOverlay.setVisible(true);

		// Fade in
		FadeTransition fadeIn = new FadeTransition(Duration.millis(600), snowballFightImage);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		// Zoom in suave
		ScaleTransition zoomIn = new ScaleTransition(Duration.millis(600), snowballFightImage);
		zoomIn.setFromX(0.7);
		zoomIn.setFromY(0.7);
		zoomIn.setToX(1.0);
		zoomIn.setToY(1.0);

		ParallelTransition entrada = new ParallelTransition(fadeIn, zoomIn);

		// Mantener visible 1.5 segundos, luego mostrar decisión
		PauseTransition espera = new PauseTransition(Duration.millis(1500));

		// Fade out de la imagen
		FadeTransition fadeOut = new FadeTransition(Duration.millis(400), snowballFightImage);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);

		SequentialTransition secuencia = new SequentialTransition(entrada, espera, fadeOut);
		secuencia.setOnFinished(e -> {
			currentAnimations.remove(secuencia);
			snowballFightImageOverlay.setVisible(false);
			mostrarDecisionGuerraBolasNieve();
		});

		currentAnimations.add(secuencia);
		secuencia.play();
	}

	/**
	 * Muestra el diálogo de decisión para el defensor.
	 * Si el defensor es una Foca (CPU), auto-ignora.
	 */
	private void mostrarDecisionGuerraBolasNieve() {
		// Si el defensor es CPU (Foca), no debería ocurrir, pero por seguridad auto-ignoramos
		if (pvpDefensor == null || !(pvpDefensor instanceof Pinguino)) {
			finalizarTurnoVisual();
			return;
		}

		int bolasAtacante = pvpAtacante.contarItem("Bola de Nieve");
		int bolasDefensor = pvpDefensor.contarItem("Bola de Nieve");

		snowballDecisionMessage.setText(
			pvpDefensor.getNombre() + ", ¿quieres iniciar una guerra de bolas de nieve contra " + pvpAtacante.getNombre() + "?\n\n" +
			"Tus bolas de nieve: " + bolasDefensor + "\n" +
			"Bolas de " + pvpAtacante.getNombre() + ": " + bolasAtacante
		);

		snowballDecisionOverlay.setVisible(true);
	}

	@FXML
	private void handleSnowballFight(ActionEvent event) {
		snowballDecisionOverlay.setVisible(false);

		if (pvpAtacante == null || pvpDefensor == null) {
			finalizarTurnoVisual();
			return;
		}

		// Ejecutar la pelea y obtener resultado
		int[] resultado = gestorPartida.ejecutarPelea(pvpAtacante, pvpDefensor);
		int ganador = resultado[0];
		int diferencia = resultado[1];
		int bolasAtacante = resultado[2];
		int bolasDefensor = resultado[3];

		agregarEvento("🎯 " + pvpAtacante.getNombre() + " (" + bolasAtacante + " bolas) vs " + pvpDefensor.getNombre() + " (" + bolasDefensor + " bolas)");

		if (ganador == 0) {
			// Atacante gana → defensor retrocede
			agregarEvento("🏆 ¡" + pvpAtacante.getNombre() + " gana la guerra de bolas de nieve!");
			agregarEvento("   ↪ " + pvpDefensor.getNombre() + " retrocede " + diferencia + " casillas");

			// Animar el retroceso del defensor
			int indiceDefensor = getIndiceJugador(pvpDefensor);
			int posVisualDefensor = Math.max(0, Math.min(pvpDefensor.getPosicion(), 49));
			animarMovimiento(indiceDefensor, posVisualDefensor, () -> {
				actualizarTodasLasFichas();
				actualizarInventarioUI();
				finalizarTurnoVisual();
			});

		} else if (ganador == 1) {
			// Defensor gana → atacante retrocede
			agregarEvento("🏆 ¡" + pvpDefensor.getNombre() + " gana la guerra de bolas de nieve!");
			agregarEvento("   ↪ " + pvpAtacante.getNombre() + " retrocede " + diferencia + " casillas");

			// Animar el retroceso del atacante
			int posVisualAtacante = Math.max(0, Math.min(pvpAtacante.getPosicion(), 49));
			animarMovimiento(pvpIndiceAtacante, posVisualAtacante, () -> {
				actualizarTodasLasFichas();
				actualizarInventarioUI();
				finalizarTurnoVisual();
			});

		} else {
			// Empate → nadie se mueve, pero ambos pierden todas sus bolas
			agregarEvento("🤝 ¡Empate! Ambos pierden todas sus bolas de nieve. Nadie se mueve.");
			actualizarInventarioUI();
			finalizarTurnoVisual();
		}

		agregarEvento("═══════════════════════");

		// Limpiar estado PvP
		pvpAtacante = null;
		pvpDefensor = null;
	}

	@FXML
	private void handleSnowballIgnore(ActionEvent event) {
		snowballDecisionOverlay.setVisible(false);

		agregarEvento("🕊️ " + pvpDefensor.getNombre() + " decide ignorar a " + pvpAtacante.getNombre() + ". No pasa nada.");
		agregarEvento("═══════════════════════");

		// Limpiar estado PvP
		pvpAtacante = null;
		pvpDefensor = null;

		finalizarTurnoVisual();
	}

	/**
	 * Obtiene el índice del jugador en la lista de jugadores de la partida.
	 */
	private int getIndiceJugador(Jugador j) {
		ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
		for (int i = 0; i < jugadores.size(); i++) {
			if (jugadores.get(i) == j) return i;
		}
		return 0;
	}

	// =========================================
	//  HANDLER USAR MOTO DE NIEVE
	// =========================================
	
	@FXML
	private void handleUsarMoto() {
		if (isPaused) return;
		
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		int indiceActual = gestorPartida.getPartida().getJugadorActualIndice();
		
		if (!(jugadorActual instanceof Pinguino)) return;
		Pinguino p = (Pinguino) jugadorActual;
		
		// Verificar que tiene Moto de Nieve
		if (!p.tieneItem("Moto de Nieve")) {
			agregarEvento("⚠️ " + p.getNombre() + " no tiene Moto de Nieve.");
			return;
		}
		
		// Buscar el siguiente trineo desde la posición actual
		int siguienteTrineo = gestorPartida.getPartida().getTablero().buscarSiguienteTrineo(p.getPosicion());
		
		if (siguienteTrineo == -1) {
			agregarEvento("⚠️ No hay trineos más adelante. La Moto de Nieve no se puede usar ahora.");
			return;
		}
		
		// Gastar la moto
		p.gastarItem("Moto de Nieve", 1);
		
		// Guardar posición anterior para la animación
		int posAnterior = p.getPosicion();
		
		// Mover al siguiente trineo
		p.setPosicion(siguienteTrineo);
		
		agregarEvento("───────────────────────");
		agregarEvento("🏍️ " + p.getNombre() + " usa la Moto de Nieve!");
		agregarEvento("   ↪ Avanza de casilla " + posAnterior + " a casilla " + siguienteTrineo + " (Trineo)");
		
		// Animar el movimiento
		dado.setDisable(true);
		btnMoto.setDisable(true);
		btnPeces.setDisable(true);
		btnNieve.setDisable(true);
		
		int posVisualFinal = Math.max(0, Math.min(siguienteTrineo, 49));
		animarMovimiento(indiceActual, posVisualFinal, () -> {
			// Ahora ejecutamos la acción del trineo (que avanza al siguiente trineo)
			Casilla casillaTrineo = gestorPartida.getPartida().getTablero().getListaCasillas().get(siguienteTrineo);
			casillaTrineo.realizarAccion(gestorPartida.getPartida(), p);
			
			// Si el trineo lo movió más adelante, animar también eso
			int posTrasTrineoEfecto = p.getPosicion();
			if (posTrasTrineoEfecto != siguienteTrineo) {
				int posVisualTrineo = Math.max(0, Math.min(posTrasTrineoEfecto, 49));
				agregarEvento("🛷 ¡El trineo lo lleva hasta la casilla " + posTrasTrineoEfecto + "!");
				
				PauseTransition pausa = new PauseTransition(Duration.millis(300));
				pausa.setOnFinished(e2 -> {
					animarMovimiento(indiceActual, posVisualTrineo, () -> {
						actualizarInventarioUI();
						dado.setDisable(false);
					});
				});
				pausa.play();
			} else {
				actualizarInventarioUI();
				dado.setDisable(false);
			}
		});
	}

	// =========================================
	//  HANDLER USAR PECES Y NIEVE
	// =========================================

	@FXML
	private void handleUsarPeces() {
		if (isPaused) return;
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		if (!(jugadorActual instanceof Pinguino)) return;
		Pinguino p = (Pinguino) jugadorActual;
		
		if (!p.tieneItem("Pez")) {
			agregarEvento("⚠️ " + p.getNombre() + " no tiene Peces.");
			return;
		}
		
		p.gastarItem("Pez", 1);
		agregarEvento("───────────────────────");
		agregarEvento("🐟 " + p.getNombre() + " usa un Pez!");
		actualizarInventarioUI();
	}

	@FXML
	private void handleUsarNieve() {
		if (isPaused) return;
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		if (!(jugadorActual instanceof Pinguino)) return;
		Pinguino p = (Pinguino) jugadorActual;
		
		if (!p.tieneItem("Bola de Nieve")) {
			agregarEvento("⚠️ " + p.getNombre() + " no tiene Bolas de Nieve.");
			return;
		}
		
		p.gastarItem("Bola de Nieve", 1);
		agregarEvento("───────────────────────");
		agregarEvento("❄️ " + p.getNombre() + " usa una Bola de Nieve!");
		actualizarInventarioUI();
	}

	// =========================================
	//  ANIMACIÓN
	// =========================================
	
	private void animarMovimiento(int playerIndex, int targetPosition, Runnable alTerminar) {
		Circle fichaObj = getFicha(playerIndex);
		int oldPosition = posiciones[playerIndex];
		
		if (oldPosition == targetPosition) {
			if (alTerminar != null) alTerminar.run();
			return;
		}

		posiciones[playerIndex] = targetPosition;
		double cellWidth = tablero.getWidth() / COLUMNS;
		double cellHeight = tablero.getHeight() / ROWS;

		SequentialTransition sequence = new SequentialTransition();
		int step = (targetPosition > oldPosition) ? 1 : -1;
		int currentPos = oldPosition;

		double currentTx = 0;
		double currentTy = 0;

		while (currentPos != targetPosition) {
			int nextPos = currentPos + step;
			int[] coordsCurr = getCoordenadas(currentPos);
			int[] coordsNext = getCoordenadas(nextPos);

			double cellDx = (coordsNext[0] - coordsCurr[0]) * cellWidth;
			double cellDy = (coordsNext[1] - coordsCurr[1]) * cellHeight;

			double nextTx = currentTx + cellDx;
			double nextTy = currentTy + cellDy;

			Path path = new Path();
			path.getElements().add(new MoveTo(currentTx + fichaObj.getRadius(), currentTy + fichaObj.getRadius()));
			
			double controlX = currentTx + (cellDx / 2) + fichaObj.getRadius();
			double controlY = Math.min(currentTy, nextTy) - 35 + fichaObj.getRadius(); // Altura del salto parabólico

			path.getElements().add(new QuadCurveTo(controlX, controlY, nextTx + fichaObj.getRadius(), nextTy + fichaObj.getRadius()));

			PathTransition jump = new PathTransition();
			jump.setNode(fichaObj);
			jump.setDuration(Duration.millis(250));
			jump.setPath(path);

			sequence.getChildren().add(jump);

			currentTx = nextTx;
			currentTy = nextTy;
			currentPos = nextPos;
		}

		sequence.setOnFinished(e -> {
			currentAnimations.remove(sequence);
			fichaObj.setTranslateX(0);
			fichaObj.setTranslateY(0);
			
			int[] finalCoords = getCoordenadas(targetPosition);
			GridPane.setRowIndex(fichaObj, finalCoords[1]);
			GridPane.setColumnIndex(fichaObj, finalCoords[0]);
			GridPane.setHalignment(fichaObj, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(fichaObj, javafx.geometry.VPos.CENTER);
			if (alTerminar != null) alTerminar.run();
		});

		currentAnimations.add(sequence);
		sequence.play();
	}
	
	private void finalizarTurnoVisual() {
		if (gestorPartida.getPartida().isFinalizada()) {
			agregarEvento("═══════════════════════");
			agregarEvento("🏆 ¡" + gestorPartida.getPartida().getGanador().getNombre() + " HA GANADO LA PARTIDA!");
			agregarEvento("═══════════════════════");
			dado.setDisable(true);
			btnMoto.setDisable(true);
			btnPeces.setDisable(true);
			btnNieve.setDisable(true);
		} else {
			// Actualizar inventario para el SIGUIENTE jugador
			actualizarInventarioUI();
			Jugador sigJ = gestorPartida.getPartida().getJugadorActual();
			agregarEvento("▶ Turno de " + sigJ.getNombre());
			
			if (sigJ instanceof Foca) {
				dado.setDisable(true);
				btnMoto.setDisable(true);
				btnPeces.setDisable(true);
				btnNieve.setDisable(true);
				cpuPauseTransition = new PauseTransition(Duration.millis(1500));
				cpuPauseTransition.setOnFinished(e -> {
					currentAnimations.remove(cpuPauseTransition);
					if (!isPaused) handleDado(null);
				});
				currentAnimations.add(cpuPauseTransition);
				cpuPauseTransition.play();
			} else {
				dado.setDisable(false);
			}
		}
	}
	
	private Circle getFicha(int index) {
		return (index == 0) ? P1 : (index == 1) ? P2 : (index == 2) ? P3 : P4;
	}
	
	private void actualizarTodasLasFichas() {
		ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			Jugador j = jugadores.get(i);
			int posVisual = Math.max(0, Math.min(j.getPosicion(), 49));
			if (posiciones[i] != posVisual) {
				Circle fichaObj = getFicha(i);
				fichaObj.setTranslateX(0);
				fichaObj.setTranslateY(0);
				int[] coords = getCoordenadas(posVisual);
				GridPane.setColumnIndex(fichaObj, coords[0]);
				GridPane.setRowIndex(fichaObj, coords[1]);
				GridPane.setHalignment(fichaObj, javafx.geometry.HPos.CENTER);
				GridPane.setValignment(fichaObj, javafx.geometry.VPos.CENTER);
				posiciones[i] = posVisual;
			}
		}
	}

	// =========================================
	//  EVENTOS LOG
	// =========================================
	
	private void registrarEventosCasilla(Jugador jugador, int posicion, int dado) {
		Tablero t = gestorPartida.getPartida().getTablero();
		
		agregarEvento("───────────────────────");
		agregarEvento("🎲 " + jugador.getNombre() + " tira el dado: " + dado);
		
		if (posicion >= 0 && posicion < t.getListaCasillas().size()) {
			Casilla casilla = t.getListaCasillas().get(posicion);
			String tipoCasilla = casilla.getClass().getSimpleName();
			
			switch (tipoCasilla) {
				case "Normal":
					agregarEvento("📍 Cae en casilla " + posicion + " (Normal). No pasa nada.");
					break;
				case "Agujero":
					agregarEvento("🕳️ ¡Cae en un AGUJERO en la casilla " + posicion + "!");
					agregarEvento("   ↪ Retrocede a la casilla " + jugador.getPosicion());
					break;
				case "Trineo":
					agregarEvento("🛷 ¡Encuentra un TRINEO en la casilla " + posicion + "!");
					if (jugador.getPosicion() != posicion) {
						agregarEvento("   ↪ Avanza hasta la casilla " + jugador.getPosicion());
					} else {
						agregarEvento("   ↪ No hay más trineos adelante. Se queda aquí.");
					}
					break;
				case "SueloQuebradizo":
					agregarEvento("🧊 ¡Pisa SUELO QUEBRADIZO en la casilla " + posicion + "!");
					if (jugador.getPosicion() == 0) {
						agregarEvento("   ↪ ¡El hielo se rompe! Vuelve a Start.");
					} else if (jugador.getTurnosPerdidos() > 0) {
						agregarEvento("   ↪ Se queda atascado. Pierde 1 turno.");
					} else {
						agregarEvento("   ↪ No lleva peso, pasa sin problema.");
					}
					break;
				case "Oso":
					agregarEvento("🐻 ¡Un OSO atrapa a " + jugador.getNombre() + " en la casilla " + posicion + "!");
					agregarEvento("   ↪ Vuelve a Start (casilla 0)");
					break;
				case "Evento":
					agregarEvento("🎁 ¡EVENTO en la casilla " + posicion + "!");
					// Mostrar qué ganó
					if (jugador instanceof Pinguino) {
						Pinguino p = (Pinguino) jugador;
						agregarEvento("   ↪ Inventario: " + p.contarItem("Pez") + " peces, " 
								+ p.contarItem("Bola de Nieve") + " bolas, " 
								+ p.contarItem("Moto de Nieve") + " motos");
					}
					break;
				default:
					agregarEvento("📍 " + jugador.getNombre() + " está en la casilla " + posicion + ".");
			}
		}
		
		agregarEvento("📌 Posición final: casilla " + jugador.getPosicion());
	}

	public void setGestorPartida(GestorPartida gestorPartida) {
		this.gestorPartida = gestorPartida;
	}

	// =========================================
	//  CARGAR PARTIDA GUARDADA
	// =========================================

	/**
	 * Restaura el estado visual del juego a partir de una Partida
	 * previamente cargada de la base de datos.
	 */
	public void cargarPartidaGuardada(Partida partida) {
		gestorPartida.setPartida(partida);
		mostrarTiposDeCasillasEnTablero(partida.getTablero());

		// Ocultar todas las fichas primero
		P1.setVisible(false);
		P2.setVisible(false);
		P3.setVisible(false);
		P4.setVisible(false);

		// Mostrar y posicionar fichas activas
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			Circle ficha = getFicha(i);
			ficha.setVisible(true);

			int posVisual = Math.max(0, Math.min(jugadores.get(i).getPosicion(), 49));
			posiciones[i] = posVisual;

			ficha.setTranslateX(0);
			ficha.setTranslateY(0);
			int[] coords = getCoordenadas(posVisual);
			GridPane.setColumnIndex(ficha, coords[0]);
			GridPane.setRowIndex(ficha, coords[1]);
			GridPane.setHalignment(ficha, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(ficha, javafx.geometry.VPos.CENTER);
		}

		actualizarInventarioUI();

		Jugador jActual = partida.getJugadorActual();
		String nombreP = partida.getNombrePartida() != null ? partida.getNombrePartida() : "Sin nombre";
		agregarEvento("💾 Partida \"" + nombreP + "\" cargada! Turno de " + jActual.getNombre());
		agregarEvento("📌 Turno numero: " + partida.getTurnos());

		// Mostrar posiciones de todos los jugadores
		for (Jugador j : jugadores) {
			agregarEvento("   " + j.getNombre() + " → casilla " + j.getPosicion());
		}

		// Si le toca a una Foca, ejecutar su turno automáticamente
		if (jActual instanceof Foca) {
			dado.setDisable(true);
			btnMoto.setDisable(true);
			btnPeces.setDisable(true);
			btnNieve.setDisable(true);
			PauseTransition pausa = new PauseTransition(Duration.millis(1500));
			pausa.setOnFinished(e -> handleDado(null));
			pausa.play();
		}
	}
}
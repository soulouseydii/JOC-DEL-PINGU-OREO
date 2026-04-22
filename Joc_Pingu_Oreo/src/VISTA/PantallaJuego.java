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
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

import CONTROLADOR.GestorPartida;
import MODELO.*;
import CONTROLADOR.GestorAnimacionesVistas;

public class PantallaJuego {


	// Botón dado
	@FXML private Button dado;
	
	// Textos UI
	@FXML private Text dadoResultText;
	
	// Inventario UI
	@FXML private Text peces_t;
	@FXML private Text nieve_t;
	@FXML private Text moto_t;
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

	// Save Success Overlay
	@FXML private VBox saveSuccessOverlay;
	@FXML private Label saveMessageLabel;

	// Snowball Fight overlays
	@FXML private StackPane snowballFightImageOverlay;
	@FXML private ImageView snowballFightImage;
	@FXML private VBox snowballDecisionOverlay;
	@FXML private Label snowballDecisionMessage;

	// Seal Encounter overlays
	@FXML private StackPane sealEncounterImageOverlay;
	@FXML private ImageView sealEncounterImage;
	@FXML private VBox sealEncounterDecisionOverlay;
	@FXML private Button btnSobornarFoca;
	@FXML private Label sealEncounterMessage;
	@FXML private Button btnSealInfo;

	// Crush (seal passing over) overlay
	@FXML private StackPane crushImageOverlay;
	@FXML private ImageView crushImage;

	// Chest event overlay (cofre del evento)
	@FXML private StackPane chestEventOverlay;
	@FXML private ImageView chestEventImage;
	@FXML private Label chestItemLabel;

	// Surprise event overlay (interrogantes)
	@FXML private StackPane surpriseEventOverlay;
	@FXML private Label surpriseMark1, surpriseMark2, surpriseMark3, surpriseMark4, surpriseMark5;
	@FXML private Label surpriseResultLabel;

	// Tablero y fichas
	// Tablero y fichas (StackPane para poder contener imagen o forma según el tipo)
	@FXML private GridPane tablero;
	@FXML private StackPane P1;
	@FXML private StackPane P2;
	@FXML private StackPane P3;
	@FXML private StackPane P4;

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

	// Seal encounter state
	private Foca sealEncounterFoca;
	private Pinguino sealEncounterPinguino;
	private int sealEncounterPinguinoIndice;

	// Imágenes de las casillas
	private Image imgNormal;
	private Image imgAgujero;
	private Image imgTrineo;
	private Image imgSueloQuebradizo;
	private Image imgOso;
	private Image imgEvento;
	private Image imgSorpresa;

	// Imagen del pingüino (fallback por defecto)
	private Image imgPinguinoDefault;
	private Image imgFocaDefault;
	private static final double FICHA_SIZE = 50.0;
	private static final double FICHA_HALF = FICHA_SIZE / 2.0;

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
			imgSorpresa = new Image(getClass().getResourceAsStream("/imagenes_casillas/casilla_sorpresa.png"));
		} catch (Exception e) {
			System.out.println("Error al cargar imágenes de las casillas: " + e.getMessage());
		}

		// Cargar imágenes de personajes (fallback por defecto)
		try {
			java.io.InputStream isPinguino = getClass().getResourceAsStream("/imagenes/pinguino/pinguino.png");
			if (isPinguino != null) {
				imgPinguinoDefault = new Image(isPinguino);
				System.out.println("✓ Imagen del pingüino default cargada correctamente.");
			} else {
				System.out.println("⚠ ERROR: No se encontró /imagenes/pinguino/pinguino.png");
			}
			java.io.InputStream isFoca = getClass().getResourceAsStream("/imagenes/foca/foca_default.png");
			if (isFoca != null) {
				imgFocaDefault = new Image(isFoca);
				System.out.println("✓ Imagen de la foca default cargada correctamente.");
			} else {
				System.out.println("⚠ ERROR: No se encontró /imagenes/foca/foca_default.png");
			}
		} catch (Exception e) {
			System.out.println("Error al cargar imágenes de personajes: " + e.getMessage());
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

		// Tooltip para el botón de info de la foca
		Tooltip sealTooltip = new Tooltip("Si la foca te golpea, serás dirigido al último agujero más cercano.");
		sealTooltip.setShowDelay(Duration.millis(100));
		sealTooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
		btnSealInfo.setTooltip(sealTooltip);
		
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
			StackPane ficha = getFicha(i);
			ficha.setVisible(true);
			asignarImagenFicha(i, jugadores.get(i));
		}
		
		actualizarTodasLasFichas();
		
		actualizarInventarioUI();
		
		Jugador j1 = gestorPartida.getPartida().getJugadorActual();
		agregarEvento("🎮 ¡El juego ha comenzado! Turno de " + j1.getNombre());
		
		if (j1 instanceof Foca) {
			dado.setDisable(true);
			btnMoto.setDisable(true);
			cpuPauseTransition = new PauseTransition(Duration.millis(1500));
			cpuPauseTransition.setOnFinished(e -> {
				currentAnimations.remove(cpuPauseTransition);
				if (!isPaused) handleDado(null);
			});
			currentAnimations.add(cpuPauseTransition);
			cpuPauseTransition.play();
		}
	}

	public void setNombrePartida(String nombrePartida) {
		if (gestorPartida != null && gestorPartida.getPartida() != null) {
			gestorPartida.getPartida().setNombrePartida(nombrePartida);
		}
	}

	// =========================================
	//  ASIGNACIÓN DE IMAGEN A FICHA
	// =========================================

	/**
	 * Carga la imagen de skin de un jugador según su getColor() (nombre de archivo).
	 * Devuelve la imagen cargada o el fallback por defecto.
	 */
	private Image cargarSkinJugador(Jugador jugador) {
		String skinFile = jugador.getColor();
		if (skinFile != null && skinFile.endsWith(".png")) {
			try {
				String carpeta = (jugador instanceof Pinguino) ? "/imagenes/pinguino/" : "/imagenes/foca/";
				java.io.InputStream is = getClass().getResourceAsStream(carpeta + skinFile);
				if (is != null) {
					Image img = new Image(is);
					System.out.println("✓ Skin '" + skinFile + "' cargada para " + jugador.getNombre());
					return img;
				}
			} catch (Exception e) {
				System.out.println("⚠ Error cargando skin '" + skinFile + "': " + e.getMessage());
			}
		}
		// Fallback
		return (jugador instanceof Pinguino) ? imgPinguinoDefault : imgFocaDefault;
	}

	/**
	 * Asigna la imagen de la skin elegida a la ficha del jugador.
	 */
	private void asignarImagenFicha(int index, Jugador jugador) {
		StackPane ficha = getFicha(index);
		ficha.getChildren().clear();
		
		Image skinImg = cargarSkinJugador(jugador);
		ImageView imgView = new ImageView();
		if (skinImg != null) {
			imgView.setImage(skinImg);
			System.out.println("✓ Ficha con skin asignada a " + jugador.getNombre());
		} else {
			System.out.println("⚠ Fallback: sin imagen para " + jugador.getNombre());
		}
		imgView.setFitWidth(FICHA_SIZE);
		imgView.setFitHeight(FICHA_SIZE);
		imgView.setPreserveRatio(true);
		ficha.getChildren().add(imgView);
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
		
		if (jActual instanceof Pinguino) {
			Pinguino p = (Pinguino) jActual;
			
			int cantPeces = p.contarItem("Pez");
			int cantBolas = p.contarItem("Bola de Nieve");
			int cantMotos = p.contarItem("Moto de Nieve");
			
			peces_t.setText("x " + cantPeces);
			nieve_t.setText("x " + cantBolas);
			moto_t.setText("x " + cantMotos);
			
			btnMoto.setDisable(cantMotos <= 0);
			
			// Dados especiales
			int cantRapidos = jActual.getInventario().getDadosRapidos();
			int cantLentos = jActual.getInventario().getDadosLentos();
			
			lblCantRapido.setText("Rápido: " + cantRapidos);
			lblCantLento.setText("Lento: " + cantLentos);
			
			btnDadoRapido.setDisable(cantRapidos <= 0);
			btnDadoLento.setDisable(cantLentos <= 0);
		} else {
			peces_t.setText("x -");
			nieve_t.setText("x -");
			moto_t.setText("x -");
			btnMoto.setDisable(true);
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
			case "Sorpresa": return imgSorpresa != null ? imgSorpresa : imgNormal;
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

		// Asegurar que los jugadores (ImageView) estén siempre por encima de las casillas
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

			// Actualizar mensaje y mostrar overlay personalizado
			saveMessageLabel.setText("Partida guardada correctamente\nID de la partida: " + idGuardado);
			saveSuccessOverlay.setVisible(true);
			GestorAnimacionesVistas.animarAparicionOverlay(saveSuccessOverlay);
			mainContainer.setDisable(true);
			if (!mainContainer.getStyleClass().contains("main-container-disabled")) {
				mainContainer.getStyleClass().add("main-container-disabled");
			}
		} else {
			agregarEvento("❌ Error al guardar la partida.");
			saveMessageLabel.setText("Error: No se pudo guardar la partida.\nComprueba la base de datos.");
			saveSuccessOverlay.setVisible(true);
			GestorAnimacionesVistas.animarAparicionOverlay(saveSuccessOverlay);
			mainContainer.setDisable(true);
		}
		}
	

	@FXML
	private void handleCloseSaveOverlay() {
		saveSuccessOverlay.setVisible(false);
		if (!isPaused) {
			mainContainer.setDisable(false);
			mainContainer.getStyleClass().remove("main-container-disabled");
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
		GestorAnimacionesVistas.animarAparicionOverlay(menuOverlay);
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
		GestorAnimacionesVistas.animarCierreOverlay(menuOverlay, () -> {
			mainContainer.setDisable(false);
			mainContainer.getStyleClass().remove("main-container-disabled");
			for (Animation a : currentAnimations) {
				if (a.getStatus() == Animation.Status.PAUSED) {
					a.play();
				}
			}
		});
	}

	@FXML
	private void handleMenuBackToMenu(ActionEvent event) {
		pendingAction = ConfirmAction.BACK_TO_MENU;
		confirmationTitle.setText("VOLVER AL MENÚ");
		confirmationMessage.setText("¿Quieres guardar la partida antes de volver al menú principal?");
		
		GestorAnimacionesVistas.animarCierreOverlay(menuOverlay, () -> {
			confirmationOverlay.setVisible(true);
			GestorAnimacionesVistas.animarAparicionOverlay(confirmationOverlay);
		});
	}

	@FXML
	private void handleMenuExit(ActionEvent event) {
		pendingAction = ConfirmAction.EXIT_GAME;
		confirmationTitle.setText("SALIR DEL JUEGO");
		confirmationMessage.setText("¿Quieres guardar la partida antes de cerrar el juego?");
		
		GestorAnimacionesVistas.animarCierreOverlay(menuOverlay, () -> {
			confirmationOverlay.setVisible(true);
			GestorAnimacionesVistas.animarAparicionOverlay(confirmationOverlay);
		});
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
		GestorAnimacionesVistas.animarCierreOverlay(confirmationOverlay, () -> {
			menuOverlay.setVisible(true);
			GestorAnimacionesVistas.animarEntradaCascada(menuOverlay);
		});
	}


	@FXML
	private void handleMenuOptions(ActionEvent event) {
		GestorAnimacionesVistas.animarCierreOverlay(menuOverlay, () -> {
			optionsSubmenu.setVisible(true);
			GestorAnimacionesVistas.animarAparicionOverlay(optionsSubmenu);
		});
	}

	@FXML
	private void handleBackFromOptions(ActionEvent event) {
		GestorAnimacionesVistas.animarCierreOverlay(optionsSubmenu, () -> {
			menuOverlay.setVisible(true);
			GestorAnimacionesVistas.animarEntradaCascada(menuOverlay);
		});
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

		// Capturar inventario ANTES del turno para detectar qué se ganó
		int pecesAntes = 0, bolasAntes = 0, motosAntes = 0, rapidosAntes = 0, lentosAntes = 0;
		int turnosBloqueadaAntes = 0;
		if (jugadorActual instanceof Pinguino) {
			Pinguino p = (Pinguino) jugadorActual;
			pecesAntes    = p.contarItem("Pez");
			bolasAntes    = p.contarItem("Bola de Nieve");
			motosAntes    = p.contarItem("Moto de Nieve");
			rapidosAntes  = p.getInv().getDadosRapidos();
			lentosAntes   = p.getInv().getDadosLentos();
		} else if (jugadorActual instanceof Foca) {
			turnosBloqueadaAntes = ((Foca) jugadorActual).getTurnosBloqueada();
		}

		gestorPartida.ejecutarTurnoCompleto(dadoEspecial);
		
		int posNueva = jugadorActual.getPosicion();
		int resultadoDado = gestorPartida.getUltimoResultadoDado();
		int casillaPisada = gestorPartida.getUltimaCasillaPisada();

		// Calcular diferencias del inventario
		int[] diffInv = null;
		if (jugadorActual instanceof Pinguino) {
			Pinguino p = (Pinguino) jugadorActual;
			diffInv = new int[]{
				p.contarItem("Pez")          - pecesAntes,
				p.contarItem("Bola de Nieve")- bolasAntes,
				p.contarItem("Moto de Nieve")- motosAntes,
				p.getInv().getDadosRapidos() - rapidosAntes,
				p.getInv().getDadosLentos()  - lentosAntes
			};
		}
		
		dadoResultText.setText(jugadorActual.getNombre() + " ha sacado: " + resultadoDado);
		registrarEventosCasilla(jugadorActual, casillaPisada, resultadoDado, diffInv);

		// Detectar si la casilla pisada es un Evento/Sorpresa (para la animación del cofre o interrogantes)
		boolean esEvento = false;
		boolean esSorpresa = false;
		String itemObtenido = null;
		if (casillaPisada >= 0 && casillaPisada < gestorPartida.getPartida().getTablero().getListaCasillas().size()) {
			Casilla casillaObj = gestorPartida.getPartida().getTablero().getListaCasillas().get(casillaPisada);
			
			if (casillaObj instanceof Evento || casillaObj instanceof Sorpresa) {
				// DETERMINAR SI ACTIVAR ANIMACION Y QUÉ TEXTO MOSTRAR
				
				if (casillaObj instanceof Evento && jugadorActual instanceof Pinguino) {
					esEvento = true;
					// Determinar qué objeto se obtuvo buscando en diffInv
					if (diffInv != null && (diffInv[0]>0 || diffInv[1]>0 || diffInv[2]>0 || diffInv[3]>0 || diffInv[4]>0)) {
						if (diffInv[3] > 0) itemObtenido = "🎲⚡ ¡Dado Rápido!";
						else if (diffInv[4] > 0) itemObtenido = "🎲🐢 ¡Dado Lento!";
						else if (diffInv[0] > 0) itemObtenido = "🐟 ¡" + diffInv[0] + " Pez!";
						else if (diffInv[1] > 0) itemObtenido = "❄️ ¡" + diffInv[1] + " Bola de Nieve!";
						else if (diffInv[2] > 0) itemObtenido = "🏍️ ¡Moto de Nieve!";
					} else {
						itemObtenido = "📦 Inventario lleno";
					}
				} else if (casillaObj instanceof Sorpresa) {
					// Lógica para SORPRESA (Pinguino o Foca)
					esSorpresa = true; // Siempre mostramos interrogantes al caer aquí
					
					if (jugadorActual instanceof Pinguino) {
						if (diffInv != null && (diffInv[0]>0 || diffInv[1]>0 || diffInv[2]>0 || diffInv[3]>0 || diffInv[4]>0)) {
							if (diffInv[3] > 0) itemObtenido = "🎲⚡ ¡Dado Rápido!";
							else if (diffInv[4] > 0) itemObtenido = "🎲🐢 ¡Dado Lento!";
							else if (diffInv[0] > 0) itemObtenido = "🐟 ¡" + diffInv[0] + " Pez!";
							else if (diffInv[1] > 0) itemObtenido = "❄️ ¡" + diffInv[1] + " Bola(s) de Nieve!";
							else if (diffInv[2] > 0) itemObtenido = "🏍️ ¡Moto de Nieve!";
						} else if (jugadorActual.getPosicion() > casillaPisada) {
							itemObtenido = "🛷 ¡Un Trineo!";
						} else if (jugadorActual.getPosicion() < casillaPisada) {
							if (jugadorActual.getPosicion() == 0) itemObtenido = "🐻 ¡El OSO!";
							else itemObtenido = "🕳️ ¡Un Agujero!";
						} else if (jugadorActual.getTurnosPerdidos() > 0) {
							itemObtenido = "🧊 ¡Atascado!";
						} else {
							itemObtenido = "❓ ¡Nada!";
						}
					} else if (jugadorActual instanceof Foca) {
						Foca f = (Foca) jugadorActual;
						if (f.getPosicion() > casillaPisada) {
							itemObtenido = "↗️ ¡Impulso Accidental!";
						} else if (f.getPosicion() < casillaPisada) {
							if (f.getPosicion() == 0) itemObtenido = "🐻 ¡Susto del Oso!";
							else itemObtenido = "🕳️ ¡Agujero!";
						} else if (f.getTurnosBloqueada() > turnosBloqueadaAntes) {
							if (f.getTurnosBloqueada() - turnosBloqueadaAntes == 1) itemObtenido = "🐟 ¡Muchos Peces!";
							else itemObtenido = "❄️ ¡Trampa de Nieve!";
						} else {
							itemObtenido = "❓ ¡Nada!";
						}
					}
				}
			}
		}

		// Guardar variables para las lambdas
		final boolean esEventoFinal = esEvento;
		final boolean esSorpresaFinal = esSorpresa;
		final String itemObtenidoFinal = itemObtenido;
		
		// Animación del movimiento del jugador (saltos parabólicos)
		int posVisualCasilla = Math.max(0, Math.min(casillaPisada, 49));
		int posVisualFinal = Math.max(0, Math.min(posNueva, 49));
		
		if (casillaPisada >= 0 && posVisualCasilla != posVisualFinal) {
			// HAY EFECTO de casilla (retroceso o avance): animación en 2 pasos
			animarMovimiento(indiceActual, posVisualCasilla, () -> {
				// Si es evento, mostrar cofre antes de animar el efecto de casilla
				Runnable continuarConEfecto = () -> {
					PauseTransition pausaEfecto = new PauseTransition(Duration.millis(400));
					pausaEfecto.setOnFinished(pausaEvt -> {
						currentAnimations.remove(pausaEfecto);
						animarMovimiento(indiceActual, posVisualFinal, () -> {
							actualizarTodasLasFichas();
							comprobarAplastamientoYContinuar(jugadorActual, indiceActual);
						});
					});
					currentAnimations.add(pausaEfecto);
					pausaEfecto.play();
				};
				if (esEventoFinal) {
					mostrarEventoCofre(itemObtenidoFinal, continuarConEfecto);
				} else if (esSorpresaFinal) {
					mostrarEventoSorpresa(itemObtenidoFinal, continuarConEfecto);
				} else {
					continuarConEfecto.run();
				}
			});
		} else {
			// Movimiento normal directo a la casilla final
			animarMovimiento(indiceActual, posVisualFinal, () -> {
				actualizarTodasLasFichas();
				// Si es evento, mostrar cofre antes de continuar
				if (esEventoFinal) {
					mostrarEventoCofre(itemObtenidoFinal, () -> {
						comprobarAplastamientoYContinuar(jugadorActual, indiceActual);
					});
				} else if (esSorpresaFinal) {
					mostrarEventoSorpresa(itemObtenidoFinal, () -> {
						comprobarAplastamientoYContinuar(jugadorActual, indiceActual);
					});
				} else {
					comprobarAplastamientoYContinuar(jugadorActual, indiceActual);
				}
			});
		}
	}

	// =========================================
	//  APLASTAMIENTO (Foca pasa por encima)
	// =========================================

	/**
	 * Comprueba si la foca ha pasado por encima de algún pingüino
	 * durante su movimiento (casillas intermedias, NO la final).
	 * Si hay aplastados, muestra la animación y aplica el efecto.
	 * Si no, continúa con comprobarFocaYFinalizar.
	 */
	private void comprobarAplastamientoYContinuar(Jugador jugadorActual, int indiceActual) {
		ArrayList<Pinguino> aplastados = gestorPartida.getUltimosPinguinosAplastados();

		if (aplastados != null && !aplastados.isEmpty()) {
			// Aplicar el efecto a todos los aplastados
			for (Pinguino p : aplastados) {
				gestorPartida.aplicarAplastamiento(p);
			}

			// Mostrar la animación visual, después continuar
			mostrarEventoAplastamiento(aplastados, jugadorActual, indiceActual);
		} else {
			// No hay aplastados → continuar con el check de foca en casilla final
			comprobarFocaYFinalizar(jugadorActual, indiceActual);
		}
	}

	/**
	 * Muestra la imagen pinguino_aplastado.png con una animación de impacto
	 * (squash + shake). Después de ~1.5s, registra los eventos en el log
	 * y continúa con el flujo normal.
	 */
	private void mostrarEventoAplastamiento(ArrayList<Pinguino> aplastados, Jugador jugadorActual, int indiceActual) {
		// Preparar la imagen para la animación
		crushImage.setOpacity(0);
		crushImage.setScaleX(1.3);
		crushImage.setScaleY(0.5);
		crushImageOverlay.setVisible(true);

		// Fade in rápido
		FadeTransition fadeIn = new FadeTransition(Duration.millis(200), crushImage);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		// Efecto squash: se aplasta de arriba abajo (impacto)
		ScaleTransition squash = new ScaleTransition(Duration.millis(200), crushImage);
		squash.setFromX(1.3);
		squash.setFromY(0.5);
		squash.setToX(1.0);
		squash.setToY(1.0);

		ParallelTransition impacto = new ParallelTransition(fadeIn, squash);

		// Pequeño rebote tras el impacto
		ScaleTransition rebote1 = new ScaleTransition(Duration.millis(100), crushImage);
		rebote1.setToX(1.05);
		rebote1.setToY(0.95);
		ScaleTransition rebote2 = new ScaleTransition(Duration.millis(100), crushImage);
		rebote2.setToX(1.0);
		rebote2.setToY(1.0);

		// Leve temblor (shake)
		TranslateTransition shake1 = new TranslateTransition(Duration.millis(40), crushImage);
		shake1.setByX(8);
		TranslateTransition shake2 = new TranslateTransition(Duration.millis(40), crushImage);
		shake2.setByX(-16);
		TranslateTransition shake3 = new TranslateTransition(Duration.millis(40), crushImage);
		shake3.setByX(12);
		TranslateTransition shake4 = new TranslateTransition(Duration.millis(40), crushImage);
		shake4.setByX(-4);
		SequentialTransition shakeSeq = new SequentialTransition(shake1, shake2, shake3, shake4);

		// Mantener visible 1.2 segundos
		PauseTransition espera = new PauseTransition(Duration.millis(1200));

		// Fade out
		FadeTransition fadeOut = new FadeTransition(Duration.millis(400), crushImage);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);

		SequentialTransition secuencia = new SequentialTransition(
			impacto, new ParallelTransition(rebote1, shakeSeq), rebote2, espera, fadeOut
		);

		secuencia.setOnFinished(e -> {
			currentAnimations.remove(secuencia);
			crushImageOverlay.setVisible(false);
			crushImage.setTranslateX(0);
			crushImage.setTranslateY(0);

			// Registrar en el log
			agregarEvento("═══════════════════════");
			for (Pinguino p : aplastados) {
				agregarEvento("💥 Una foca ha aplastado a " + p.getNombre() + " y ha perdido todos sus objetos.");
			}
			agregarEvento("═══════════════════════");

			actualizarInventarioUI();
			actualizarTodasLasFichas();

			// Continuar con el flujo normal: check foca en casilla final
			comprobarFocaYFinalizar(jugadorActual, indiceActual);
		});

		currentAnimations.add(secuencia);
		secuencia.play();
	}

	//  ANIMACIÓN COFRE DE EVENTO

	/**
	 * @param itemNombre
	 * @param alTerminar
	 */
	private void mostrarEventoCofre(String itemNombre, Runnable alTerminar) {
		// Preparar el overlay
		chestEventImage.setOpacity(0);
		chestEventImage.setScaleX(0.4);
		chestEventImage.setScaleY(0.4);
		chestEventImage.setRotate(0);
		chestItemLabel.setOpacity(0);
		chestItemLabel.setTranslateY(0);
		chestItemLabel.setText(itemNombre != null ? itemNombre : "");
		chestEventOverlay.setVisible(true);

		// === PASO 1: Cofre aparece con zoom-in + fade ===
		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), chestEventImage);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		ScaleTransition zoomIn = new ScaleTransition(Duration.millis(500), chestEventImage);
		zoomIn.setFromX(0.4);
		zoomIn.setFromY(0.4);
		zoomIn.setToX(1.0);
		zoomIn.setToY(1.0);

		ParallelTransition entrada = new ParallelTransition(fadeIn, zoomIn);

		// === PASO 2: Cofre tiembla (shake) ===
		SequentialTransition chestShake = new SequentialTransition();
		for (int i = 0; i < 6; i++) {
			RotateTransition r = new RotateTransition(Duration.millis(60), chestEventImage);
			r.setToAngle(i % 2 == 0 ? 5 : -5);
			chestShake.getChildren().add(r);
		}
		RotateTransition resetRotate = new RotateTransition(Duration.millis(60), chestEventImage);
		resetRotate.setToAngle(0);
		chestShake.getChildren().add(resetRotate);

		// === PASO 3: Cofre se abre (escala vertical + brillo dorado) ===
		ScaleTransition openY = new ScaleTransition(Duration.millis(350), chestEventImage);
		openY.setToX(1.1);
		openY.setToY(1.15);

		ScaleTransition settle = new ScaleTransition(Duration.millis(200), chestEventImage);
		settle.setToX(1.05);
		settle.setToY(1.05);

		// === PASO 4: Item aparece y flota hacia arriba ===
		FadeTransition itemFadeIn = new FadeTransition(Duration.millis(400), chestItemLabel);
		itemFadeIn.setFromValue(0);
		itemFadeIn.setToValue(1);

		TranslateTransition itemFloat = new TranslateTransition(Duration.millis(800), chestItemLabel);
		itemFloat.setFromY(0);
		itemFloat.setToY(-80);

		ParallelTransition itemAppear = new ParallelTransition(itemFadeIn, itemFloat);

		// === PASO 5: Mantener visible ===
		PauseTransition hold = new PauseTransition(Duration.millis(1200));

		// === PASO 6: Fade out de todo ===
		FadeTransition fadeOutChest = new FadeTransition(Duration.millis(400), chestEventImage);
		fadeOutChest.setToValue(0);

		FadeTransition fadeOutItem = new FadeTransition(Duration.millis(400), chestItemLabel);
		fadeOutItem.setToValue(0);

		ParallelTransition fadeOutAll = new ParallelTransition(fadeOutChest, fadeOutItem);

		// Secuencia completa
		SequentialTransition secuenciaEvento = new SequentialTransition(
			entrada,                                    // Cofre aparece
			new PauseTransition(Duration.millis(200)),  // Breve pausa
			chestShake,                                 // Cofre tiembla
			openY,                                      // Cofre se abre
			settle,                                     // Cofre se estabiliza
			itemAppear,                                 // Item flota
			hold,                                       // Mantener
			fadeOutAll                                   // Desaparecer
		);

		secuenciaEvento.setOnFinished(e -> {
			currentAnimations.remove(secuenciaEvento);
			chestEventOverlay.setVisible(false);
			chestEventImage.setScaleX(1);
			chestEventImage.setScaleY(1);
			chestEventImage.setRotate(0);
			chestItemLabel.setTranslateY(0);
			if (alTerminar != null) alTerminar.run();
		});

		currentAnimations.add(secuenciaEvento);
		secuenciaEvento.play();
	}

	/**
	 * Muestra una animación especial para la casilla Sorpresa:
	 * Varios interrogantes (?) flotando y un texto central con el resultado.
	 */
	private void mostrarEventoSorpresa(String resultadoTexto, Runnable alTerminar) {
		// Resetear estado
		surpriseEventOverlay.setVisible(true);
		surpriseResultLabel.setText(resultadoTexto);
		surpriseResultLabel.setOpacity(0);
		surpriseResultLabel.setScaleX(0.5);
		surpriseResultLabel.setScaleY(0.5);

		Label[] marks = {surpriseMark1, surpriseMark2, surpriseMark3, surpriseMark4, surpriseMark5};
		ParallelTransition marksAnim = new ParallelTransition();

		for (int i = 0; i < marks.length; i++) {
			Label mark = marks[i];
			mark.setOpacity(0);
			mark.setTranslateX((Math.random() - 0.5) * 600);
			mark.setTranslateY((Math.random() - 0.5) * 400);
			mark.setRotate((Math.random() - 0.5) * 60);

			FadeTransition fi = new FadeTransition(Duration.millis(400), mark);
			fi.setToValue(0.4);

			TranslateTransition tt = new TranslateTransition(Duration.millis(1500), mark);
			tt.setByY((Math.random() - 0.5) * 100);
			tt.setByX((Math.random() - 0.5) * 100);
			tt.setCycleCount(1);

			RotateTransition rt = new RotateTransition(Duration.millis(1500), mark);
			rt.setByAngle((Math.random() - 0.5) * 40);

			marksAnim.getChildren().addAll(fi, tt, rt);
		}

		// Animación del texto central
		FadeTransition textFade = new FadeTransition(Duration.millis(500), surpriseResultLabel);
		textFade.setDelay(Duration.millis(300));
		textFade.setToValue(1);

		ScaleTransition textZoom = new ScaleTransition(Duration.millis(500), surpriseResultLabel);
		textZoom.setDelay(Duration.millis(300));
		textZoom.setToX(1.0);
		textZoom.setToY(1.0);
		
		ParallelTransition textAnim = new ParallelTransition(textFade, textZoom);

		// Pausa para leer
		PauseTransition hold = new PauseTransition(Duration.millis(1800));

		// Desvanecer todo
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), surpriseEventOverlay);
		fadeOut.setToValue(0);

		SequentialTransition total = new SequentialTransition(
			new ParallelTransition(marksAnim, textAnim),
			hold,
			fadeOut
		);

		total.setOnFinished(e -> {
			currentAnimations.remove(total);
			surpriseEventOverlay.setVisible(false);
			surpriseEventOverlay.setOpacity(1); // Reset para la próxima vez
			if (alTerminar != null) alTerminar.run();
		});

		currentAnimations.add(total);
		total.play();
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
	private void comprobarFocaYFinalizar(Jugador jugadorActual, int indiceActual) {
		// ── CASO 1: El jugador actual es un Pingüino → comprobar si hay foca ──
		if (jugadorActual instanceof Pinguino) {
			Pinguino pActual = (Pinguino) jugadorActual;
			Foca foca = gestorPartida.detectarFoca(pActual);

			if (foca != null && pActual.getPosicion() != 0) {
				// Guardar estado del encuentro
				sealEncounterFoca = foca;
				sealEncounterPinguino = pActual;
				sealEncounterPinguinoIndice = indiceActual;

				agregarEvento("═══════════════════════");
				agregarEvento("🦭 ¡" + pActual.getNombre() + " se encuentra con la foca en la casilla " + pActual.getPosicion() + "!");

				// Lanzar la secuencia visual del encuentro con foca
				mostrarEventoEncuentroFoca();
				return;
			}
		}

		// ── CASO 2: El jugador actual es una Foca → comprobar si hay pingüino ──
		if (jugadorActual instanceof Foca) {
			Foca fActual = (Foca) jugadorActual;
			Pinguino pEncontrado = gestorPartida.detectarPinguinoEnCasillaFoca(fActual);

			if (pEncontrado != null && fActual.getPosicion() != 0) {
				// Guardar estado del encuentro
				sealEncounterFoca = fActual;
				sealEncounterPinguino = pEncontrado;
				sealEncounterPinguinoIndice = getIndiceJugador(pEncontrado);

				agregarEvento("═══════════════════════");
				agregarEvento("🦭 ¡La foca cae en la casilla de " + pEncontrado.getNombre() + " (casilla " + fActual.getPosicion() + ")!");

				// Lanzar la secuencia visual del encuentro con foca
				mostrarEventoEncuentroFoca();
				return;
			}
		}

		// No hay encuentro con foca → comprobar PvP
		comprobarPvPYFinalizar(jugadorActual, indiceActual);
	}

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
	//  ENCUENTRO CON LA FOCA
	// =========================================

	/**
	 * Muestra la imagen encuentro_foca.png en el centro de la pantalla
	 * con una animación de fade-in + zoom. Después de ~1.5s, muestra el diálogo de decisión.
	 */
	private void mostrarEventoEncuentroFoca() {
		// Preparar la imagen para la animación
		sealEncounterImage.setOpacity(0);
		sealEncounterImage.setScaleX(0.7);
		sealEncounterImage.setScaleY(0.7);
		sealEncounterImageOverlay.setVisible(true);

		// Fade in
		FadeTransition fadeIn = new FadeTransition(Duration.millis(600), sealEncounterImage);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		// Zoom in suave
		ScaleTransition zoomIn = new ScaleTransition(Duration.millis(600), sealEncounterImage);
		zoomIn.setFromX(0.7);
		zoomIn.setFromY(0.7);
		zoomIn.setToX(1.0);
		zoomIn.setToY(1.0);

		ParallelTransition entrada = new ParallelTransition(fadeIn, zoomIn);

		// Mantener visible 1.5 segundos, luego mostrar decisión
		PauseTransition espera = new PauseTransition(Duration.millis(1500));

		// Fade out de la imagen
		FadeTransition fadeOut = new FadeTransition(Duration.millis(400), sealEncounterImage);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);

		SequentialTransition secuencia = new SequentialTransition(entrada, espera, fadeOut);
		secuencia.setOnFinished(e -> {
			currentAnimations.remove(secuencia);
			sealEncounterImageOverlay.setVisible(false);
			mostrarDecisionEncuentroFoca();
		});

		currentAnimations.add(secuencia);
		secuencia.play();
	}

	/**
	 * Muestra el panel de decisión del encuentro con la foca.
	 * Habilita/deshabilita el botón de sobornar según si el pingüino tiene peces.
	 */
	private void mostrarDecisionEncuentroFoca() {
		if (sealEncounterPinguino == null || sealEncounterFoca == null) {
			finalizarTurnoVisual();
			return;
		}

		boolean tienePez = sealEncounterPinguino.tieneItem("Pez");
		int cantPeces = sealEncounterPinguino.contarItem("Pez");

		if (tienePez) {
			btnSobornarFoca.setText("Sobornar a la foca");
			btnSobornarFoca.setDisable(false);
		} else {
			btnSobornarFoca.setText("¡No te quedan peces!");
			btnSobornarFoca.setDisable(true);
		}

		sealEncounterMessage.setText(
			sealEncounterPinguino.getNombre() + " se ha encontrado con la foca.\n\n" +
			"Peces disponibles: " + cantPeces + "\n" +
			"¿Qué quieres hacer?"
		);

		sealEncounterDecisionOverlay.setVisible(true);
	}

	@FXML
	private void handleSobornarFoca(ActionEvent event) {
		sealEncounterDecisionOverlay.setVisible(false);

		if (sealEncounterFoca == null || sealEncounterPinguino == null) {
			finalizarTurnoVisual();
			return;
		}

		// Resolver el soborno en el controlador
		gestorPartida.resolverSobornoFoca(sealEncounterFoca, sealEncounterPinguino);

		agregarEvento("🐟 " + sealEncounterPinguino.getNombre() + " soborna a la foca con un pez.");
		agregarEvento("   ↪ La foca queda bloqueada 2 turnos. ¡" + sealEncounterPinguino.getNombre() + " se queda en su casilla!");
		agregarEvento("═══════════════════════");

		actualizarInventarioUI();

		// Limpiar estado
		sealEncounterFoca = null;
		sealEncounterPinguino = null;

		// Evento finalizado → devolver control al flujo del juego
		finalizarTurnoVisual();
	}

	@FXML
	private void handleNoHacerNadaFoca(ActionEvent event) {
		sealEncounterDecisionOverlay.setVisible(false);

		if (sealEncounterFoca == null || sealEncounterPinguino == null) {
			finalizarTurnoVisual();
			return;
		}

		// Resolver el golpe en el controlador
		int posAnterior = sealEncounterPinguino.getPosicion();
		int nuevaPos = gestorPartida.resolverGolpeFoca(sealEncounterFoca, sealEncounterPinguino);

		agregarEvento("💥 ¡La foca golpea a " + sealEncounterPinguino.getNombre() + "!");
		agregarEvento("   ↪ Enviado al agujero más cercano: casilla " + nuevaPos);
		agregarEvento("═══════════════════════");

		// Pequeña animación de impacto (temblor) antes de mover
		StackPane fichaPinguino = getFicha(sealEncounterPinguinoIndice);
		RotateTransition impacto = new RotateTransition(Duration.millis(60), fichaPinguino);
		impacto.setFromAngle(-20);
		impacto.setToAngle(20);
		impacto.setCycleCount(8);
		impacto.setAutoReverse(true);

		int indicePinguino = sealEncounterPinguinoIndice;

		impacto.setOnFinished(e -> {
			currentAnimations.remove(impacto);
			fichaPinguino.setRotate(0);

			// Pausa breve antes de mover
			PauseTransition pausa = new PauseTransition(Duration.millis(400));
			pausa.setOnFinished(e2 -> {
				currentAnimations.remove(pausa);
				int posVisualFinal = Math.max(0, Math.min(nuevaPos, 49));
				animarMovimiento(indicePinguino, posVisualFinal, () -> {
					actualizarTodasLasFichas();
					actualizarInventarioUI();
					finalizarTurnoVisual();
				});
			});
			currentAnimations.add(pausa);
			pausa.play();
		});

		// Limpiar estado
		sealEncounterFoca = null;
		sealEncounterPinguino = null;

		currentAnimations.add(impacto);
		impacto.play();
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
	//  ANIMACIÓN
	// =========================================
	
	private void animarMovimiento(int playerIndex, int targetPosition, Runnable alTerminar) {
		StackPane fichaObj = getFicha(playerIndex);
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
			path.getElements().add(new MoveTo(currentTx + FICHA_HALF, currentTy + FICHA_HALF));
			
			double controlX = currentTx + (cellDx / 2) + FICHA_HALF;
			double controlY = Math.min(currentTy, nextTy) - 35 + FICHA_HALF; // Altura del salto parabólico

			path.getElements().add(new QuadCurveTo(controlX, controlY, nextTx + FICHA_HALF, nextTy + FICHA_HALF));

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
		} else {
			// Actualizar inventario para el SIGUIENTE jugador
			actualizarInventarioUI();
			Jugador sigJ = gestorPartida.getPartida().getJugadorActual();
			agregarEvento("▶ Turno de " + sigJ.getNombre());
			
			if (sigJ instanceof Foca) {
				dado.setDisable(true);
				btnMoto.setDisable(true);
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
	
	private StackPane getFicha(int index) {
		return (index == 0) ? P1 : (index == 1) ? P2 : (index == 2) ? P3 : P4;
	}
	
	private void actualizarTodasLasFichas() {
		ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			Jugador j = jugadores.get(i);
			int posVisual = Math.max(0, Math.min(j.getPosicion(), 49));
			if (posiciones[i] != posVisual) {
				StackPane fichaObj = getFicha(i);
				int[] coords = getCoordenadas(posVisual);
				GridPane.setColumnIndex(fichaObj, coords[0]);
				GridPane.setRowIndex(fichaObj, coords[1]);
				GridPane.setHalignment(fichaObj, javafx.geometry.HPos.CENTER);
				GridPane.setValignment(fichaObj, javafx.geometry.VPos.CENTER);
				posiciones[i] = posVisual;
			}
		}
		
		// Recalcular posiciones (TranslateX, Y) para evitar que se pisen
		java.util.Map<Integer, java.util.List<Integer>> casillasOcupadas = new java.util.HashMap<>();
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			int pos = posiciones[i];
			if (!casillasOcupadas.containsKey(pos)) {
				casillasOcupadas.put(pos, new java.util.ArrayList<>());
			}
			casillasOcupadas.get(pos).add(i);
		}
		
		for (Integer pos : casillasOcupadas.keySet()) {
			java.util.List<Integer> ocupantes = casillasOcupadas.get(pos);
			int numOcupantes = ocupantes.size();
			
			for (int ocupanteIndex = 0; ocupanteIndex < numOcupantes; ocupanteIndex++) {
				int playerIndex = ocupantes.get(ocupanteIndex);
				StackPane ficha = getFicha(playerIndex);
				
				if (numOcupantes == 1) {
					ficha.setTranslateX(0);
					ficha.setTranslateY(0);
				} else if (numOcupantes == 2) {
					// 2 ocupantes: uno a la izq, otro a la der
					ficha.setTranslateX(ocupanteIndex == 0 ? -12 : 12);
					ficha.setTranslateY(0);
				} else if (numOcupantes == 3) {
					// 3 ocupantes: triangulo
					if (ocupanteIndex == 0) {
						ficha.setTranslateX(0);
						ficha.setTranslateY(-12);
					} else {
						ficha.setTranslateX(ocupanteIndex == 1 ? -12 : 12);
						ficha.setTranslateY(12);
					}
				} else {
					// 4 ocupantes: cuadrado
					ficha.setTranslateX(ocupanteIndex % 2 == 0 ? -12 : 12);
					ficha.setTranslateY(ocupanteIndex < 2 ? -12 : 12);
				}
			}
		}
	}

	// =========================================
	//  EVENTOS LOG
	// =========================================
	
	private void registrarEventosCasilla(Jugador jugador, int posicion, int dado, int[] diffInv) {
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
					agregarEvento("   ↪ ¡El hielo se rompe! Vuelve a Inicio.");
					} else if (jugador.getTurnosPerdidos() > 0) {
						agregarEvento("   ↪ Se queda atascado. Pierde 1 turno.");
					} else {
						agregarEvento("   ↪ No lleva peso, pasa sin problema.");
					}
					break;
				case "Oso":
					agregarEvento("🐻 ¡Un OSO atrapa a " + jugador.getNombre() + " en la casilla " + posicion + "!");
					agregarEvento("   ↪ Vuelve a Inicio (casilla 0)");
					break;
				case "Evento":
					agregarEvento("🎁 ¡EVENTO en la casilla " + posicion + "!");
					if (diffInv != null) {
						// diffInv: [0]=peces, [1]=bolas, [2]=motos, [3]=dadosRapidos, [4]=dadosLentos
						if (diffInv[3] > 0)
							agregarEvento("   ↪ ¡Ha encontrado un DADO RÁPIDO! 🎲⚡");
						else if (diffInv[4] > 0)
							agregarEvento("   ↪ ¡Ha encontrado un DADO LENTO! 🎲🐢");
						else if (diffInv[0] > 0)
							agregarEvento("   ↪ ¡Ha encontrado " + diffInv[0] + " Pez(ces)! 🐟");
						else if (diffInv[1] > 0)
							agregarEvento("   ↪ ¡Ha encontrado " + diffInv[1] + " Bola(s) de Nieve! ❄️");
						else if (diffInv[2] > 0)
							agregarEvento("   ↪ ¡Ha encontrado una Moto de Nieve! 🏍️");
						else
							agregarEvento("   ↪ El inventario está lleno, no cabe nada.");
					}
					break;
				case "Sorpresa":
					agregarEvento("❓ ¡CASILLA SORPRESA en la casilla " + posicion + "!");
					if (jugador instanceof Pinguino) {
						Pinguino p = (Pinguino) jugador;
						if (diffInv != null && (diffInv[0]>0 || diffInv[1]>0 || diffInv[2]>0 || diffInv[3]>0 || diffInv[4]>0)) {
							// Caso Items
							if (diffInv[3] > 0) agregarEvento("   ↪ ¡Sorpresa! ¡DADO RÁPIDO! 🎲⚡");
							else if (diffInv[4] > 0) agregarEvento("   ↪ ¡Sorpresa! ¡DADO LENTO! 🎲🐢");
							else if (diffInv[0] > 0) agregarEvento("   ↪ ¡Sorpresa! ¡Pez! 🐟");
							else if (diffInv[1] > 0) agregarEvento("   ↪ ¡Sorpresa! ¡" + diffInv[1] + " Bola(s) de Nieve! ❄️");
							else if (diffInv[2] > 0) agregarEvento("   ↪ ¡Sorpresa! ¡Moto de Nieve! 🏍️");
						} else if (p.getPosicion() > posicion) {
							agregarEvento("   ↪ ¡Sorpresa! ¡Has encontrado un TRINEO! 🛷");
							agregarEvento("      ↪ Avanza hasta la casilla " + p.getPosicion());
						} else if (p.getPosicion() < posicion && p.getPosicion() >= 0) {
							if (p.getPosicion() == 0)
								agregarEvento("   ↪ ¡Sorpresa! ¡Un OSO! 🐻 Vuelve a Inicio.");
							else
								agregarEvento("   ↪ ¡Sorpresa! ¡Un AGUJERO! 🕳️ Retrocede a la casilla " + p.getPosicion());
						} else if (p.getTurnosPerdidos() > 0) {
							agregarEvento("   ↪ ¡Sorpresa! ¡SUELO QUEBRADIZO! 🧊");
							agregarEvento("      ↪ Te has quedado atascado. Pierdes 1 turno.");
						} else {
							agregarEvento("   ↪ ¡Sorpresa! ...pero no pasa nada. Es una casilla normal. 🧊");
						}
					} else if (jugador instanceof Foca) {
						if (jugador.getPosicion() == 0 && posicion != 0) {
							agregarEvento("   ↪ 🐻 ¡Un Oso asusta a la Foca! Vuelve a Inicio.");
						} else {
							// Para foca, no tenemos diffInv de items, asique miramos diferencias de posicion u otros efectos
							// pero por simplicidad mostramos mensaje generico o el resultado directo
							agregarEvento("   ↪ ¡La foca experimenta un evento inesperado!");
						}
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

		// Mostrar, asignar imagen y posicionar fichas activas
		ArrayList<Jugador> jugadores = partida.getJugadores();
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			StackPane ficha = getFicha(i);
			ficha.setVisible(true);
			asignarImagenFicha(i, jugadores.get(i));

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
			PauseTransition pausa = new PauseTransition(Duration.millis(1500));
			pausa.setOnFinished(e -> handleDado(null));
			pausa.play();
		}
	}
}
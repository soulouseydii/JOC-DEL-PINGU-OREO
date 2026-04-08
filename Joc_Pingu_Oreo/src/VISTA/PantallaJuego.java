package VISTA;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import CONTROLADOR.GestorPartida;
import MODELO.*;

public class PantallaJuego {

	// Menu items
	@FXML private MenuItem newGame;
	@FXML private MenuItem saveGame;
	@FXML private MenuItem loadGame;
	@FXML private MenuItem quitGame;

	// Botón dado
	@FXML private Button dado;
	
	// Textos UI
	@FXML private Text dadoResultText;
	
	// Inventario UI
	@FXML private Label inventarioTitulo;
	@FXML private Text peces_t;
	@FXML private Text nieve_t;
	@FXML private Text moto_t;
	@FXML private Button btnMoto;
	@FXML private Button btnVolverMenu;
	
	// Log de eventos
	@FXML private TextArea eventosLog;

	// Tablero y fichas
	@FXML private GridPane tablero;
	@FXML private Circle P1;
	@FXML private Circle P2;
	@FXML private Circle P3;
	@FXML private Circle P4;

	private GestorPartida gestorPartida;
	private int[] posiciones = new int[4]; // Todos empiezan en 0 (Start)
	private static final int COLUMNS = 5;
	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";

	// =========================================
	//  INICIALIZACIÓN
	// =========================================

	@FXML
	private void initialize() {
		gestorPartida = new GestorPartida();
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
		
		actualizarInventarioUI();
		
		Jugador j1 = gestorPartida.getPartida().getJugadorActual();
		agregarEvento("🎮 ¡El juego ha comenzado! Turno de " + j1.getNombre());
		
		if (j1 instanceof Foca) {
			dado.setDisable(true);
			btnMoto.setDisable(true);
			PauseTransition pausa = new PauseTransition(Duration.millis(1500));
			pausa.setOnFinished(e -> handleDado(null));
			pausa.play();
		}
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
	
	/**
	 * Lee el inventario del jugador ACTUAL y actualiza los contadores en la UI.
	 * Si el jugador no tiene un item, muestra 0.
	 */
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
			
			// Solo habilitar el botón de Moto si tiene al menos 1
			btnMoto.setDisable(cantMotos <= 0);
		} else {
			// Si es una Foca (no tiene inventario visible)
			peces_t.setText("Peces: -");
			nieve_t.setText("Bolas de nieve: -");
			moto_t.setText("Moto de Nieve: -");
			btnMoto.setDisable(true);
		}
	}

	// =========================================
	//  TABLERO
	// =========================================

	private void mostrarTiposDeCasillasEnTablero(Tablero t) {
		tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

		for (int i = 0; i < t.getListaCasillas().size(); i++) {
			Casilla casilla = t.getListaCasillas().get(i);
			if (i > 0 && i < 49) {
				String tipo = casilla.getClass().getSimpleName();
				Text texto = new Text(tipo);
				texto.setUserData(TAG_CASILLA_TEXT);
				texto.getStyleClass().add("cell-type");

				int row = i / COLUMNS;
				int col = i % COLUMNS;
				GridPane.setRowIndex(texto, row);
				GridPane.setColumnIndex(texto, col);
				tablero.getChildren().add(texto);
			}
		}
	}

	// =========================================
	//  MENÚ
	// =========================================

	@FXML private void handleNewGame() { System.out.println("Nuevo Juego."); }
	@FXML private void handleSaveGame() { System.out.println("Guardar Juego."); }
	@FXML private void handleLoadGame() { System.out.println("Cargar Juego."); }
	@FXML private void handleQuitGame() { System.out.println("Salir..."); }

	// =========================================
	//  VOLVER AL MENÚ
	// =========================================

	@FXML
	private void handleVolverMenu(ActionEvent event) {
		try {
			// 1. Cargamos el FXML del Menú
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/RESOURCES/PantallaInicio.fxml"));
			Parent menuRoot = loader.load();
			Scene menuScene = new Scene(menuRoot);

			// 2. Obtenemos el Stage actual a través del botón que disparó el evento
			Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

			// 3. Cambiamos la escena al Menú
			stage.setScene(menuScene);
			stage.setMaximized(false);
			stage.setMaximized(true);

			System.out.println("Volviendo al Menú Principal.");
		} catch (Exception e) {
			System.out.println("Error al volver al Menú: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// =========================================
	//  HANDLER DEL DADO — ANIMACIÓN EN 2 PASOS
	// =========================================
	
	@FXML
	private void handleDado(ActionEvent event) {
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		int indiceActual = gestorPartida.getPartida().getJugadorActualIndice();
		
		dado.setDisable(true);
		btnMoto.setDisable(true);

		gestorPartida.ejecutarTurnoCompleto();
		
		int posNueva = jugadorActual.getPosicion();
		int resultadoDado = gestorPartida.getUltimoResultadoDado();
		int casillaPisada = gestorPartida.getUltimaCasillaPisada();
		
		dadoResultText.setText(jugadorActual.getNombre() + " ha sacado: " + resultadoDado);
		registrarEventosCasilla(jugadorActual, casillaPisada, resultadoDado);
		
		// Animación en 2 pasos
		int posVisualCasilla = Math.max(0, Math.min(casillaPisada, 49));
		int posVisualFinal = Math.max(0, Math.min(posNueva, 49));
		
		if (casillaPisada >= 0 && posVisualCasilla != posVisualFinal) {
			// HAY EFECTO: animación en 2 pasos
			animarMovimiento(indiceActual, posVisualCasilla, () -> {
				PauseTransition pausa = new PauseTransition(Duration.millis(400));
				pausa.setOnFinished(pausaEvt -> {
					animarMovimiento(indiceActual, posVisualFinal, () -> {
						actualizarTodasLasFichas();
						finalizarTurnoVisual();
					});
				});
				pausa.play();
			});
		} else {
			animarMovimiento(indiceActual, posVisualFinal, () -> {
				actualizarTodasLasFichas();
				finalizarTurnoVisual();
			});
		}
	}

	// =========================================
	//  HANDLER USAR MOTO DE NIEVE
	// =========================================
	
	@FXML
	private void handleUsarMoto() {
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
		Circle fichaObj = getFicha(playerIndex);
		int oldPosition = posiciones[playerIndex];
		posiciones[playerIndex] = targetPosition;

		int oldRow = oldPosition / COLUMNS;
		int oldCol = oldPosition % COLUMNS;
		int newRow = targetPosition / COLUMNS;
		int newCol = targetPosition % COLUMNS;

		double cellWidth = tablero.getWidth() / COLUMNS;
		double cellHeight = tablero.getHeight() / 10;

		double dx = (newCol - oldCol) * cellWidth;
		double dy = (newRow - oldRow) * cellHeight;

		TranslateTransition slide = new TranslateTransition(Duration.millis(350), fichaObj);
		slide.setByX(dx);
		slide.setByY(dy);

		slide.setOnFinished(e -> {
			fichaObj.setTranslateX(0);
			fichaObj.setTranslateY(0);
			GridPane.setRowIndex(fichaObj, newRow);
			GridPane.setColumnIndex(fichaObj, newCol);
			if (alTerminar != null) alTerminar.run();
		});

		slide.play();
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
				PauseTransition pausa = new PauseTransition(Duration.millis(1500));
				pausa.setOnFinished(e -> handleDado(null));
				pausa.play();
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
				GridPane.setRowIndex(fichaObj, posVisual / COLUMNS);
				GridPane.setColumnIndex(fichaObj, posVisual % COLUMNS);
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
}
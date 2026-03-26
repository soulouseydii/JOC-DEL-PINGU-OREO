package VISTA;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import CONTROLADOR.GestorPartida;
import MODELO.*;

public class PantallaJuego {

	// Menu items
	@FXML
	private MenuItem newGame;
	@FXML
	private MenuItem saveGame;
	@FXML
	private MenuItem loadGame;
	@FXML
	private MenuItem quitGame;

	// Buttons
	@FXML
	private Button dado;
	@FXML
	private Button rapido;
	@FXML
	private Button lento;
	@FXML
	private Button peces;
	@FXML
	private Button nieve;

	// Texts
	@FXML
	private Text dadoResultText;
	@FXML
	private Text rapido_t;
	@FXML
	private Text lento_t;
	@FXML
	private Text peces_t;
	@FXML
	private Text nieve_t;
	
	// Log de eventos (TextArea acumulativo con scroll)
	@FXML
	private TextArea eventosLog;

	// Game board and player pieces
	@FXML
	private GridPane tablero;
	@FXML
	private Circle P1;
	@FXML
	private Circle P2;
	@FXML
	private Circle P3;
	@FXML
	private Circle P4;

	private GestorPartida gestorPartida;
	private int[] posiciones = new int[4];
	private static final int COLUMNS = 5;

	private static final String TAG_CASILLA_TEXT = "CASILLA_TEXT";

	@FXML
	private void initialize() {
		// Crear el gestor de partida
		gestorPartida = new GestorPartida();

		// Crear la lista de jugadores (4 jugadores por defecto)
		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
		
		for (int i = 1; i <= 4; i++) {
			Inventario inventario = new Inventario();
			inventario.getlista().add(new Dado("Normal"));
			String color = (i == 1) ? "Azul" : (i == 2) ? "Rojo" : (i == 3) ? "Verde" : "Amarillo";
			jugadores.add(new Pinguino("Jugador " + i, color, 0, inventario));
		}

		// Crear nueva partida pasando los jugadores
		gestorPartida.nuevaPartida(jugadores);

		// Show board info
		mostrarTiposDeCasillasEnTablero(gestorPartida.getPartida().getTablero());
        
		agregarEvento("🎮 ¡El juego ha comenzado! Turno de Jugador 1");
	}
	
	// =========================================
	//  MÉTODO PARA AÑADIR EVENTOS AL LOG
	// =========================================
	
	/**
	 * Añade un mensaje al log de eventos acumulativo.
	 * Los mensajes nuevos aparecen al final y el scroll baja automáticamente.
	 */
	private void agregarEvento(String mensaje) {
		if (eventosLog.getText().isEmpty()) {
			eventosLog.setText(mensaje);
		} else {
			eventosLog.appendText("\n" + mensaje);
		}
		// Auto-scroll al final
		eventosLog.setScrollTop(Double.MAX_VALUE);
		eventosLog.positionCaret(eventosLog.getText().length());
	}

	private void mostrarTiposDeCasillasEnTablero(Tablero t) {
		// Clear only the labels we generated in previous calls
		tablero.getChildren().removeIf(node -> TAG_CASILLA_TEXT.equals(node.getUserData()));

		for (int i = 0; i < t.getListaCasillas().size(); i++) {
			Casilla casilla = t.getListaCasillas().get(i);

			// Skip position 0 and 49 (start/end)
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

	// Menu actions
	@FXML
	private void handleNewGame() {
		System.out.println("Nuevo Juego.");
		// TODO
	}

	@FXML
	private void handleSaveGame() {
		System.out.println("Guardar Juego.");
		// TODO
	}

	@FXML
	private void handleLoadGame() {
		System.out.println("Cargar Juego.");
		// TODO
	}

	@FXML
	private void handleQuitGame() {
		System.out.println("Salir...");
		// TODO
	}

	// Button actions
	@FXML
	private void handleDado(ActionEvent event) {
		Jugador jugadorActual = gestorPartida.getPartida().getJugadorActual();
		int indiceActual = gestorPartida.getPartida().getJugadorActualIndice();
		
		dado.setDisable(true);

		// ===================================================
		// DELEGAMOS TODA la lógica al controlador
		// ejecutarTurnoCompleto() hace: tirar dado, mover,
		// ejecutar casilla, comprobar foca, comprobar PvP,
		// y pasar al siguiente turno.
		// ===================================================
		
		gestorPartida.ejecutarTurnoCompleto();
		
		// Después de ejecutar, la posición del jugador ya ha sido actualizada
		// (puede haber cambiado por casillas especiales: Agujero, Oso, Trineo, etc.)
		int posNueva = jugadorActual.getPosicion();
		int resultadoDado = gestorPartida.getUltimoResultadoDado();
		
		// Mostrar el resultado del dado en la UI
		dadoResultText.setText(jugadorActual.getNombre() + " ha sacado: " + resultadoDado);
		
		// Calcular posición visual (el tablero visual es 0-indexed, el modelo es 1-indexed)
		int posicionDestinoVisual = posNueva - 1;
		if (posicionDestinoVisual >= 49) {
			posicionDestinoVisual = 49;
		}
		if (posicionDestinoVisual < 0) {
			posicionDestinoVisual = 0;
		}
		
		// Animar el movimiento de la ficha
		moveTo(indiceActual, posicionDestinoVisual);
		
		// Actualizar también las fichas de otros jugadores que pueden haber sido movidos
		// (por ejemplo, un pingüino enviado a un agujero por la foca, o retrocedido por PvP)
		actualizarTodasLasFichas();
		
		// Añadir los eventos al log acumulativo
		int casillaPisada = gestorPartida.getUltimaCasillaPisada();
		registrarEventosCasilla(jugadorActual, casillaPisada, resultadoDado);
	}
	
	/**
	 * Actualiza la posición visual de TODAS las fichas para reflejar
	 * los cambios que haya hecho el controlador (efectos de casilla, PvP, foca, etc.)
	 */
	private void actualizarTodasLasFichas() {
		ArrayList<Jugador> jugadores = gestorPartida.getPartida().getJugadores();
		
		for (int i = 0; i < jugadores.size() && i < 4; i++) {
			Jugador j = jugadores.get(i);
			int posVisual = j.getPosicion() - 1;
			if (posVisual < 0) posVisual = 0;
			if (posVisual >= 49) posVisual = 49;
			
			// Solo actualizar si la posición visual cambió
			if (posiciones[i] != posVisual) {
				Circle fichaObj = (i == 0) ? P1 : (i == 1) ? P2 : (i == 2) ? P3 : P4;
				
				int newRow = posVisual / COLUMNS;
				int newCol = posVisual % COLUMNS;
				
				fichaObj.setTranslateX(0);
				fichaObj.setTranslateY(0);
				GridPane.setRowIndex(fichaObj, newRow);
				GridPane.setColumnIndex(fichaObj, newCol);
				
				posiciones[i] = posVisual;
			}
		}
	}
	
	/**
	 * Registra en el log de eventos qué pasó en el turno del jugador.
	 * Incluye el dado, la casilla pisada y el efecto aplicado.
	 */
	private void registrarEventosCasilla(Jugador jugador, int posicion, int dado) {
		Tablero t = gestorPartida.getPartida().getTablero();
		
		// Separador visual entre turnos
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
					agregarEvento("   ↪ Avanza hasta la casilla " + jugador.getPosicion());
					break;
				case "SueloQuebradizo":
					agregarEvento("🧊 ¡Pisa SUELO QUEBRADIZO en la casilla " + posicion + "!");
					if (jugador.getPosicion() == 1) {
						agregarEvento("   ↪ ¡El hielo se rompe! Vuelve al inicio.");
					} else if (jugador.getTurnosPerdidos() > 0) {
						agregarEvento("   ↪ Se queda atascado. Pierde 1 turno.");
					} else {
						agregarEvento("   ↪ No lleva peso, pasa sin problema.");
					}
					break;
				case "Oso":
					agregarEvento("🐻 ¡Un OSO atrapa a " + jugador.getNombre() + " en la casilla " + posicion + "!");
					agregarEvento("   ↪ Vuelve a la casilla " + jugador.getPosicion());
					break;
				case "Evento":
					agregarEvento("🎁 ¡EVENTO en la casilla " + posicion + "! Ha ganado un objeto.");
					break;
				default:
					agregarEvento("📍 " + jugador.getNombre() + " está en la casilla " + posicion + ".");
			}
		}
		
		agregarEvento("📌 Posición final: casilla " + jugador.getPosicion());
		
		// Comprobar si alguien ha ganado
		if (gestorPartida.getPartida().isFinalizada()) {
			agregarEvento("═══════════════════════");
			agregarEvento("🏆 ¡" + gestorPartida.getPartida().getGanador().getNombre() + " HA GANADO LA PARTIDA!");
			agregarEvento("═══════════════════════");
			this.dado.setDisable(true);
		}
	}

	private void moveTo(int playerIndex, int targetPosition) {

		Circle fichaObj = (playerIndex == 0) ? P1 : (playerIndex == 1) ? P2 : (playerIndex == 2) ? P3 : P4;
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

			// El turno ya fue pasado por ejecutarTurnoCompleto(),
			// así que solo actualizamos el texto y reactivamos el dado
			if (!gestorPartida.getPartida().isFinalizada()) {
				agregarEvento("▶ Turno de " + gestorPartida.getPartida().getJugadorActual().getNombre());
				dado.setDisable(false);
			}
		});

		slide.play();
	}

	@FXML
	private void handleRapido() {
		System.out.println("Fast.");
		// TODO
	}

	@FXML
	private void handleLento() {
		System.out.println("Slow.");
		// TODO
	}

	@FXML
	private void handlePeces() {
		System.out.println("Fish.");
		// TODO
	}

	@FXML
	private void handleNieve() {
		System.out.println("Snow.");
		// TODO
	}

	public void setGestorPartida(GestorPartida gestorPartida) {
		this.gestorPartida = gestorPartida;
	}
}
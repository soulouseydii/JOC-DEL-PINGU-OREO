package VISTA;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
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
	@FXML
	private Text eventos;

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
        
		eventos.setText("El juego ha comenzado! Turno de Jugador 1");
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
		
		Dado d = null;
		if (jugadorActual instanceof Pinguino) {
			d = (Dado) ((Pinguino) jugadorActual).getInv().getlista().get(0);
		}

		dado.setDisable(true);

		int resultado = gestorPartida.tirarDado(jugadorActual, d);
		
		if (jugadorActual instanceof Pinguino) {
			((Pinguino) jugadorActual).moverPosicion(resultado);
		}

		dadoResultText.setText(jugadorActual.getNombre() + " ha sacado: " + resultado);

		int posicionDestino = jugadorActual.getPosicion() - 1;
		
		if (posicionDestino >= 49) {
			posicionDestino = 49;
		}

		moveTo(indiceActual, posicionDestino);
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

			// Comprobar final
			if (targetPosition >= 49) {
				eventos.setText(gestorPartida.getPartida().getJugadorActual().getNombre() + ", ha ganado!");
				dado.setDisable(true); // Asegurar que quede desactivado
			} else {
				gestorPartida.siguienteTurno();
				eventos.setText("Turno de " + gestorPartida.getPartida().getJugadorActual().getNombre());
				dado.setDisable(false); // Reactivar si no ha ganado
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
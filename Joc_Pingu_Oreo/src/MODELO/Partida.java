package MODELO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Partida {

	// ATRIBUTOS DE PARTIDA
	private Tablero tablero;
	private ArrayList<Jugador> jugadores;
	private int turnos;
	private int jugadorActual;
	private boolean finalizada;
	private Jugador ganador;
	private int id;
	private String nombrePartida;

	// CONSTRUCTOR

	public Partida() {
		this.tablero = new Tablero();
		this.jugadores = new ArrayList<Jugador>();
		this.turnos = 0;
		this.jugadorActual = 0;
		this.finalizada = false;
		this.ganador = null;

		ArrayList<Casilla> casillas = new ArrayList<Casilla>();

		// Posición 0: Start (Normal)
		casillas.add(new Normal(0));

		Random rand = new Random();

		// Lista total de casillas que vamos a generar (48 casillas aleatorias)
		ArrayList<Integer> tiposGenerados = new ArrayList<Integer>();

		// Dividimos el tablero en 3 ZONAS (16 casillas cada zona) para asegurar que
		// la dificultad es equitativa y no se agrupan todos los agujeros al principio.
		for (int zona = 0; zona < 3; zona++) {
			ArrayList<Integer> zonaActual = new ArrayList<Integer>();
			// En cada tercio del tablero habrá exactamente:
			for (int i = 0; i < 2; i++)
				zonaActual.add(1); // 2 Osos
			for (int i = 0; i < 2; i++)
				zonaActual.add(3); // 2 Agujeros
			for (int i = 0; i < 2; i++)
				zonaActual.add(2); // 2 Trineos
			for (int i = 0; i < 2; i++)
				zonaActual.add(5); // 2 Suelos Quebradizos
			for (int i = 0; i < 2; i++)
				zonaActual.add(4); // 2 Eventos
			for (int i = 0; i < 3; i++)
				zonaActual.add(0); // 3 Normales
			for (int i = 0; i < 3; i++)
				zonaActual.add(6); // 3 Sorpresas

			// Mezclar las casillas de esta zona específica
			Collections.shuffle(zonaActual, rand);

			// Añadirlas a la lista total
			tiposGenerados.addAll(zonaActual);
		}

		// Posiciones 1-48: casillas ya pre-balanceadas
		for (int i = 1; i < 49; i++) {
			int tipo = tiposGenerados.get(i - 1);

			Casilla c;
			switch (tipo) {
				case 0:
					c = new Normal(i);
					break;
				case 1:
					c = new Oso(i);
					break;
				case 2:
					c = new Trineo(i);
					break;
				case 3:
					c = new Agujero(i);
					break;
				case 4:
					c = new Evento(i);
					break;
				case 5:
					c = new SueloQuebradizo(i);
					break;
				case 6:
					c = new Sorpresa(i);
					break;
				default:
					c = new Normal(i);
			}

			casillas.add(c);
			System.out.println("Pos " + i + " → " + c.getClass().getSimpleName());
		}

		// Posición 49: Finish (Normal) — última casilla del tablero
		casillas.add(new Normal(49));

		this.tablero.setListaCasillas(casillas);

	}

	// GETTERS I SETTERS

	public Tablero getTablero() {
		return tablero;
	}

	public void setTablero(Tablero tablero) {
		this.tablero = tablero;
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(ArrayList<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public int getTurnos() {
		return turnos;
	}

	public void setTurnos(int turnos) {
		this.turnos = turnos;
	}

	public int getJugadorActualIndice() {
		return jugadorActual;
	}

	public void setJugadorActualIndice(int jugadorActual) {
		this.jugadorActual = jugadorActual;
	}

	public boolean isFinalizada() {
		return finalizada;
	}

	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public Jugador getGanador() {
		return ganador;
	}

	public void setGanador(Jugador ganador) {
		this.ganador = ganador;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombrePartida() {
		return nombrePartida;
	}

	public void setNombrePartida(String nombrePartida) {
		this.nombrePartida = nombrePartida;
	}

	// JUGADOR GETJUGADORACTUAL

	public Jugador getJugadorActual() {
		if (jugadores == null || jugadores.isEmpty()) {
			return null;
		}

		return jugadores.get(jugadorActual);
	}

}
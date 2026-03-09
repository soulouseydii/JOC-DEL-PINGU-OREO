package MODELO;

import CONTROLADOR.GestorTablero;

public class Partida {

	private GestorTablero gestorTablero;
	private Tablero tablero; //Añadimos tablero para que partida tenga el mapa
	
	//Constructor
	public Partida() {
		this.setGestorTablero(new GestorTablero());
		this.tablero = new Tablero();
	}

	public GestorTablero getGestorTablero() {
		return gestorTablero;
	}

	public void setGestorTablero(GestorTablero gestorTablero) {
		this.gestorTablero = gestorTablero;
	}

	public Tablero getTablero() {
		return tablero;
	}

	public void setTablero(Tablero tablero) {
		this.tablero = tablero;
	}
	
}

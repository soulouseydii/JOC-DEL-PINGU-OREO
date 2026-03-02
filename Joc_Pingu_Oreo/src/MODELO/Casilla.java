package MODELO;

public abstract class Casilla {
	
	/* ATRIBUTS */
	
	private int posicion;
	
	/* CONSTRUCTOR */
	public Casilla(int posicion) {
		this.posicion = posicion;
	}
	
	
	/* GETTERS I SETTERS HEREDADOS */

	public int getposicion() {
		return this.posicion;
	}

	public void setposicion(int posicion) {
		this.posicion = posicion;
	}
	
	/*
	abstract void realizarAccion(Partida partida, Jugador jugador) {
		
	}
	*/
	
}

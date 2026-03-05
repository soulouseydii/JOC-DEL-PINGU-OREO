package MODELO;

public abstract class Casilla {
	
	/* ATRIBUTS */
	
	private int posicion;
	
	/* CONSTRUCTOR */
	public Casilla(int posicion) {
		this.posicion = posicion;
	}
	
	
	/* GETTERS I SETTERS HEREDADOS */

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	
    /* METODO ABSTRACTO */
	
	public abstract void realizarAccion(Partida partida, Jugador jugador);
	
}
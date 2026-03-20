package MODELO;

public abstract class Casilla {
	
	/* ATRIBUTS */
	
	private int posicion;
	private String tipo;
	
	/* CONSTRUCTOR */
	public Casilla(int posicion, String tipo) {
		this.posicion = posicion;
		this.setTipo(tipo);
	}
	
	
	/* GETTERS I SETTERS */

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	
    /* METODO ABSTRACTO */
	
	public abstract void realizarAccion(Partida partida, Jugador jugador);


	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
}
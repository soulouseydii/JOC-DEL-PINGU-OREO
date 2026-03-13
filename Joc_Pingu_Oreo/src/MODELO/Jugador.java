package MODELO;

public abstract class Jugador {
	
	/* ATRIBUTS DEL JUGADOR */

	protected int posicion;
	protected String nombre;
	protected String color;
	private Inventario inventario;
	private int turnosPerdidos = 0;
	
	
	/* CONSTRUCTOR DEL JUGADOR */
	
	public Jugador(String nombre, String color, int posicion) {
		this.posicion = 1;
		this.nombre = nombre;
		this.color = color;
	}

	
	/* GETTERS I SETTERS */

	public int getPosicion() {
		return posicion;
	}


	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}
	
	public Inventario getInventario() {
	    return inventario;
	}
	
	/* METODO POSICION */
	public void moverPosicion(int p) {
		this.posicion += p;
		
		//Limit de 50 caselles
		if (this.posicion > 50) {
			this.posicion = 50;
		}
		
		//Evitar posicions negatives
		if (this.posicion < 1) {
			this.posicion = 1;
		}
		
	}

	public int getTurnosPerdidos() {
		return turnosPerdidos;
	}

	public void setTurnosPerdidos(int turnos) {
		this.turnosPerdidos = turnos;
	}
	
}

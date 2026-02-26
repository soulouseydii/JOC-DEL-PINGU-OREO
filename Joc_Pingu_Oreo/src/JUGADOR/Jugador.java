package JUGADOR;

abstract class Jugador {
	
	/* ATRIBUTS DEL JUGADOR */

	private int posicion;
	private String nombre;
	private String color;
	
	
	/* CONSTRUCTOR DEL JUGADOR */
	
	public Jugador(int posicion, String nombre, String color) {
		super();
		this.posicion = posicion;
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
	
	
	
	
	
	

	
	
	
	
}

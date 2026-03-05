package MODELO;

public class Foca extends Jugador {

	private boolean soborno;
	
	public Foca(String nombre, String color) {
		super(nombre, color);
		this.setSoborno(false);
	}
	
	//GETTERS Y SETTERS
	
	public boolean isSoborno() {
		return soborno;
	}

	public void setSoborno(boolean soborno) {
		this.soborno = soborno;
	}
	
}

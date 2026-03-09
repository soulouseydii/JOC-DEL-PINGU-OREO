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
	
	public void aplastarJugador(Pinguino p) {
		int cantidadActual = p.getInv().getlista().size();
		int aQuitar = cantidadActual / 2;
		
		System.out.println("¡La foca aplasta a " + p.getNombre() + " al pasar por su casilla!");
		
		for (int i = 0; i < aQuitar; i++) {
			if (!p.getInv().getlista().isEmpty()) {
				p.getInv().getlista().remove(0);
			}
		}
		
		System.out.println(p.getNombre() + " ha perdido " + aQuitar + " objetos de su inventario.");
		
	}
	
	public void golpearJugador(Pinguino p) {
		System.out.println("¡La foca golpea a " + p.getNombre() + " con la cola!");
	}
	
	public boolean esSobornado() {
        return this.soborno;
    }
	
	
}

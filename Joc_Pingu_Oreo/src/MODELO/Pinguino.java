package MODELO;

public class Pinguino extends Jugador {
	

	/* ATRIBUTO */
	private Inventario inv;
	
	
	/* CONSTRUCTOR QUE HEREDA DE JUGADOR */
	public Pinguino(String nombre, String color, Inventario inv) {
		super(nombre, color);
		this.inv = inv;
	}


	/* GETTERS I SETTERS DEL INVENTARIO */
	public Inventario getInv() {
		return inv;
	}


	public void setInv(Inventario inv) {
		this.inv = inv;
	}
	
	
	/* MÉTODO USAR ITEM */
	
	public void usarItem (Item i) {
		
		
	}
	
	/* MÉTODO AÑADIR ITEM */
	
	public void añadirItem (Item i) {
		
		
	}
	
	
	/* MÉTODO QUITAR ITEM */
	
	public void quitarItem (Item i) {
		
		
	}
	
	/* MÉTODO GESTIONAR BATALLA */
	
	public void gestionarBatalla (Pinguino p) {
		
		
		
	}
	
	
	
}

package MODELO;

public class Pinguino extends Jugador {	

	/* ATRIBUTO */
	private Inventario inv;
	
	
	/* CONSTRUCTOR QUE HEREDA DE JUGADOR */
	public Pinguino(String nombre, String color) {
		super(nombre, color);
		this.inv = new Inventario();
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
		// VALIDA SI NO HAY NINGUN ITEM EN EL INVENTARIO 
		
		if (!inv.getlista().contains(i)) {
			System.out.println("Este item no existe en el inventario. !!!");
			return;
		}
		
		// DETECTAR SI EL ITEM ES UN DADO Y USARLO
		
		if (i instanceof Dado) {
			Dado d = (Dado) i;
			
			int pasos = d.tirar();			
			this.moverPosicion(pasos);
			
		} else if (i instanceof bolaDeNieve) {			
			
			
		} else if (i instanceof Pez) {
			
			
		}
		
		// RESTAR 1 ITEM DEL INVENTARIO AL UTILIZARLO Y ELIMINARLO SI LLEGA A 0. 
		
					
		
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

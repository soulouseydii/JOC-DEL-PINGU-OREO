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
			
			System.out.println("Has utilizado :" + i.getNombre());
						
		} else if (i instanceof bolaDeNieve) {	
			
			
			
			
		} else if (i instanceof Pez) {
			
			
			
			
		}
		
		// RESTAR 1 ITEM DEL INVENTARIO AL UTILIZARLO Y ELIMINARLO SI LLEGA A 0. 
		i.setCantidad(i.getCantidad() - 1);
		
		if (i.getCantidad() <= 0) {
			inv.getlista().remove(i);
		}
	}
	
	/* MÉTODO AÑADIR ITEM */
	
	public void añadirItem (Item i) {
		
		
		if (i instanceof Dado) {
			Dado d = (Dado) i;

			if (d.getCantidad() < 3) {
				inv.getlista().add(d);
				System.out.println(d.getNombre() + " añadido al inventario.");

			} else {
				System.out.println("Superaste el límite de dados...");
			}
			
		} else if (i instanceof bolaDeNieve) {
			bolaDeNieve bn = (bolaDeNieve) i;
			
			if (bn.getCantidad() < 6) {
				inv.getlista().add(bn);
				System.out.println(bn.getNombre() + " añadido al inventario.     ");
			} else {
				System.out.println("Superaste el límite de bolas de nieve...");
			}
			
		} else if (i instanceof Pez) {
			Pez p = (Pez) i;
			
			if (p.getCantidad() < 2) {
				inv.getlista().add(p);
				System.out.println(p.getNombre() + " añadido al inventario.");
			} else {
				System.out.println("Superaste el límite de peces...");
			}
			
		}
	
	}
	
	
	/* MÉTODO QUITAR ITEM */
	
	public void quitarItem (Item i) {
		
		// CUANDO EXISTAN ITEMS EN EL INVENTARIO, SE RESTA LA CANTIDAD.
		if (i.getCantidad() > 0) {
			
			i.setCantidad(i.getCantidad() - 1);
			System.out.println("Item eliminado. Quedan: " + i.getCantidad());

			// CUANDO LA CANTIDAD SEA 0, SE ELIMINARA EL ITEM DEL INVENTARIO 
			if (i.getCantidad() == 0) {
				inv.getlista().remove(i);
				System.out.println("Item eliminado del inventario.");
			}			
			
		} else {
			System.out.println("No hay items disponibles en el inventario...");
		}
		
		
	}
	
	/* MÉTODO GESTIONAR BATALLA */
	
	public void gestionarBatalla (Pinguino p) {
		
		
		
	}
	
	
	
}

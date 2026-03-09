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
		
		// BUCLE QUE RECORRERA LOS ITEMS DEL INVENTARIO 
		for (Item item: inv.getlista()) {
			
			// COMPRUEBA SI EL ITEM YA EXISTE 
			if (item.getClass() == i.getClass()) {
				
				// LA UNICA CONDICIÓN ES QUE LA CANTIDAD NO SUPERE AL LIMITE TOTAL DE ITEMS EN EL INVENTARIO 
				if (item.getCantidad() < item.getLimite()) {
					
					// AUMENTAMOS UN ITEM, EN EL CASO DE QUE SE PUEDA AÑADIR AL INVENTARIO 
					item.setCantidad(item.getCantidad() + 1);
	                System.out.println(item.getNombre() + " añadido al inventario. Cantidad actual: " + item.getCantidad());

				} else { 
					
					System.out.println("No puedes tener más. Llegaste al límite de " + item.getNombre());
				}
				
				return;
				
			}
			
		}
		
		// EN EL CASO DE Q EL ITEM NO ESTUVIERA EN EL INVENTARIO. LO AÑADIMOS DESDE 0. 
		i.setCantidad(1);
	    inv.getlista().add(i);

	    System.out.println(i.getNombre() + " añadido al inventario.");
	
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
	
	
}
package MODELO;

public class Pinguino extends Jugador {	

	/* CONSTRUCTOR QUE HEREDA DE JUGADOR */
	public Pinguino(String nombre, String color, int posicion, Inventario inv) {
		super(nombre, color, posicion);
		// Usamos el inventario del padre para que getInv() y getInventario() apunten al mismo objeto
		super.setInventario(inv);
	}


	/* GETTERS I SETTERS DEL INVENTARIO */
	// getInv() y setInv() delegan en el campo inventario del padre (Jugador)
	// para garantizar que getInv() y getInventario() devuelven SIEMPRE el mismo objeto.
	public Inventario getInv() {
		return super.getInventario();
	}

	public void setInv(Inventario inv) {
		super.setInventario(inv);
	}
	

	/* MÉTODO USAR ITEM */
	
	public void usarItem (Item i) {
		
		// VALIDA SI NO HAY NINGUN ITEM EN EL INVENTARIO 
		
		if (!getInventario().getlista().contains(i)) {
			System.out.println("Este item no existe en el inventario. !!!");
			return;
		}
		
		// DETECTAR EL TIPO DE ITEM Y APLICAR SU EFECTO BASICO
		// Los items que requieren contexto del tablero (MotoDeNieve) son gestionados
		// por GestorJugador.jugadorUsaItem, que ya recibe el Tablero como parámetro.
		
		if (i instanceof Dado) {
			Dado d = (Dado) i;
			int pasos = d.tirar();			
			this.moverPosicion(pasos);
			System.out.println("Has utilizado :" + i.getNombre());
				
		} else if (i instanceof bolaDeNieve) {	
			System.out.println("Has usado una bola de nieve.");
				
		} else if (i instanceof Pez) {
			System.out.println("Usaras un pez cuando aparezca un oso.");
				
		} else if (i instanceof MotoDeNieve) {
			// El movimiento real lo gestiona GestorJugador.jugadorUsaItem (tiene acceso al Tablero)
			System.out.println(this.getNombre() + " usa una Moto de Nieve! (Efecto aplicado por el controlador)");
		}
		
		// RESTAR 1 ITEM DEL INVENTARIO AL UTILIZARLO Y ELIMINARLO SI LLEGA A 0. 
		i.setCantidad(i.getCantidad() - 1);
		
		if (i.getCantidad() <= 0) {
			getInventario().getlista().remove(i);
		}
	}
	
	/* MÉTODO AÑADIR ITEM */
	
	public void añadirItem (Item i) {
		
		// BUCLE QUE RECORRERA LOS ITEMS DEL INVENTARIO 
		for (Item item: getInventario().getlista()) {
			
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
	    getInventario().getlista().add(i);

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
				getInventario().getlista().remove(i);
				System.out.println("Item eliminado del inventario.");
			}			
			
		} else {
			System.out.println("No hay items disponibles en el inventario...");
		}
		
		
	}
	
	
	// =========================================================
	//  MÉTODOS HELPER DE INVENTARIO (usados por los Gestores)
	// =========================================================
	
	/**
	 * Cuenta la cantidad TOTAL de objetos en el inventario
	 * sumando la propiedad 'cantidad' de cada Item.
	 */
	public int contarTotalObjetos() {
		int total = 0;
		for (Item item : getInventario().getlista()) {
			total += item.getCantidad();
		}
		return total;
	}
	
	/**
	 * Cuenta cuántas unidades tiene de un item concreto por nombre.
	 * Ejemplo: contarItem("Bola de Nieve") → 3
	 */
	public int contarItem(String nombre) {
		for (Item item : getInventario().getlista()) {
			if (item.getNombre().equalsIgnoreCase(nombre)) {
				return item.getCantidad();
			}
		}
		return 0;
	}
	
	/**
	 * Comprueba si tiene al menos 1 unidad del item indicado.
	 */
	public boolean tieneItem(String nombre) {
		return contarItem(nombre) > 0;
	}
	
	/**
	 * Gasta (elimina) una cantidad concreta de un item por nombre.
	 * Si la cantidad llega a 0 o menos, elimina el item de la lista.
	 */
	public void gastarItem(String nombre, int cantidadAGastar) {
		for (int i = getInventario().getlista().size() - 1; i >= 0; i--) {
			Item item = getInventario().getlista().get(i);
			if (item.getNombre().equalsIgnoreCase(nombre)) {
				int nuevaCantidad = item.getCantidad() - cantidadAGastar;
				if (nuevaCantidad <= 0) {
					getInventario().getlista().remove(i);
				} else {
					item.setCantidad(nuevaCantidad);
				}
				return;
			}
		}
	}
	
	/**
	 * Gasta TODAS las unidades de un item concreto (lo elimina del inventario).
	 * Usado para gastar todas las bolas de nieve tras un PvP.
	 */
	public void gastarTodoItem(String nombre) {
		for (int i = getInventario().getlista().size() - 1; i >= 0; i--) {
			if (getInventario().getlista().get(i).getNombre().equalsIgnoreCase(nombre)) {
				getInventario().getlista().remove(i);
				return;
			}
		}
	}
	
	/**
	 * Pierde la mitad del inventario total (redondeado hacia abajo).
	 * Quita objetos empezando por el final de la lista.
	 */
	public void perderMitadInventario() {
		int total = contarTotalObjetos();
		int aQuitar = total / 2;
		int quitados = 0;
		
		System.out.println(getNombre() + " tiene " + total + " objetos. Pierde " + aQuitar + ".");
		
		// Recorremos la lista de items del final al principio
		for (int i = getInventario().getlista().size() - 1; i >= 0 && quitados < aQuitar; i--) {
			Item item = getInventario().getlista().get(i);
			int puedeQuitar = Math.min(item.getCantidad(), aQuitar - quitados);
			item.setCantidad(item.getCantidad() - puedeQuitar);
			quitados += puedeQuitar;
			
			// Si el item se queda a 0, lo eliminamos de la lista
			if (item.getCantidad() <= 0) {
				getInventario().getlista().remove(i);
			}
		}
		
		System.out.println(getNombre() + " ha perdido " + quitados + " objetos.");
	}
	
	
}
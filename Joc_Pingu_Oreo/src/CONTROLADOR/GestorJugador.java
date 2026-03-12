package CONTROLADOR;

import MODELO.*;

public class GestorJugador {

	//CONSTRUCTOR
	public GestorJugador() {
		
	}
	
	//METODOS DE GESTORJUGADOR
	
	public void jugadorUsaItem(Jugador j, String nombreItem) {
		System.out.println(j.getNombre() + " está intentando usar: " + nombreItem);
	
	//Bucle para recorrer el inventario del jugador y buscar item
	for (int i = 0; i < j.getInventario().getlista().size(); i++) {
		
		//Si el nombre del item coincide 
		if (j.getInventario().getlista().get(i).getNombre().equalsIgnoreCase(nombreItem)) {
			System.out.println("i" + j.getNombre() + " ha usado " + nombreItem + "!");
			
			//Aqui va el efecto del item
			switch (nombreItem.toLowerCase()) {
			case "dado rapido":
				int avanceRapido = (int) (Math.random() * 6) + 5;
				System.out.println(j.getNombre() + " tira el dado rapido y saca un " + avanceRapido);
				break;
				
			case "dado lento":
				int avanceLento = (int) (Math.random() * 3) + 1;
				System.out.println(j.getNombre() + " tira el dado lento y saca un " + avanceLento);
				break;
			}
			
			
			//Una vez usado lo elimina del inventario y se sale
			j.getInventario().getlista().remove(i);
			return;
		}
	}
	System.out.println(j.getNombre() + " no tiene ese item en el inventario.");
	}
	
	public void jugadorSeMueve(Jugador j, int pasos, Tablero t) {
		System.out.println(j.getNombre() + " avanza " + pasos + " casillas.");
		
		//Calculo de a que casilla va a ir sumando su posicion actual + los pasos
		int nuevaPosicion = j.getPosicion() + pasos;
		
		//Calcula cuals es la ultima casilla del tablero
		int meta = t.getListaCasillas().size() - 1;
		
		//Si con los dado se pasa del final lo deja en el final
		if (nuevaPosicion > meta) {
			nuevaPosicion = meta;
		}
		
		//Actualiza su posicion actual
		j.setPosicion(nuevaPosicion);
		System.out.println(j.getNombre() + " ha caido en la casilla " + nuevaPosicion);
	}
}

package MODELO;

public class SueloQuebradizo extends Casilla {

	
	
	/* CONSTRUCTOR */

	public SueloQuebradizo(int posicion) {
		super(posicion, "SueloQuebradizo");
		// TODO Auto-generated constructor stub
	}	
	
	/* METODO REALIZAR ACCIÓN*/
	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {
		System.out.println("El jugador " + jugador.getNombre() + " ha pisado un suelo quebradizo!");
		
		//Comprovamos que es un pinguino (porque la foca no tiene el mismo sistema de inventario)
		if (jugador.getClass().getSimpleName().equals("Pinguino")) {
			
			Pinguino p = (Pinguino) jugador;
			
			int cantidadObjetos = 0;
			
			//Recorremos la lista del inventario y sumamos las cantidades
			for (Item item : p.getInventario().getlista()) {
				cantidadObjetos = cantidadObjetos + item.getCantidad();
			}
			
			if (cantidadObjetos > 5) {
				//Mas de 5 objetos cae y vuelve al inicio
				p.setPosicion(1);
				System.out.println("El hielo se ha roto! " + p.getNombre() + " Llevava demasiado peso (" + cantidadObjetos + " objetos) y vuelve al inicio.");
			
			} else if (cantidadObjetos > 0 && cantidadObjetos <= 5) {
				//Enre 1 y 5 objetos pierde un turno
				System.out.println(p.getNombre() + " se ha quedado atascado en el hielo (" + cantidadObjetos + " objetos). Pierde 1 turno!");
				
				p.setTurnosPerdidos(p.getTurnosPerdidos() + 1);
				
			} else {
				//0 objetos pasa sin penalizacion
				System.out.println(p.getNombre() + " no lleva nada en el inventario i pasa sin problema.");
			}
			
		} else {
			//Si el que cae en la casilla no es un pinguino (la foca)
			System.out.println("La foca pasa sin problema.");
		}
	}
	
}

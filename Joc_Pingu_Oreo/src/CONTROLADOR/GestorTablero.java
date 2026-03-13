package CONTROLADOR;

import MODELO.*;

public class GestorTablero {
	
	
	// MÈTODO EJECUTAR CASILLA 
	
	public void ejecutarCasilla(Partida partida, Pinguino p, Casilla c) {
		
		// validamos si la casilla o el pinguino es nulo
		
		if (c == null || p == null) {
			return;
		}
		
		// Miramos en la casilla que cae el jugador, y ejecutamos la accion de la casilla.
		System.out.println("El jugador cayó en: " + c.getClass().getSimpleName());
		c.realizarAccion(partida, p);
		
	}
	
	
	// MÈTODO COMPROBAR FIN TURNO 
	
	public void comprobarFinTurno(Partida partida) {
		
		
		
		
		
	}
	
	
}
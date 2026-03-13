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
		
		// Agafar al jugador actual
		Jugador j = partida.getJugadorActual();
		
		
		// Creo una variable, que serà el calcul de la casilla final
		
		int ultimaCasilla = partida.getTablero().getListaCasillas().size() - 1; 
		
		
		
	}
	
	
}
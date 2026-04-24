package CONTROLADOR;

import MODELO.*;

public class GestorTablero {
	
	
	// MÈTODO EJECUTAR CASILLA 
	
	public void ejecutarCasilla(Partida partida, Pinguino p, Casilla c) {
		
		// Solo ejecutamos si la casilla y el pinguino existen
		if (c != null && p != null) {
			// Miramos en la casilla que cae el jugador, y ejecutamos la accion de la casilla.
			System.out.println("El jugador cayó en: " + c.getClass().getSimpleName());
			c.realizarAccion(partida, p);
		}
	}
	
	
	// MÈTODO COMPROBAR FIN TURNO 
	
	public void comprobarFinTurno(Partida partida) {
		
		// Solo comprobamos si hay partida y jugador actual
		if (partida != null && partida.getJugadorActual() != null) {
		
			// Agafar al jugador actual
			Jugador j = partida.getJugadorActual();
			
			// Creo una variable, que serà el calcul de la casilla final
			int ultimaCasilla = partida.getTablero().getListaCasillas().size() - 1; 	
			
			// en caso de que el jugador gane la partida
			if (j.getPosicion() >= ultimaCasilla) {
				partida.setFinalizada(true);
				partida.setGanador(j);
				System.out.println("El jugador " + j.getNombre() + " ha ganado !!!");
				
			} else {
				// en el caso de que no gane la partida, pasamos al siguiente turno.
				int turnoActual = partida.getJugadorActualIndice();
				int siguienteTurno = (turnoActual + 1) % partida.getJugadores().size();
				partida.setJugadorActualIndice(siguienteTurno);
			}
		}
	}
		
}
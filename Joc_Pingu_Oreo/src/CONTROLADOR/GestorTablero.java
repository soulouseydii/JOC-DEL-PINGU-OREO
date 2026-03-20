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
		
		// validacio en el cas de que no hi hagi partida o cap jugador
		
		if (partida == null || partida.getJugadorActual() == null) {
			return;
		}
		
		// Agafar al jugador actual
		Jugador j = partida.getJugadorActual();
		
		
		// Creo una variable, que serà el calcul de la casilla final
		
		int ultimaCasilla = partida.getTablero().getListaCasillas().size() - 1; 	
		
		// en caso de que el jugador gane la partida, aquel jugador será el ganador y se finalizará la partida. 
		if (j.getPosicion() >= ultimaCasilla) {
			partida.setFinalizada(true);
			partida.setGanador(j);
			
			System.out.println("El jugador " + j.getNombre() + " ha ganado !!!");
			
			// al acabar la partida y ganarla, se sale del mètodo.
			return;
			
			
		} 
		
		// en el caso de que no gane la partida, pasamos al siguiente turno.
			
			int turnoActual = partida.getJugadorActualIndice();
			int siguienteTurno = (turnoActual + 1) % partida.getJugadores().size();
			
			partida.setJugadorActualIndice(siguienteTurno);					
				
	}
		
}
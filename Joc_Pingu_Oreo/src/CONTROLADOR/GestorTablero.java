package CONTROLADOR;

import MODELO.Tablero;
import MODELO.Casilla;

public class GestorTablero {
	
	/* Metodo buscarAgujeroAnterior para GestorPartida y foca*/
	
	public int buscarAgujeroAnterior(Tablero tablero, int posicionActual) {
		
		//Bucle que busca la posicion del hueco anterior mas cercano
		for (int i = posicionActual - 1; i > 1; i--) {
			
			Casilla c = tablero.getListaCasillas().get(i);
			
			//Si encuentra el agujero
			if (c.getTipo().equals("Hueco")) {
				return i + 1; //Devuelve la posicion humana
			}
		}
		//Si el bucle termina y no encuentra ningun agujero vuelve al inicio
		return 1;
		
	}
	
}

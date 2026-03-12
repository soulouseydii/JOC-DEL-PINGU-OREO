package MODELO;

import java.util.ArrayList;

public class Tablero {
	
	// ATRIBUTOS
	private ArrayList<Casilla> listaCasillas;
	
	// CONSTRUCTOR 
	public Tablero() {
		this.listaCasillas = new ArrayList<Casilla>();
	}
	

	// GETTER I SETTER
	public ArrayList<Casilla> getListaCasillas() {
		return listaCasillas;
	}
	
	public void setListaCasillas(ArrayList<Casilla> listacasillas) {
		this.listaCasillas = listacasillas;
	}
	
	
	//Metodo buscarAgujeroAnterior
	public int buscarAgujeroAnterior(int posicionActual) {
		
		//Bucle que busca la posicion del hueco anterior mas cercano
		for (int i = posicionActual - 1; i > 1; i--) {
			
			Casilla c = this.getListaCasillas().get(i);
			
			//Si encuentra el agujero
			if (c.getTipo().equals("Hueco")) {
				return i + 1; //Devuelve la posicion humana
			}
		}
		//Si el bucle termina y no encuentra ningun agujero vuelve al inicio
		return 1;
		
	}
	
	//Metodo buscarSiguienteTrineo
	
	public int buscarSiguienteTrineo(int posicionActual) {
		
		//Bucle para recorrer el tablero 
		for(int i = posicionActual + 1; i < listaCasillas.size(); i++) {
			Casilla c = listaCasillas.get(i);
			if (c.getClass().getSimpleName().equals("Trineo")) {
				return i;
			}
		}
		
		//Si llegamos al final del tablero y no hay trineos
		return -1;
	}
	
}

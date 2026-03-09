package MODELO;

import java.util.ArrayList;

public class Tablero {
	
	private ArrayList<Casilla> listaCasillas;
	
	public Tablero() {
		this.listaCasillas = new ArrayList<>();
	}
	
	public ArrayList<Casilla> getListaCasillas() {
		return listaCasillas;
	}
}

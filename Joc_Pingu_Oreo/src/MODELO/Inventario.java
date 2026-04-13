package MODELO;

import java.util.ArrayList;

public class Inventario {
	
	/* ATRIBUTOS */
    private ArrayList<Item> lista;
    private int dadosRapidos = 0;
    private int dadosLentos = 0;

    // Constructor
    public Inventario() {
    	this.lista = new ArrayList<>();
    }
    
    // Getter
    public ArrayList<Item> getlista() {
    	return lista;
    }
    
    // Setter
    public void setLista(ArrayList<Item> lista) {
        this.lista = lista;
    }

}


package MODELO;

import java.util.ArrayList;

public class Inventario {
	
	/* ATRIBUTOS */
    private ArrayList<Item> lista;

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
    
    /* METODOS PARA AÑADIR ITEMS */

    public void addPez() {

        int contador = 0;

        for (Item item : lista) {
            if (item instanceof Pez) {
                contador++;
            }
        }

        if (contador < 2) {
            lista.add(new Pez());
        }
    }

    public void addBolaDeNieve(int cantidad) {

        int contador = 0;

        for (Item item : lista) {
            if (item instanceof BolaDeNieve) {
                contador++;
            }
        }

        for (int i = 0; i < cantidad && contador < 6; i++) {
            lista.add(new BolaDeNieve());
            contador++;
        }
    }

    public void addDado(String tipo) {

        int contador = 0;

        for (Item item : lista) {
            if (item instanceof Dado) {
                contador++;
            }
        }

        if (contador < 3) {
            lista.add(new Dado(tipo));
        }
    }
}

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
    
    // MÉTODOS PARA DADOS ESPECIALES
    
    public int getDadosRapidos() {
        return dadosRapidos;
    }

    public int getDadosLentos() {
        return dadosLentos;
    }

    public int getTotalDadosEspeciales() {
        return dadosRapidos + dadosLentos;
    }

    public void agregarDadoRapido() {
        if (getTotalDadosEspeciales() < 3) {
            dadosRapidos++;
            System.out.println("Dado Rápido añadido. Total: " + dadosRapidos);
        } else {
            System.out.println("Inventario de dados especiales lleno (máximo 3).");
        }
    }

    public void agregarDadoLento() {
        if (getTotalDadosEspeciales() < 3) {
            dadosLentos++;
            System.out.println("Dado Lento añadido. Total: " + dadosLentos);
        } else {
            System.out.println("Inventario de dados especiales lleno (máximo 3).");
        }
    }

    public void usarDadoRapido() {
        if (dadosRapidos > 0) {
            dadosRapidos--;
        }
    }

    public void usarDadoLento() {
        if (dadosLentos > 0) {
            dadosLentos--;
        }
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


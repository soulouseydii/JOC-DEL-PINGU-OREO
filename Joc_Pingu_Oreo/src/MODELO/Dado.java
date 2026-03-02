package MODELO;
import java.util.Random;
import java.util.random.*;
public class Dado extends Item {
    Random r = new Random();

	// ATRIBUTOS DEL DADO

	private int max;
	private int min;
	
	// CONSTRUCTOR DEL DADO Y LA HERENCIA
	
	public Dado(String nombre, int cantidad, int max, int min) {
		super(nombre, cantidad);
		this.max = 6;
		this.min = 1;
	}

		
	// GETTERS I SETTERS DADO 
	
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	
	
	// MÈTODE TIRAR

	public int tirar (Random r) {		   
        return r.nextInt(max) + min;     	
	}
	
	
	
	
	
	

}

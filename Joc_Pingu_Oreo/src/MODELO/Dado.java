package MODELO;
import java.util.Random;
public class Dado extends Item {
    Random r = new Random();

	// ATRIBUTOS DEL DADO

	private int max;
	private int min;
	
	// CONSTRUCTOR DEL DADO Y LA HERENCIA
	
	public Dado(String tipo) {
        super("Dado", 0, 3);
        
        switch (tipo.toLowerCase()) {
            case "rapido":
                this.min = 5;
                this.max = 10;
                break;
            case "lento":
                this.min = 1;
                this.max = 3;
                break;
            case "normal":
            default:
                this.min = 1;
                this.max = 6;
                break;
        }
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

	public int tirar() {
	    // Formula para max i min
	    return r.nextInt((max - min) + 1) + min;
	}
	

}

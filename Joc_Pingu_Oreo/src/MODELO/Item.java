package MODELO;

public class Item {
	
	protected String nombre;
	protected int cantidad;
	protected int limite;
	
	// CONSTRUCTOR
	
	public Item(String nombre, int cantidad, int limite) {
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.limite = limite;
	}

	// GETTER Y SETTERS
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getLimite() {
		return limite;
	}

}

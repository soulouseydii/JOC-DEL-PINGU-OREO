package MODELO;

public class Pez extends Item {

	// CONSTRUCTOR
	public Pez(String nombre, int cantidad) {
		super(nombre, cantidad);
	}
	
	// GETTER Y SETTERS
	
	@Override
	public String getNombre() {
		return super.nombre;
	}

	@Override
	public void setNombre(String nombre) {
		super.setNombre(nombre);
	}

	@Override
	public int getCantidad() {
		return super.cantidad;
	}

	@Override
	public void setCantidad(int cantidad) {
		super.setCantidad(cantidad);
	}
	
}

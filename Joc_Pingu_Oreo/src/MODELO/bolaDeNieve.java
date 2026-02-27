package MODELO;

public class bolaDeNieve extends Item {

	/*HEREDA LOS ATRIBUTOS DE LA CLASE PADRE ITEM */
	
	public bolaDeNieve(String nombre, int cantidad) {
		super(nombre, cantidad);
		
	}
	
	
	/* GETTERS I SETTERS HEREDADOS */
	
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

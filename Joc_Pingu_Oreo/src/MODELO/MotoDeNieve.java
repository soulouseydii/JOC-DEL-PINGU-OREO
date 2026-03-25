package MODELO;

public class MotoDeNieve extends Item {

    /* CONSTRUCTOR */
    public MotoDeNieve() {
        super("Moto de Nieve", 0, 2); // Limite: maximo 2 motos de nieve en el inventario
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

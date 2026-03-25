package MODELO;

public class Oso extends Casilla {
	
	/* CONSTRUCTOR */
	
	public Oso (int posicion) {
		super(posicion, "Oso");
	}
	
    /* METODOS REALIZAR ACCION */
	
	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {
        
        // El oso atrapa al jugador y lo manda de vuelta a la casilla 1 (salida)
        jugador.setPosicion(1);
        System.out.println("Un oso ha atrapado a " + jugador.getNombre() + "! Vuelve a la casilla 1.");
	}
}
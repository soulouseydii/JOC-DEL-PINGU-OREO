package MODELO;

public class Normal extends Casilla {
	
	/* CONSTRUCTOR */

	public Normal (int posicion) {
		super(posicion, "Normal");
	}
	
    /* METODOS REALIZAR ACCION */

    @Override
	public void realizarAccion(Partida partida, Jugador jugador) {
		// Casilla normal, no pasa nada
	}
	
}
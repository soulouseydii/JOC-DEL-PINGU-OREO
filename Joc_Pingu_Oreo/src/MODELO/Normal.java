package MODELO;

public class Normal extends Casilla {
	
	/* CONSTRUCTOR */

	public Normal (int posicion, String tipo) {
		super(posicion, tipo);
	}
	
    /* METODOS REALIZAR ACCION */

    @Override
	public void realizarAccion(Partida partida, Jugador jugador) {
		// Casilla normal, no pasa nada
	}
	
}
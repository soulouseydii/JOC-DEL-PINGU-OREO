package MODELO;

public class Oso extends Casilla {
	
	/* CONSTRUCTOR */
	
	public Oso (int posicion, String tipo) {
		super(posicion, tipo);
	}
	
    /* METODOS REALIZAR ACCION */
	
	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {
        
        // Logica de la casilla oso
        jugador.setPosicion(0);
	}
}
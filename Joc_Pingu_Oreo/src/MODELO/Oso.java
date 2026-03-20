package MODELO;

public class Oso extends Casilla {
	
	/* CONSTRUCTOR */
	
	public Oso (int posicion) {
		super(posicion, "Oso");
	}
	
    /* METODOS REALIZAR ACCION */
	
	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {
        
        // Logica de la casilla oso
        jugador.setPosicion(0);
	}
}
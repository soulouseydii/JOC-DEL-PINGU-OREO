package MODELO;

public class Agujero extends Casilla {

	/* CONSTRUCTOR */

	public Agujero (int posicion) {
		super(posicion, "Agujero");
	}
	
	 /* METODO REALIZAR ACCION */

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {
    	
    	// A partir de la posicion buscamos un agujero anterior y lo guardamos en una variable
        int agujeroAnterior = partida.getTablero().buscarAgujeroAnterior(jugador.getPosicion());
        
        
        // En caso de que exista un agujero anterior pasara esto
        if (agujeroAnterior != -1) {
            jugador.setPosicion(agujeroAnterior);
            System.out.println("i" + jugador.getNombre() + " ha caido en un agujero y retrocede a la casilla " + agujeroAnterior + "!");
        }

    }
}
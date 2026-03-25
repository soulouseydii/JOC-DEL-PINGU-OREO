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
            System.out.println(jugador.getNombre() + " ha caido en un agujero y retrocede a la casilla " + agujeroAnterior + "!");
        } else {
            // Si no hay agujero anterior, vuelve a la casilla 1 (salida)
            jugador.setPosicion(1);
            System.out.println(jugador.getNombre() + " ha caido en un agujero pero no hay agujeros anteriores. Vuelve a la casilla 1!");
        }

    }
}
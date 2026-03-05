package MODELO;

public class Trineo extends Casilla {

    /* CONSTRUCTOR */

    public Trineo(int posicion) {
        super(posicion);
    }

    /* METODO REALIZAR ACCION */

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {

    	// A partir de la posicion buscamos un trineo proximo y lo guardamos en una variable
        int siguienteTrineo = partida.getTablero()
                                     .buscarSiguienteTrineo(jugador.getPosicion());
        
        // En caso de que exista un trineo mas adelante pasara esto
        if (siguienteTrineo != -1) {
            jugador.setPosicion(siguienteTrineo);
        }
    }

}
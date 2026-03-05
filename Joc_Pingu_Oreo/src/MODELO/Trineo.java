package MODELO;

public class Trineo extends Casilla {

    /* CONSTRUCTOR */

    public Trineo(int posicion) {
        super(posicion);
    }

    /* METODO REALIZAR ACCION */

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {

        int siguienteTrineo = partida.getTablero()
                                     .buscarSiguienteTrineo(jugador.getPosicion());
        
        // En caso de que exista un trineo mas adelante pasara esto
        if (siguienteTrineo != -1) {
            jugador.setPosicion(siguienteTrineo);
        }
    }

}
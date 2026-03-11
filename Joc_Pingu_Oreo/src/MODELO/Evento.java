package MODELO;
import java.util.Random;

public class Evento extends Casilla {
	
	/* ATRIBUTOS */
	
	String [] eventos;
	
	/* CONSTRUCTOR */

	public Evento(int posicion, String tipo) {
        super(posicion, tipo);
    }
	

	/* METODO REALIZAR ACCION */

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {

        Random random = new Random();

        int evento = random.nextInt(4); 
        // genera 0,1,2 o 3

        switch (evento) {

            case 0:
                jugador.getInventario().addPez();
                break;

            case 1:
                int bolas = random.nextInt(3) + 1; // 1-3
                jugador.getInventario().addBolaDeNieve(bolas);
                break;
        }
    }
}
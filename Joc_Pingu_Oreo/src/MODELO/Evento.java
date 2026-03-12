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

    	//Comprobamos que es un pinguino
    	if (jugador.getClass().getSimpleName().equals("Pinguino")) {
    		
    		//Lo convertimos a pinguino para que podamos acceder al inventario
    		Pinguino p = (Pinguino) jugador;
    		
    		Random random = new Random();
    		int evento = random.nextInt(4);
    		
    		switch (evento) {
    		case 0:
    			System.out.println("Evento! " + p.getNombre() + " ha enconctrado un Pez.");
    			p.añadirItem(new Pez());
    			break;
    			
    		case 1:
    			int bolas = random.nextInt(3) + 1;
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado " + bolas + " bolas de nieve.");
    			//Bucle para añadir las bolas de nieve
    			for (int i = 0; i < bolas; i++) {
    				p.añadirItem(new bolaDeNieve());
    			}
    			break;
    			
    		case 2:
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado un dado lento.");
				p.añadirItem(new Dado("Lento"));
    			break;
    			
    		case 3:
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado un dado rapido.");
				p.añadirItem(new Dado("Rapido"));
    			break;

    		}
    	
        } else {
        	//Si es la foca
        	System.out.println("La foca ha caido en la casilla evento pero ignora los objetos.");
        }
    }
}


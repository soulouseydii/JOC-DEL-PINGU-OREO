package MODELO;
import java.util.Random;

public class Evento extends Casilla {
	
	/* ATRIBUTOS */
	
	String [] eventos;
	
	/* CONSTRUCTOR */

	public Evento(int posicion) {
        super(posicion, "Evento");
    }
	

	/* METODO REALIZAR ACCION */

    @Override
    public void realizarAccion(Partida partida, Jugador jugador) {

    	//Comprobamos que es un pinguino
    	if (jugador.getClass().getSimpleName().equals("Pinguino")) {
    		
    		//Lo convertimos a pinguino para que podamos acceder al inventario
    		Pinguino p = (Pinguino) jugador;
    		
    		Random random = new Random();
    		int evento = random.nextInt(3); // 3 posibles eventos: 0=Pez, 1=Bolas de Nieve, 2=Moto de Nieve
    		
    		switch (evento) {
    		case 0:
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado un Pez.");
    			p.añadirItem(new Pez());
    			break;
    			
    		case 1:
    			int bolas = random.nextInt(3) + 1; // Entre 1 y 3 bolas
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado " + bolas + " bola(s) de nieve.");
    			//Bucle para añadir las bolas de nieve
    			for (int i = 0; i < bolas; i++) {
    				p.añadirItem(new bolaDeNieve());
    			}
    			break;
    			
    		case 2:
    			System.out.println("Evento! " + p.getNombre() + " ha encontrado una Moto de Nieve.");
    			p.añadirItem(new MotoDeNieve());
    			break;

    		}
    	
        } else {
        	//Si es la foca
        	System.out.println("La foca ha caido en la casilla evento pero ignora los objetos.");
        }
    }
}


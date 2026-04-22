package MODELO;
import java.util.Random;

public class Sorpresa extends Casilla {
	
	/* CONSTRUCTOR */
	
	public Sorpresa(int posicion) {
		super(posicion, "Sorpresa");
	}
	
	/* METODO REALIZAR ACCION */
	
	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {
		Random random = new Random();
		
		System.out.println("¡" + jugador.getNombre() + " ha caido en una Casilla Sorpresa!");

		if (jugador instanceof Pinguino) {
			Pinguino p = (Pinguino) jugador;
			int prob = random.nextInt(10); // 10 posibilidades (10% cada una)
			
			switch (prob) {
				case 0:
					// 10% Oso
					System.out.println("¡Sorpresa! ¡Es un Oso! " + p.getNombre() + " vuelve a Start.");
					new Oso(getPosicion()).realizarAccion(partida, p);
					break;
				case 1:
					// 10% Agujero
					System.out.println("¡Sorpresa! ¡Es un Agujero!");
					new Agujero(getPosicion()).realizarAccion(partida, p);
					break;
				case 2:
					// 10% Suelo Quebradizo
					System.out.println("¡Sorpresa! ¡Pisas Suelo Quebradizo!");
					new SueloQuebradizo(getPosicion()).realizarAccion(partida, p);
					break;
				case 3:
					// 10% Normal
					System.out.println("¡Sorpresa! ...pero no pasa nada. Es una casilla normal.");
					break;
				case 4:
					// 10% Trineo
					System.out.println("¡Sorpresa! ¡Has encontrado un Trineo!");
					new Trineo(getPosicion()).realizarAccion(partida, p);
					break;
				case 5:
					// 10% Pez
					System.out.println("¡Sorpresa! " + p.getNombre() + " encuentra un Pez.");
					p.añadirItem(new Pez());
					break;
				case 6:
					// 10% Bolas
					int bolas = random.nextInt(3) + 1;
					System.out.println("¡Sorpresa! " + p.getNombre() + " encuentra " + bolas + " bola(s) de nieve.");
					for (int i = 0; i < bolas; i++) {
						p.añadirItem(new bolaDeNieve());
					}
					break;
				case 7:
					// 10% Moto
					System.out.println("¡Sorpresa! " + p.getNombre() + " encuentra una Moto de Nieve.");
					p.añadirItem(new MotoDeNieve());
					break;
				case 8:
					// 10% Dado Rapido
					if (p.getInv().getTotalDadosEspeciales() < 3) {
						System.out.println("¡Sorpresa! " + p.getNombre() + " encuentra un DADO RAPIDO.");
						p.getInv().agregarDadoRapido();
					} else {
						System.out.println("¡Sorpresa! Encontró un dado rapido, pero el inventario está lleno.");
					}
					break;
				case 9:
					// 10% Dado Lento
					if (p.getInv().getTotalDadosEspeciales() < 3) {
						System.out.println("¡Sorpresa! " + p.getNombre() + " encuentra un DADO LENTO.");
						p.getInv().agregarDadoLento();
					} else {
						System.out.println("¡Sorpresa! Encontró un dado lento, pero el inventario está lleno.");
					}
					break;
			}
			
		} else if (jugador instanceof Foca) {
			Foca f = (Foca) jugador;
			int prob = random.nextInt(5); // Solo 5 posibilidades (Oso, Agujero, Suelo, Normal, Trineo)
			
			switch (prob) {
				case 0:
					System.out.println("¡Sorpresa! Un Oso asusta a la Foca, volviendo a Start.");
					new Oso(getPosicion()).realizarAccion(partida, f);
					break;
				case 1:
					System.out.println("¡Sorpresa! La Foca cae en un Agujero.");
					new Agujero(getPosicion()).realizarAccion(partida, f);
					break;
				case 2:
					System.out.println("¡Sorpresa! La Foca pisa Suelo Quebradizo.");
					new SueloQuebradizo(getPosicion()).realizarAccion(partida, f);
					break;
				case 3:
					System.out.println("¡Sorpresa! ...pero no pasa nada. Es una casilla normal.");
					break;
				case 4:
					System.out.println("¡Sorpresa! La Foca cae en un Trineo! Avanza rápidamente.");
					new Trineo(getPosicion()).realizarAccion(partida, f);
					break;
			}
		}
	}
}

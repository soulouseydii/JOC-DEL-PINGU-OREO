package MODELO;

import java.util.Random;

public class Evento extends Casilla {

	/* ATRIBUTOS */

	String[] eventos;

	/* CONSTRUCTOR */

	public Evento(int posicion) {
		super(posicion, "Evento");
	}

	/* METODO REALIZAR ACCION */

	@Override
	public void realizarAccion(Partida partida, Jugador jugador) {

		// Comprobamos que es un pinguino
		if (jugador.getClass().getSimpleName().equals("Pinguino")) {

			// Lo convertimos a pinguino para que podamos acceder al inventario
			Pinguino p = (Pinguino) jugador;

			Random random = new Random();
			double prob = random.nextDouble(); // Generamos un número entre 0.0 y 1.0

			if (prob < 0.12) {
				// 12% de probabilidad: Dado Rápido
				if (p.getInv().getTotalDadosEspeciales() < 3) {
					System.out.println("¡Evento! " + p.getNombre() + " ha encontrado un DADO RÁPIDO.");
					p.getInv().agregarDadoRapido();
				} else {
					System.out.println("Evento! " + p.getNombre() + " encontró un dado rápido pero no tiene espacio.");
				}
			} else if (prob < 0.37) {
				// 25% de probabilidad (0.12 a 0.37): Dado Lento
				if (p.getInv().getTotalDadosEspeciales() < 3) {
					System.out.println("¡Evento! " + p.getNombre() + " ha encontrado un DADO LENTO.");
					p.getInv().agregarDadoLento();
				} else {
					System.out.println("Evento! " + p.getNombre() + " encontró un dado lento pero no tiene espacio.");
				}
			} else {
				// El resto de probabilidad se divide entre los items antiguos
				int evento = random.nextInt(3); // 0=Pez, 1=Bolas de Nieve, 2=Moto de Nieve

				switch (evento) {
					case 0:
						System.out.println("Evento! " + p.getNombre() + " ha encontrado un Pez.");
						p.añadirItem(new Pez());
						break;

					case 1:
						int bolas = random.nextInt(3) + 1; // Entre 1 y 3 bolas
						System.out
								.println("Evento! " + p.getNombre() + " ha encontrado " + bolas + " bola(s) de nieve.");
						for (int i = 0; i < bolas; i++) {
							p.añadirItem(new bolaDeNieve());
						}
						break;

					case 2:
						System.out.println("Evento! " + p.getNombre() + " ha encontrado una Moto de Nieve.");
						p.añadirItem(new MotoDeNieve());
						break;
				}
			}

		} else {
			// Si es la foca
			System.out.println("La foca ha caido en la casilla evento pero ignora los objetos.");
		}
	}
}

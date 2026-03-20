package CONTROLADOR;

import MODELO.*;

public class GestorJugador {

	//CONSTRUCTOR
	public GestorJugador() {
		
	}
	
	//METODOS DE GESTORJUGADOR
	
	
	// ----------------
	//  JUGADORUSAITEM
	// ----------------
	
	public void jugadorUsaItem(Jugador j, String nombreItem, Tablero t) {
		System.out.println(j.getNombre() + " está intentando usar: " + nombreItem);
	
	//Bucle para recorrer el inventario del jugador y buscar item
	for (int i = 0; i < j.getInventario().getlista().size(); i++) {
		
		//Si el nombre del item coincide 
		if (j.getInventario().getlista().get(i).getNombre().equalsIgnoreCase(nombreItem)) {
			System.out.println("i" + j.getNombre() + " ha usado " + nombreItem + "!");
			
			//Aqui va el efecto del item
			switch (nombreItem.toLowerCase()) {
				case "dado rapido":
					int avanceRapido = (int) (Math.random() * 6) + 5;
					System.out.println(j.getNombre() + " tira el dado rapido y saca un " + avanceRapido);
					this.jugadorSeMueve(j, avanceRapido, t);
					break;
					
				case "dado lento":
					int avanceLento = (int) (Math.random() * 3) + 1;
					System.out.println(j.getNombre() + " tira el dado lento y saca un " + avanceLento);
					this.jugadorSeMueve(j, avanceLento, t);
					break;
				}
			
			
			//Una vez usado lo elimina del inventario y se sale
			j.getInventario().getlista().remove(i);
			return;
		}
	}
	System.out.println(j.getNombre() + " no tiene ese item en el inventario.");
	}
	
	// ----------------
	//  JUGADORSEMUEVE
	// ----------------
	
	public void jugadorSeMueve(Jugador j, int pasos, Tablero t) {
		System.out.println(j.getNombre() + " avanza " + pasos + " casillas.");
		
		//Calculo de a que casilla va a ir sumando su posicion actual + los pasos
		int nuevaPosicion = j.getPosicion() + pasos;
		
		//Calcula cuals es la ultima casilla del tablero
		int meta = t.getListaCasillas().size() - 1;
		
		//Si con los dado se pasa del final lo deja en el final
		if (nuevaPosicion > meta) {
			nuevaPosicion = meta;
		}
		
		//Actualiza su posicion actual
		j.setPosicion(nuevaPosicion);
		System.out.println(j.getNombre() + " ha caido en la casilla " + nuevaPosicion);
	}
	
	// ----------------------
	//  JUGADORFINALIZATURNO
	// ----------------------
	
	public void jugadorFinalizaTurno(Jugador j) {
		System.out.println("El truno de " + j.getNombre() + " ha terminado!");
		
		//QUAN CONECTEM BASE DE DADES AQUI ES GUARDARA L'INVENTARI I LA POSICIO DEL JUGADOR
		
	}
	
	// ----------------
	//  PINGUINOFRENTE
	// ----------------

	public void pinguinoFrente(Pinguino p) {
		//PARA JAVAFX, EL PINGUINO CUANDO SE PARA SE PONE MIRANDO DE FRENTE.
		System.out.println(p.getNombre() + " mira al frente.");
	}
	
	// ----------------
	//  PINGUINOGOLPEA
	// ----------------
	
	public void pinguinoGolpea(Pinguino p1, Pinguino p2, Tablero t) {
		System.out.println("Gerra de bolas de nieve entre " + p1.getNombre() + " i " + p2.getNombre() + ".");
		
		//Contar bolas de cada uno
		int bolasP1 = contarBolas(p1);
		int bolasP2 = contarBolas(p1);
		
		System.out.println(p1.getNombre() + " tiene " + bolasP1 + " bolas.");
        System.out.println(p2.getNombre() + " tiene " + bolasP2 + " bolas.");

        // 2. Calcular la diferencia
        int diferencia = Math.abs(bolasP1 - bolasP2);

        // 3. Ver quién gana y hacer retroceder al perdedor
        if (bolasP1 > bolasP2) {
            System.out.println(p1.getNombre() + " gana la pelea!");
            // p2 retrocede (enviamos la diferencia en negativo)
            this.jugadorSeMueve(p2, -diferencia, t); 
            
        } else if (bolasP2 > bolasP1) {
            System.out.println(p2.getNombre() + " gana la pelea!");
            // p1 retrocede
            this.jugadorSeMueve(p1, -diferencia, t);
            
        } else {
            System.out.println("Empate! Nadie retrocede.");
        }

        // 4. Ambos gastan todas sus bolas de nieve al terminar
        eliminarTodasLasBolas(p1);
        eliminarTodasLasBolas(p2);
    }
	
	//METODO CONTARBOLAS Y ELIMINARTODASLASBOLAS
	
	private int contarBolas(Pinguino p) {
        int contador = 0;
        for (int i = 0; i < p.getInventario().getlista().size(); i++) {
            if (p.getInventario().getlista().get(i).getNombre().equalsIgnoreCase("bola de nieve")) {
                contador++; // Suma 1 por cada bola que encuentra
            }
        }
        return contador;
    }

    private void eliminarTodasLasBolas(Pinguino p) {
        // Recorre la lista al revés para evitar errores al borrar elementos
        for (int i = p.getInventario().getlista().size() - 1; i >= 0; i--) {
            if (p.getInventario().getlista().get(i).getNombre().equalsIgnoreCase("bola de nieve")) {
                p.getInventario().getlista().remove(i);
            }
        }
    }
	
	// ----------------
	//  FOCAINTERACTUA
	// ----------------
	
	public void focaInteractua(Pinguino p, Foca f, Tablero t, GestorPartida gp) {
        System.out.println("La foca " + f.getNombre() + " se acerca peligrosamente a " + p.getNombre() + "...");
        gp.interaccionFocaPinguino(f, p, t);
    }
	
}

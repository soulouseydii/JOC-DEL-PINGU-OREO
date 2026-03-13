package CONTROLADOR;

import MODELO.*;
import java.util.Random;
import MODELO.Dado;
import MODELO.Jugador;
import java.util.ArrayList;

public class GestorPartida {
	
    // Atributos 
    private Partida partida;
    private GestorTablero gestorTablero;
    private GestorJugador gestorJugador;
    private Random random = new Random();
    
    public void siguienteTurno() {
        // Ponemos los jugadores totales en totalJugadores para operar luego
        int totalJugadores = this.partida.getJugadores().size();
        
        // para saber quien esta jugando actualmente
        int indiceActual = this.partida.getJugadorActualIndice();
        
        // sumamos el indice mas 1 y si es divisible entre total de jugadores se repite el orden de tiradas entre jugadores
        int siguienteIndice = (indiceActual + 1) % totalJugadores;
        
        // El jugadoractual pasa a ser 0
        this.partida.setJugadorActualIndice(siguienteIndice);
        
        // Sumamos 1 al contador para saber cuantos turnos lleva la partida
        this.partida.setTurnos(this.partida.getTurnos() + 1);
    }
    
    /* Crea una nueva instancia de Partida y configura los elementos iniciales. */
    public void nuevaPartida(ArrayList<Jugador> listaJugadores) {
        // Creamos el objeto Partida 
        this.partida = new Partida();
        
        // Asignamos la lista de jugadores que nos pasa la Vista.
        this.partida.setJugadores(listaJugadores);
        
        // Valores de inicio de partida a 0
        this.partida.setTurnos(0);              // Contador de turnos totales a 0
        this.partida.setJugadorActualIndice(0); // Primer jugador de la lista
        this.partida.setFinalizada(false);      // Partida acabada sera false
        this.partida.setGanador(null);          // Ganador nulo
        
        System.out.println("Nueva partida creada con " + listaJugadores.size() + " jugadores.");
    }
    
    /* Método en GestorPartida */
    public int tirarDado(Jugador j, Dado dadoSeleccionado) {
        // Si el jugador no ha elegido un dado especial, creamos uno normal
        if (dadoSeleccionado == null) {
            dadoSeleccionado = new Dado("Normal"); 
        }
        
        // Llamamos al método tirar() que ya tienes en la clase Dado
        return dadoSeleccionado.tirar();
    }
	
    /*Metodo interaccionFocaPinguino*/
    public void interaccionFocaPinguino(Foca foca, Pinguino pinguino, Tablero tablero) {
		  
        // Comprobamos si coinciden en la misma casilla
        if (foca.getPosicion() == pinguino.getPosicion()) {
			  
            boolean tienePez = false;
            int indicePez = -1;
			  
            // Buscamos si el pinguino tiene un "Pez" en su inventario
            for (int i = 0; i < pinguino.getInv().getlista().size(); i++) {
                if (pinguino.getInv().getlista().get(i).getNombre().equalsIgnoreCase("Pez")) {
                    indicePez = i;
                    tienePez = true; 
                    break; // dejar de buscar una vez encontrado
                }
            }
			  
            // Resolución según las reglas
            if (tienePez) {
                // Le damos el pez a la foca
                pinguino.getInv().getlista().remove(indicePez);
                
                // Aplicamos el bloqueo de 2 turnos
                foca.setTurnosBloqueada(2); 
                
                System.out.println(pinguino.getNombre() + " ha alimentado a la foca con un pez, ¡la foca queda bloqueada 2 turnos!");
            } else {
                // Si no tiene pez la foca lo ataca
                foca.golpearJugador(pinguino);
				  
                // GestorTablero busca donde lo tiene que enviar
                int nuevaPosicion = tablero.buscarAgujeroAnterior(pinguino.getPosicion());				  
                
                // Movemos el pinguino a la posicion
                pinguino.setPosicion(nuevaPosicion);
                System.out.println(pinguino.getNombre() + " ha sido enviado al hueco de la casilla " + nuevaPosicion);
            }
        }
    }
}

package CONTROLADOR;

import MODELO.*;
import java.util.Random;
import MODELO.Dado;
import MODELO.Jugador;
import java.util.ArrayList;
import java.sql.Connection;
import java.util.LinkedHashMap;

public class GestorPartida {
	
    // Atributos 
    private Partida partida;
    private GestorTablero gestorTablero;
    private GestorJugador gestorJugador;
    private Random random = new Random();
    private Connection conexion;
    private GestorBBDD gestorBBDD;

    // Constructor
    public GestorPartida() {
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        this.gestorBBDD = new GestorBBDD();
    }
    
    public void guardarPartida() {
        gestorBBDD.guardarBBDD(partida);
    }

    public void cargarPartida(int id) {

        Partida partidaCargada = gestorBBDD.cargarBBDD(id);

        if (partidaCargada != null) {
            this.partida = partidaCargada;
            System.out.println("Partida recuperada de la base de datos.");
        }
    }
    
    /* Gestiona las reglas específicas según el tipo de jugador, Pingüino o Foca */
    public void procesarTurnoJugador(Jugador j) {
        
        // Si el jugador es una Foca
        if (j instanceof Foca) {
            Foca f = (Foca) j; // Hacemos que jugador sea foca para ver sus datos, ahora se llamara f
            
            // Regla: Si tiene turnos de bloqueo (por el pez), los reducimos y no mueve
            if (f.getTurnosBloqueada() > 0) {
                System.out.println("La foca está bloqueada. Turnos restantes: " + f.getTurnosBloqueada());
                f.reducirBloqueo(); 
            } else {
                // Si no esta bloqueada, la foca tira el dado y se mueve
                int pasos = tirarDado(f, null);
                gestorJugador.jugadorSeMueve(f, pasos, partida.getTablero());
            }
        } 
        
        // Si el jugador es un Pingüino
        else if (j instanceof Pinguino) {
            int pasos = tirarDado(j, null);
            gestorJugador.jugadorSeMueve(j, pasos, partida.getTablero());
        }
    }
    
    public void ejecutarTurnoCompleto() {
        // Para saber quien es el jugador que tiene que mover ficha
        Jugador jActual = partida.getJugadorActual();
        
        System.out.println("--- COMIENZA EL TURNO DE: " + jActual.getNombre() + " ---");

        // Comprobacion de turnos perdidos (SueloQuebradizo)
        if (jActual.getTurnosPerdidos() > 0) {
            jActual.setTurnosPerdidos(jActual.getTurnosPerdidos() - 1);
            System.out.println(jActual.getNombre() + " pierde este turno por el suelo quebradizo. Turnos perdidos restantes: " + jActual.getTurnosPerdidos());
            siguienteTurno();
            return;
        }

        // Si es una Foca
        if (jActual instanceof Foca) {
            Foca f = (Foca) jActual;
            if (f.getTurnosBloqueada() > 0) {
                System.out.println("La foca está bloqueada. Turnos restantes: " + f.getTurnosBloqueada());
                f.reducirBloqueo();
                siguienteTurno();
                return;
            }
            int pasosFoca = tirarDado(f, null);
            System.out.println(f.getNombre() + " ha sacado un " + pasosFoca);
            gestorJugador.jugadorSeMueve(f, pasosFoca, partida.getTablero());
            System.out.println("Nueva posición de la foca: " + f.getPosicion());

        // Si es un Pinguino
        } else if (jActual instanceof Pinguino) {
            int pasos = tirarDado(jActual, null);
            System.out.println(jActual.getNombre() + " ha sacado un " + pasos);
            gestorJugador.jugadorSeMueve(jActual, pasos, partida.getTablero());
            System.out.println("Nueva posición: " + jActual.getPosicion());
        }

        // Ejecutar la accion de la casilla donde ha caido
        Casilla casillaDondeCae = partida.getTablero().getListaCasillas().get(jActual.getPosicion());
        casillaDondeCae.realizarAccion(partida, jActual);

        // Si el que ha movido es un pinguino, miramos si hay una foca en su casilla
        for (Jugador j : partida.getJugadores()) {
            if (j instanceof Foca && jActual instanceof Pinguino) {
                interaccionFocaPinguino((Foca) j, (Pinguino) jActual, partida.getTablero());
            }
        }

        // Verificamos si ha ganado el jugador actual
        if (jActual.getPosicion() >= 50) {
            partida.setFinalizada(true);
            partida.setGanador(jActual);
            System.out.println("¡TENEMOS UN GANADOR: " + jActual.getNombre() + "!");
        } else {
            // Si no gana, pasamos al siguiente turno
            siguienteTurno();
        }
    }
    
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
    
    /* Sobrecarga sin parámetros: crea una nueva partida con lista de jugadores vacía */
    public void nuevaPartida() {
        nuevaPartida(new ArrayList<Jugador>());
    }
    
    /* Getter de Partida */
    public Partida getPartida() {
        return partida;
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
			  
                // GestorTablero busca donde lo tiene que enviar (agujero anterior o casilla 1)
                int nuevaPosicion = tablero.buscarAgujeroAnterior(pinguino.getPosicion());
                
                // Si no hay agujero anterior, vuelve a la casilla 1
                if (nuevaPosicion == -1) {
                    nuevaPosicion = 1;
                }
                
                // Movemos el pinguino a la posicion
                pinguino.setPosicion(nuevaPosicion);
                System.out.println(pinguino.getNombre() + " ha sido enviado a la casilla " + nuevaPosicion);
            }
        }
    }
}

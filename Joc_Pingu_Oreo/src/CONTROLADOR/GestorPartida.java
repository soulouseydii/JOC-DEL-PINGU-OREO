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
    
    // Guarda la posición donde cayó el jugador ANTES del efecto de casilla
    // para que la Vista sepa qué tipo de casilla pisó
    private int ultimaCasillaPisada = -1;
    private int ultimoResultadoDado = 0;

    // Constructor
    public GestorPartida() {
        this.gestorTablero = new GestorTablero();
        this.gestorJugador = new GestorJugador();
        this.gestorBBDD = new GestorBBDD();
    }
    
    /**
     * Guarda la partida actual en la BD.
     * Si ya tiene ID (fue guardada antes), hace UPDATE.
     * Si es nueva, hace INSERT.
     * @return ID de la partida guardada, o -1 si fallo
     */
    public int guardarPartida() {
        int id = gestorBBDD.guardarBBDD(partida);
        return id;
    }

    public boolean cargarPartida(int id) {
        Partida partidaCargada = gestorBBDD.cargarBBDD(id);
        if (partidaCargada != null) {
            this.partida = partidaCargada;
            System.out.println("Partida recuperada de la base de datos.");
            return true;
        }
        return false;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public GestorBBDD getGestorBBDD() {
        return gestorBBDD;
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
    
    public void ejecutarTurnoCompleto(Dado dadoEspecial) {
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

        // ============================
        //  TURNO DE LA FOCA
        // ============================
        if (jActual instanceof Foca) {
            Foca f = (Foca) jActual;
            
            // Comprobar bloqueo por pez
            if (f.getTurnosBloqueada() > 0) {
                System.out.println("La foca está bloqueada. Turnos restantes: " + f.getTurnosBloqueada());
                f.reducirBloqueo();
                siguienteTurno();
                return;
            }
            
            // Guardamos la posición antes de mover para comprobar casillas intermedias
            int posicionAnterior = f.getPosicion();
            
            // La foca tira el dado y se mueve
            int pasosFoca = tirarDado(f, dadoEspecial);
            ultimoResultadoDado = pasosFoca;
            System.out.println(f.getNombre() + " ha sacado un " + pasosFoca);
            gestorJugador.jugadorSeMueve(f, pasosFoca, partida.getTablero());
            System.out.println("Nueva posición de la foca: " + f.getPosicion());
            
            // Guardamos la casilla donde cayó ANTES del efecto
            ultimaCasillaPisada = f.getPosicion();
            
            // Ejecutar la acción de la casilla donde ha caído la foca
            Casilla casillaFoca = partida.getTablero().getListaCasillas().get(f.getPosicion());
            casillaFoca.realizarAccion(partida, f);
            
            // REGLA: La foca aplasta a los pingüinos de las casillas INTERMEDIAS
            // (entre posicionAnterior+1 y posicionFinal-1)
            for (int casilla = posicionAnterior + 1; casilla < f.getPosicion(); casilla++) {
                for (Jugador j : partida.getJugadores()) {
                    if (j instanceof Pinguino && j.getPosicion() == casilla) {
                        Pinguino p = (Pinguino) j;
                        System.out.println("¡La foca ha pasado por encima de " + p.getNombre() + " en la casilla " + casilla + "!");
                        f.aplastarJugador(p); // Solo pierde la mitad del inventario
                    }
                }
            }
            
            // REGLA: La interacción foca-pingüino en casilla FINAL
            // se maneja ahora desde la VISTA (PantallaJuego) para mostrar
            // la animación visual y el diálogo de decisión.

        // ============================
        //  TURNO DEL PINGÜINO
        // ============================
        } else if (jActual instanceof Pinguino) {
            Pinguino pActual = (Pinguino) jActual;
            
            int pasos = tirarDado(jActual, dadoEspecial);
            ultimoResultadoDado = pasos;
            System.out.println(jActual.getNombre() + " ha sacado un " + pasos);
            gestorJugador.jugadorSeMueve(jActual, pasos, partida.getTablero());
            System.out.println("Nueva posición: " + jActual.getPosicion());
            
            // Guardamos la posición ANTES del efecto de casilla para la Vista
            ultimaCasillaPisada = jActual.getPosicion();
            
            // Ejecutar la accion de la casilla donde ha caido
            Casilla casillaDondeCae = partida.getTablero().getListaCasillas().get(jActual.getPosicion());
            casillaDondeCae.realizarAccion(partida, jActual);
            
            // REGLA: La interacción pingüino-foca en la misma casilla
            // se maneja ahora desde la VISTA (PantallaJuego) para mostrar
            // la animación visual y el diálogo de decisión.
            
            // REGLA PvP: La detección de otro pingüino en la misma casilla
            // se maneja ahora desde la VISTA (PantallaJuego) para mostrar
            // la animación visual y el diálogo de decisión.
        }

        // Verificamos si ha ganado el jugador actual
        if (jActual.getPosicion() >= 49) {
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
    
    /* Getter de la última casilla pisada (antes del efecto) */
    public int getUltimaCasillaPisada() {
        return ultimaCasillaPisada;
    }
    
    /* Getter del último resultado del dado */
    public int getUltimoResultadoDado() {
        return ultimoResultadoDado;
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
	
    /*
     * INTERACCIÓN FOCA-PINGÜINO (casilla final)
     * 
     * Si el pingüino TIENE pez: gasta 1 pez, la foca queda bloqueada 2 turnos.
     * Si el pingüino NO tiene pez: la foca le golpea (pierde mitad inventario)
     *   y lo envía al agujero anterior más cercano (o casilla 1).
     */
    public void interaccionFocaPinguino(Foca foca, Pinguino pinguino, Tablero tablero) {
		  
        // Comprobamos si coinciden en la misma casilla
        if (foca.getPosicion() == pinguino.getPosicion()) {
			  
            // Resolución según las reglas usando los nuevos métodos de Pinguino
            if (pinguino.tieneItem("Pez")) {
                // Le damos el pez a la foca (gasta 1 unidad)
                pinguino.gastarItem("Pez", 1);
                
                // Aplicamos el bloqueo de 2 turnos
                foca.setTurnosBloqueada(2); 
                
                System.out.println(pinguino.getNombre() + " ha alimentado a la foca con un pez. ¡La foca queda bloqueada 2 turnos!");
            } else {
                // Si no tiene pez, la foca lo golpea (pierde mitad inventario)
                foca.golpearJugador(pinguino);
			  
                // GestorTablero busca donde lo tiene que enviar (agujero anterior o casilla 1)
                int nuevaPosicion = tablero.buscarAgujeroAnterior(pinguino.getPosicion());
                
                // Si no hay agujero anterior, vuelve a la casilla 1
                if (nuevaPosicion == -1) {
                    nuevaPosicion = 0;
                }
                
                // Movemos el pinguino a la posicion
                pinguino.setPosicion(nuevaPosicion);
                System.out.println(pinguino.getNombre() + " ha sido enviado a la casilla " + nuevaPosicion);
            }
        }
    }

    /**
     * Detecta si hay otro Pinguino en la misma casilla que el atacante.
     * Devuelve el Pinguino rival o null si no hay encuentro PvP.
     */
    public Pinguino detectarPvP(Pinguino atacante) {
        for (Jugador j : partida.getJugadores()) {
            if (j instanceof Pinguino && j != atacante && j.getPosicion() == atacante.getPosicion()) {
                return (Pinguino) j;
            }
        }
        return null;
    }

    /**
     * Ejecuta la pelea PvP y devuelve información del resultado.
     * result[0] = índice del ganador: 0=atacante, 1=defensor, -1=empate
     * result[1] = diferencia de bolas (casillas que retrocede el perdedor)
     * result[2] = bolas del atacante (antes de la pelea)
     * result[3] = bolas del defensor (antes de la pelea)
     */
    public int[] ejecutarPelea(Pinguino atacante, Pinguino defensor) {
        int bolasAtacante = atacante.contarItem("Bola de Nieve");
        int bolasDefensor = defensor.contarItem("Bola de Nieve");
        int diferencia = Math.abs(bolasAtacante - bolasDefensor);

        // Delegar la resolución al GestorJugador
        gestorJugador.pinguinoGolpea(atacante, defensor, partida.getTablero());

        int ganador;
        if (bolasAtacante > bolasDefensor) {
            ganador = 0; // atacante gana
        } else if (bolasDefensor > bolasAtacante) {
            ganador = 1; // defensor gana
        } else {
            ganador = -1; // empate
        }

        return new int[]{ganador, diferencia, bolasAtacante, bolasDefensor};
    }

    // =========================================
    //  ENCUENTRO CON FOCA — Detección y resolución
    // =========================================

    /**
     * Detecta si hay una Foca en la misma casilla que el pingüino.
     * Devuelve la Foca o null si no hay encuentro.
     */
    public Foca detectarFoca(Pinguino pinguino) {
        for (Jugador j : partida.getJugadores()) {
            if (j instanceof Foca && j.getPosicion() == pinguino.getPosicion()) {
                return (Foca) j;
            }
        }
        return null;
    }

    /**
     * Detecta si hay un Pingüino en la misma casilla que la foca.
     * Devuelve el primer Pingüino encontrado o null.
     */
    public Pinguino detectarPinguinoEnCasillaFoca(Foca foca) {
        for (Jugador j : partida.getJugadores()) {
            if (j instanceof Pinguino && j.getPosicion() == foca.getPosicion()) {
                return (Pinguino) j;
            }
        }
        return null;
    }

    /**
     * Resuelve el soborno: el pingüino gasta 1 pez y la foca queda bloqueada 2 turnos.
     * El pingüino permanece en su casilla actual.
     */
    public void resolverSobornoFoca(Foca foca, Pinguino pinguino) {
        pinguino.gastarItem("Pez", 1);
        foca.setTurnosBloqueada(2);
        System.out.println(pinguino.getNombre() + " ha sobornado a la foca con un pez. ¡La foca queda bloqueada 2 turnos!");
    }

    /**
     * Resuelve el golpe de la foca: golpea al pingüino y lo envía
     * al agujero anterior más cercano (o casilla 0 si no hay).
     * @return la nueva posición del pingüino tras el golpe
     */
    public int resolverGolpeFoca(Foca foca, Pinguino pinguino) {
        foca.golpearJugador(pinguino);
        int nuevaPos = partida.getTablero().buscarAgujeroAnterior(pinguino.getPosicion());
        if (nuevaPos == -1) {
            nuevaPos = 0;
        }
        pinguino.setPosicion(nuevaPos);
        System.out.println(pinguino.getNombre() + " ha sido golpeado por la foca y enviado a la casilla " + nuevaPos);
        return nuevaPos;
    }
}

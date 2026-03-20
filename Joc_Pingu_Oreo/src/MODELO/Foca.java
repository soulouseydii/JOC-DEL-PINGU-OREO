package MODELO;

public class Foca extends Jugador {

    // Cambiamos el boolean por un int para contar los turnos
    private int turnosBloqueada; 

    // CONSTRUCTOR
    public Foca(String nombre, String color, int posicion) {
        super(nombre, color, posicion);
        this.turnosBloqueada = 0; // Al principio no esta bloqueada
    }
    
    // GETTERS Y SETTERS
    public int getTurnosBloqueada() {
        return turnosBloqueada;
    }

    public void setTurnosBloqueada(int turnos) {
        this.turnosBloqueada = turnos;
    }

    // Metodo para que el GestorPartida le reste turnos cada vez que le toque
    public void reducirBloqueo() {
        if (this.turnosBloqueada > 0) {
            this.turnosBloqueada--;
        }
    }

    // Aplastar jugador
    public void aplastarJugador(Pinguino p) {
        int cantidadActual = p.getInv().getlista().size();
        int aQuitar = cantidadActual / 2;
        
        System.out.println("¡La foca aplasta a " + p.getNombre() + " al pasar por su casilla!");
        
        for (int i = 0; i < aQuitar; i++) {
            if (!p.getInv().getlista().isEmpty()) {
                p.getInv().getlista().remove(0);
            }
        }
        System.out.println(p.getNombre() + " ha perdido " + aQuitar + " objetos.");
    }
    
    // Logica de golpearjugador
    public void golpearJugador(Pinguino p) {
        System.out.println("¡La foca golpea a " + p.getNombre() + " con la cola!");
    }
    
    // Este metodo sustituye a esSobornado
    public boolean estaBloqueada() {
        return this.turnosBloqueada > 0;
    }
    
}
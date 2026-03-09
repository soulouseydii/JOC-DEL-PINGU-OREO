package CONTROLADOR;

import MODELO.Foca;
import MODELO.Pinguino;

public class GestorPartida {
	
	/*Metodo interaccionFocaPinguino*/
	 
	  public void interaccionFocaPinguino(Foca foca, Pinguino pinguino, GestorTablero gestorTablero) {
		  
		  //Comprovamos si coinciden en la misma casilla
		  if (foca.getPosicion() == pinguino.getPosicion()) {
			  
			  boolean tienePez = false;
			  int indicePez = -1;
			  
			  //Buscamos si el pinguino tiene un "Pez" en su inventario
			  for (int i = 0; i < pinguino.getInv().getlista().size(); i++) {
				  if (pinguino.getInv().getlista().get(i).getNombre().equalsIgnoreCase("Pez")) {
					  tienePez = true;
					  indicePez = i;
				  }
			  }
			  
			  //Resolucion segun las reglas
			  if (tienePez) {
				  //le damos el pez a la foca
				  pinguino.getInv().getlista().remove(indicePez);
				  foca.setSoborno(true);
				  System.out.println(pinguino.getNombre() + " ha alimentado a la foca con un pez, la foca queda bloqueada 2 turnos!");
			  } else {
				  //Si no tiene pez la foca lo ataca
				  foca.golpearJugador(pinguino);
				  
				  //GestorTablero busca donde lo tiene que enviar
				  int nuevaPosicion = gestorTablero.buscarAgujeroAnterior(pinguino.getPosicion());
				  
				  //Movemos el pinguino a la posicion
				  pinguino.setPosicion(nuevaPosicion);
				  System.out.println(pinguino.getNombre() + " ha sido enviado al hueco de la casilla " + nuevaPosicion);
			  }
			  
		  }
		  
	  }
	  
	 
	
}

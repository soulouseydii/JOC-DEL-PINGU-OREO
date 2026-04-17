package CONTROLADOR;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

public class GestorAnimacionesVistas {

    /**
     * Aplica un efecto en cascada (Fade in + Deslizamiento hacia arriba) 
     * a todos los nodos hijos del contenedor principal que le pases.
     * Ideal para llamadas al arrancar una nueva Scene.
     */
    public static void animarEntradaCascada(Parent container) {
        if (container == null) return;
        animarNodosEnCascada(container.getChildrenUnmodifiable().toArray(new Node[0]), 0);
    }

    /**
     * Anima un conjunto específico de nodos en cascada.
     */
    public static void animarNodosEnCascada(Node[] nodos, int delayInicialMillis) {
        int delayOffset = delayInicialMillis;

        for (Node nodo : nodos) {
            if (!nodo.isVisible() || !nodo.isManaged()) continue;

            // Iniciar ocultos
            nodo.setOpacity(0.0);
            nodo.setTranslateY(25); 

            // Configurar animaciones de aparición
            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), nodo);
            fadeIn.setToValue(1.0);

            TranslateTransition slideUp = new TranslateTransition(Duration.millis(450), nodo);
            slideUp.setToY(0);

            ParallelTransition pt = new ParallelTransition(fadeIn, slideUp);
            pt.setDelay(Duration.millis(delayOffset));

            // Arrancar en paralelo
            pt.play();

            // Siguiente elemento tardará un poco más en empezar
            delayOffset += 50; 
        }
    }

    /**
     * Hace rebotar sutilmente la aparición de un Overlay (Pausa, Opciones...)
     */
    public static void animarAparicionOverlay(Node overlayContainer) {
        if (overlayContainer == null) return;
        
        overlayContainer.setOpacity(0);
        overlayContainer.setScaleX(0.8);
        overlayContainer.setScaleY(0.8);
        overlayContainer.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlayContainer);
        fadeIn.setToValue(1.0);

        javafx.animation.ScaleTransition bounce = new javafx.animation.ScaleTransition(Duration.millis(300), overlayContainer);
        bounce.setToX(1.0);
        bounce.setToY(1.0);
        bounce.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(fadeIn, bounce);
        pt.play();
    }

    /**
     * Anima la desaparición de un overlay (Fade out + Scale down)
     * y ejecuta una acción al terminar (como setVisible(false)).
     */
    public static void animarCierreOverlay(Node overlayContainer, Runnable onFinished) {
        if (overlayContainer == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlayContainer);
        fadeOut.setToValue(0.0);

        javafx.animation.ScaleTransition scaleDown = new javafx.animation.ScaleTransition(Duration.millis(200), overlayContainer);
        scaleDown.setToX(0.8);
        scaleDown.setToY(0.8);

        ParallelTransition pt = new ParallelTransition(fadeOut, scaleDown);
        pt.setOnFinished(e -> {
            overlayContainer.setVisible(false);
            if (onFinished != null) onFinished.run();
        });
        pt.play();
    }
}

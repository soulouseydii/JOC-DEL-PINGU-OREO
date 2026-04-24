package CONTROLADOR;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class GestorAudio {
    private static GestorAudio instance;
    private MediaPlayer mediaPlayerFondo;
    private AudioClip sonidoHover;
    private boolean musicaHabilitada = true;
    private boolean sonidoHabilitado = true;

    private GestorAudio() {
        try {
            java.net.URL urlMusica = getClass().getResource("/musicaYsonidos/musica_principal.mp3");
            if (urlMusica != null) {
                Media media = new Media(urlMusica.toExternalForm());
                mediaPlayerFondo = new MediaPlayer(media);
                mediaPlayerFondo.setCycleCount(MediaPlayer.INDEFINITE); // Para que se reproduzca en bucle
            } else {
                System.out.println("No se encontró el archivo musica_principal.mp3");
            }
            
            java.net.URL urlHover = getClass().getResource("/musicaYsonidos/encima_boton.mp3");
            if (urlHover != null) {
                sonidoHover = new AudioClip(urlHover.toExternalForm());
            } else {
                System.out.println("No se encontró el archivo encima_boton.mp3");
            }
        } catch (Exception e) {
            System.out.println("Error inicializando el GestorAudio: " + e.getMessage());
        }
    }

    public static GestorAudio getInstance() {
        if (instance == null) {
            instance = new GestorAudio();
        }
        return instance;
    }

    public void playMusicaFondo() {
        if (mediaPlayerFondo != null && musicaHabilitada) {
            mediaPlayerFondo.play();
        }
    }

    public void stopMusicaFondo() {
        if (mediaPlayerFondo != null) {
            mediaPlayerFondo.pause();
        }
    }

    public void setMusicaHabilitada(boolean habilitada) {
        this.musicaHabilitada = habilitada;
        if (habilitada) {
            playMusicaFondo();
        } else {
            stopMusicaFondo();
        }
    }

    public boolean isMusicaHabilitada() {
        return musicaHabilitada;
    }

    public void setVolumenMusica(double volumen) {
        if (mediaPlayerFondo != null) {
            mediaPlayerFondo.setVolume(volumen);
        }
    }

    public void setVolumenSonidos(double volumen) {
        if (sonidoHover != null) {
            sonidoHover.setVolume(volumen);
        }
    }

    public void setSonidosHabilitados(boolean habilitado) {
        this.sonidoHabilitado = habilitado;
    }

    public boolean isSonidoHabilitado() {
        return sonidoHabilitado;
    }

    public void playSonidoHover() {
        if (sonidoHover != null && sonidoHabilitado) {
            sonidoHover.play();
        }
    }

    public static void configurarBotonEfectos(Button b) {
        if (!b.getProperties().containsKey("efectosConfigurados")) {
            b.getProperties().put("efectosConfigurados", true);

            b.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                 GestorAudio.getInstance().playSonidoHover();
                 if (b.getScaleX() <= 1.01) { // Para no sobreescribir botones que ya tienen escalas grandes
                     b.setScaleX(1.05);
                     b.setScaleY(1.05);
                 }
                 b.setStyle("-fx-cursor: hand; " + (b.getStyle() != null ? b.getStyle() : ""));
            });
            b.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                 if (b.getScaleX() > 1.01 && b.getScaleX() <= 1.06) {
                     b.setScaleX(1.0);
                     b.setScaleY(1.0);
                 }
            });
        }
    }

    public static void aplicarEfectosATodosLosBotones(Node nodo) {
        if (nodo != null) {
            if (nodo instanceof Button) {
                configurarBotonEfectos((Button) nodo);
            } else if (nodo instanceof Parent) {
                for (Node hijo : ((Parent) nodo).getChildrenUnmodifiable()) {
                    aplicarEfectosATodosLosBotones(hijo);
                }
            }
        }
    }
}

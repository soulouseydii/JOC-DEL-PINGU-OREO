package CONTROLADOR;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GestorAudio {
    private static GestorAudio instance;
    private MediaPlayer mediaPlayerFondo;
    private boolean musicaHabilitada = true;

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
}

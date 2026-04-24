package VISTA;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

/**
 * PantallaCarga — Pantalla de carga con temática polar/pingüino/oreo.
 * 
 * Se muestra antes de cargar el menú principal del juego.
 * Incluye animaciones ligeras: copos de nieve, pingüino con balanceo,
 * barra de progreso animada y texto "Cargando..." pulsante.
 * 
 * No bloquea el hilo principal de JavaFX.
 * Se cierra automáticamente cuando la carga finaliza.
 */
public class PantallaCarga {

    // --- Componentes de la UI ---
    private StackPane raiz;
    private Region barraRelleno;
    private Label textoCargando;
    private Label textoDetalle;
    private Timeline animacionProgreso;
    private Timeline animacionTexto;

    // --- Callback al finalizar ---
    private Runnable alFinalizar;

    /**
     * Constructor principal.
     * @param alFinalizar Runnable que se ejecuta cuando la pantalla de carga termina.
     */
    public PantallaCarga(Runnable alFinalizar) {
        this.alFinalizar = alFinalizar;
    }

    /**
     * Crea y devuelve la escena de la pantalla de carga.
     * @return Scene lista para mostrar en un Stage.
     */
    public Scene crearEscena() {
        raiz = new StackPane();
        raiz.getStyleClass().add("pantalla-carga");

        // Cargar hoja de estilos
        try {
            String css = getClass().getResource("/RESOURCES/PantallaCarga.css").toExternalForm();
            raiz.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar PantallaCarga.css");
        }

        // --- CAPAS DE FONDO ---

        // Aurora boreal (3 capas)
        StackPane capaAurora = new StackPane();
        capaAurora.setMaxHeight(350);
        capaAurora.setAlignment(Pos.TOP_CENTER);
        Region aurora1 = new Region(); aurora1.getStyleClass().add("aurora-carga-1");
        Region aurora2 = new Region(); aurora2.getStyleClass().add("aurora-carga-2");
        Region aurora3 = new Region(); aurora3.getStyleClass().add("aurora-carga-3");
        capaAurora.getChildren().addAll(aurora1, aurora2, aurora3);
        StackPane.setAlignment(capaAurora, Pos.TOP_CENTER);

        // Hielo inferior (2 capas)
        StackPane capaHielo = new StackPane();
        capaHielo.setMaxHeight(250);
        capaHielo.setAlignment(Pos.BOTTOM_CENTER);
        Region hielo1 = new Region(); hielo1.getStyleClass().add("hielo-carga-1");
        Region hielo2 = new Region(); hielo2.getStyleClass().add("hielo-carga-2");
        capaHielo.getChildren().addAll(hielo1, hielo2);
        StackPane.setAlignment(capaHielo, Pos.BOTTOM_CENTER);

        // --- COPOS DE NIEVE ---
        AnchorPane capaCopos = crearCoposDeNieve();
        capaCopos.setMouseTransparent(true);

        // --- CONTENIDO CENTRAL ---
        VBox contenidoCentral = new VBox(12);
        contenidoCentral.setAlignment(Pos.CENTER);
        contenidoCentral.setMaxWidth(600);

        // Imagen del pingüino con oreo
        ImageView imgPinguino = crearImagenPinguino();

        // Título del juego
        VBox tituloBox = crearTitulo();

        // Barra de progreso
        StackPane barraProgreso = crearBarraProgreso();

        // Texto "Cargando..."
        textoCargando = new Label("C A R G A N D O . . .");
        textoCargando.getStyleClass().add("texto-cargando");

        // Texto detalle
        textoDetalle = new Label("P R E P A R A N D O   E L   H I E L O");
        textoDetalle.getStyleClass().add("texto-subtitulo-carga");

        // Separador
        Region separador = new Region();
        separador.setMinHeight(15);

        // Sello Oreo inferior
        VBox selloOreo = crearSelloOreo();

        contenidoCentral.getChildren().addAll(
            imgPinguino,
            tituloBox,
            separador,
            barraProgreso,
            textoCargando,
            textoDetalle,
            new Region() {{ setMinHeight(25); }},
            selloOreo
        );

        // Ensamblar todas las capas
        raiz.getChildren().addAll(capaAurora, capaHielo, capaCopos, contenidoCentral);

        // --- INICIAR ANIMACIONES ---
        iniciarAnimacionTexto();
        iniciarAnimacionProgreso();

        return new Scene(raiz);
    }

    /**
     * Crea la imagen del pingüino (con oreo si existe).
     */
    private ImageView crearImagenPinguino() {
        ImageView imgView = new ImageView();
        try {
            // Intentar cargar pingüino con oreo primero
            Image img = new Image(getClass().getResourceAsStream("/imagenes/pinguino/pinguino_oreo.png"));
            imgView.setImage(img);
        } catch (Exception e) {
            try {
                // Fallback al pingüino normal
                Image img = new Image(getClass().getResourceAsStream("/imagenes/pinguino/pinguino.png"));
                imgView.setImage(img);
            } catch (Exception e2) {
                System.out.println("No se pudo cargar imagen del pingüino para pantalla de carga");
                return imgView;
            }
        }

        imgView.setFitHeight(180);
        imgView.setFitWidth(180);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);

        // Sombra polar al pingüino
        imgView.setEffect(new javafx.scene.effect.DropShadow(25, Color.rgb(94, 207, 255, 0.6)));

        // Animación de balanceo suave (como caminando)
        RotateTransition balanceo = new RotateTransition(Duration.millis(1800), imgView);
        balanceo.setFromAngle(-4);
        balanceo.setToAngle(4);
        balanceo.setCycleCount(Animation.INDEFINITE);
        balanceo.setAutoReverse(true);
        balanceo.setInterpolator(Interpolator.EASE_BOTH);
        balanceo.play();

        // Animación de rebote vertical suave
        TranslateTransition rebote = new TranslateTransition(Duration.millis(1200), imgView);
        rebote.setFromY(-5);
        rebote.setToY(5);
        rebote.setCycleCount(Animation.INDEFINITE);
        rebote.setAutoReverse(true);
        rebote.setInterpolator(Interpolator.EASE_BOTH);
        rebote.play();

        return imgView;
    }

    /**
     * Crea el bloque de título del juego.
     */
    private VBox crearTitulo() {
        VBox tituloBox = new VBox(-2);
        tituloBox.setAlignment(Pos.CENTER);

        Label tituloSuperior = new Label("EL JUEGO DEL");
        tituloSuperior.getStyleClass().add("carga-titulo-superior");

        Label tituloInferior = new Label("P I N G Ü I N O");
        tituloInferior.getStyleClass().add("carga-titulo-inferior");

        Label subtitulo = new Label("O R E O");
        subtitulo.setStyle(
            "-fx-font-family: 'Verdana'; -fx-font-size: 12px; " +
            "-fx-text-fill: #4a9bbf; -fx-letter-spacing: 3px; -fx-padding: 5 0 0 0;"
        );

        tituloBox.getChildren().addAll(tituloSuperior, tituloInferior, subtitulo);
        return tituloBox;
    }

    /**
     * Crea la barra de progreso visual.
     */
    private StackPane crearBarraProgreso() {
        // Contenedor exterior (fondo)
        StackPane barraContenedor = new StackPane();
        barraContenedor.getStyleClass().add("barra-progreso-fondo");
        barraContenedor.setMaxWidth(380);
        barraContenedor.setMinHeight(12);
        barraContenedor.setMaxHeight(12);
        barraContenedor.setAlignment(Pos.CENTER_LEFT);

        // Relleno interior (animado)
        barraRelleno = new Region();
        barraRelleno.getStyleClass().add("barra-progreso-relleno");
        barraRelleno.setMinHeight(10);
        barraRelleno.setMaxHeight(10);
        barraRelleno.setMaxWidth(0);
        barraRelleno.setPrefWidth(0);

        StackPane.setMargin(barraRelleno, new Insets(0, 1, 0, 1));
        barraContenedor.getChildren().add(barraRelleno);

        return barraContenedor;
    }

    /**
     * Crea los copos de nieve animados.
     */
    private AnchorPane crearCoposDeNieve() {
        AnchorPane capa = new AnchorPane();
        Random random = new Random();
        int cantidadCopos = 80; // Muchos más copos para un efecto denso de nevada

        for (int i = 0; i < cantidadCopos; i++) {
            // Crear copo como círculo — tamaño variado para dar profundidad
            double radio = 1.5 + random.nextDouble() * 4.5;
            Circle copo = new Circle(radio);

            // Color blanco-azulado con opacidad más alta para que se note
            double opacidad = 0.25 + random.nextDouble() * 0.55;
            int r = 200 + random.nextInt(55);
            int g = 230 + random.nextInt(25);
            copo.setFill(Color.rgb(r, g, 255, opacidad));
            copo.setMouseTransparent(true);

            capa.getChildren().add(copo);

            // Posición X aleatoria (se enlazará al ancho)
            double xPorcentaje = random.nextDouble();
            copo.translateXProperty().bind(capa.widthProperty().multiply(xPorcentaje));
            copo.setLayoutY(-20);

            // Animación de caída — velocidades variadas
            double duracionSegundos = 4 + random.nextDouble() * 8;
            TranslateTransition caida = new TranslateTransition(
                Duration.seconds(duracionSegundos), copo
            );
            caida.setFromY(0);
            caida.setToY(1500);
            caida.setCycleCount(Animation.INDEFINITE);

            // Dispersar los copos para que no empiecen todos arriba
            caida.playFrom(Duration.seconds(random.nextDouble() * duracionSegundos));
        }

        return capa;
    }

    /**
     * Crea el sello Oreo Studios del footer.
     */
    private VBox crearSelloOreo() {
        VBox sello = new VBox(2);
        sello.setAlignment(Pos.CENTER);
        sello.setOpacity(0.4);

        VBox pila = new VBox(1);
        pila.setAlignment(Pos.CENTER);
        pila.getStyleClass().add("oreo-carga-stack");

        Region galletaSuperior = new Region();
        galletaSuperior.getStyleClass().add("oreo-carga-dark");

        Region crema = new Region();
        crema.getStyleClass().add("oreo-carga-cream");

        Region galletaInferior = new Region();
        galletaInferior.getStyleClass().add("oreo-carga-dark");

        pila.getChildren().addAll(galletaSuperior, crema, galletaInferior);

        Label textoOreo = new Label("O R E O   S T U D I O S");
        textoOreo.getStyleClass().add("oreo-carga-text");

        sello.getChildren().addAll(pila, textoOreo);
        return sello;
    }

    /**
     * Anima el texto "Cargando..." con efecto de pulso.
     */
    private void iniciarAnimacionTexto() {
        // Pulso de opacidad en el texto
        FadeTransition pulso = new FadeTransition(Duration.millis(900), textoCargando);
        pulso.setFromValue(1.0);
        pulso.setToValue(0.4);
        pulso.setCycleCount(Animation.INDEFINITE);
        pulso.setAutoReverse(true);
        pulso.setInterpolator(Interpolator.EASE_BOTH);
        pulso.play();

        // Cambiar texto de detalle periódicamente
        String[] mensajes = {
            "P R E P A R A N D O   E L   H I E L O",
            "A L I M E N T A N D O   P I N G Ü I N O S",
            "R E P A R T I E N D O   O R E O S",
            "C O N G E L A N D O   E L   T A B L E R O",
            "I N V O C A N D O   L A   F O C A",
            "C O N S T R U Y E N D O   I G L Ú S",
            "P U L I E N D O   E L   H I E L O",
            "C A L E N T A N D O   M O T O R E S",
            "D E S P E R T A N D O   A L   O S O",
            "C A S I   L I S T O . . ."
        };

        animacionTexto = new Timeline();
        for (int i = 0; i < mensajes.length; i++) {
            final int indice = i;
            animacionTexto.getKeyFrames().add(
                new KeyFrame(Duration.seconds(i * 2.0), e -> {
                    // Fade out -> cambiar texto -> fade in
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(200), textoDetalle);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(ev -> {
                        textoDetalle.setText(mensajes[indice]);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), textoDetalle);
                        fadeIn.setToValue(1);
                        fadeIn.play();
                    });
                    fadeOut.play();
                })
            );
        }
        animacionTexto.setCycleCount(Animation.INDEFINITE);
        animacionTexto.play();
    }

    /**
     * Anima la barra de progreso de 0% a 100%.
     * Al llegar al 100%, ejecuta la transición de cierre.
     */
    private void iniciarAnimacionProgreso() {
        // Duración total de la barra: ~10 segundos (entre 8-15s según requisitos)
        animacionProgreso = new Timeline();

        // Pasos de progreso suaves
        int pasos = 100;
        double duracionTotalMs = 10000; // 10 segundos

        for (int i = 0; i <= pasos; i++) {
            final double progreso = (double) i / pasos;
            double tiempoMs = (duracionTotalMs / pasos) * i;

            animacionProgreso.getKeyFrames().add(
                new KeyFrame(Duration.millis(tiempoMs), e -> {
                    // Actualizar ancho de la barra (máximo 378 px)
                    double anchoMaximo = 378;
                    barraRelleno.setMaxWidth(anchoMaximo * progreso);
                    barraRelleno.setPrefWidth(anchoMaximo * progreso);
                })
            );
        }

        // Al finalizar: transición de desvanecimiento y callback
        animacionProgreso.setOnFinished(e -> {
            // Pequeña pausa antes de cerrar
            PauseTransition pausa = new PauseTransition(Duration.millis(600));
            pausa.setOnFinished(ev -> finalizarCarga());
            pausa.play();
        });

        animacionProgreso.play();
    }

    /**
     * Realiza la transición de cierre y ejecuta el callback.
     */
    private void finalizarCarga() {
        // Fade out elegante
        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), raiz);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setInterpolator(Interpolator.EASE_IN);
        fadeOut.setOnFinished(e -> {
            // Detener todas las animaciones
            if (animacionProgreso != null) animacionProgreso.stop();
            if (animacionTexto != null) animacionTexto.stop();

            // Ejecutar callback (cargar menú principal)
            if (alFinalizar != null) {
                alFinalizar.run();
            }
        });
        fadeOut.play();
    }
}

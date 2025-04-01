package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.service.MatchService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class TieBreakerController implements Initializable {

    @FXML
    private ImageView cajaA, cajaB, cajaC, imgBalon;

    @FXML
    private Label lblTurno;

    @FXML
    private ImageView imgFondoDeporte;

    @FXML
    private StackPane spTieBreaker;

    private List<Integer> valoresCajas;
    private final Random random = new Random();

    private String equipoA;
    private String equipoB;
    private boolean turnoEquipoA;
    private boolean equipoAAcierto;
    private boolean equipoBAcierto;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarDragAndDrop();
        imgFondoDeporte.fitWidthProperty().bind(spTieBreaker.widthProperty());
        cargarFondoPredeterminado();
    }

    public void initializeTieBreaker(String equipoA, String equipoB, MatchService matchService) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;

        turnoEquipoA = true;
        lblTurno.setText("Turno: " + equipoA);

        // Cargar imagen del balón desde MatchService (se carga dinámicamente desde disco)
        try {
            imgBalon.setImage(matchService.getImagenBalon());
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar imagen del balón desde MatchService");
        }

        // Cargar imagen de las cajas
        try {
            Image imgCaja = new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/caja-empate.png"));
            cajaA.setImage(imgCaja);
            cajaB.setImage(imgCaja);
            cajaC.setImage(imgCaja);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar la imagen de caja-empate.png");
        }

        prepararNuevaRonda(); // Mezcla inicial
    }

    private void configurarDragAndDrop() {
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString("balon");
            content.putImage(imgBalon.getImage()); // para que se vea el balón al arrastrar

            db.setContent(content);
            event.consume();
        });

        configurarCaja(cajaA, 0);
        configurarCaja(cajaB, 1);
        configurarCaja(cajaC, 2);
    }

    private void configurarCaja(ImageView caja, int index) {
        caja.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        caja.setOnDragDropped(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasString()) {
                evaluarIntento(index);
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void prepararNuevaRonda() {
        valoresCajas = Arrays.asList(0, 0, 1);
        Collections.shuffle(valoresCajas);
    }

    private void evaluarIntento(int cajaSeleccionada) {
        boolean acierto = valoresCajas.get(cajaSeleccionada) == 1;
        ImageView caja = switch (cajaSeleccionada) {
            case 0 -> cajaA;
            case 1 -> cajaB;
            case 2 -> cajaC;
            default -> null;
        };

        if (acierto && caja != null) {
            animarAcierto(caja);
        } else if (caja != null) {
            animarFallo(caja);
        }

        if (turnoEquipoA) {
            equipoAAcierto = acierto;
            System.out.println(acierto
                    ? "✅ ACIERTO - " + equipoA
                    : "❌ FALLÓ - " + equipoA);
            turnoEquipoA = false;
            lblTurno.setText("Turno: " + equipoB);
            prepararNuevaRonda(); // se mezcla para que el siguiente intento tenga nuevos valores
        } else {
            equipoBAcierto = acierto;
            System.out.println(acierto
                    ? "✅ ACIERTO - " + equipoB
                    : "❌ FALLÓ - " + equipoB);
            verificarGanador();
        }
    }

    private void verificarGanador() {
        if (equipoAAcierto && !equipoBAcierto) {
            mostrarGanador("🏆 " + equipoA + " gana el desempate");
        } else if (!equipoAAcierto && equipoBAcierto) {
            mostrarGanador("🏆 " + equipoB + " gana el desempate");
        } else {
            mostrarEmpateParcial();
        }
    }

    private void mostrarGanador(String mensaje) {
        lblTurno.setText("Juego Finalizado");
        imgBalon.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("🎯 Desempate Resuelto");
                alert.setHeaderText(mensaje);
                alert.setContentText("¡Felicidades al equipo ganador!");
                alert.showAndWait();
                lblTurno.setText("Fin del juego");
            });
        });
        delay.play();
    }

    private void mostrarEmpateParcial() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("🔁 Empate");
                alert.setHeaderText("Ambos equipos fallaron o acertaron.");
                alert.setContentText("Nueva ronda de desempate.");
                alert.showAndWait();
                turnoEquipoA = true;
                lblTurno.setText("Turno: " + equipoA);
                prepararNuevaRonda();
            });
        });
        delay.play();
    }

    private void animarAcierto(ImageView caja) {
        // Efecto glow
        Glow glow = new Glow(0.8);
        caja.setEffect(glow);

        // Animación de escala (rebote)
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.4), caja);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.setOnFinished(e -> caja.setEffect(null));
        scale.play();
    }

    private void animarFallo(ImageView caja) {
        // Efecto de color rojo temporal
        ColorAdjust red = new ColorAdjust();
        red.setBrightness(-0.3);
        red.setHue(-0.05);
        caja.setEffect(red);

        // Animación de sacudida
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), caja);
        shake.setByX(10);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.setOnFinished(e -> {
            caja.setTranslateX(0);
            caja.setEffect(null);
        });
        shake.play();
    }

    private void cargarFondoPredeterminado() {
        try {
            Image fondo = new Image(getClass().getResourceAsStream(
                    "/cr/ac/una/tareatorneos/resources/FondoGeneral.png")); // fondo genérico
            imgFondoDeporte.setImage(fondo);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar FondoGeneral.png");
        }
    }

}

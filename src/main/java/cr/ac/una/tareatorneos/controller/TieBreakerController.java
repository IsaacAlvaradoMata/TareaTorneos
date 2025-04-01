package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.service.MatchService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.net.URL;
import java.util.*;

public class TieBreakerController implements Initializable {

    @FXML
    private ImageView cajaA, cajaB, cajaC, imgBalon;

    @FXML
    private Label lblTurno;

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
    }

    public void initializeTieBreaker(String equipoA, String equipoB, MatchService matchService) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;

        turnoEquipoA = true;
        lblTurno.setText("Turno: " + equipoA);

        // Cargar imagen dinámicamente desde MatchService
        try {
            imgBalon.setImage(matchService.getImagenBalon());
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar imagen del balón desde MatchService");
        }

        // Cargar imagen en las cajas
        try {
            Image imgCaja = new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/caja-empate.png"));
            cajaA.setImage(imgCaja);
            cajaB.setImage(imgCaja);
            cajaC.setImage(imgCaja);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar la imagen de caja-empate.png");
            e.printStackTrace();
        }

        prepararNuevaRonda();
    }

    private void configurarDragAndDrop() {
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("balon");
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

        equipoAAcierto = false;
        equipoBAcierto = false;
        turnoEquipoA = true;
        lblTurno.setText("Turno: " + equipoA);
    }

    private void evaluarIntento(int cajaSeleccionada) {
        boolean acierto = valoresCajas.get(cajaSeleccionada) == 1;

        if (turnoEquipoA) {
            equipoAAcierto = acierto;
            turnoEquipoA = false;
            lblTurno.setText("Turno: " + equipoB);
        } else {
            equipoBAcierto = acierto;
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
            prepararNuevaRonda();
        }
    }

    private void mostrarGanador(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎯 Desempate Resuelto");
        alert.setHeaderText(mensaje);
        alert.setContentText("¡Felicidades al equipo ganador!");
        alert.showAndWait();

        lblTurno.setText("Juego Finalizado");
        imgBalon.setDisable(true);
    }

    private void mostrarEmpateParcial() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🔁 Empate");
        alert.setHeaderText("Ambos equipos fallaron o acertaron.");
        alert.setContentText("Nueva ronda de desempate.");
        alert.showAndWait();
    }
}

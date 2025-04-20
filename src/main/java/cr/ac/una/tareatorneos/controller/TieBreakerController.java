package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Achievement;
import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.service.BracketMatchService;
import cr.ac.una.tareatorneos.service.MatchService;
import cr.ac.una.tareatorneos.service.TournamentService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class TieBreakerController extends Controller implements Initializable {
    private final Random random = new Random();
    @FXML
    private ImageView cajaA, cajaB, cajaC, imgBalon;
    @FXML
    private ImageView imgInfoTie;
    @FXML
    private Label lblTurno;
    @FXML
    private ImageView imgFondoDeporte;
    @FXML
    private StackPane spTieBreaker;
    @FXML
    private AnchorPane root;
    private List<Integer> valoresCajas;
    private String equipoA;
    private String equipoB;
    private boolean turnoEquipoA;
    private boolean equipoAAcierto;
    private boolean equipoBAcierto;
    private MatchService matchService;
    private BracketMatchService bracketMatchService;
    private BracketMatch bracketMatch;
    private BracketGeneratorController parentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarDragAndDrop();
        imgFondoDeporte.fitWidthProperty().bind(spTieBreaker.widthProperty());
        cargarFondoPredeterminado();

        Tooltip tooltip = new Tooltip("Objetivo:\n\n" +
                "Cada equipo debe intentar acertar la caja correcta arrastrando el balón hacia una de las tres cajas\n" +
                "disponibles. La caja ganadora es seleccionada aleatoriamente en cada ronda.\n\n" +
                "Instrucciones:\n\n" +
                "① Turno por equipo: El equipo en turno arrastra el balón y lo suelta sobre una caja.\n" +
                "② Resultado inmediato:\n" +
                "    → Si acierta: gana el desempate si el otro equipo falla.\n" +
                "    → Si falla: el turno pasa al siguiente equipo.\n" +
                "③ Finaliza cuando uno acierta y el otro falla. Si ambos fallan o aciertan, se repite.");
        tooltip.setStyle("-fx-background-color: rgba(245, 232, 208, 0.9); " +
                "-fx-text-fill: #8b5a2b; " +
                "-fx-padding: 8px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-font-family: \"Trebuchet MS\", sans-serif; " +
                "-fx-border-color: #8b5a2b; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px;");

        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        imgInfoTie.setOnMouseEntered(event -> {
            Platform.runLater(() -> {
                double x = imgInfoTie.localToScene(imgInfoTie.getBoundsInLocal()).getMinX();
                double y = imgInfoTie.localToScene(imgInfoTie.getBoundsInLocal()).getMinY();

                tooltip.show(imgInfoTie,
                        imgInfoTie.getScene().getWindow().getX() + imgInfoTie.getScene().getX() + x - 492,
                        imgInfoTie.getScene().getWindow().getY() + imgInfoTie.getScene().getY() + y + 50);
            });
        });

        imgInfoTie.setOnMouseExited(event -> tooltip.hide());

    }

    public void initializeTieBreaker(String equipoA, String equipoB, MatchService matchService, BracketMatchService bracketMatchService, BracketGeneratorController parentController) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.matchService = matchService;
        this.bracketMatchService = bracketMatchService;
        this.parentController = parentController;
        this.bracketMatch = null;

        turnoEquipoA = true;
        lblTurno.setText("Turno: " + equipoA);

        try {
            imgBalon.setImage(matchService.getImagenBalon());
        } catch (Exception e) {
            System.out.println("No se pudo cargar imagen del balón desde MatchService");
        }

        try {
            Image imgCaja = new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/caja-empate.png"));
            cajaA.setImage(imgCaja);
            cajaB.setImage(imgCaja);
            cajaC.setImage(imgCaja);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen de caja-empate.png");
        }

        prepararNuevaRonda();
    }

    private void configurarDragAndDrop() {
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString("balon");
            content.putImage(imgBalon.getImage());

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
            turnoEquipoA = false;
            lblTurno.setText("Turno: " + equipoB);
            prepararNuevaRonda();
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
        }
    }

    private void mostrarGanador(String mensaje) {
        lblTurno.setText("Juego Finalizado");
        imgBalon.setDisable(true);
        cajaA.setDisable(true);
        cajaB.setDisable(true);
        cajaC.setDisable(true);

        String ganadorDesempate = equipoAAcierto ? equipoA : equipoB;
        List<Achievement> nuevosLogros = matchService.finalizarPartidoConDesempate(ganadorDesempate);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("🎯 Desempate Resuelto");
                alert.setHeaderText(mensaje);
                alert.setContentText("¡Felicidades al equipo ganador!");
                alert.showAndWait();

                if (!nuevosLogros.isEmpty()) {
                    cr.ac.una.tareatorneos.util.AchievementAnimationQueue.setPermitirMostrar(true);
                    for (Achievement logro : nuevosLogros) {
                        cr.ac.una.tareatorneos.util.AchievementAnimationQueue.agregarALaCola(logro);
                    }
                    cr.ac.una.tareatorneos.util.AchievementAnimationQueue.ejecutarLuegoDeMostrarTodos(() -> {
                        cerrarVistaYActualizarBracket();
                    });

                    cr.ac.una.tareatorneos.util.AchievementAnimationQueue.mostrarCuandoPosible(nuevosLogros);
                } else {
                    cerrarVistaYActualizarBracket();
                }
            });
        });
        delay.play();
    }

    private void cerrarVistaYActualizarBracket() {
        try {
            root.getScene().getWindow().hide();
        } catch (Exception e) {
            System.out.println("Error al cerrar TieBreaker: " + e.getMessage());
        }

        bracketMatchService.cargarPartidosDesdeArchivo(matchService.getMatch().getTorneoNombre());
        parentController.setTorneoActual(new TournamentService().getTournamentByName(matchService.getMatch().getTorneoNombre()));
        parentController.cargarBracketDesdePartidos(bracketMatchService.getTodosLosPartidos());
        parentController.actualizarLabelPartidoPendiente();
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
        Glow glow = new Glow(0.8);
        caja.setEffect(glow);

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
        ColorAdjust red = new ColorAdjust();
        red.setBrightness(-0.3);
        red.setHue(-0.05);
        caja.setEffect(red);

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
                    "/cr/ac/una/tareatorneos/resources/FondoGeneral.png"));
            imgFondoDeporte.setImage(fondo);
        } catch (Exception e) {
            System.out.println("No se pudo cargar FondoGeneral.png");
        }
    }

    @Override
    public void initialize() {

    }
}

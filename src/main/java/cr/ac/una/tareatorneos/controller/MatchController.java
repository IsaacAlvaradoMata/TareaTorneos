package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.MatchService;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MatchController extends Controller implements Initializable {

    @FXML
    private MFXButton btnFinalizar;
    @FXML
    private ImageView imgBalon, imgEquipoA, imgEquipoB;
    @FXML
    private Label lblTiempo, lblTorneo, lblEquipoA, lblEquipoB, lblPuntajeA, lblPuntajeB;
    @FXML
    private AnchorPane root;

    private MatchService matchService;
    private Timeline countdown;
    private int tiempoRestante;
    @FXML
    private ImageView imgFondoDeporte;
    @FXML
    private StackPane spMatch;

    private boolean popupMostrado = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarEventosDragAndDrop();
        imgFondoDeporte.fitHeightProperty().bind(spMatch.heightProperty());
        imgFondoDeporte.fitWidthProperty().bind(spMatch.widthProperty());
    }

    @Override
    public void initialize() {
        // requerido por clase Controller base
    }

    private void mostrarPopupFinalizado() {
        int puntajeA = matchService.getPuntajeA();
        int puntajeB = matchService.getPuntajeB();
        String equipoA = lblEquipoA.getText();
        String equipoB = lblEquipoB.getText();

        if (puntajeA == puntajeB) {
            Alert empateAlert = new Alert(Alert.AlertType.INFORMATION);
            empateAlert.setTitle("🏁 Empate Detectado");
            empateAlert.setHeaderText("⚠ El partido terminó en empate");
            empateAlert.setContentText("Se iniciará una ronda de desempate.");
            empateAlert.showAndWait();

            iniciarPantallaDesempate(equipoA, equipoB);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎉 Partido Finalizado");
        alert.setHeaderText("✅ ¡El partido ha concluido!");

        StringBuilder resultado = new StringBuilder();
        resultado.append("📊 *Marcador Final*\n\n");
        resultado.append(String.format("⚽ %-15s | %2d pts\n", equipoA, puntajeA));
        resultado.append(String.format("⚽ %-15s | %2d pts\n", equipoB, puntajeB));

        alert.setContentText(resultado.toString());
        alert.showAndWait();
    }

    private void iniciarPantallaDesempate(String equipoA, String equipoB) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/tareatorneos/view/TieBreakerView.fxml"));
            Parent root = loader.load();

            TieBreakerController controller = loader.getController();
            controller.initializeTieBreaker(equipoA, equipoB, matchService);

            Stage stage = new Stage();
            stage.setTitle("Desempate ⚔️");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta cerrar
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarEventosDragAndDrop() {
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imgBalon.getImage());
            db.setContent(content);
            event.consume();
        });

        imgEquipoA.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoA.setOnDragDropped(event -> {
            if (matchService != null) {
                matchService.sumarPuntoB();
                lblPuntajeB.setText("Puntaje: " + matchService.getPuntajeB());
            }
            event.setDropCompleted(true);
            event.consume();
        });

        imgEquipoB.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoB.setOnDragDropped(event -> {
            if (matchService != null) {
                matchService.sumarPuntoA();
                lblPuntajeA.setText("Puntaje: " + matchService.getPuntajeA());
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void reanudarCuentaRegresiva() {
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));

            if (tiempoRestante <= 0) {
                detenerTiempo();
                if (matchService != null && !matchService.getMatch().isFinalizado()) {
                    matchService.finalizarPartido();
                }
                lblTiempo.setText("Tiempo: 00:00");
                desactivarControles();
                popupMostrado = true;
                javafx.application.Platform.runLater(() -> mostrarPopupFinalizado());
                return;
            }

            tiempoRestante--;

        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    @FXML
    void onActionBtnFinalizar(ActionEvent event) {
        detenerTiempo(); // pausa antes de mostrar confirmación

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🚨 Confirmar Finalización");
        alert.setHeaderText("¿Deseas finalizar este partido?");
        alert.setContentText("⚠ Esta acción detendrá el tiempo y guardará el resultado final.");

        alert.showAndWait().ifPresent(response -> {
            switch (response.getButtonData()) {
                case OK_DONE -> {
                    if (matchService != null && !matchService.getMatch().isFinalizado()) {
                        matchService.finalizarPartido();
                    }
                    lblTiempo.setText("Tiempo: 00:00");
                    desactivarControles();
                    mostrarPopupFinalizado();
                }
                default -> {
                    reanudarCuentaRegresiva();
                }
            }
        });
    }

    private void iniciarCuentaRegresiva(int minutos) {
        tiempoRestante = minutos * 60;

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));

            if (tiempoRestante <= 0) {
                detenerTiempo();
                if (matchService != null && !matchService.getMatch().isFinalizado()) {
                    matchService.finalizarPartido();
                }

                lblTiempo.setText("Tiempo: 00:00");
                desactivarControles();

                boolean esEmpate = matchService.getPuntajeA() == matchService.getPuntajeB();

                if (esEmpate) {
                    javafx.application.Platform.runLater(() -> mostrarPopupEmpate());
                } else {
                    javafx.application.Platform.runLater(() -> mostrarPopupFinalizado());
                }

                return;
            }

            tiempoRestante--;

        }));

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    private void mostrarPopupEmpate() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("⏱️ Partido Empatado");
        alert.setHeaderText("El partido terminó en empate");
        alert.setContentText("Será necesario un desempate para definir al ganador.");

        alert.getButtonTypes().setAll(javafx.scene.control.ButtonType.OK);

        alert.showAndWait().ifPresent(response -> {
            iniciarPantallaDesempate(matchService.getMatch().getEquipoA(), matchService.getMatch().getEquipoB());
        });
    }

    private void detenerTiempo() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void activarControlesDragAndDrop() {
        imgEquipoA.setDisable(false);
        imgEquipoB.setDisable(false);
        imgBalon.setDisable(false);
    }

    private void desactivarControles() {
        btnFinalizar.setDisable(true);
        imgEquipoA.setDisable(true);
        imgEquipoB.setDisable(true);
        imgBalon.setDisable(true);
    }

    public void initializeMatch(String torneoNombre, String nombreEquipoA, String nombreEquipoB) {
        Tournament torneo = new TournamentService().getTournamentByName(torneoNombre);
        if (torneo == null) return;

        Team equipoA = new TeamService().getTeamByName(nombreEquipoA);
        Team equipoB = new TeamService().getTeamByName(nombreEquipoB);
        Sport sport = new SportService().getSportByName(torneo.getDeporte());

        if (equipoA == null || equipoB == null || sport == null) return;

        matchService = new MatchService(torneo, equipoA, equipoB);

        // Establecer texto en etiquetas
        lblTorneo.setText(torneo.getNombre());
        lblTiempo.setText("Tiempo: " + torneo.getTiempoPorPartido() + ":00");

        btnFinalizar.setDisable(false);
        iniciarCuentaRegresiva(torneo.getTiempoPorPartido());
        activarControlesDragAndDrop();

        lblEquipoA.setText(equipoA.getNombre());
        lblPuntajeA.setText("Puntaje: 0");

        lblEquipoB.setText(equipoB.getNombre());
        lblPuntajeB.setText("Puntaje: 0");

        // Imágenes de equipos y balón
        Image imgA = matchService.getImagenEquipoA();
        if (imgA != null) imgEquipoA.setImage(imgA);

        Image imgB = matchService.getImagenEquipoB();
        if (imgB != null) imgEquipoB.setImage(imgB);

        Image balon = matchService.getImagenBalon();
        if (balon != null) imgBalon.setImage(balon);

        // Fondo dinámico según el deporte
        cargarFondoDeporte(sport.getNombre());
    }

    public void mostrarPrimerosDosEquiposDelTorneo(String nombreTorneo) {
        Tournament torneo = new TournamentService().getTournamentByName(nombreTorneo);

        if (torneo == null || torneo.getEquiposParticipantes().size() < 2) {
            System.out.println("❌ Torneo inválido o con menos de 2 equipos.");
            return;
        }

        String equipoA = torneo.getEquiposParticipantes().get(0);
        String equipoB = torneo.getEquiposParticipantes().get(1);

        initializeMatch(nombreTorneo, equipoA, equipoB);
    }

    private final Map<String, String> fondoDeporteMap = Map.ofEntries(
            Map.entry("futbol", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("soccer", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("futbol 7", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("futbol 5", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("baloncesto", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("basketball", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("basket", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("voley", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("voleibol", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("tenis", "/cr/ac/una/tareatorneos/resources/FondoTenis.png"),
            Map.entry("pinpog", "/cr/ac/una/tareatorneos/resources/FondoPinpog.png"),
            Map.entry("tenis de mesa", "/cr/ac/una/tareatorneos/resources/FondoPinpog.png"),
            Map.entry("baseball", "/cr/ac/una/tareatorneos/resources/FondoBaseball.png")
    );

    private void cargarFondoDeporte(String nombreDeporte) {
        if (nombreDeporte == null) return;

        String deporteNormalizado = nombreDeporte.trim().toLowerCase();
        String rutaImagen = fondoDeporteMap.get(deporteNormalizado);

        if (rutaImagen != null) {
            Image fondo = new Image(getClass().getResourceAsStream(rutaImagen));
            imgFondoDeporte.setImage(fondo);
        } else {
            System.out.println("⚠ No hay fondo definido para el deporte: " + nombreDeporte);
        }
    }

}

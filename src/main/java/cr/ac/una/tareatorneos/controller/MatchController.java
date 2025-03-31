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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchController extends Controller implements Initializable {

    @FXML
    private MFXButton btnFinalizar;

    @FXML
    private ImageView imgBalon;

    @FXML
    private ImageView imgEquipoA;

    @FXML
    private ImageView imgEquipoB;

    @FXML
    private Label lblTiempo;

    @FXML
    private Label lblTorneo;

    @FXML
    private Label lblEquipoA;

    @FXML
    private Label lblEquipoB;

    @FXML
    private Label lblPuntajeA;

    @FXML
    private Label lblPuntajeB;

    @FXML
    private AnchorPane root;

    private MatchService matchService;
    private Timeline countdown;
    private int tiempoRestante;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarEventosDragAndDrop();
    }

    @Override
    public void initialize() {
        // requerido por clase Controller base
    }

    private void mostrarPopupFinalizado() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partido Finalizado");
        alert.setHeaderText("✔ El partido ha finalizado");

        String equipoA = lblEquipoA.getText();
        String equipoB = lblEquipoB.getText();
        int puntajeA = matchService.getPuntajeA();
        int puntajeB = matchService.getPuntajeB();

        StringBuilder resultado = new StringBuilder();
        resultado.append("📊 Resultado Final\n\n");
        resultado.append(String.format("🏅 %-15s | %2d pts\n", equipoA, puntajeA));
        resultado.append(String.format("🏅 %-15s | %2d pts\n", equipoB, puntajeB));

        alert.setContentText(resultado.toString());
        alert.showAndWait();
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
            if (tiempoRestante <= 0) {
                detenerTiempo();
                if (matchService != null && !matchService.getMatch().isFinalizado()) {
                    matchService.finalizarPartido();
                    mostrarPopupFinalizado();
                }
                lblTiempo.setText("Tiempo: 00:00");
                desactivarControles();
                return;
            }

            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));
            tiempoRestante--;
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    @FXML
    void onActionBtnFinalizar(ActionEvent event) {
        detenerTiempo(); // pausa antes de mostrar confirmación

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Finalización");
        alert.setHeaderText("¿Estás seguro de que deseas finalizar el partido?");
        alert.setContentText("Esta acción no se puede deshacer.");

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
                    // Usuario canceló, reanudar desde donde se pausó ⏸️
                    reanudarCuentaRegresiva();
                }
            }
        });
    }

    private void iniciarCuentaRegresiva(int minutos) {
        tiempoRestante = minutos * 60;

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (tiempoRestante <= 0) {
                detenerTiempo();
                if (matchService != null && !matchService.getMatch().isFinalizado()) {
                    matchService.finalizarPartido();
                    System.out.println("🛎️ Tiempo finalizado automáticamente.");
                }
                desactivarControles();
                mostrarPopupFinalizado();
                return;
            }

            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));
            tiempoRestante--;
        }));

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
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

        lblTorneo.setText(torneo.getNombre());
        lblTiempo.setText("Tiempo: " + torneo.getTiempoPorPartido() + ":00");

        btnFinalizar.setDisable(false); // Por si venimos de otra vista

        iniciarCuentaRegresiva(torneo.getTiempoPorPartido());
        activarControlesDragAndDrop();
        lblEquipoA.setText(equipoA.getNombre());
        lblPuntajeA.setText("Puntaje: 0");

        lblEquipoB.setText(equipoB.getNombre());
        lblPuntajeB.setText("Puntaje: 0");

        Image imgA = matchService.getImagenEquipoA();
        if (imgA != null) imgEquipoA.setImage(imgA);

        Image imgB = matchService.getImagenEquipoB();
        if (imgB != null) imgEquipoB.setImage(imgB);

        Image balon = matchService.getImagenBalon();
        if (balon != null) imgBalon.setImage(balon);
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
}

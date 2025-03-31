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
        // Ya no se llama aquí ningún torneo de prueba por defecto
    }

    @Override
    public void initialize() {
        // Método vacío por la clase base
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

    @FXML
    void onActionBtnFinalizar(ActionEvent event) {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }

        if (matchService != null && !matchService.getMatch().isFinalizado()) {
            matchService.finalizarPartido();
        }

        btnFinalizar.setDisable(true);
        imgEquipoA.setDisable(true);
        imgEquipoB.setDisable(true);
        imgBalon.setDisable(true);

        System.out.println("🛑 Partido finalizado manualmente.");
    }

    private void iniciarCuentaRegresiva(int minutos) {
        tiempoRestante = minutos * 60;

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));
            tiempoRestante--;

            if (tiempoRestante < 0) {
                countdown.stop();
                matchService.finalizarPartido();
                btnFinalizar.setDisable(true);
                System.out.println("🛎️ Tiempo finalizado automáticamente.");
            }
        }));

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
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
        iniciarCuentaRegresiva(torneo.getTiempoPorPartido());

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

    // ✅ Llamado desde ActiveTournamentsController
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

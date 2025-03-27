package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.MatchService;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import io.github.palexdev.materialfx.controls.MFXButton;
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
    private int tiempoRestante; // en segundos

    @FXML
    void onActionBtnFinalizar(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Drag start (balón)
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imgBalon.getImage());
            db.setContent(content);
            event.consume();
        });

        // Equipo A acepta drop (si le hacen gol, punto para B)
        imgEquipoA.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoA.setOnDragDropped(event -> {
            if (matchService != null) {
                matchService.sumarPuntoB(); // ⚽ Real Madrid fue anotado => gol para Saprissa
                lblPuntajeB.setText("Puntaje: " + matchService.getPuntajeB());
            }
            event.setDropCompleted(true);
            event.consume();
        });

        // Equipo B acepta drop (si le hacen gol, punto para A)
        imgEquipoB.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoB.setOnDragDropped(event -> {
            if (matchService != null) {
                matchService.sumarPuntoA(); // ⚽ Saprissa fue anotado => gol para Real Madrid
                lblPuntajeA.setText("Puntaje: " + matchService.getPuntajeA());
            }
            event.setDropCompleted(true);
            event.consume();
        });
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

        lblEquipoA.setText(equipoA.getNombre());
        lblPuntajeA.setText("Puntaje: 0");

        lblEquipoB.setText(equipoB.getNombre());
        lblPuntajeB.setText("Puntaje: 0");

        // Cargar imágenes
        Image imgA = matchService.getImagenEquipoA();
        if (imgA != null) imgEquipoA.setImage(imgA);

        Image imgB = matchService.getImagenEquipoB();
        if (imgB != null) imgEquipoB.setImage(imgB);

        Image balon = matchService.getImagenBalon();
        if (balon != null) imgBalon.setImage(balon);
    }

    public void pruebaSimulacion() {
        Tournament torneo = new TournamentService().getTournamentByName("Torneo de Prueba");

        if (torneo == null || torneo.getEquiposParticipantes().size() < 2) {
            System.out.println("❌ Torneo no encontrado o no tiene suficientes equipos.");
            return;
        }

        String equipoA = torneo.getEquiposParticipantes().get(0);
        String equipoB = torneo.getEquiposParticipantes().get(1);

        initializeMatch(torneo.getNombre(), equipoA, equipoB);
    }

    @Override
    public void initialize() {
        pruebaSimulacion();
    
    }

}

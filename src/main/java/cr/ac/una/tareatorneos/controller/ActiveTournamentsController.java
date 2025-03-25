package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ActiveTournamentsController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTorneosActivosTitulo;
    @FXML
    private ImageView imgLupaAmarilla;
    @FXML
    private MFXTableView<Tournament> tbvTorneosActivos;
    @FXML
    private Separator sprActiveTournaments;
    @FXML
    private Label lblNombreTorneo;
    @FXML
    private Label lblDeporteTorneo;
    @FXML
    private Label lblTiempoTorneo;
    @FXML
    private Label lblCantidadEquiposTorneo;
    // ListView para los equipos ya agregados al torneo
    @FXML
    private MFXListView<String> listviewEquiposSeleccionadosTorneo;
    // CheckListView para los equipos disponibles (del mismo deporte y no incluidos aún)
    @FXML
    private MFXCheckListView<String> chklistviewEquiposDisponibles;
    @FXML
    private MFXButton btnReanudarTorneo;
    @FXML
    private MFXButton btnAgregarEquipos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateTableView();
        loadTournaments();
        // Listener para actualizar información y listas cuando se seleccione un torneo
        tbvTorneosActivos.getSelectionModel().selectionProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                handleTableClickTorneosActivos(null);
            }
        });
    }

    @FXML
    private void handleTableClickTorneosActivos(MouseEvent event) {
        List<Tournament> selected = tbvTorneosActivos.getSelectionModel().getSelectedValues();
        if (!selected.isEmpty()) {
            Tournament selectedTournament = selected.get(0);
            // Actualiza los labels con la información del torneo
            lblNombreTorneo.setText(selectedTournament.getNombre());
            lblDeporteTorneo.setText(selectedTournament.getDeporte());
            lblTiempoTorneo.setText(String.valueOf(selectedTournament.getTiempoPorPartido()));
            lblCantidadEquiposTorneo.setText(String.valueOf(selectedTournament.getCantidadEquipos()));
            // Carga el ListView con los equipos ya incluidos (tomados del JSON del torneo)
            listviewEquiposSeleccionadosTorneo.setItems(
                    FXCollections.observableArrayList(selectedTournament.getEquiposParticipantes())
            );
            // Carga el CheckListView con los equipos disponibles: los equipos del mismo deporte que aún NO están en el torneo
            loadAvailableTeams(selectedTournament);
        }
    }

    private void loadAvailableTeams(Tournament tournament) {
        TeamService teamService = new TeamService();
        List<Team> allTeams = teamService.getAllTeams();
        List<String> availableTeamNames = allTeams.stream()
                .filter(team -> team.getDeporte().equalsIgnoreCase(tournament.getDeporte()))
                .map(Team::getNombre)
                .filter(name -> !tournament.getEquiposParticipantes().contains(name))
                .toList();
        chklistviewEquiposDisponibles.setItems(FXCollections.observableArrayList(availableTeamNames));
    }

    @FXML
    private void OnActionBtnReanudarTorneo(ActionEvent event) {
        FlowController.getInstance().goView("MatchView");
    }

    @FXML
    void OnActionBtnAgregarEquipos(ActionEvent event) {
        List<Tournament> selectedTournaments = tbvTorneosActivos.getSelectionModel().getSelectedValues();
        if (selectedTournaments.isEmpty()) {
            // Opción: mostrar mensaje "No tournament selected"
            return;
        }
        Tournament selectedTournament = selectedTournaments.get(0);
        // Obtén los equipos seleccionados del CheckListView
        List<String> teamsToAdd = chklistviewEquiposDisponibles.getSelectionModel().getSelectedValues();
        if (teamsToAdd.isEmpty()) {
            // Opción: mostrar mensaje "No teams selected to add"
            return;
        }
        // Agrega los nuevos equipos a la lista de equipos participantes (sin duplicados)
        List<String> currentTeams = selectedTournament.getEquiposParticipantes();
        if (currentTeams == null) {
            currentTeams = new ArrayList<>();
            selectedTournament.setEquiposParticipantes(currentTeams);
        }
        boolean added = false;
        for (String team : teamsToAdd) {
            if (!currentTeams.contains(team)) {
                currentTeams.add(team);
                added = true;
            }
        }
        if (!added) {
            // Opción: mostrar mensaje "No new teams to add"
            return;
        }
        // Actualiza el torneo en el JSON
        TournamentService tournamentService = new TournamentService();
        boolean success = tournamentService.updateTournament(selectedTournament.getNombre(), selectedTournament);
        if (success) {
            // Refresca los ListViews:
            // - Actualiza el ListView de equipos ya agregados
            listviewEquiposSeleccionadosTorneo.setItems(
                    FXCollections.observableArrayList(selectedTournament.getEquiposParticipantes())
            );
            // - Actualiza el CheckListView de equipos disponibles
            loadAvailableTeams(selectedTournament);
            // Opción: mostrar mensaje de éxito
        } else {
            // Opción: mostrar mensaje de error
        }
    }

    private void populateTableView() {
        MFXTableColumn<Tournament> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setMinWidth(150);
        colNombre.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getNombre));

        MFXTableColumn<Tournament> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getDeporte));

        MFXTableColumn<Tournament> colEstado = new MFXTableColumn<>("Estado");
        colEstado.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getEstado));

        tbvTorneosActivos.getTableColumns().clear();
        tbvTorneosActivos.getTableColumns().addAll(Arrays.asList(colNombre, colDeporte, colEstado));
    }

    private void loadTournaments() {
        TournamentService tournamentService = new TournamentService();
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        // Filtra solo los torneos activos (estado "iniciado")
        List<Tournament> activeTournaments = tournaments.stream()
                .filter(t -> "iniciado".equalsIgnoreCase(t.getEstado()))
                .toList();
        tbvTorneosActivos.getItems().clear();
        tbvTorneosActivos.getItems().addAll(activeTournaments);
    }

    @Override
    public void initialize() {
        // Método vacío para cumplir con la clase abstracta.
    }
}

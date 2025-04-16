package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TournamentService;
import cr.ac.una.tareatorneos.util.FlowController;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
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
    @FXML
    private MFXListView<String> listviewEquiposSeleccionadosTorneo;
    @FXML
    private MFXButton btnVerTorneo;
    @FXML
    private MFXFilterComboBox<String> cmbTorneosActivos;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateTableView();
        loadSportsToComboBox();
        filterTournamentsBySport("Todos");

        tbvTorneosActivos.getSelectionModel().selectionProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                handleTableClickTorneosActivos(null);
            }
        });

        cmbTorneosActivos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterTournamentsBySport(newVal.toString());
            }
        });

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.focusedProperty().addListener((obsF, wasFocused, isNowFocused) -> {
                    if (isNowFocused) {
                        filterTournamentsBySport("Todos");
                    }
                });
            }
        });
    }

    @FXML
    private void handleTableClickTorneosActivos(MouseEvent event) {
        List<Tournament> selected = tbvTorneosActivos.getSelectionModel().getSelectedValues();
        if (!selected.isEmpty()) {
            TournamentService service = new TournamentService();
            Tournament selectedTournament = service.getTournamentByName(selected.get(0).getNombre());

            if (selectedTournament == null) return;

            lblNombreTorneo.setText(selectedTournament.getNombre());
            lblDeporteTorneo.setText(selectedTournament.getDeporte());
            lblTiempoTorneo.setText(String.valueOf(selectedTournament.getTiempoPorPartido()));
            lblCantidadEquiposTorneo.setText(String.valueOf(selectedTournament.getCantidadEquipos()));

            listviewEquiposSeleccionadosTorneo.setItems(
                    FXCollections.observableArrayList(selectedTournament.getEquiposParticipantes())
            );
        }
    }

    @FXML
    private void OnActionBtnReanudarTorneo(ActionEvent event) {
        List<Tournament> selected = tbvTorneosActivos.getSelectionModel().getSelectedValues();

        if (selected == null || selected.isEmpty()) {
            System.out.println("❌ No se ha seleccionado ningún torneo.");
            return;
        }

        String nombreTorneo = selected.get(0).getNombre();
        TournamentService service = new TournamentService();
        Tournament torneoSeleccionado = service.getTournamentByName(nombreTorneo);

        if (torneoSeleccionado == null) {
            System.out.println("⚠️ No se encontró el torneo en el JSON.");
            return;
        }


        if (!"por comenzar".equalsIgnoreCase(torneoSeleccionado.getEstado()) &&
                !"iniciado".equalsIgnoreCase(torneoSeleccionado.getEstado())) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Estado inválido", getStage(),
                    "Solo se pueden seleccionar torneos que estén 'Por comenzar' o 'Iniciados'.");
            return;
        }

        if (torneoSeleccionado.getEquiposParticipantes() == null || torneoSeleccionado.getEquiposParticipantes().size() < 2) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Faltan equipos", getStage(),
                    "Este torneo aún no tiene suficientes equipos para iniciar.");
            return;
        }

        BracketGeneratorController controller = (BracketGeneratorController)
                FlowController.getInstance().getController("BracketGeneratorView");

        if (controller != null) {
            controller.setModoVisualizacion(false);
            controller.inicializarBracketDesdeTorneo(torneoSeleccionado);
            FlowController.getInstance().goView("BracketGeneratorView");

            TournamentService refreshedService = new TournamentService();
            Tournament torneoActualizado = refreshedService.getTournamentByName(nombreTorneo);

            filterTournamentsBySport("Todos");

            List<Tournament> actualizados = tbvTorneosActivos.getItems();
            for (int i = 0; i < actualizados.size(); i++) {
                if (actualizados.get(i).getNombre().equalsIgnoreCase(nombreTorneo)) {
                    tbvTorneosActivos.getSelectionModel().clearSelection();
                    tbvTorneosActivos.getSelectionModel().selectIndex(i);
                    break;
                }
            }
        } else {
            System.out.println("⚠️ No se pudo cargar el controlador de BracketGeneratorView.");
        }
    }


    private void populateTableView() {
        MFXTableColumn<Tournament> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setMinWidth(150);
        colNombre.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getNombre));

        MFXTableColumn<Tournament> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setMinWidth(150);
        colDeporte.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getDeporte));

        MFXTableColumn<Tournament> colEstado = new MFXTableColumn<>("Estado");
        colEstado.setMinWidth(150);
        colEstado.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getEstado));

        tbvTorneosActivos.getTableColumns().clear();
        tbvTorneosActivos.getTableColumns().addAll(Arrays.asList(colNombre, colDeporte, colEstado));
    }

    private void loadTournaments() {
        TournamentService tournamentService = new TournamentService();
        List<Tournament> tournaments = tournamentService.getAllTournaments();

        List<Tournament> activeTournaments = tournaments.stream()
                .filter(t -> "iniciado".equalsIgnoreCase(t.getEstado()))
                .toList();
        tbvTorneosActivos.getItems().clear();
        tbvTorneosActivos.getItems().addAll(activeTournaments);
    }

    private void loadSportsToComboBox() {
        SportService sportService = new SportService();
        List<String> nombresDeportes = sportService.getAllSports().stream()
                .map(s -> s.getNombre())
                .toList();


        List<String> opciones = FXCollections.observableArrayList();
        opciones.add("Todos");
        opciones.addAll(nombresDeportes);

        cmbTorneosActivos.setItems(FXCollections.observableArrayList(opciones));
        cmbTorneosActivos.selectFirst();
    }

    private void filterTournamentsBySport(String deporte) {
        TournamentService tournamentService = new TournamentService();
        List<Tournament> tournaments = tournamentService.getAllTournaments();

        List<Tournament> filtered;

        if (deporte.equalsIgnoreCase("Todos")) {
            filtered = tournaments.stream()
                    .toList();
        } else {
            filtered = tournaments.stream()
                    .filter(t -> t.getDeporte().equalsIgnoreCase(deporte))
                    .toList();
        }

        tbvTorneosActivos.getItems().clear();
        tbvTorneosActivos.getItems().addAll(filtered);

        javafx.application.Platform.runLater(() -> {
            tbvTorneosActivos.requestFocus();
        });
    }

    public void actualizarComboBoxActiveTournaments() {
        loadSportsToComboBox();
    }

    @FXML
    private void onActionBtnVerTorneo(ActionEvent event) {
        Tournament torneoSeleccionado = tbvTorneosActivos.getSelectionModel().getSelectedValue();

        if (torneoSeleccionado == null) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Torneo no seleccionado", getStage(), "Selecciona un torneo primero.");
            return;
        }

        if (!"Finalizado".equalsIgnoreCase(torneoSeleccionado.getEstado())) {
            new Mensaje().showModal(Alert.AlertType.INFORMATION, "Estado inválido", getStage(), "Este botón solo permite visualizar torneos finalizados.");
            return;
        }

        BracketGeneratorController controller = (BracketGeneratorController)
                FlowController.getInstance().getController("BracketGeneratorView");

        controller.setModoVisualizacion(true);
        controller.inicializarBracketDesdeTorneo(torneoSeleccionado);

        FlowController.getInstance().goViewInWindowModal("BracketGeneratorView", getStage(), false);
    }

    @Override
    public void initialize() {
    }
}

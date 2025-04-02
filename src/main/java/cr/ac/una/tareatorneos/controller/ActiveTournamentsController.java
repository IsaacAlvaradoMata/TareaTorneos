package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
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
    private MFXButton btnReanudarTorneo;
    @FXML
    private MFXFilterComboBox<String> cmbTorneosActivos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateTableView();
        loadSportsToComboBox();
        filterTournamentsBySport("Todos"); // 👈 carga todos los torneos activos desde el inicio

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
    }

    @FXML
    private void handleTableClickTorneosActivos(MouseEvent event) {
        List<Tournament> selected = tbvTorneosActivos.getSelectionModel().getSelectedValues();
        if (!selected.isEmpty()) {
            TournamentService service = new TournamentService();
            Tournament selectedTournament = service.getTournamentByName(selected.get(0).getNombre()); // 🔁 Relee desde JSON actualizado

            if (selectedTournament == null) return;

            // 🟨 Actualiza los labels
            lblNombreTorneo.setText(selectedTournament.getNombre());
            lblDeporteTorneo.setText(selectedTournament.getDeporte());
            lblTiempoTorneo.setText(String.valueOf(selectedTournament.getTiempoPorPartido()));
            lblCantidadEquiposTorneo.setText(String.valueOf(selectedTournament.getCantidadEquipos()));

            // ✅ Refresca lista de equipos actuales en el torneo
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

        Tournament torneoSeleccionado = selected.get(0);

        // Obtener el controlador de la vista de brackets
        BracketGeneratorController controller = (BracketGeneratorController)
                FlowController.getInstance().getController("BracketGeneratorView");

        if (controller != null) {
            controller.inicializarBracketDesdeTorneo(torneoSeleccionado);
            FlowController.getInstance().goView("BracketGeneratorView");
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
        // Filtra solo los torneos activos (estado "iniciado")
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

        // Agrega la opción "Todos" al principio
        List<String> opciones = FXCollections.observableArrayList();
        opciones.add("Todos");
        opciones.addAll(nombresDeportes);

        cmbTorneosActivos.setItems(FXCollections.observableArrayList(opciones));
        cmbTorneosActivos.selectFirst(); // Auto-selecciona "Todos" al inicio
    }

    private void filterTournamentsBySport(String deporte) {
        TournamentService tournamentService = new TournamentService();
        List<Tournament> tournaments = tournamentService.getAllTournaments();

        List<Tournament> filtered;

        if (deporte.equalsIgnoreCase("Todos")) {
            // Mostrar todos los torneos activos (iniciados o por comenzar)
            filtered = tournaments.stream()
                    .filter(t -> {
                        String estado = t.getEstado().toLowerCase();
                        return estado.equals("iniciado") || estado.equals("por comenzar");
                    })
                    .toList();
        } else {
            // Filtrar por deporte
            filtered = tournaments.stream()
                    .filter(t -> t.getDeporte().equalsIgnoreCase(deporte))
                    .filter(t -> {
                        String estado = t.getEstado().toLowerCase();
                        return estado.equals("iniciado") || estado.equals("por comenzar");
                    })
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

    @Override
    public void initialize() {
        // Método vacío para cumplir con la clase abstracta.
    }
}


package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class RankingsController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblRankingsTitulo;
    @FXML
    private ImageView imgRankingTrophyIcon;
    @FXML
    private ImageView imgFlechaArriba;
    @FXML
    private Separator sprRankings;
    private MFXTableView<Team> tbvEstadisticasGenerales;
    @FXML
    private MFXTableView<Team> tbvRankingEquipos;
    @FXML
    private MFXFilterComboBox<String> cmbRankings;
    @FXML
    private Label lblPartidosTotales;
    @FXML
    private Label lblPartidosGanados;
    @FXML
    private Label lblPartidosPerdidos;
    @FXML
    private Label lblPartidosEmpatados;
    @FXML
    private Label lblTorneosTotales;
    @FXML
    private Label lblTorneosGanados;
    @FXML
    private Label lblTorneosPerdidos;
    @FXML
    private Label lblAnotaciones;
    @FXML
    private Label lblAnotacionesContra;
    @FXML
    private MFXButton btnEstadisticasAvanzadas;
    @FXML
    private TableView<?> tbvStatsTorneos;
    @FXML
    private MFXButton btnEstadisticasGenerales;
    
    private ObservableList<Team> teamsData = FXCollections.observableArrayList();
    private TeamService teamService;
    private SportService sportService = new SportService();
    private final ObservableList<Team> equiposFiltrados = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        teamService = new TeamService();
        populateComboBoxRankings();
        populateTableViewTeams();
        loadAllTeams();
        cmbRankings.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarYOrdenarEquipos(newVal);
            }
        });

        tbvRankingEquipos.getSelectionModel().selectionProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Team equipoSeleccionado = newVal.get(0);
                mostrarEstadisticasEquipo(equipoSeleccionado); // 👈 actualiza labels
            }
        });
    }

    @Override
    public void initialize() {

    }

    @FXML
    private void handleTableClickRankingEquipos(MouseEvent event) {
        List<Team> seleccion = tbvRankingEquipos.getSelectionModel().getSelectedValues();
        if (!seleccion.isEmpty()) {
            Team seleccionado = seleccion.get(0);
            mostrarEstadisticasEquipo(seleccionado);
        }
    }



    @FXML
    private void onActionBtnEstadisticasAvanzadas(ActionEvent event) {
    }

    @FXML
    private void onActionBtnEstadisticasGenerales(ActionEvent event) {
    }



    private void mostrarEstadisticasEquipo(Team team) {
        limpiarLabelsEstadisticas(); // 👈 primero limpiamos

        if (team == null || team.getEstadisticas() == null) return;

        lblPartidosTotales.setText(String.valueOf(team.getEstadisticas().getPartidosTotales()));
        lblPartidosGanados.setText(String.valueOf(team.getEstadisticas().getPartidosGanados()));
        lblPartidosPerdidos.setText(String.valueOf(team.getEstadisticas().getPartidosPerdidos()));
        lblPartidosEmpatados.setText(String.valueOf(team.getEstadisticas().getPartidosEmpatados()));

        lblTorneosTotales.setText(String.valueOf(team.getEstadisticas().getTorneosTotales()));
        lblTorneosGanados.setText(String.valueOf(team.getEstadisticas().getTorneosGanados()));
        lblTorneosPerdidos.setText(String.valueOf(team.getEstadisticas().getTorneosPerdidos()));

        lblAnotaciones.setText(String.valueOf(team.getEstadisticas().getAnotaciones()));
        lblAnotacionesContra.setText(String.valueOf(team.getEstadisticas().getAnotacionesEnContra()));
    }

    private void limpiarLabelsEstadisticas() {
        lblPartidosTotales.setText("0");
        lblPartidosGanados.setText("0");
        lblPartidosPerdidos.setText("0");
        lblPartidosEmpatados.setText("0");

        lblTorneosTotales.setText("0");
        lblTorneosGanados.setText("0");
        lblTorneosPerdidos.setText("0");

        lblAnotaciones.setText("0");
        lblAnotacionesContra.setText("0");
    }

    private void populateTableViewTeams() {
        MFXTableColumn<Team> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setMinWidth(150);
        colNombre.setRowCellFactory(team -> {
            MFXTableRowCell<Team, String> cell = new MFXTableRowCell<>(Team::getNombre);
            cell.setAlignment(Pos.CENTER); // CENTRADO
            return cell;
        });

        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setMinWidth(110);
        colDeporte.setRowCellFactory(team -> {
            MFXTableRowCell<Team, String> cell = new MFXTableRowCell<>(Team::getDeporte);
            cell.setAlignment(Pos.CENTER); // CENTRADO
            return cell;
        });

        MFXTableColumn<Team> colPuntosGlobales = new MFXTableColumn<>("Puntos Globales");
        colPuntosGlobales.setMinWidth(110);
        colPuntosGlobales.setRowCellFactory(team -> {
            MFXTableRowCell<Team, String> cell = new MFXTableRowCell<>(
                    t -> String.valueOf(t.getEstadisticas().getPuntosGlobales())
            );
            cell.setAlignment(Pos.CENTER); // CENTRADO
            return cell;
        });

        tbvRankingEquipos.getTableColumns().clear();
        tbvRankingEquipos.getTableColumns().addAll(Arrays.asList(
                colNombre, colDeporte, colPuntosGlobales
        ));
    }



    private void loadAllTeams() {
        equiposFiltrados.clear();

        List<Team> equipos = teamService.getAllTeams().stream()
                .sorted((t1, t2) -> Integer.compare(
                        t2.getEstadisticas().getPuntosGlobales(),
                        t1.getEstadisticas().getPuntosGlobales()
                ))
                .toList();

        equiposFiltrados.addAll(equipos);

        tbvRankingEquipos.getSelectionModel().clearSelection();
        tbvRankingEquipos.setItems(null);
        tbvRankingEquipos.setItems(equiposFiltrados);

        Platform.runLater(() -> {
            tbvRankingEquipos.getSelectionModel().clearSelection();
            tbvRankingEquipos.requestFocus();
        });
    }


    private void populateComboBoxRankings() {
        ObservableList<String> sports = FXCollections.observableArrayList();
        sports.add("Todos"); // 👈 Agregar opción especial
        sportService.getAllSports().forEach(s -> sports.add(s.getNombre()));
        cmbRankings.setItems(sports);
        cmbRankings.selectFirst(); // Selecciona "Todos" al cargar
    }

    public void actualizarComboBoxRankings() {
        populateComboBoxRankings();
    }

    private void filtrarYOrdenarEquipos(String deporteSeleccionado) {
        equiposFiltrados.clear();

        List<Team> equipos = teamService.getAllTeams().stream()
                .filter(team -> deporteSeleccionado.equals("Todos") ||
                        team.getDeporte().equalsIgnoreCase(deporteSeleccionado))
                .sorted((t1, t2) -> Integer.compare(
                        t2.getEstadisticas().getPuntosGlobales(),
                        t1.getEstadisticas().getPuntosGlobales()
                ))
                .toList();

        equiposFiltrados.addAll(equipos);

        tbvRankingEquipos.getSelectionModel().clearSelection();
        tbvRankingEquipos.setItems(null);
        tbvRankingEquipos.setItems(equiposFiltrados);

        Platform.runLater(() -> {
            tbvRankingEquipos.getSelectionModel().clearSelection();
            tbvRankingEquipos.requestFocus();
        });
    }



}

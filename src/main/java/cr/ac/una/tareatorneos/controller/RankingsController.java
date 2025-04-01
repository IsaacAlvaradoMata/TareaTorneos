
package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML
    private MFXTableView<Team> tbvEstadisticasGenerales;
    @FXML
    private MFXTableView<Team> tbvEstadisticasAvanzadas;
    @FXML
    private MFXTableView<Team> tbvRankingEquipos;
    @FXML
    private MFXFilterComboBox<String> cmbRankings;


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
        populateTableViewGeneralStats();
        loadAllTeams();
        cmbRankings.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarYOrdenarEquipos(newVal);
            }
        });
    }

    @FXML
    private void handleTableClickRankingEquipos(MouseEvent event) {
    }

    @FXML
    private void handleTableClickEstadisticasGenerales(MouseEvent event) {
    }

    @FXML
    private void handleTableClickEstadisticasAvanzadas(MouseEvent event) {
    }

    @Override
    public void initialize() {

    }

    private void populateTableViewTeams() {
        MFXTableColumn<Team> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setMinWidth(150);
        colNombre.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setMinWidth(150);
        colDeporte.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getDeporte));

        MFXTableColumn<Team> colPuntosGlobales = new MFXTableColumn<>("Puntos Globales");
        colPuntosGlobales.setMinWidth(150);
        colPuntosGlobales.setRowCellFactory(t -> new MFXTableRowCell<>(team ->
                String.valueOf(team.getEstadisticas().getPuntosGlobales()) // Mostrar como texto
        ));

        tbvRankingEquipos.getTableColumns().clear();
        tbvRankingEquipos.getTableColumns().addAll(Arrays.asList(colNombre, colDeporte, colPuntosGlobales));
    }


    private void populateTableViewGeneralStats() {
        MFXTableColumn<Team> colPartidosTotales = new MFXTableColumn<>("Partidos Totales");
        colPartidosTotales.setMinWidth(110);
        colPartidosTotales.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colPartidosGanados = new MFXTableColumn<>("Partidos Ganados");
        colPartidosGanados.setMinWidth(110);
        colPartidosGanados.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colPartidosPerdidos = new MFXTableColumn<>("Partidos Perdidos");
        colPartidosPerdidos.setMinWidth(110);
        colPartidosPerdidos.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colTorneosTotales = new MFXTableColumn<>("Torneos Totales");
        colTorneosTotales.setMinWidth(110);
        colTorneosTotales.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getDeporte));

        MFXTableColumn<Team> colAnotacionesTotales = new MFXTableColumn<>("Anotaciones Totales");
        colAnotacionesTotales.setMinWidth(110);
        colAnotacionesTotales.setRowCellFactory(t -> new MFXTableRowCell<>(Team::getEstado));

        tbvEstadisticasGenerales.getTableColumns().clear();
        tbvEstadisticasGenerales.getTableColumns().addAll(Arrays.asList(colPartidosTotales,colPartidosGanados,colPartidosPerdidos, colTorneosTotales, colAnotacionesTotales));

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


package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.TeamTournamentStats;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TeamTournamentStatsService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

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
    private TableView<ObservableList<String>> tbvStatsTorneos;
    private final TeamTournamentStatsService statsService = new TeamTournamentStatsService();
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
        populateTableViewAdvanceStats();
        loadAllTeams();

        tbvRankingEquipos.getSelectionModel().selectionProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Team equipoSeleccionado = newVal.get(0);
                handleTableClickRankingEquipos(null);
            }
        });

        cmbRankings.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarYOrdenarEquipos(newVal);
            }
        });

    }

    @Override
    public void initialize() {

    }

    @FXML
    private void handleTableClickRankingEquipos(MouseEvent event) {
        List<Team> seleccion = tbvRankingEquipos.getSelectionModel().getSelectedValues();
        if (seleccion.isEmpty()) return;

        Team team = seleccion.get(0);

        limpiarLabelsEstadisticas();
        tbvStatsTorneos.getItems().clear();

        if (team.getEstadisticas() == null) return;

        lblPartidosTotales.setText(String.valueOf(team.getEstadisticas().getPartidosTotales()));
        lblPartidosGanados.setText(String.valueOf(team.getEstadisticas().getPartidosGanados()));
        lblPartidosPerdidos.setText(String.valueOf(team.getEstadisticas().getPartidosPerdidos()));
        lblPartidosEmpatados.setText(String.valueOf(team.getEstadisticas().getPartidosEmpatados()));
        lblTorneosTotales.setText(String.valueOf(team.getEstadisticas().getTorneosTotales()));
        lblTorneosGanados.setText(String.valueOf(team.getEstadisticas().getTorneosGanados()));
        lblTorneosPerdidos.setText(String.valueOf(team.getEstadisticas().getTorneosPerdidos()));
        lblAnotaciones.setText(String.valueOf(team.getEstadisticas().getAnotaciones()));
        lblAnotacionesContra.setText(String.valueOf(team.getEstadisticas().getAnotacionesEnContra()));

        statsService.getAllStats().stream()
                .filter(e -> e.getNombreEquipo() != null && e.getNombreEquipo().equalsIgnoreCase(team.getNombre()))
                .findFirst()
                .ifPresent(stats -> {
                    for (TeamTournamentStats.TournamentStat torneo : stats.getTorneos()) {
                        ObservableList<String> fila = FXCollections.observableArrayList();
                        fila.add(torneo.getNombreTorneo());

                        for (int i = 0; i < 5; i++) {
                            if (i < torneo.getPartidos().size()) {
                                var p = torneo.getPartidos().get(i);
                                fila.add(p.getRival() != null ? p.getRival() : "");
                                fila.add(p.getAnotaciones() + " - " + p.getAnotacionesEnContra());
                                fila.add(p.getResultadoReal());
                            } else {
                                fila.add(""); fila.add(""); fila.add("");
                            }
                        }

                        fila.add(torneo.getResultadoTorneo() != null ? torneo.getResultadoTorneo() : "");
                        tbvStatsTorneos.getItems().add(fila);
                    }
                });
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
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setMinWidth(110);
        colDeporte.setRowCellFactory(team -> {
            MFXTableRowCell<Team, String> cell = new MFXTableRowCell<>(Team::getDeporte);
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        MFXTableColumn<Team> colPuntosGlobales = new MFXTableColumn<>("Puntos Globales");
        colPuntosGlobales.setMinWidth(110);
        colPuntosGlobales.setRowCellFactory(team -> {
            MFXTableRowCell<Team, String> cell = new MFXTableRowCell<>(
                    t -> String.valueOf(t.getEstadisticas().getPuntosGlobales())
            );
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        tbvRankingEquipos.getTableColumns().clear();
        tbvRankingEquipos.getTableColumns().addAll(Arrays.asList(
                colNombre, colDeporte, colPuntosGlobales
        ));
    }

    private void populateTableViewAdvanceStats() {
        tbvStatsTorneos.getColumns().clear();

        TableColumn<ObservableList<String>, String> torneoCol = new TableColumn<>("Torneo");
        torneoCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(0)));
        torneoCol.setMinWidth(150);
        centrarContenidoColumna(torneoCol);
        tbvStatsTorneos.getColumns().add(torneoCol);

        for (int i = 0; i < 5; i++) {
            int baseIndex = 1 + (i * 3);

            TableColumn<ObservableList<String>, String> rivalCol = new TableColumn<>("Rival " + (i + 1));
            rivalCol.setMinWidth(120);
            rivalCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(baseIndex)));
            centrarContenidoColumna(rivalCol);

            TableColumn<ObservableList<String>, String> marcadorCol = new TableColumn<>("Marcador " + (i + 1));
            marcadorCol.setMinWidth(120);
            marcadorCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(baseIndex + 1)));
            centrarContenidoColumna(marcadorCol);

            TableColumn<ObservableList<String>, String> resultadoCol = new TableColumn<>("Resultado " + (i + 1));
            resultadoCol.setMinWidth(120);
            resultadoCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(baseIndex + 2)));
            centrarContenidoColumna(resultadoCol);

            tbvStatsTorneos.getColumns().addAll(rivalCol, marcadorCol, resultadoCol);
        }

        TableColumn<ObservableList<String>, String> resultadoTorneoCol = new TableColumn<>("Resultado Torneo");
        resultadoTorneoCol.setMinWidth(150);
        centrarContenidoColumna(resultadoTorneoCol);
        resultadoTorneoCol.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().get(data.getValue().size() - 1))
        );
        tbvStatsTorneos.getColumns().add(resultadoTorneoCol);
    }

    private <T> void centrarContenidoColumna(TableColumn<ObservableList<String>, T> columna) {
        columna.setCellFactory(tc -> {
            TableCell<ObservableList<String>, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
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
        sports.add("Todos");
        sportService.getAllSports().forEach(s -> sports.add(s.getNombre()));
        cmbRankings.setItems(sports);
        cmbRankings.selectFirst(); 
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

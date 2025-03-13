package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import cr.ac.una.tareatorneos.model.Team;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TeamsMaintenanceController extends Controller implements Initializable {

    @FXML
    private MFXButton btnBarrerEquipo;

    @FXML
    private MFXButton btnEliminarEquipo;

    @FXML
    private MFXButton btnGuardarEquipo;

    @FXML
    private MFXButton btnModificarEquipo;

    @FXML
    private MFXButton btnTomarFoto;

    @FXML
    private MFXFilterComboBox<?> cmbEquipos;

    @FXML
    private ImageView imgLupaRoja;

    @FXML
    private ImageView imgSeleccionar;

    @FXML
    private ImageView imgviewImagenDeporte;

    @FXML
    private Label lblMantenimientoEquiposTitulo;

    @FXML
    private AnchorPane root;

    @FXML
    private StackPane spImagenEquipos;

    @FXML
    private Separator sprTeamsMaintenance;

    @FXML
    private MFXTableView<Team> tbvEquiposExistentes;

    @FXML
    private MFXTextField txtfieldNombreEquipos;


    private ObservableList<Team> teamsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTableView();
        loadTeams();
        applyFilter();
    }


    @Override
    public void initialize() {
    }

    private void populateTableView() {

        MFXTableColumn<Team> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getDeporte));

        tbvEquiposExistentes.getTableColumns().clear();
        tbvEquiposExistentes.getTableColumns().addAll(Arrays.asList(colNombre, colDeporte));
    }

    // -------------------------------------------------------------------
    // Método para cargar datos de ejemplo en la tabla
    // -------------------------------------------------------------------
    private void loadTeams() {
        List<Team> sampleTeams = List.of(
                new Team("Equipo A", "Fútbol"),
                new Team("Equipo B", "Baloncesto")
        );
        teamsData.setAll(sampleTeams);
        tbvEquiposExistentes.setItems(teamsData);
    }

    // -------------------------------------------------------------------
    // Método para aplicar el filtro según lo ingresado en el campo de texto
    // -------------------------------------------------------------------
    private void applyFilter() {
        txtfieldNombreEquipos.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Team> filteredTeams = teamsData.stream()
                    .filter(team -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return team.getNombre().toLowerCase().contains(lowerCaseFilter)
                                || team.getDeporte().toLowerCase().contains(lowerCaseFilter);
                    })
                    .toList();
            tbvEquiposExistentes.setItems(FXCollections.observableArrayList(filteredTeams));
            System.out.println("Cantidad de elementos filtrados: " + filteredTeams.size());
        });

        tbvEquiposExistentes.setItems(teamsData);
    }


    @FXML
    void OnActionBtnBarrerEquipo(ActionEvent event) {

        imgviewImagenDeporte.setImage(null);
    }

    @FXML
    void OnActionBtnEliminarEquipo(ActionEvent event) {

    }

    @FXML
    void OnActionBtnGuardarEquipo(ActionEvent event) {

    }

    @FXML
    void OnActionBtnModificarEquipo(ActionEvent event) {

    }

    @FXML
    void OnActionBtnTomarFoto(ActionEvent event) {
        FlowController.getInstance().goViewInWindow("CameraView");
    }
    @FXML
    void handleTableClickEquiposExistentes(MouseEvent event) {

    }
}

package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import cr.ac.una.tareatorneos.model.Team;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.util.AppContext;
import cr.ac.una.tareatorneos.util.FlowController;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TeamsMaintenanceController extends Controller implements Initializable {
    @FXML private MFXButton btnBarrerEquipo, btnEliminarEquipo, btnGuardarEquipo, btnModificarEquipo, btnTomarFoto;
    @FXML private MFXFilterComboBox<String> cmbEquipos;
    @FXML private ImageView imgLupaRoja, imgSeleccionar, imgviewImagenDeporte;
    @FXML private Label lblMantenimientoEquiposTitulo;
    @FXML private AnchorPane root;
    @FXML private StackPane spImagenEquipos;
    @FXML private Separator sprTeamsMaintenance;
    @FXML private MFXTableView<Team> tbvEquiposExistentes;
    @FXML private MFXTextField txtfieldNombreEquipos;

    private ObservableList<Team> teamsData = FXCollections.observableArrayList();
    private TeamService teamService = new TeamService();
    private SportService sportService = new SportService();
    private String originalNombre, originalDeporte, originalFoto;
    private Mensaje mensajeUtil = new Mensaje();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTableView();
        loadTeams();
        populateComboBoxDeportes();

        tbvEquiposExistentes.getSelectionModel().selectionProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                handleTableClickEquiposExistentes(null);
            }
        });

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.focusedProperty().addListener((obsF, wasFocused, isNowFocused) -> {
                    if (isNowFocused) {
                        String photoPath = (String) AppContext.getInstance().get("teamPhoto");
                        if (photoPath != null && !photoPath.isEmpty()) {
                            File file = new File(photoPath);
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                imgviewImagenDeporte.setImage(image);
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    public void initialize() {}

    private void populateTableView() {
        MFXTableColumn<Team> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getNombre));
        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getDeporte));
        tbvEquiposExistentes.getTableColumns().clear();
        tbvEquiposExistentes.getTableColumns().addAll(colNombre, colDeporte);
    }

    private void loadTeams() {
        teamsData.setAll(teamService.getAllTeams());
        tbvEquiposExistentes.setItems(teamsData);
    }

    private void populateComboBoxDeportes() {
        ObservableList<String> sportsNames = FXCollections.observableArrayList();
        sportService.getAllSports().forEach(sport -> sportsNames.add(sport.getNombre()));
        cmbEquipos.setItems(sportsNames);
    }

    public static void actualizarListaDeportes() {
        TeamsMaintenanceController controller = (TeamsMaintenanceController) FlowController.getInstance().getController("TeamsMaintenanceView");
        if (controller != null) {
            controller.populateComboBoxDeportes();
        }
    }

    public static void actualizarImagenEquipo(String nuevaFoto) {
        TeamsMaintenanceController controller = (TeamsMaintenanceController)
                FlowController.getInstance().getController("TeamsMaintenanceView");

        if (controller != null) {
            File file = new File(nuevaFoto);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                controller.imgviewImagenDeporte.setImage(image);
            }
        }
    }


    @FXML
    void OnActionBtnBarrerEquipo(ActionEvent event) {
        txtfieldNombreEquipos.clear();
        cmbEquipos.setValue(null);
        imgviewImagenDeporte.setImage(null);
        AppContext.getInstance().set("teamPhoto", null);
        tbvEquiposExistentes.getSelectionModel().clearSelection();
    }


    @FXML
    void OnActionBtnEliminarEquipo(ActionEvent event) {
        List<Team> selected = tbvEquiposExistentes.getSelectionModel().getSelectedValues();
        if (selected.isEmpty()) {
            mensajeUtil.show(AlertType.WARNING, "Eliminar Equipo", "Seleccione un equipo para eliminar.");
            return;
        }
        Team selectedTeam = selected.get(0);

        if (mensajeUtil.showConfirmation("Confirmar Eliminación", root.getScene().getWindow(),
                "¿Está seguro de que desea eliminar el equipo \"" + selectedTeam.getNombre() + "\"?")) {
            if (teamService.deleteTeam(selectedTeam.getNombre())) {
                mensajeUtil.show(AlertType.INFORMATION, "Eliminar Equipo", "Equipo eliminado exitosamente.");
                loadTeams();
                OnActionBtnBarrerEquipo(event);
            } else {
                mensajeUtil.show(AlertType.ERROR, "Eliminar Equipo", "No se pudo eliminar el equipo.");
            }
        }
    }

    @FXML
    void OnActionBtnGuardarEquipo(ActionEvent event) {
        // **SOLUCIÓN:** Quitar la selección antes de verificar si es un equipo nuevo
        tbvEquiposExistentes.getSelectionModel().clearSelection();

        String nombreEquipo = txtfieldNombreEquipos.getText().trim();
        if (nombreEquipo.isEmpty()) {
            mensajeUtil.show(AlertType.ERROR, "Error", "Debe ingresar un nombre de equipo.");
            return;
        }

        String deporteSeleccionado = cmbEquipos.getValue();
        if (deporteSeleccionado == null || deporteSeleccionado.isEmpty()) {
            mensajeUtil.show(AlertType.ERROR, "Error", "Debe seleccionar un deporte.");
            return;
        }

        for (Team t : teamsData) {
            if (t.getNombre().equalsIgnoreCase(nombreEquipo) && t.getDeporte().equalsIgnoreCase(deporteSeleccionado)) {
                mensajeUtil.show(AlertType.WARNING, "Guardar Equipo", "Ya existe un equipo con ese nombre para ese deporte.");
                return;
            }
        }

        String foto = (String) AppContext.getInstance().get("teamPhoto");
        Team newTeam = new Team(nombreEquipo, deporteSeleccionado, foto);

        if (teamService.addTeam(newTeam)) {
            teamsData.add(newTeam);
            tbvEquiposExistentes.setItems(FXCollections.observableArrayList(teamsData));
            mensajeUtil.show(AlertType.INFORMATION, "Éxito", "Equipo guardado correctamente.");
            OnActionBtnBarrerEquipo(event);
        } else {
            mensajeUtil.show(AlertType.ERROR, "Error", "No se pudo guardar el equipo.");
        }
    }




    @FXML
    void OnActionBtnModificarEquipo(ActionEvent event) {
        List<Team> selected = tbvEquiposExistentes.getSelectionModel().getSelectedValues();
        if (selected.isEmpty()) {
            mensajeUtil.show(AlertType.WARNING, "Modificar Equipo", "Seleccione un equipo para modificar.");
            return;
        }

        String newNombre = txtfieldNombreEquipos.getText().trim();
        String newDeporte = cmbEquipos.getValue();
        String newFoto = (String) AppContext.getInstance().get("teamPhoto");

        if (newFoto == null || newFoto.isEmpty()) {
            newFoto = originalFoto;
        }

        if (newNombre.equalsIgnoreCase(originalNombre) &&
                newDeporte.equals(originalDeporte) &&
                newFoto.equals(originalFoto)) {
            mensajeUtil.show(AlertType.WARNING, "Modificar Equipo", "No se han realizado cambios.");
            return;
        }

        Team selectedTeam = selected.get(0);
        selectedTeam.setNombre(newNombre);
        selectedTeam.setDeporte(newDeporte);
        selectedTeam.setFoto(newFoto);

        if (teamService.updateTeam(selectedTeam)) {
            mensajeUtil.show(AlertType.INFORMATION, "Modificar Equipo", "Equipo modificado exitosamente.");
            loadTeams();  // Recargar la tabla
            OnActionBtnBarrerEquipo(event);
        } else {
            mensajeUtil.show(AlertType.ERROR, "Modificar Equipo", "No se pudo modificar el equipo.");
        }
    }



    @FXML
    void OnActionBtnTomarFoto(ActionEvent event) {
        FlowController.getInstance().goViewInWindow("CameraView");
    }

    @FXML
    void handleTableClickEquiposExistentes(MouseEvent event) {
        List<Team> selected = tbvEquiposExistentes.getSelectionModel().getSelectedValues();
        if (!selected.isEmpty()) {
            Team selectedTeam = selected.get(0);
            originalNombre = selectedTeam.getNombre();
            originalDeporte = selectedTeam.getDeporte();
            originalFoto = selectedTeam.getFoto();

            txtfieldNombreEquipos.setText(selectedTeam.getNombre());
            cmbEquipos.setValue(selectedTeam.getDeporte());

            if (selectedTeam.getFoto() != null && !selectedTeam.getFoto().isEmpty()) {
                File file = new File(selectedTeam.getFoto());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imgviewImagenDeporte.setImage(image);
                } else {
                    imgviewImagenDeporte.setImage(null);
                }
            } else {
                imgviewImagenDeporte.setImage(null);
            }
        }
    }

}

package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.util.AchievementUtils;
import cr.ac.una.tareatorneos.util.AppContext;
import cr.ac.una.tareatorneos.util.FlowController;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TeamsMaintenanceController extends Controller implements Initializable {
    @FXML
    private MFXButton btnBarrerEquipo, btnEliminarEquipo, btnGuardarEquipo, btnModificarEquipo, btnTomarFoto;
    @FXML
    private MFXFilterComboBox<String> cmbEquipos;
    @FXML
    private ImageView imgLupaRoja, imgSeleccionar, imgviewImagenDeporte;
    @FXML
    private Label lblMantenimientoEquiposTitulo;
    @FXML
    private MFXButton btnCargarFoto;
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
    private TeamService teamService;
    private SportService sportService = new SportService();
    private String originalNombre, originalDeporte, originalFoto;
    private String currentTeamImagePath = "";
    private Mensaje mensajeUtil = new Mensaje();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        teamService = new TeamService();
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
                        updateImageFromAppContext();
                    }
                });
            }
        });
    }

    @Override
    public void initialize() {
    }

    private void populateTableView() {
        MFXTableColumn<Team> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setMinWidth(180);
        colNombre.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getNombre));

        MFXTableColumn<Team> colDeporte = new MFXTableColumn<>("Deporte");
        colDeporte.setMinWidth(150);
        colDeporte.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getDeporte));

        MFXTableColumn<Team> colEstado = new MFXTableColumn<>("Estado");
        colEstado.setMinWidth(130);
        colEstado.setRowCellFactory(team -> new MFXTableRowCell<>(Team::getEstado));

        tbvEquiposExistentes.getTableColumns().clear();
        tbvEquiposExistentes.getTableColumns().addAll(colNombre, colDeporte, colEstado);
    }


    public void loadTeams() {
        List<Team> loadedTeams = teamService.getAllTeams();
        teamsData.setAll(loadedTeams);
        tbvEquiposExistentes.getItems().clear();
        tbvEquiposExistentes.getItems().addAll(teamsData);
    }

    private void populateComboBoxDeportes() {
        ObservableList<String> sportsNames = FXCollections.observableArrayList();
        sportService.getAllSports().forEach(sport -> sportsNames.add(sport.getNombre()));
        cmbEquipos.setItems(sportsNames);
        cmbEquipos.getSelectionModel().clearSelection();
        cmbEquipos.setValue(null);
    }

    public static void actualizarListaDeportes() {
        TeamsMaintenanceController controller = (TeamsMaintenanceController) FlowController.getInstance().getController("TeamsMaintenanceView");
        if (controller != null) {
            controller.populateComboBoxDeportes();
        }
    }

    @FXML
    void OnActionBtnBarrerEquipo(ActionEvent event) {
        txtfieldNombreEquipos.clear();
        cmbEquipos.getSelectionModel().clearSelection();
        cmbEquipos.setValue(null);
        cmbEquipos.clearSelection();
        cmbEquipos.getSelectionModel().clearSelection();
        cmbEquipos.setText("");
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

        if ("participante".equalsIgnoreCase(selectedTeam.getEstado())) {
            mensajeUtil.show(AlertType.WARNING, "Eliminar Equipo",
                    "Este equipo está participando en un torneo y no puede ser eliminado.");
            return;
        }

        boolean confirmacion = mensajeUtil.showConfirmation(
                "Confirmar Eliminación",
                root.getScene().getWindow(),
                "¿Está seguro de que desea eliminar el equipo \"" + selectedTeam.getNombre() + "\"?"
        );

        if (confirmacion) {
            boolean success = teamService.deleteTeam(selectedTeam.getNombre());
            if (success) {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION, "Eliminar Equipo", "Equipo eliminado exitosamente.");
                loadTeams();
                OnActionBtnBarrerEquipo(event);

            } else {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR, "Eliminar Equipo", "No se pudo eliminar el Equipo.");
            }
        }
    }

    @FXML
    void OnActionBtnGuardarEquipo(ActionEvent event) {
        if (!tbvEquiposExistentes.getSelectionModel().getSelectedValues().isEmpty()) {
            mensajeUtil.show(AlertType.WARNING, "Guardar Equipo", "El equipo ya está seleccionado. Use 'Modificar' en su lugar.");
            return;
        }

        String nombreEquipo = txtfieldNombreEquipos.getText().trim();
        String deporteSeleccionado = cmbEquipos.getValue();

        BufferedImage tempImage = (BufferedImage) AppContext.getInstance().get("teamPhotoTemp");
        if (tempImage != null) {
            String fileName = nombreEquipo.replace(" ", "_") + ".jpg";
            File destinationFile = new File("teamsPhotos", fileName);
            try {
                ImageIO.write(tempImage, "jpg", destinationFile);
                currentTeamImagePath = destinationFile.getAbsolutePath();
                AppContext.getInstance().set("teamPhotoTemp", null);
            } catch (IOException e) {
                mensajeUtil.show(AlertType.ERROR, "Error", "No se pudo guardar la imagen desde la cámara.");
                return;
            }
        }

        if (nombreEquipo.isEmpty() || currentTeamImagePath.isEmpty()) {
            mensajeUtil.show(AlertType.WARNING, "Guardar Equipo", "Debe ingresar el nombre y la imagen.");
            return;
        }

        if (deporteSeleccionado == null || deporteSeleccionado.isEmpty()) {
            mensajeUtil.show(AlertType.ERROR, "Error", "Debe seleccionar un deporte.");
            return;
        }

        for (Team t : teamsData) {
            if (t.getNombre().equalsIgnoreCase(nombreEquipo) &&
                    t.getDeporte().equalsIgnoreCase(deporteSeleccionado)) {
                mensajeUtil.show(AlertType.WARNING, "Guardar Equipo", "Ya existe un equipo con ese nombre para ese deporte.");
                return;
            }
        }

        File directory = new File("teamsPhotos");
        if (!directory.exists()) directory.mkdirs();

        File destinationFile = new File(directory, nombreEquipo.replace(" ", "_") + ".jpg");
        try {
            java.nio.file.Files.copy(new File(currentTeamImagePath).toPath(), destinationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            currentTeamImagePath = destinationFile.getName();
        } catch (IOException e) {
            mensajeUtil.show(AlertType.ERROR, "Error", "No se pudo copiar la imagen.");
            return;
        }

        Team newTeam = new Team(nombreEquipo, deporteSeleccionado, currentTeamImagePath);
        newTeam.setLogros(AchievementUtils.generarLogrosIniciales());

        boolean success = teamService.addTeam(newTeam);
        if (success) {
            mensajeUtil.show(AlertType.INFORMATION, "Guardar Equipo", "Equipo guardado exitosamente.");
            teamsData.add(newTeam);
            tbvEquiposExistentes.setItems(FXCollections.observableArrayList(teamsData));
            loadTeams();
            OnActionBtnBarrerEquipo(event);
            cmbEquipos.getSelectionModel().clearSelection();
            cmbEquipos.setValue(null);
            cmbEquipos.clearSelection();
            cmbEquipos.getSelectionModel().clearSelection();
            cmbEquipos.setText("");
        } else {
            mensajeUtil.show(AlertType.ERROR, "Guardar Equipo", "No se pudo guardar el Equipo.");
        }
    }

    @FXML
    void OnActionBtnModificarEquipo(ActionEvent event) {
        List<Team> selected = tbvEquiposExistentes.getSelectionModel().getSelectedValues();
        if (selected.isEmpty()) {
            mensajeUtil.show(AlertType.WARNING, "Modificar Equipo", "Seleccione un equipo para modificar.");
            return;
        }

        Team selectedTeam = selected.get(0);

        if ("participante".equalsIgnoreCase(selectedTeam.getEstado())) {
            mensajeUtil.show(AlertType.WARNING, "Modificar Equipo",
                    "Este equipo está participando en un torneo y no puede ser modificado.");
            return;
        }

        String oldNombre = selectedTeam.getNombre();
        String newNombre = txtfieldNombreEquipos.getText().trim();
        String newDeporte = cmbEquipos.getValue();

        String imagenActual = originalFoto == null ? "" : new File(originalFoto).getName();
        String imagenFinal = currentTeamImagePath.isEmpty()
                ? imagenActual
                : new File(currentTeamImagePath).getName();

        BufferedImage tempImage = (BufferedImage) AppContext.getInstance().get("teamPhotoTemp");
        boolean imageChangedFromCamera = tempImage != null;

        boolean imageChangedFromExplorer = !currentTeamImagePath.isEmpty()
                && new File(currentTeamImagePath).isAbsolute()
                && !imagenActual.equalsIgnoreCase(imagenFinal);

        boolean nameChanged = !oldNombre.equals(newNombre);
        boolean sportChanged = !originalDeporte.equalsIgnoreCase(newDeporte);
        boolean imageChanged = imageChangedFromCamera || imageChangedFromExplorer;

        if (!nameChanged && !sportChanged && !imageChanged) {
            mensajeUtil.show(AlertType.WARNING, "Modificar Equipo", "No se han realizado cambios.");
            return;
        }

        if (imageChangedFromCamera) {
            String fileName = newNombre.replace(" ", "_") + ".jpg";
            File destination = new File("teamsPhotos", fileName);
            try {
                ImageIO.write(tempImage, "jpg", destination);
                currentTeamImagePath = destination.getAbsolutePath();
                imagenFinal = fileName;
                AppContext.getInstance().set("teamPhotoTemp", null);
            } catch (IOException e) {
                mensajeUtil.show(AlertType.ERROR, "Error", "No se pudo guardar la imagen de la cámara.");
                return;
            }
        }

        if (imageChangedFromExplorer) {
            File origin = new File(currentTeamImagePath);
            String fileName = newNombre.replace(" ", "_") + ".jpg";
            File dest = new File("teamsPhotos", fileName);
            try {
                java.nio.file.Files.copy(origin.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                imagenFinal = fileName;
            } catch (IOException e) {
                mensajeUtil.show(AlertType.ERROR, "Error", "No se pudo copiar la imagen seleccionada.");
                return;
            }
        }

        if (nameChanged && !imageChangedFromCamera && !imageChangedFromExplorer) {
            File oldImageFile = new File("teamsPhotos", imagenActual);
            File newImageFile = new File("teamsPhotos", newNombre.replace(" ", "_") + ".jpg");
            if (oldImageFile.exists()) {
                boolean renamed = oldImageFile.renameTo(newImageFile);
                if (renamed) {
                    imagenFinal = newImageFile.getName();
                }
            }
        }

        selectedTeam.setNombre(newNombre);
        selectedTeam.setDeporte(newDeporte);
        selectedTeam.setTeamImage(imagenFinal);

        boolean success = teamService.updateTeam(oldNombre, selectedTeam);
        if (success) {
            mensajeUtil.show(AlertType.INFORMATION, "Modificar Equipo", "Equipo modificado exitosamente.");
            loadTeams();
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
            originalFoto = selectedTeam.getTeamImage();

            txtfieldNombreEquipos.setText(originalNombre);
            cmbEquipos.setValue(originalDeporte);

            if (originalFoto != null && !originalFoto.isEmpty()) {
                File imageFile = new File("teamsPhotos/" + originalFoto);
                if (imageFile.exists()) {
                    imgviewImagenDeporte.setImage(new Image(imageFile.toURI().toString()));
                    currentTeamImagePath = imageFile.getAbsolutePath();
                } else {
                    imgviewImagenDeporte.setImage(null);
                    currentTeamImagePath = "";
                }
            } else {
                imgviewImagenDeporte.setImage(null);
                currentTeamImagePath = "";
            }
        }
    }

    @FXML
    void OnActionBtnCargarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            currentTeamImagePath = selectedFile.getAbsolutePath();

            Image image = new Image(selectedFile.toURI().toString());
            imgviewImagenDeporte.setImage(image);
        }

    }

    private void updateImageFromAppContext() {
        String photoPath = (String) AppContext.getInstance().get("teamPhoto");
        if (photoPath != null && !photoPath.isEmpty()) {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imgviewImagenDeporte.setImage(image);
                return;
            }
        }

        BufferedImage tempImage = (BufferedImage) AppContext.getInstance().get("teamPhotoTemp");
        if (tempImage != null) {
            Image fxImage = SwingFXUtils.toFXImage(tempImage, null);
            imgviewImagenDeporte.setImage(fxImage);
        }
    }

    public void recargarEquiposDesdeJSON() {
        List<Team> loadedTeams = teamService.getAllTeams();
        teamsData.setAll(loadedTeams);
        tbvEquiposExistentes.setItems(FXCollections.observableArrayList(teamsData));
    }

    public void actualizarComboBoxTeamsManintenance() {
        populateComboBoxDeportes();
    }

}

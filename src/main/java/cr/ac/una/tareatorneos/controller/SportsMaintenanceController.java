package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.util.FlowController;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SportsMaintenanceController extends Controller implements Initializable {

    @FXML
    private MFXButton btnBarrerDeporte;
    @FXML
    private MFXButton btnBuscarImagen;
    @FXML
    private MFXButton btnEliminarDeporte;
    @FXML
    private MFXButton btnGuardarDeporte;
    @FXML
    private MFXButton btnModificar;
    @FXML
    private ImageView imgviewImagenDeporte;
    @FXML
    private AnchorPane root;
    @FXML
    private MFXTableView<Sport> tbvDeportesExistentes;
    @FXML
    private MFXTextField txtfieldNombreDeporte;

    private ObservableList<Sport> sportsData = FXCollections.observableArrayList();
    private SportService sportService;
    private String currentBallImagePath = "";
    private Mensaje mensajeUtil = new Mensaje();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sportService = new SportService();
        populateTableView();
        loadSports();
        tbvDeportesExistentes.getSelectionModel().selectionProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                handleTableClickDeportesExistentes(null);
            }
        });
    }

    @Override
    public void initialize() {
    }

    private void populateTableView() {
        MFXTableColumn<Sport> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setRowCellFactory(sport -> new MFXTableRowCell<>(Sport::getNombre));
        colNombre.setMinWidth(180);
        MFXTableColumn<Sport> colFecha = new MFXTableColumn<>("Fecha de Creación");
        colFecha.setRowCellFactory(sport -> new MFXTableRowCell<>(s -> s.getFechaCreacion().toString()));
        tbvDeportesExistentes.getTableColumns().clear();
        tbvDeportesExistentes.getTableColumns().addAll(Arrays.asList(colNombre, colFecha));
    }

    private void loadSports() {
        List<Sport> loadedSports = sportService.getAllSports();
        sportsData.setAll(loadedSports);
        tbvDeportesExistentes.getItems().clear();
        tbvDeportesExistentes.getItems().addAll(sportsData);
    }

    @FXML
    void OnActionBtnBarrerCampos(ActionEvent event) {
        imgviewImagenDeporte.setImage(null);
        txtfieldNombreDeporte.clear();
        currentBallImagePath = "";
        tbvDeportesExistentes.getSelectionModel().clearSelection();
    }

    @FXML
    void OnActionBtnBuscarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            currentBallImagePath = selectedFile.getAbsolutePath();

            Image image = new Image(selectedFile.toURI().toString());
            imgviewImagenDeporte.setImage(image);
        }
    }

    @FXML
    void OnActionBtnEliminar(ActionEvent event) {
        List<Sport> selectedItems = tbvDeportesExistentes.getSelectionModel().getSelectedValues();
        if (selectedItems.isEmpty()) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING, "Eliminar Deporte", "Seleccione un deporte para eliminar.");
            return;
        }

        Sport selectedSport = selectedItems.get(0);

        boolean confirmacion = mensajeUtil.showConfirmation(
                "Confirmar Eliminación",
                root.getScene().getWindow(),
                "¿Está seguro de que desea eliminar el deporte \"" + selectedSport.getNombre() + "\"?"
        );

        if (confirmacion) {
            boolean success = sportService.deleteSport(selectedSport.getNombre());
            if (success) {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION, "Eliminar Deporte", "Deporte eliminado exitosamente.");
                loadSports();
                OnActionBtnBarrerCampos(event);
                TeamsMaintenanceController.actualizarListaDeportes();
                notificarActualizacionComboBoxesTorneo();
                ActualizacionComboBoxTeamsManintenace();
                ActualizacionComboBoxActiveTournaments();
                ActualizacionComboBoxAchievements();

            } else {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR, "Eliminar Deporte", "No se pudo eliminar el deporte.");
            }
        }
    }

    @FXML
    void OnActionBtnGuardar(ActionEvent event) {
        if (!tbvDeportesExistentes.getSelectionModel().getSelectedValues().isEmpty()) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                    "Guardar Deporte", "El deporte ya está seleccionado. Use 'Modificar' en su lugar.");
            return;
        }

        String nombre = txtfieldNombreDeporte.getText().trim();
        if (nombre.isEmpty() || currentBallImagePath.isEmpty()) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING, "Guardar Deporte",
                    "Debe ingresar el nombre y la imagen.");
            return;
        }

        for (Sport s : sportsData) {
            if (s.getNombre().equalsIgnoreCase(nombre)) {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                        "Guardar Deporte", "Ya existe un deporte con ese nombre.");
                return;
            }
        }

        File directory = new File(System.getProperty("user.dir") + "/sportsPhotos");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = nombre.replace(" ", "_") + ".jpg";
        File destinationFile = new File(directory, fileName);

        try {
            java.nio.file.Files.copy(new File(currentBallImagePath).toPath(), destinationFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            currentBallImagePath = fileName;

        } catch (IOException e) {
            e.printStackTrace();
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR, "Error", "No se pudo copiar la imagen.");
            return;
        }

        Sport newSport = new Sport(nombre, currentBallImagePath);
        boolean success = sportService.addSport(newSport);
        if (success) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Guardar Deporte", "Deporte guardado exitosamente.");
            loadSports();
            OnActionBtnBarrerCampos(event);
            TeamsMaintenanceController.actualizarListaDeportes();
            notificarActualizacionComboBoxesTorneo();
            ActualizacionComboBoxTeamsManintenace();
            ActualizacionComboBoxActiveTournaments();
            ActualizacionComboBoxAchievements();

        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Guardar Deporte", "No se pudo guardar el deporte.");
        }

    }

    @FXML
    void OnActionBtnModificar(ActionEvent event) {
        List<Sport> selectedItems = tbvDeportesExistentes.getSelectionModel().getSelectedValues();
        if (selectedItems.isEmpty()) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING, "Modificar Deporte", "Seleccione un deporte para modificar.");
            return;
        }

        Sport selectedSport = selectedItems.get(0);
        String oldNombre = selectedSport.getNombre();
        String newNombre = txtfieldNombreDeporte.getText().trim();

        String newImage = currentBallImagePath.isEmpty() ? selectedSport.getBallImage() : currentBallImagePath;

        if (new File(newImage).isAbsolute()) {
            File sourceFile = new File(newImage);
            String imageFileName = newNombre.replace(" ", "_") + ".jpg";
            File destinationFile = new File("sportsPhotos", imageFileName);
            try {
                java.nio.file.Files.copy(sourceFile.toPath(), destinationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                newImage = imageFileName;
            } catch (IOException e) {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR, "Error", "No se pudo copiar la imagen seleccionada.");
                return;
            }
        }

        for (Sport s : sportsData) {
            if (!s.getNombre().equalsIgnoreCase(oldNombre) && s.getNombre().equalsIgnoreCase(newNombre)) {
                mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING, "Modificar Deporte", "Ya existe un deporte con ese nombre.");
                return;
            }
        }

        String imagenActual = selectedSport.getBallImage() != null
                ? selectedSport.getBallImage()
                : "";

        String imagenFinal = newImage != null
                ? newImage
                : "";

        boolean nameChanged = !oldNombre.equals(newNombre);

        boolean imageChanged = !currentBallImagePath.isEmpty() &&
                new File(currentBallImagePath).isAbsolute();

        if (!nameChanged && !imageChanged) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING, "Modificar Deporte", "No se han realizado cambios.");
            return;
        }

        if (!oldNombre.equalsIgnoreCase(newNombre)) {
            File oldFile = new File("sportsPhotos/" + selectedSport.getBallImage());
            File renamedFile = new File("sportsPhotos/" + newNombre.replace(" ", "_") + ".jpg");

            if (oldFile.exists() && !imagenActual.equalsIgnoreCase(renamedFile.getName())) {
                boolean renamed = oldFile.renameTo(renamedFile);
                if (renamed) {
                    newImage = renamedFile.getName();
                }
            }
        }

        if (newImage.startsWith("sportsPhotos/")) {
            newImage = newImage.replace("sportsPhotos/", "");
        }

        selectedSport.setNombre(newNombre);
        selectedSport.setBallImage(newImage);

        boolean success = sportService.updateSport(oldNombre, selectedSport);
        if (success) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION, "Modificar Deporte", "Deporte modificado exitosamente.");
            loadSports();
            OnActionBtnBarrerCampos(event);
            notificarActualizacionComboBoxesTorneo();
            ActualizacionComboBoxTeamsManintenace();
            ActualizacionComboBoxActiveTournaments();
            ActualizacionComboBoxAchievements();
        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR, "Modificar Deporte", "No se pudo modificar el deporte.");
        }
    }

    @FXML
    void handleTableClickDeportesExistentes(MouseEvent event) {
        List<Sport> selected = tbvDeportesExistentes.getSelectionModel().getSelectedValues();
        if (!selected.isEmpty()) {
            Sport selectedSport = selected.get(0);
            txtfieldNombreDeporte.setText(selectedSport.getNombre());

            if (selectedSport.getBallImage() != null && !selectedSport.getBallImage().isEmpty()) {
                currentBallImagePath = "sportsPhotos/" + selectedSport.getBallImage();
                File imageFile = new File(currentBallImagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imgviewImagenDeporte.setImage(image);
                } else {
                    imgviewImagenDeporte.setImage(null);
                }
            } else {
                imgviewImagenDeporte.setImage(null);
            }
        }

    }

    private void notificarActualizacionComboBoxesTorneo() {
        TournamentMaintenanceController torneoController = (TournamentMaintenanceController)
                FlowController.getInstance().getController("TournamentMaintenanceView");

        if (torneoController != null) {
            torneoController.actualizarComboBoxesDeportes();
        }
    }

    private void ActualizacionComboBoxTeamsManintenace() {
        TeamsMaintenanceController EquiposController = (TeamsMaintenanceController)
                FlowController.getInstance().getController("TeamsMaintenanceView");

        if (EquiposController != null) {
            EquiposController.actualizarComboBoxTeamsManintenance();
        }
    }

    private void ActualizacionComboBoxActiveTournaments() {
        ActiveTournamentsController ActiveController = (ActiveTournamentsController)
                FlowController.getInstance().getController("ActiveTournamentsView");

        if (ActiveController != null) {
            ActiveController.actualizarComboBoxActiveTournaments();
        }
    }

    private void ActualizacionComboBoxAchievements() {
        AchievementsController ActiveController = (AchievementsController)
                FlowController.getInstance().getController("AchievementsView");

        if (ActiveController != null) {
            ActiveController.actualizarComboBoxAchievements();
        }
    }

    private void ActualizacionComboBoxRankings() {
        RankingsController ActiveController = (RankingsController)
                FlowController.getInstance().getController("RankingsView");

        if (ActiveController != null) {
            ActiveController.actualizarComboBoxRankings();
        }
    }


}
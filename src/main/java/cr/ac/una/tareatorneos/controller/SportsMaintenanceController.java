package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.util.FlowController;
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
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sportService = new SportService();
        populateTableView();
        loadSports();
    }

    @Override
    public void initialize() {
    }

    private void populateTableView() {

        MFXTableColumn<Sport> colNombre = new MFXTableColumn<>("Nombre");
        colNombre.setRowCellFactory(sport -> new MFXTableRowCell<>(Sport::getNombre));

        MFXTableColumn<Sport> colBallImage = new MFXTableColumn<>("Imagen del Balón");
        colBallImage.setRowCellFactory(sport -> new MFXTableRowCell<>(Sport::getBallImage));

        tbvDeportesExistentes.getTableColumns().addAll(Arrays.asList(colNombre, colBallImage));
    }

    private void loadSports() {
        // Cargar los deportes desde el JSON
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
    }

    @FXML
    void OnActionBtnBuscarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Filtrar archivos de imagen (png, jpg, jpeg, gif)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Validar que la carpeta de imágenes del usuario existe
        File picturesDir = new File(System.getProperty("user.home") + "/Pictures");
        if (picturesDir.exists() && picturesDir.isDirectory()) {
            fileChooser.setInitialDirectory(picturesDir);
        }

        // Obtener la ventana principal para abrir el FileChooser
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Guardar la ruta absoluta de la imagen
            currentBallImagePath = selectedFile.getAbsolutePath();

            // Mostrar la imagen en el ImageView
            Image image = new Image(selectedFile.toURI().toString());
            imgviewImagenDeporte.setImage(image);
        }
    }



    @FXML
    void OnActionBtnEliminar(ActionEvent event) {
        List<Sport> selectedItems = tbvDeportesExistentes.getSelectionModel().getSelectedValues();
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Eliminar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione un deporte para eliminar.");
            alert.show();
            return;
        }

        Sport selectedSport = selectedItems.get(0);
        boolean success = sportService.deleteSport(selectedSport.getNombre());

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Eliminar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Deporte eliminado exitosamente.");
            alert.show();
            loadSports();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eliminar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo eliminar el deporte.");
            alert.show();
        }
    }


    @FXML
    void OnActionBtnGuardar(ActionEvent event) {
        String nombre = txtfieldNombreDeporte.getText().trim();
        if(nombre.isEmpty() || currentBallImagePath.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Guardar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Debe ingresar el nombre del deporte y seleccionar una imagen.");
            alert.show();
            return;
        }
        Sport newSport = new Sport(nombre, currentBallImagePath);
        boolean success = sportService.addSport(newSport);
        if(success){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Guardar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Deporte guardado exitosamente.");
            alert.show();
            loadSports();
            OnActionBtnBarrerCampos(event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Guardar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo guardar el deporte.");
            alert.show();
        }
    }

    @FXML
    void OnActionBtnModificar(ActionEvent event) {
        List<Sport> selectedItems = tbvDeportesExistentes.getSelectionModel().getSelectedValues();
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modificar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione un deporte para modificar.");
            alert.show();
            return;
        }

        Sport selectedSport = selectedItems.get(0);
        String newNombre = txtfieldNombreDeporte.getText().trim();
        String newImage = currentBallImagePath.isEmpty() ? selectedSport.getBallImage() : currentBallImagePath;

        if (newNombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Modificar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("El nombre del deporte no puede estar vacío.");
            alert.show();
            return;
        }

        selectedSport.setNombre(newNombre);
        selectedSport.setBallImage(newImage);
        boolean success = sportService.updateSport(selectedSport);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modificar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("Deporte modificado exitosamente.");
            alert.show();
            loadSports();
            OnActionBtnBarrerCampos(event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Modificar Deporte");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo modificar el deporte.");
            alert.show();
        }
    }

    @FXML
    void handleTableClickDeportesExistentes(MouseEvent event) {

    }


}

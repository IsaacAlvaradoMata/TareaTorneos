package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SportsMaintenanceController extends Controller implements Initializable  {

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
    private MFXTableView<?> tbvDeportesExistentes;

    @FXML
    private MFXTextField txtfieldNombreDeporte;

    @FXML
    void OnActionBtnBarrerCampos(ActionEvent event) {
        imgviewImagenDeporte.setImage(null);
    }

    @FXML
    void OnActionBtnBuscarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Filtrar archivos tipo imagen
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // 👉 Directorio inicial = Carpeta Imágenes del usuario
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));

        // Obtener la ventana (Stage) actual
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // Abrir el explorador de archivos
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imgviewImagenDeporte.setImage(image);
        }
    }


    @FXML
    void OnActionBtnEliminar(ActionEvent event) {

    }

    @FXML
    void OnActionBtnGuardar(ActionEvent event) {

    }

    @FXML
    void OnActionBtnModificar(ActionEvent event) {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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

    }

    @FXML
    void OnActionBtnBuscarImagen(ActionEvent event) {

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

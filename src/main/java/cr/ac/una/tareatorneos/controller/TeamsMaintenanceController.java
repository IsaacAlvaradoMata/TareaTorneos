package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
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
    private MFXTableView<?> tbvDeportesExistentes;

    @FXML
    private MFXTextField txtfieldNombreEquipos;

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

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

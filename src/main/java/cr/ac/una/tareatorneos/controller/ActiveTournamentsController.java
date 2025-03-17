
package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXTableView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class ActiveTournamentsController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTorneosActivosTitulo;
    @FXML
    private ImageView imgLupaAmarilla;
    @FXML
    private MFXTableView<?> tbvTorneosActivos;
    @FXML
    private Separator sprActiveTournaments;
    @FXML
    private Label lblNombreTorneo;
    @FXML
    private Label lblDeporteTorneo;
    @FXML
    private Label lblTiempoTorneo;
    @FXML
    private Label lblCantidadEquiposTorneo;
    @FXML
    private MFXListView<?> listviewEquiposSeleccionadosTorneo;
    @FXML
    private MFXButton btnReanudarTorneo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleTableClickTorneosActivos(MouseEvent event) {
    }

    @FXML
    private void OnActionBtnReanudarTorneo(ActionEvent event) {
    }

    @Override
    public void initialize() {

    }
}
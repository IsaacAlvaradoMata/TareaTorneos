/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class TournamentMaintenanceController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTournamentCreationTitulo;
    @FXML
    private Separator sprTournamentMaintenance;
    @FXML
    private MFXButton btnGuardarTorneo;
    @FXML
    private MFXFilterComboBox<?> cmbMantenimientoTorneo;
    @FXML
    private MFXTableView<?> tbvMantenimientoTorneo;
    @FXML
    private MFXButton btnEliminarTorneo;
    @FXML
    private MFXTextField txtfieldNombreMantenimiento;
    @FXML
    private MFXFilterComboBox<?> cmbDeportesRegistradosMantenimiento;
    @FXML
    private MFXTextField txtfieldTiempoMantenimiento;
    @FXML
    private MFXTextField txtfieldCantidadMantenimiento;
    @FXML
    private MFXButton btnEliminarEquipos;
    @FXML
    private TabPane tabPanePrincipal;
    @FXML
    private Tab tabMantenimientoTorneos;
    @FXML
    private ImageView imgLupaMorada;
    @FXML
    private ImageView imgInfoTorneos;
    @FXML
    private MFXButton btnAgregarEquiposMantenimientoTorneo;
    @FXML
    private MFXButton btnModificarTorneo;
    @FXML
    private MFXButton btnBarrrerInfoTorneo;
    @FXML
    private ImageView imgInfoCreacionTorneo;
    @FXML
    private Tab tabSeleccionEquipos;
    @FXML
    private Label lblNombreTorneoSeleccionEquipos;
    @FXML
    private Label lblCantidadEquiposSeleccionEquipos;
    @FXML
    private MFXCheckListView<?> chklistviewEquiposSeleccionados1;
    @FXML
    private MFXTextField txtfieldNombreMantenimiento1;
    @FXML
    private MFXButton btnBuscarPorNombre;
    @FXML
    private MFXCheckListView<?> chklistviewEquiposDisponibles1;
    @FXML
    private MFXButton btnAgregarEquiposSeleccionEquipos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    




    @FXML
    private void OnActionBtnGuardarTorneo(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnEliminarTorneo(ActionEvent event) {
    }


    @FXML
    private void OnActionBtnEliminarEquipos(ActionEvent event) {
    }

    @FXML
    private void handleTableClickTorneosMantenimiento(MouseEvent event) {
    }

    @FXML
    private void OnActionBtnAgregarEquiposMantenimientoTorneo(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnModificarTorneo(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnBarrerInfoTorneo(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnBuscarPorNombre(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnAgregarEquiposSeleccionEquipos(ActionEvent event) {
    }

    @Override
    public void initialize() {

    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class RankingsController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblRankingsTitulo;
    @FXML
    private ImageView imgRankingTrophyIcon;
    @FXML
    private ImageView imgFlechaArriba;
    @FXML
    private Separator sprRankings;
    @FXML
    private MFXTableView<?> tbvEstadisticasGenerales;
    @FXML
    private MFXTableView<?> tbvEstadisticasAvanzadas;
    @FXML
    private MFXTableView<?> tbvRankingEquipos;
    @FXML
    private MFXFilterComboBox<?> cmbRankings;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleTableClickRankingEquipos(MouseEvent event) {
    }

    @FXML
    private void handleTableClickEstadisticasGenerales(MouseEvent event) {
    }

    @FXML
    private void handleTableClickEstadisticasAvanzadas(MouseEvent event) {
    }

    @Override
    public void initialize() {

    }
}

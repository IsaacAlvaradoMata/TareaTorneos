/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tareatorneos.controller;

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
public class AchievementsController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblLogrosTitulo;
    @FXML
    private MFXTableView<?> tbvLogrosEquipos;
    @FXML
    private ImageView imgFlechaArribaLogros;
    @FXML
    private Separator sprLogros;
    @FXML
    private ImageView imgLogrosIcon;
    @FXML
    private ImageView imgGanador8Torneos;
    @FXML
    private ImageView imgGanador6Torneos;
    @FXML
    private ImageView imgGanador3Torneos;
    @FXML
    private ImageView img20Puntos;
    @FXML
    private ImageView imgEscudo;
    @FXML
    private ImageView imgBalanza;
    @FXML
    private ImageView img3Consecutivos;
    @FXML
    private ImageView imgMontanaRusa;
    @FXML
    private ImageView imgMedallaTorneo;
    @FXML
    private ImageView imgInfo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleTableClickLogrosEquipos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickGanador8Torneos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickGanador6Torneos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickGanador3Torneos(MouseEvent event) {
    }

    @FXML
    private void handleImageClick20Puntos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickEscudo(MouseEvent event) {
    }

    @FXML
    private void handleImageClickBalanza(MouseEvent event) {
    }

    @FXML
    private void handleImageClick3Consecutivos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickMontanaRusa(MouseEvent event) {
    }

    @FXML
    private void handleImageClickMedallaTorneo(MouseEvent event) {
    }

    @Override
    public void initialize() {

    }
}

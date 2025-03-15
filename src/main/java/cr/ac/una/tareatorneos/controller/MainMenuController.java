package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Node;

public class MainMenuController extends Controller implements Initializable {

    @FXML
    private AnchorPane achpSlider;

    @FXML
    private MFXButton btnCreacionTorneos;

    @FXML
    private MFXButton btnMantenimientoDeportes;

    @FXML
    private MFXButton btnMantenimientoEquipos;

    @FXML
    private MFXButton btnLogros;

    @FXML
    private MFXButton btnRankings;

    @FXML
    private MFXButton btnTorneosActivos;

    @FXML
    private ImageView imgFullScreen;

    @FXML
    private ImageView imgMinimizar;

    @FXML
    private ImageView imgSalir;

    @FXML
    private Label lblMenu;

    @FXML
    private Label lblMenuAtras;

    @FXML
    void OnActionBtnCreacionTorneos(ActionEvent event) {
        FlowController.getInstance().goView("TournamentCreationView");
    }

    @FXML
    void OnActionBtnMantenimientoDeportes(ActionEvent event) {
        FlowController.getInstance().goView("SportsMaintenanceView");


    }

    @FXML
    void OnActionBtnMantenimientoEquipos(ActionEvent event) {
        FlowController.getInstance().goView("TeamsMaintenanceView");

    }

    @FXML
    void OnActionBtnRankings(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLogros(ActionEvent event) {

    }

    @FXML
    void OnActionBtnTorneosActivos(ActionEvent event) {

    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(() -> achpSlider.requestFocus());
        imgSalir.setOnMouseClicked(event -> {
            System.exit(0);
        });

        imgMinimizar.setOnMouseClicked(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setIconified(true); // Minimiza la ventana
        });

        imgFullScreen.setOnMouseClicked(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen()); // Alterna pantalla completa
        });


        achpSlider.setTranslateX(-176);
        lblMenu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(achpSlider);

            slide.setToX(0);
            slide.play();

            achpSlider.setTranslateX(-176);

            slide.setOnFinished((ActionEvent e)-> {
                lblMenu.setVisible(false);
                lblMenuAtras.setVisible(true);
            });
        });

        lblMenuAtras.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(achpSlider);

            slide.setToX(-176);
            slide.play();

            achpSlider.setTranslateX(0);

            slide.setOnFinished((ActionEvent e)-> {
                lblMenu.setVisible(true);
                lblMenuAtras.setVisible(false);
            });
        });  // Generated from nbfs://nbhost/SysteeSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

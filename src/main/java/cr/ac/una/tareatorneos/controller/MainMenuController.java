package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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

    private BorderPane mainLayout;
    private List<MFXButton> botonesMenu;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        botonesMenu = Arrays.asList(
                btnCreacionTorneos,
                btnMantenimientoDeportes,
                btnMantenimientoEquipos,
                btnTorneosActivos,
                btnRankings,
                btnLogros
        );
        Platform.runLater(() -> {
            mainLayout = (BorderPane) achpSlider.getScene().getRoot();
            mainLayout.setLeft(achpSlider);
            achpSlider.setTranslateX(0);
            achpSlider.requestFocus();
        });
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
            stage.setMaximized(!stage.isMaximized()); // Alterna entre maximizado y normal
        });


        lblMenu.setOnMouseClicked(event -> {
            if (mainLayout.getLeft() == null) {
                mainLayout.setLeft(achpSlider);
                achpSlider.setTranslateX(-achpSlider.getWidth());
            }

            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), achpSlider);
            slide.setToX(0);
            slide.play();

            slide.setOnFinished(e -> {
                lblMenu.setVisible(false);
                lblMenuAtras.setVisible(true);
            });
        });

        lblMenuAtras.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), achpSlider);
            slide.setToX(-achpSlider.getWidth());
            slide.play();

            slide.setOnFinished(e -> {
                mainLayout.setLeft(null);
                lblMenu.setVisible(true);
                lblMenuAtras.setVisible(false);
            });
        });

    }

    @FXML
    void OnActionBtnCreacionTorneos(ActionEvent event) {
        seleccionarBotonMenu(btnCreacionTorneos);
        FlowController.getInstance().goView("TournamentMaintenanceView");
        TournamentMaintenanceController controller = (TournamentMaintenanceController)
                FlowController.getInstance().getController("TournamentMaintenanceView");
        controller.cargarTorneosEnTabla();

    }

    @FXML
    void OnActionBtnMantenimientoDeportes(ActionEvent event) {
        seleccionarBotonMenu(btnMantenimientoDeportes);
        FlowController.getInstance().goView("SportsMaintenanceView");

    }

    @FXML
    void OnActionBtnMantenimientoEquipos(ActionEvent event) {
        seleccionarBotonMenu(btnMantenimientoEquipos);
        FlowController.getInstance().goView("TeamsMaintenanceView");

    }

    @FXML
    void OnActionBtnRankings(ActionEvent event) {
        seleccionarBotonMenu(btnRankings);
        FlowController.getInstance().goView("RankingsView");

    }

    @FXML
    void OnActionBtnLogros(ActionEvent event) {
        seleccionarBotonMenu(btnLogros);
        FlowController.getInstance().goView("AchievementsView");
        AchievementsController controller = (AchievementsController)
                FlowController.getInstance().getController("AchievementsView");
        controller.refrescarLogros();
    }

    @FXML
    void OnActionBtnTorneosActivos(ActionEvent event) {
        seleccionarBotonMenu(btnTorneosActivos);
        FlowController.getInstance().goView("ActiveTournamentsView");

    }

    @Override
    public void initialize() {

    }

    private void seleccionarBotonMenu(MFXButton botonPresionado) {
        for (MFXButton boton : botonesMenu) {
            boton.getStyleClass().remove("boton-seleccionado");
        }
        botonPresionado.getStyleClass().add("boton-seleccionado");
    }
}

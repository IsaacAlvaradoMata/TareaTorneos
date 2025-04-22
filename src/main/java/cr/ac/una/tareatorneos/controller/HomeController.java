/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tareatorneos.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.tareatorneos.util.AnimationDepartment;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class HomeController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private StackPane spHomeIcon;
    @FXML
    private ImageView imgHomeIcon;
    @FXML
    private VBox vboxIcon;
    @FXML
    private VBox vboxTitle;
    @FXML
    private VBox vboxSubtitle;
    @FXML
    private StackPane spFondoHome;
    private Image trophyRain;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        AnimationDepartment.slideFromTop(vboxIcon, Duration.seconds(1));
        AnimationDepartment.fadeIn(vboxTitle, Duration.seconds(2));

        vboxSubtitle.setTranslateY(0);
        vboxSubtitle.setOpacity(0);
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                double sceneHeight = spFondoHome.getHeight();
                AnimationDepartment.slideUpWithEpicBounceClean(vboxSubtitle, Duration.seconds(2.5), sceneHeight);
            });
        });
        trophyRain = new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/TrophyRainIcon.png"));
        AnimationDepartment.startInfiniteRainingAchievements(spFondoHome, trophyRain, 6, Duration.seconds(1), Duration.seconds(5));



    }

    @Override
    public void initialize() {

    }
}

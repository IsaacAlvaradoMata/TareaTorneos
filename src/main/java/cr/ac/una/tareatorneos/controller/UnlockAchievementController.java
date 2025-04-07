package cr.ac.una.tareatorneos.controller;


import java.net.URL;
import java.util.ResourceBundle;
import cr.ac.una.tareatorneos.util.AnimationDepartment;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.event.ActionEvent;

public class UnlockAchievementController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private StackPane stackPane;
    @FXML
    private StackPane spfondo;
    @FXML
    private VBox mainVBox;
    @FXML
    private HBox titleBox;
    @FXML
    private ImageView leffUnlock;
    @FXML
    private Label titleLabel;
    @FXML
    private ImageView rightUnlock;
    @FXML
    private Label lblAchievementName;
    @FXML
    private StackPane AchievementContainer;
    @FXML
    private ImageView imgAchievement;
    @FXML
    private ImageView imgUnlockgif;
    @FXML
    private Button btnCerrar;
    @FXML
    private Pane spLluvia;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadImagesLogros();
        spfondo.setClip(new javafx.scene.shape.Rectangle(
                spfondo.getPrefWidth(),
                spfondo.getPrefHeight()
        ));
        Platform.runLater(() -> {
            javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
            clip.widthProperty().bind(spfondo.widthProperty());
            clip.heightProperty().bind(spfondo.heightProperty());
            spfondo.setClip(clip);
        });
        imgAchievement.getStyleClass().add("persistent");
        imgUnlockgif.getStyleClass().add("persistent");
        leffUnlock.getStyleClass().add("persistent");
        rightUnlock.getStyleClass().add("persistent");
    }

    @Override
    public void initialize() {
        // vacío porque ya usamos el otro método
    }

    @FXML
    private void onActionBtnCerrar(ActionEvent event) {


    }

    private void loadImagesLogros() {
        leffUnlock.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/UnlockIcon.png")));
        rightUnlock.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/UnlockIcon.png")));
        imgAchievement.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/8TournamentsWinnerIcon.png")));
        imgUnlockgif.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/PadlockGifIcon.gif")));
    }

    public void runAchievementIntro(String achievementName) {
        resetAchievementView(); // 🔁 Reset antes de todo
        imgUnlockgif.setVisible(true);
        imgUnlockgif.setOpacity(1);
        imgUnlockgif.setScaleX(1.0);
        imgUnlockgif.setScaleY(1.0);
        imgUnlockgif.toFront();

        // 🔓 Animación de candado ➜ luego aparecen componentes
        AnimationDepartment.animateUnlockExplosion(imgUnlockgif, () -> {
            Platform.runLater(() -> runAnimationsLogros(achievementName));
        });
    }

    private void runAnimationsLogros(String achievementName) {
        lblAchievementName.setText(achievementName);
        titleLabel.setStyle("-fx-text-fill: linear-gradient(#FFD700, #FFA500);");

        AnimationDepartment.fadeIn(mainVBox, Duration.seconds(0));
        AnimationDepartment.slideFromTop(titleBox, Duration.seconds(0.5));
        AnimationDepartment.fadeIn(lblAchievementName, Duration.seconds(1.5));
        AnimationDepartment.animatedLightSweep(lblAchievementName);
        AnimationDepartment.neonGlowLoop(imgAchievement);

        Platform.runLater(() -> {
            Platform.runLater(() -> {
                double sceneHeight = spfondo.getHeight();

                AnimationDepartment.slideUpWithEpicBounceClean(AchievementContainer, Duration.seconds(1.5), sceneHeight);
                AnimationDepartment.revealAchievementImage(imgAchievement, Duration.seconds(2.5));

                // ⏳ Esperar a que se vea el logro antes de explotar partículas
                PauseTransition wait = new PauseTransition(Duration.seconds(3.1));
                wait.setOnFinished(e -> {
                    AnimationDepartment.goldenBurstExplosion(spfondo, 250, Duration.seconds(3.0));
                    Image logroImg = new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/8TournamentsWinnerIcon.png"));
                    AnimationDepartment.startInfiniteRainingAchievements(spLluvia, logroImg, 6, Duration.seconds(1), Duration.seconds(5));

                });
                wait.play();
            });
        });

        AnimationDepartment.fadeIn(btnCerrar, Duration.seconds(5.0));
    }


    public void resetAndRunAnimationsLogros(String teamName) {
        resetAchievementView();
        AnimationDepartment.stopAnimatedLightSweep();

        // 💥 Resetear transformaciones
        imgAchievement.setTranslateY(0);
        AchievementContainer.setTranslateY(0);


        // Reset de visibilidad y opacidad
        lblAchievementName.setOpacity(0);
        btnCerrar.setOpacity(0);
        AchievementContainer.setOpacity(0);
        imgAchievement.setOpacity(0);
        imgAchievement.setVisible(false);

        titleBox.setOpacity(0);
        lblAchievementName.setStyle("");

        runAchievementIntro(teamName);
    }


    private void resetAchievementView() {
        AnimationDepartment.stopRainingAchievements(spLluvia);
        AnimationDepartment.stopAnimatedLightSweep();


        spfondo.getChildren().removeIf(node ->
                node instanceof ImageView &&
                        node != imgAchievement &&  // 🏆 No eliminar la imagen principal del logro
                        node != imgUnlockgif &&    // 🔓 Tampoco el gif si es visible aún
                        node != leffUnlock && node != rightUnlock // 🔒 Iconos decorativos
        );
        // 🔁 Reset transformaciones
        AchievementContainer.setTranslateY(0);
        AchievementContainer.setLayoutY(0);
        AchievementContainer.setOpacity(0);
        lblAchievementName.setOpacity(0);
        titleBox.setOpacity(0);
        btnCerrar.setOpacity(0);
        lblAchievementName.setStyle("");

        // 👑 Preparar candado
        imgUnlockgif.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/PadlockGifIcon.gif"))); // 👈 volver a cargar la imagen fuerza el reinicio
        imgUnlockgif.setVisible(true);
        imgUnlockgif.setOpacity(1);
        imgUnlockgif.setScaleX(1.0);
        imgUnlockgif.setScaleY(1.0);

        // 🏆 Asegurar visibilidad inicial (oculto por opacidad)
        imgAchievement.setVisible(true);
        imgAchievement.setOpacity(0);
    }



}

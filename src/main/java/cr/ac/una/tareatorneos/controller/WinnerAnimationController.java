package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.AnimationDepartment;
import cr.ac.una.tareatorneos.util.PdfGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class WinnerAnimationController extends Controller implements Initializable {

    @FXML
    private VBox mainVBox;
    @FXML
    private HBox titleBox;
    @FXML
    private Label titleLabel;
    @FXML
    private Label teamNameLabel;
    @FXML
    private ImageView leftDecoration;
    @FXML
    private ImageView rightDecoration;
    @FXML
    private StackPane winnerContainer;
    @FXML
    private ImageView podiumImage;
    @FXML
    private StackPane teamImageBorder;
    @FXML
    private ImageView teamImage;
    @FXML
    private Button printButton;
    @FXML
    private ImageView imgCrown;
    @FXML
    private ImageView imgSparkle;
    @FXML
    private StackPane spfondo;
    @FXML
    private ImageView imgLuz;
    @FXML
    private Pane confettiPane;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadImages();
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
    }

    @Override
    public void initialize() {

    }

    @FXML
    void OnActionprintButton(ActionEvent event) {
        String nombreEquipo = teamNameLabel.getText();
        var statsService = new cr.ac.una.tareatorneos.service.TeamTournamentStatsService();
        var teamStats = statsService.getStatsByTeamName(nombreEquipo);

        if (teamStats != null && !teamStats.getTorneos().isEmpty()) {
            var torneo = teamStats.getTorneos().get(teamStats.getTorneos().size() - 1);

            String torneoNombre = torneo.getNombreTorneo();
            String deporte = new cr.ac.una.tareatorneos.service.TournamentService()
                    .getTournamentByName(torneoNombre)
                    .getDeporte();

            String rutaImg = "teamsPhotos/" + new cr.ac.una.tareatorneos.service.TeamService()
                    .getTeamByName(nombreEquipo).getTeamImage();

            PdfGenerator.crearCarneCampeon(nombreEquipo, torneoNombre, deporte, rutaImg, torneo);
        }

        Platform.runLater(() -> {
            Stage stage = (Stage) printButton.getScene().getWindow();
            if (stage != null) {
                onClose();
                stage.close();
            }
        });

    }

    private void loadImages() {
        leftDecoration.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/AwardIcon.png")));
        rightDecoration.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/AwardIcon.png")));
        podiumImage.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/Winner1Icon.png")));
        teamImage.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/LakersIcon.jpg")));
        imgCrown.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/CrownIcon.png")));
        imgCrown.setVisible(false);
        imgSparkle.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/SparkleIcon.png")));
        imgSparkle.setVisible(false);
        imgLuz.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/AngelicalLightIcon.png")));
        imgLuz.setVisible(false);
    }

    private void runAnimations(String teamName) {
        teamNameLabel.setText(teamName);
        titleLabel.setStyle("-fx-text-fill: linear-gradient(#FFD700, #FFA500);");

        AnimationDepartment.fadeIn(mainVBox, Duration.seconds(0));

        AnimationDepartment.slideFromTop(titleBox, Duration.seconds(0.5));

        AnimationDepartment.fadeIn(teamNameLabel, Duration.seconds(1.5));

        AnimationDepartment.animatedLightSweep(teamNameLabel);

        Platform.runLater(() -> {
            Platform.runLater(() -> {
                double sceneHeight = spfondo.getHeight();
                AnimationDepartment.slideUpWithEpicBounceClean(winnerContainer, Duration.seconds(1.5), sceneHeight);
            });
        });

        AnimationDepartment.glowBorder(teamImageBorder);

        AnimationDepartment.subtleBounce(winnerContainer);

        AnimationDepartment.fadeIn(printButton, Duration.seconds(5.0));

        AnimationDepartment.spotlightEffect(imgLuz, Duration.seconds(3.0));

        AnimationDepartment.dropCrown(imgCrown, Duration.seconds(3.4));

        AnimationDepartment.sparkleEffect(imgSparkle, Duration.seconds(3.9));

        AnimationDepartment.startConfettiLoopWithDelay(confettiPane, 10, Duration.millis(400), Duration.seconds(4.0));

    }

    public void resetAndRunAnimations(String teamName) {
        AnimationDepartment.stopConfetti();
        confettiPane.getChildren().clear();
        AnimationDepartment.stopAnimatedLightSweep();
        winnerContainer.setTranslateY(0);
        winnerContainer.setOpacity(0);

        imgCrown.setVisible(false);
        imgCrown.setOpacity(0);
        imgCrown.setTranslateY(-200);

        imgSparkle.setVisible(false);
        imgSparkle.setOpacity(0);

        imgLuz.setVisible(false);
        imgLuz.setOpacity(0);

        teamNameLabel.setOpacity(0);
        printButton.setOpacity(0);
        winnerContainer.setOpacity(0);
        titleBox.setOpacity(0);
        teamNameLabel.setStyle("");

        try {
            String rawPath = new cr.ac.una.tareatorneos.service.TeamService().getTeamByName(teamName).getTeamImage();
            String finalPath = rawPath != null ? "file:teamsPhotos/" + rawPath : "file:teamsPhotos/default.png";
            teamImage.setImage(new Image(finalPath));
        } catch (Exception e) {
            System.out.println("No se pudo cargar imagen del equipo " + teamName);
            teamImage.setImage(new Image("file:teamsPhotos/default.png"));
        }

        Platform.runLater(() -> runAnimations(teamName));
    }

    public void onClose() {
        AnimationDepartment.stopConfetti();
        confettiPane.getChildren().clear();
        AnimationDepartment.stopAnimatedLightSweep();
        winnerContainer.setTranslateY(0);
        winnerContainer.setOpacity(0);

        imgCrown.setVisible(false);
        imgCrown.setOpacity(0);
        imgCrown.setTranslateY(-200);

        imgSparkle.setVisible(false);
        imgSparkle.setOpacity(0);

        imgLuz.setVisible(false);
        imgLuz.setOpacity(0);

        teamNameLabel.setOpacity(0);
        printButton.setOpacity(0);
        winnerContainer.setOpacity(0);
        titleBox.setOpacity(0);
        teamNameLabel.setStyle("");
    }

}

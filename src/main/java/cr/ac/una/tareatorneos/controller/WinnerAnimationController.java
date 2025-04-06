package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.AnimationDepartment;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.awt.*;
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
        runAnimations("Los Titanes del Código"); // Puedes reemplazar esto con el nombre dinámico del equipo
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

    private void loadImages() {
        leftDecoration.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/AwardIcon.png"))); // Reemplaza con tus imágenes reales
        rightDecoration.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/AwardIcon.png")));
        podiumImage.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/Winner1Icon.png")));
        teamImage.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/LakersIcon.jpg"))); // Reemplaza con imagen dinámica si quieres
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
//        teamNameLabel.setStyle("-fx-text-fill: linear-gradient(#FFD700, #cc8702);");

        // Animar el contenedor principal (fade in total)
        AnimationDepartment.fadeIn(mainVBox, Duration.seconds(0));

        // Animar el título y decoraciones desde arriba
        AnimationDepartment.slideFromTop(titleBox, Duration.seconds(0.5));

        // Nombre del equipo (fade in simple)
        AnimationDepartment.fadeIn(teamNameLabel, Duration.seconds(1.5));

//        AnimationDepartment.neonGlowLoop(teamNameLabel);
        AnimationDepartment.animatedLightSweep(teamNameLabel);

        // Imagen del podio y foto del equipo desde abajo
//        AnimationDepartment.slideUpWithEpicBounce(winnerContainer, Duration.seconds(1.5));
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                double sceneHeight = spfondo.getHeight(); // o root.getHeight()
                System.out.println("✅ Altura confirmada al segundo frame: " + sceneHeight);

                AnimationDepartment.slideUpWithEpicBounceClean(winnerContainer, Duration.seconds(1.5), sceneHeight);
            });
        });


        // Aplicar animación de borde brillante
        AnimationDepartment.glowBorder(teamImageBorder);

        // Movimiento sutil del podio
        AnimationDepartment.subtleBounce(winnerContainer);

        // Mostrar el botón al final
        AnimationDepartment.fadeIn(printButton, Duration.seconds(5.0));

        AnimationDepartment.spotlightEffect(imgLuz, Duration.seconds(3.0));

        AnimationDepartment.dropCrown(imgCrown, Duration.seconds(3.4));

        AnimationDepartment.sparkleEffect(imgSparkle, Duration.seconds(3.9));

        AnimationDepartment.startConfettiLoopWithDelay(confettiPane, 10, Duration.millis(400), Duration.seconds(4.0));

    }

    public void resetAndRunAnimations(String teamName) {
        AnimationDepartment.stopConfetti(); // Detiene el loop anterior
        confettiPane.getChildren().clear(); // Limpia cualquier partícula vieja
        AnimationDepartment.stopAnimatedLightSweep();
        winnerContainer.setTranslateY(0); // 🔁 Resetear transformaciones acumuladas
        winnerContainer.setOpacity(0);    // Asegura opacidad reiniciada

        // Reset estados visuales
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

        // Detener confetti si ya estaba corriendo
        AnimationDepartment.stopConfetti();

        // Reiniciar estilo del nombre
        teamNameLabel.setStyle(""); // limpiamos estilos previos

        // Ejecutar animaciones como si fuera la primera vez
        runAnimations(teamName);
    }



}

package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Achievement;
import cr.ac.una.tareatorneos.util.AchievementImageMapper;
import cr.ac.una.tareatorneos.util.AnimationDepartment;
import javafx.animation.PauseTransition;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private Label lblEquipoGanador;
    @FXML
    private Pane spLluvia;
    private Image logroActualImagen; // Guarda la imagen del logro actual

    private List<Achievement> colaLogros = new ArrayList<>();
    private int indiceActual = 0;
    private Runnable callbackFinal; // opcional, para ejecutar algo al final
    private boolean estaVentanaActiva = true;

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
        estaVentanaActiva = false;

        onClose();

        // cerrar esta ventana
        if (currentStage != null) currentStage.close();

        // mostrar el siguiente logro
        Platform.runLater(() -> mostrarSiguienteLogro());
    }

    public void mostrarLogrosEnCadena(List<Achievement> logros, Runnable onFinish) {
        if (logros == null || logros.isEmpty()) return;

        this.colaLogros = logros;
        this.indiceActual = 0;
        this.callbackFinal = onFinish;

        Platform.runLater(() -> {
            currentStage = (Stage) root.getScene().getWindow();
            mostrarSiguienteLogro(); // se ejecuta desde aquí para garantizar el stage actual
        });
    }

    private void mostrarSiguienteLogro() {
        if (indiceActual < colaLogros.size()) {
            Achievement logro = colaLogros.get(indiceActual);
            indiceActual++;

            resetAndRunAnimationsLogros(logro.getNombre(), logro.getEquipoAsociado(), () -> {

                // Espera que el usuario presione "Cerrar", NO pasa automáticamente
                btnCerrar.setOnAction(event -> {
                    mostrarSiguienteLogro(); // avanza cuando el usuario lo decida
                });
            });

        } else {
            if (callbackFinal != null) callbackFinal.run();
        }
    }

    private void loadImagesLogros() {
        leffUnlock.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/UnlockIcon.png")));
        rightUnlock.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/UnlockIcon.png")));
        imgAchievement.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/8TournamentsWinnerIcon.png")));
        imgUnlockgif.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/PadlockGifIcon.gif")));
    }

    private void runAnimationsLogros(String achievementName, String equipoGanador, Runnable onDone) {

        if (!estaVentanaActiva) {
            System.out.println("⛔ Cancelado runAnimationsLogros: ventana cerrada.");
            return;
        }

        lblAchievementName.setText(achievementName);
        titleLabel.setStyle("-fx-text-fill: linear-gradient(#FFD700, #FFA500);");

        lblEquipoGanador.setText("¡Felicidades! El equipo " + equipoGanador + " ha conseguido un nuevo logro.");
        lblEquipoGanador.setOpacity(0);


        AnimationDepartment.fadeIn(mainVBox, Duration.seconds(0));
        AnimationDepartment.slideFromTop(titleBox, Duration.seconds(0.5));
        AnimationDepartment.fadeIn(lblAchievementName, Duration.seconds(1.5));
        AnimationDepartment.animatedLightSweep(lblAchievementName);
        AnimationDepartment.fadeIn(lblEquipoGanador, Duration.seconds(3.0));
        AnimationDepartment.neonGlowLoop(imgAchievement);

        Platform.runLater(() -> {
            double sceneHeight = spfondo.getHeight();

            AnimationDepartment.slideUpWithEpicBounceClean(AchievementContainer, Duration.seconds(1.5), sceneHeight);
            AnimationDepartment.revealAchievementImage(imgAchievement, Duration.seconds(2.5));
 
            PauseTransition wait = new PauseTransition(Duration.seconds(4.0));
            wait.setOnFinished(e -> {
                AnimationDepartment.goldenBurstExplosion(spfondo, 250, Duration.seconds(3.0));
                if (logroActualImagen == null) {
                    System.err.println("⛔ No se puede iniciar lluvia: imagen del logro '" + achievementName + "' no fue cargada correctamente.");
                    return;
                }

                if (!estaVentanaActiva) {
                    System.out.println("⛔ No iniciar lluvia: ventana ya fue cerrada.");
                    return;
                }

//                AnimationDepartment.startInfiniteRainingAchievements(spLluvia, logroImg, 6, Duration.seconds(1), Duration.seconds(5));
                System.out.println("🌧 Iniciando lluvia con imagen de logro: " + achievementName);
                AnimationDepartment.startInfiniteRainingAchievements(spLluvia, logroActualImagen, 6, Duration.seconds(1), Duration.seconds(5));

                PauseTransition finalizar = new PauseTransition(Duration.seconds(4.0));
                finalizar.setOnFinished(ev -> {
                    if (onDone != null) onDone.run();
                });
                finalizar.play();
            });
            wait.play();
        });

        AnimationDepartment.fadeIn(btnCerrar, Duration.seconds(5.0));
    }


    public void resetAndRunAnimationsLogros(String achievementName, String equipoGanador, Runnable onDone){

        if (!estaVentanaActiva) {
            System.out.println("⛔ Cancelado resetAndRunAnimationsLogros: ventana cerrada.");
            return;
        }

        resetAchievementView();
        AnimationDepartment.stopAnimatedLightSweep();

        String ruta = AchievementImageMapper.getRutaImagen(achievementName);
        if (ruta == null) {
            System.err.println("⛔ Animación cancelada: no hay ruta válida para el logro '" + achievementName + "'");
            return;
        }

        var stream = getClass().getResourceAsStream(ruta);
        if (stream == null) {
            System.err.println("⛔ No se encontró la imagen del logro '" + achievementName + "' en: " + ruta);
            return;
        }

        logroActualImagen = new Image(stream);
        imgAchievement.setImage(logroActualImagen);

        imgAchievement.setTranslateY(0);
        AchievementContainer.setTranslateY(0);
        imgAchievement.setVisible(false);
        imgAchievement.setOpacity(0);
        lblAchievementName.setOpacity(0);
        btnCerrar.setOpacity(0);
        AchievementContainer.setOpacity(0);
        lblAchievementName.setStyle("");
        titleBox.setOpacity(0);
        lblEquipoGanador.setText("");
        lblEquipoGanador.setOpacity(0);

        runAchievementIntro(achievementName, equipoGanador, onDone); // 👈 ahora correcto
    }


    public void runAchievementIntro(String achievementName, String equipoGanador, Runnable onDone) {
        resetAchievementView();
        imgUnlockgif.setVisible(true);
        imgUnlockgif.setOpacity(1);
        imgUnlockgif.setScaleX(1.0);
        imgUnlockgif.setScaleY(1.0);
        imgUnlockgif.toFront();

        AnimationDepartment.animateUnlockExplosion(imgUnlockgif, () -> {
            if (!estaVentanaActiva) {
                System.out.println("⛔ Cancelado antes de animar logro: ventana cerrada.");
                return;
            }
            Platform.runLater(() -> runAnimationsLogros(achievementName, equipoGanador, onDone));
        });
    }



    private Stage currentStage;

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
        lblEquipoGanador.setText("");
        lblEquipoGanador.setOpacity(0);

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

    public void onClose() {
        estaVentanaActiva = false;

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
        lblEquipoGanador.setText("");
        lblEquipoGanador.setOpacity(0);

        // 👑 Preparar candado
        imgUnlockgif.setImage(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/PadlockGifIcon.gif"))); // 👈 volver a cargar la imagen fuerza el reinicio
        imgUnlockgif.setVisible(true);
        imgUnlockgif.setOpacity(1);
        imgUnlockgif.setScaleX(1.0);
        imgUnlockgif.setScaleY(1.0);

        // 🏆 Asegurar visibilidad inicial (oculto por opacidad)
        imgAchievement.setVisible(true);
        imgAchievement.setOpacity(0);
        System.out.println("🎬 Animaciones detenidas correctamente al cerrar.");
    }

}

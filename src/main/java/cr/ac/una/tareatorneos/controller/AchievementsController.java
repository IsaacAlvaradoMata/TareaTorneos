/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tareatorneos.controller;


import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Duration;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.net.URL;
import java.util.Map;
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

    @FXML
    private MFXFilterComboBox<?> cmbAchievements;


    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        Tooltip tooltip = new Tooltip("Haga click sobre los logros para ver la descripción de cada uno de ellos.");
        tooltip.setStyle("-fx-background-color: rgba(238, 189, 149, 0.9); " +
                "-fx-text-fill: #a25600; " +
                "-fx-padding: 8px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-font-family: \"Trebuchet MS\", sans-serif; " +
                "-fx-border-color: #a25600; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px;");

        // 🔹 Configurar la duración de la visibilidad
        tooltip.setShowDelay(Duration.millis(200)); // Aparece después de 200ms
        tooltip.setHideDelay(Duration.millis(100)); // Desaparece rápido al salir

        // 🔹 Asociar el Tooltip al icono
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        // 🔹 Manejar manualmente la posición del Tooltip
        imgInfo.setOnMouseEntered(event -> {
            double x = imgInfo.localToScene(imgInfo.getBoundsInLocal()).getMinX();
            double y = imgInfo.localToScene(imgInfo.getBoundsInLocal()).getMinY();

            tooltip.show(imgInfo, imgInfo.getScene().getWindow().getX() + x -200,
                    imgInfo.getScene().getWindow().getY() + y -52); // Ajusta posición arriba del icono
        });

        imgInfo.setOnMouseExited(event -> tooltip.hide()); // Ocultar tooltip al salir del icono


        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();

            this.dialogContent = MFXGenericDialogBuilder.build()
                    .makeScrollable(true)
                    .get();

            this.dialog = MFXGenericDialogBuilder.build(dialogContent)
                    .toStageDialogBuilder()
                    .initOwner(stage)
                    .initModality(Modality.APPLICATION_MODAL)
                    .setDraggable(true)
                    .setTitle("Información de Logros")
                    .setOwnerNode(root)
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();

            dialogContent.addActions(

                    Map.entry(new MFXButton("Cerrar"), event -> dialog.close())
            );

            dialogContent.setMaxSize(400, 200);
        });
    }

    @FXML
    private void handleTableClickLogrosEquipos(MouseEvent event) {
    }

    @FXML
    private void handleImageClickGanador8Torneos(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-trophy", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Dominador Supremo");
        dialogContent.setContentText("¡Ocho títulos no son para cualquiera! Tu equipo se ha consolidado como una verdadera dinastía, dejando su huella en la historia de la competición.\n\n" +
                "🔓 Cómo conseguirlo: Gana un total de 8 torneos.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClickGanador6Torneos(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-trophy", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Leyenda Plateada");
        dialogContent.setContentText("Seis torneos ganados demuestran que tu equipo no es solo una sorpresa, sino una leyenda en construcción. ¡Sigue sumando trofeos!.\n\n" +
                "🔓 Cómo conseguirlo: Gana un total de 6 torneos.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClickGanador3Torneos(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-trophy", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Tricampeón");
        dialogContent.setContentText("Tres campeonatos ya están en la vitrina. Tu equipo empieza a ser temido por sus rivales y reconocido por su grandeza.\n\n" +
                "🔓 Cómo conseguirlo: Gana un total de 3 torneos.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClick20Puntos(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-bolt-lightning", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Máxima Potencia");
        dialogContent.setContentText("¡Pura ofensiva! Tu equipo ha logrado marcar más de 20 puntos en un solo torneo, demostrando que el ataque es su mejor defensa.\n\n" +
                "🔓 Cómo conseguirlo: Anota más de 20 puntos a lo largo de un torneo.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClickEscudo(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-shield-halved", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Muralla Imbatible");
        dialogContent.setContentText("Defensa inquebrantable. Tu equipo ha ganado un torneo sin recibir ni una sola anotación en contra. ¡Un verdadero muro de acero!.\n\n" +
                "🔓 Cómo conseguirlo: Gana un torneo sin que el equipo reciba ninguna anotación en contra.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClickBalanza(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-scale-balanced", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Equilibrio Perfecto");
        dialogContent.setContentText("Tu equipo ha demostrado sangre fría y resistencia en los momentos más críticos. Después de empate en el tiempo reglamentario, supo demostrar que nunca se rinde hasta el último segundo.\n\n" +
                "🔓 Cómo conseguirlo: Empata 5 partidos en el tiempo reglamentario y luego gana cada uno de ellos en el desempate.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClick3Consecutivos(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-fire", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Imparable");
        dialogContent.setContentText("Cuando se encienden, no hay quien los detenga. Tu equipo ha logrado una racha de tres victorias consecutivas en un mismo torneo, demostrando su dominio en la cancha.\n\n" +
                "🔓 Cómo conseguirlo: Gana 3 partidos consecutivos en un torneo.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();

    }

    @FXML
    private void handleImageClickMontanaRusa(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-arrow-trend-up", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Regreso Triunfal");
        dialogContent.setContentText("Después de una amarga derrota en el torneo anterior, tu equipo ha resurgido de las cenizas y ha conquistado la gloria. ¡La revancha es dulce!.\n\n" +
                "🔓 Cómo conseguirlo: Gana un torneo después de haber perdido el torneo anterior.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();
    }

    @FXML
    private void handleImageClickMedallaTorneo(MouseEvent event) {
        MFXFontIcon infoIcon = new MFXFontIcon("fas-medal", 18);
        dialogContent.setHeaderIcon(infoIcon);
        dialogContent.setHeaderText("Campeón Inaugural");
        dialogContent.setContentText("Tu equipo ha demostrado su valía y ha conquistado su primer torneo. Este es solo el comienzo de una gran historia.\n\n" +
                "🔓 Cómo conseguirlo: Gana un torneo por primera vez.");
        convertDialogTo("mfx-info-dialog");
        dialog.showDialog();

    }

    private void convertDialogTo(String styleClass) {
        dialogContent.getStyleClass().removeIf(
                s -> s.equals("mfx-info-dialog") || s.equals("mfx-warn-dialog") || s.equals("mfx-error-dialog")
        );

        if (styleClass != null)
            dialogContent.getStyleClass().add(styleClass);
    }

}

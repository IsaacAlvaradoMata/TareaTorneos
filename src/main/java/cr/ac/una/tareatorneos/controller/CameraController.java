package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class CameraController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private Label lblMantenimientoEquiposTitulo;
    @FXML private StackPane spImagenEquipos;
    @FXML private ImageView imgviewPrevistaFoto;
    @FXML private Separator sprTeamsMaintenance;
    @FXML private ImageView imgviewDefinitivaFoto;
    @FXML private MFXButton btnCapturarFoto;
    @FXML private MFXButton btnReintentarFoto;
    @FXML private MFXButton btnGuardarFoto;
    @FXML private MFXButton btnSalirFoto;

    private Webcam webcam;
    private ScheduledExecutorService executor;
    private File savedImageFile;
    private boolean cameraActive = false;

    @Override
    public void initialize() {
        resetUI();
        openCamera();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Implementación vacía para cumplir con la clase abstracta.
    }

    private void openCamera() {
        try {
            if (webcam == null) {
                webcam = Webcam.getDefault();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
            }
            if (!webcam.isOpen()) {
                webcam.open();
            }
            startCameraPreview();
            cameraActive = true;
        } catch (Exception ex) {
            System.err.println("Cámara no encontrada: " + ex.getMessage());
            disableCameraButtons();
        }
    }

    @FXML
    private void OnActionBtnCapturarFoto() {
        try {
            String folderPath = "./Photos";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdir();
            }

            String uniqueName = "captured_photo_" + System.currentTimeMillis() + ".jpg";
            savedImageFile = new File(folder, uniqueName);

            ImageIO.write(webcam.getImage(), "JPG", savedImageFile);
            System.out.println("Imagen guardada correctamente en: " + savedImageFile.getAbsolutePath());

            displayCapturedImage(savedImageFile);
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }

        btnReintentarFoto.setDisable(false);
        btnGuardarFoto.setDisable(false);
        btnCapturarFoto.setDisable(true);
        btnSalirFoto.setDisable(true);
    }

    @FXML
    public void OnActionBtnReintentarFoto() {
        if (savedImageFile != null && savedImageFile.exists()) {
            savedImageFile.delete();
        }
        imgviewDefinitivaFoto.setImage(null);
        resetUI();
    }

    @FXML
    public void OnActionBtnGuardarFoto() {
        AppContext.getInstance().set("teamPhoto", savedImageFile.getAbsolutePath());
        TeamsMaintenanceController.actualizarImagenEquipo(savedImageFile.getAbsolutePath());

        showAlert("Foto guardada exitosamente.");
        stopCamera();
        closeWindow();
    }


    @FXML
    public void OnActionBtnSalirFoto() {
        stopCamera();
        closeWindow();
    }

    private void startCameraPreview() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (webcam != null && webcam.isOpen()) {
                Image image = SwingFXUtils.toFXImage(webcam.getImage(), null);
                imgviewPrevistaFoto.setImage(image);
            }
        }, 1, 33, TimeUnit.MILLISECONDS);
    }

    private void stopCamera() {
        cameraActive = false;
        if (executor != null) {
            executor.shutdown();
        }
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    private void displayCapturedImage(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            imgviewDefinitivaFoto.setImage(image);
        } catch (Exception e) {
            System.err.println("Error al mostrar la imagen capturada: " + e.getMessage());
        }
    }

    private void resetUI() {
        imgviewDefinitivaFoto.setImage(null);
        btnCapturarFoto.setDisable(false);
        btnReintentarFoto.setDisable(true);
        btnGuardarFoto.setDisable(true);
        btnSalirFoto.setDisable(false);
    }

    private void disableCameraButtons() {
        btnCapturarFoto.setDisable(true);
        btnReintentarFoto.setDisable(true);
        btnGuardarFoto.setDisable(true);
        btnSalirFoto.setDisable(false);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cámara");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        ((Stage) btnSalirFoto.getScene().getWindow()).close();
    }
}

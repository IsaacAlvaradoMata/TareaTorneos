package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.*;
import cr.ac.una.tareatorneos.service.*;
import cr.ac.una.tareatorneos.util.AchievementAnimationQueue;
import cr.ac.una.tareatorneos.util.AchievementUtils;
import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MatchController extends Controller implements Initializable {

    @FXML
    private MFXButton btnFinalizar;
    @FXML
    private ImageView imgBalon, imgEquipoA, imgEquipoB, imgFondoDeporte;
    @FXML
    private Label lblTiempo, lblTorneo, lblEquipoA, lblEquipoB, lblPuntajeA, lblPuntajeB;
    @FXML
    private AnchorPane root;
    @FXML
    private StackPane spMatch;

    private MatchService matchService;
    private Timeline countdown;
    private int tiempoRestante;
    private boolean popupMostrado = false;

    private BracketMatch partidoActual;
    private BracketMatchService bracketService;
    private BracketGeneratorController bracketParent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarEventosDragAndDrop();
        imgFondoDeporte.fitHeightProperty().bind(spMatch.heightProperty());
        imgFondoDeporte.fitWidthProperty().bind(spMatch.widthProperty());
    }

    @Override
    public void initialize() {
        // requerido por clase Controller base
    }

    public void inicializarMatch(BracketMatch partido, BracketMatchService bracketMatchService, BracketGeneratorController parentController) {
        this.matchService = new MatchService(
                new TournamentService().getTournamentByName(partido.getTorneo()),
                new TeamService().getTeamByName(partido.getEquipo1()),
                new TeamService().getTeamByName(partido.getEquipo2())
        );

        this.partidoActual = partido;
        this.bracketService = bracketMatchService;
        this.bracketParent = parentController;

        initializeMatch(partido.getTorneo(), partido.getEquipo1(), partido.getEquipo2());
    }

    private void mostrarPopupFinalizado() {
        if (popupMostrado) return;
        popupMostrado = true;

        cancelarDragActivo();

        int puntajeA = matchService.getPuntajeA();
        int puntajeB = matchService.getPuntajeB();
        String equipoA = lblEquipoA.getText();
        String equipoB = lblEquipoB.getText();

        if (puntajeA == puntajeB) {
            Alert empateAlert = new Alert(Alert.AlertType.INFORMATION);
            empateAlert.setTitle("🏁 Empate Detectado");
            empateAlert.setHeaderText("⚠ El partido terminó en empate");
            empateAlert.setContentText("Se iniciará una ronda de desempate.");
            empateAlert.showAndWait();
            iniciarPantallaDesempate(equipoA, equipoB);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partido Finalizado");
        alert.setHeaderText("¡El partido ha concluido!");

        StringBuilder resultado = new StringBuilder();
        resultado.append("📊 Marcador Final\n\n");
        resultado.append(String.format("%-15s | %2d pts\n", equipoA, puntajeA));
        resultado.append(String.format("%-15s | %2d pts\n", equipoB, puntajeB));

        alert.setContentText(resultado.toString());
        alert.showAndWait();

        partidoActual.setGanador(puntajeA > puntajeB ? equipoA : equipoB);
        partidoActual.setJugado(true);
        partidoActual.setPuntajeEquipo1(puntajeA);
        partidoActual.setPuntajeEquipo2(puntajeB);

        AchievementService achievementService = new AchievementService();
        List<Achievement> antes = achievementService.calcularLogrosParaEquipo(partidoActual.getGanador());

        bracketService.guardarPartidosEnArchivo(partidoActual.getTorneo());
        bracketService.cargarPartidosDesdeArchivo(partidoActual.getTorneo());
        matchService.finalizarPartido();
        bracketService.registrarGanador(partidoActual, partidoActual.getGanador(), true);

        List<Achievement> despues = achievementService.calcularLogrosParaEquipo(partidoActual.getGanador());
        List<Achievement> nuevos = AchievementUtils.filtrarNuevosLogros(antes, despues);

        if (!nuevos.isEmpty()) {
            Tournament torneo = new TournamentService().getTournamentByName(partidoActual.getTorneo());
            boolean esUltimoPartido = torneo.getEstado().equalsIgnoreCase("Finalizado") &&
                    bracketService.getPartidosPendientes().isEmpty();

            for (Achievement logro : nuevos) {
                AchievementAnimationQueue.agregarALaCola(logro);
            }

            if (esUltimoPartido) {
                AchievementAnimationQueue.setPermitirMostrar(false); // ⛔ se mostrarán después
            } else {
                AchievementAnimationQueue.setPermitirMostrar(true);  // ✅ se muestran ahora
                AchievementAnimationQueue.mostrarCuandoPosible(nuevos);
            }
        }




        Platform.runLater(() -> {
            Tournament torneoActualizado = new TournamentService().getTournamentByName(partidoActual.getTorneo());
            bracketParent.setTorneoActual(torneoActualizado);
            bracketParent.cargarBracketDesdePartidos(bracketService.getTodosLosPartidos());
            bracketParent.actualizarLabelPartidoPendiente();
            Platform.runLater(() -> {
                try {
                    Stage stage = (Stage) btnFinalizar.getScene().getWindow();
                    if (stage != null) stage.close();
                } catch (Exception e) {
                    System.out.println("❌ Error al cerrar la ventana de la final: " + e.getMessage());
                }
            });
        });
    }

    private void cancelarDragActivo() {
        Platform.runLater(() -> {
            // Simular un "mouse released" manual para evitar drag congelado
            imgBalon.setDisable(true);
            imgBalon.setDisable(false);
        });
    }

    private void iniciarPantallaDesempate(String equipoA, String equipoB) {
        try {
            // 🚪 Cerrar la ventana actual de partido antes de abrir desempate
            Stage matchStage = (Stage) btnFinalizar.getScene().getWindow();
            if (matchStage != null) {
                matchStage.close(); // <- 💥 cierra MatchView.fxml
            }

            // 🆕 Cargar vista de desempate
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/tareatorneos/view/TieBreakerView.fxml"));
            Parent root = loader.load();

            TieBreakerController controller = loader.getController();
            controller.initializeTieBreaker(equipoA, equipoB, matchService, bracketService, bracketParent);

            Stage stage = new Stage();
            stage.setTitle("Desempate ⚔️");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> e.consume()); // evitar bugs por cierre manual
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarEventosDragAndDrop() {
        imgBalon.setOnDragDetected(event -> {
            Dragboard db = imgBalon.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imgBalon.getImage());
            db.setContent(content);
            event.consume();
        });

        imgEquipoA.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoA.setOnDragDropped(event -> {
            matchService.sumarPuntoB();
            lblPuntajeB.setText("Puntaje: " + matchService.getPuntajeB());
            event.setDropCompleted(true);
            event.consume();
        });

        imgEquipoB.setOnDragOver(event -> {
            if (event.getGestureSource() == imgBalon && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        imgEquipoB.setOnDragDropped(event -> {
            matchService.sumarPuntoA();
            lblPuntajeA.setText("Puntaje: " + matchService.getPuntajeA());
            event.setDropCompleted(true);
            event.consume();
        });
    }

    private void iniciarCuentaRegresiva(int segundos) {
        tiempoRestante = segundos;

        if (tiempoRestante <= 0) return;

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int min = tiempoRestante / 60;
            int seg = tiempoRestante % 60;
            lblTiempo.setText(String.format("Tiempo: %02d:%02d", min, seg));

            if (tiempoRestante <= 0) {
                detenerTiempo();
                lblTiempo.setText("Tiempo: 00:00");
                desactivarControles();
                Platform.runLater(() -> mostrarPopupFinalizado());
            }

            tiempoRestante--;
        }));

        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    @FXML
    void onActionBtnFinalizar(ActionEvent event) {
        detenerTiempo(); // ⏸️ Pausar temporamente el tiempo

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🚨 Confirmar Finalización");
        alert.setHeaderText("¿Deseas finalizar este partido?");
        alert.setContentText("Esta acción detendrá el tiempo y mostrará el resultado final.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // ✅ Confirmado: terminar partido
            lblTiempo.setText("Tiempo: 00:00");
            desactivarControles();
            mostrarPopupFinalizado();
        } else {
            // ❌ Cancelado o cerró con la X: reanudar normal
            activarControlesDragAndDrop();
            reanudarCuentaRegresiva();
        }
    }

    private void reanudarCuentaRegresiva() {
        if (tiempoRestante > 0) {
            iniciarCuentaRegresiva(tiempoRestante);
        }
    }

    private void detenerTiempo() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void activarControlesDragAndDrop() {
        btnFinalizar.setDisable(false);
        imgEquipoA.setDisable(false);
        imgEquipoB.setDisable(false);
        imgBalon.setDisable(false);
    }

    private void desactivarControles() {
        btnFinalizar.setDisable(true);
        imgEquipoA.setDisable(true);
        imgEquipoB.setDisable(true);
        imgBalon.setDisable(true);
    }

    public void initializeMatch(String torneoNombre, String nombreEquipoA, String nombreEquipoB) {
        Tournament torneo = new TournamentService().getTournamentByName(torneoNombre);
        Team equipoA = new TeamService().getTeamByName(nombreEquipoA);
        Team equipoB = new TeamService().getTeamByName(nombreEquipoB);
        Sport sport = new SportService().getSportByName(torneo.getDeporte());

        if (torneo == null || equipoA == null || equipoB == null || sport == null) return;

        matchService = new MatchService(torneo, equipoA, equipoB);

        lblTorneo.setText(torneo.getNombre());
        lblTiempo.setText("Tiempo: " + torneo.getTiempoPorPartido() + ":00");
        lblEquipoA.setText(equipoA.getNombre());
        lblEquipoB.setText(equipoB.getNombre());
        lblPuntajeA.setText("Puntaje: 0");
        lblPuntajeB.setText("Puntaje: 0");

        Image imgA = matchService.getImagenEquipoA();
        if (imgA != null) imgEquipoA.setImage(imgA);
        Image imgB = matchService.getImagenEquipoB();
        if (imgB != null) imgEquipoB.setImage(imgB);
        Image balon = matchService.getImagenBalon();
        if (balon != null) imgBalon.setImage(balon);

        cargarFondoDeporte(sport.getNombre());
        iniciarCuentaRegresiva(torneo.getTiempoPorPartido() * 60);
        activarControlesDragAndDrop();
    }

    private void cargarFondoDeporte(String nombreDeporte) {
        if (nombreDeporte == null) return;
        String deporteNormalizado = nombreDeporte.trim().toLowerCase();
        String rutaImagen = fondoDeporteMap.getOrDefault(deporteNormalizado, "/cr/ac/una/tareatorneos/resources/FondoGeneral.png");

        try {
            Image fondo = new Image(getClass().getResourceAsStream(rutaImagen));
            imgFondoDeporte.setImage(fondo);
        } catch (Exception e) {
            System.out.println("⚠ No se pudo cargar la imagen de fondo: " + rutaImagen);
        }
    }

    private final Map<String, String> fondoDeporteMap = Map.ofEntries(
            Map.entry("futbol", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("soccer", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("futbol 7", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("futbol 5", "/cr/ac/una/tareatorneos/resources/FondoFutbol.png"),
            Map.entry("baloncesto", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("basketball", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("basket", "/cr/ac/una/tareatorneos/resources/FondoBaloncesto.png"),
            Map.entry("voley", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("volleyball", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("voleibol", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("volibol", "/cr/ac/una/tareatorneos/resources/FondoVoleibol.png"),
            Map.entry("tenis", "/cr/ac/una/tareatorneos/resources/FondoTenis.png"),
            Map.entry("pingpong", "/cr/ac/una/tareatorneos/resources/FondoPinpog.png"),
            Map.entry("tenis de mesa", "/cr/ac/una/tareatorneos/resources/FondoPinpog.png"),
            Map.entry("baseball", "/cr/ac/una/tareatorneos/resources/FondoBaseball.png")
    );

    public void inicializarMatchDesdeTorneo(Tournament torneo) {
        List<String> equipos = torneo.getEquiposParticipantes();
        if (equipos.size() < 2) {
            System.out.println("❌ El torneo no tiene suficientes equipos para iniciar un partido.");
            return;
        }

        String equipoA = equipos.get(0);
        String equipoB = equipos.get(1);

        initializeMatch(torneo.getNombre(), equipoA, equipoB);
    }

}

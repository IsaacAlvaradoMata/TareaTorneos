package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.BracketGenerator;
import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.BracketMatchService;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BracketGeneratorController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane bracketContainer;
    @FXML
    private Label lblTorneoNombre;
    @FXML
    private Label lblPartidoActual;
    @FXML
    private MFXButton btnPlay;

    private BracketMatchService matchService = new BracketMatchService();
    private Tournament torneoActual;

    private static final double NODE_WIDTH = 160;
    private static final double NODE_HEIGHT = 60;
    private static final double H_GAP = 200;
    private static final double V_GAP = 30;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // usado al cargar desde FXML
    }

    public void inicializarBracketDesdeTorneo(Tournament torneo) {
        this.torneoActual = torneo; // ✅ Asignar torneo actual
        matchService.generarPartidosDesdeEquipos(torneo); // Cargar desde archivo si ya existen
        cargarBracket(matchService.getEstadoVisualActual());
        actualizarLabelPartidoPendiente();
    }

    private void actualizarLabelPartidoPendiente() {
        BracketMatch p = matchService.getSiguientePartidoPendiente();
        if (p != null && p.getEquipo1() != null) {
            String texto = "🎯 Partido pendiente: " + p.getEquipo1() + (p.getEquipo2() != null ? " vs " + p.getEquipo2() : " (sin rival)");
            lblPartidoActual.setText(texto);
        } else {
            lblPartidoActual.setText("✅ Todos los partidos han sido jugados.");
            btnPlay.setDisable(true);
        }
    }

    @FXML
    private void onActionBtnPlay(ActionEvent event) {
        BracketMatch siguientePartido = matchService.getSiguientePartidoPendiente();

        if (siguientePartido == null) {
            lblPartidoActual.setText("✅ Todos los partidos han sido jugados.");
            btnPlay.setDisable(true);
            return;
        }

        String equipo1 = siguientePartido.getEquipo1();
        String equipo2 = siguientePartido.getEquipo2();

        lblPartidoActual.setText("🎯 Partido pendiente: " + equipo1 + (equipo2 != null ? " vs " + equipo2 : " (sin rival)"));

        if (equipo2 == null) {
            matchService.registrarGanador(siguientePartido, equipo1);
            lblPartidoActual.setText("⚠ " + equipo1 + " pasa automáticamente");
            cargarBracket(matchService.getEstadoVisualActual());
            actualizarLabelPartidoPendiente();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/tareatorneos/view/MatchView.fxml"));
            Parent root = loader.load();

            MatchController controller = loader.getController();
            controller.inicializarMatch(siguientePartido, matchService, this);

            Stage stage = new Stage();
            stage.setTitle("Partido: " + equipo1 + " vs " + equipo2);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnCloseRequest(Event::consume);
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Error al abrir MatchView.fxml");
            e.printStackTrace();
        }
    }

    public void cargarBracket(List<BracketGenerator> equiposIniciales) {
        bracketContainer.getChildren().clear();
        List<List<StackPane>> rondasVisuales = new ArrayList<>();
        List<StackPane> rondaActual = new ArrayList<>();
        double x = 0;

        for (int i = 0; i < equiposIniciales.size(); i++) {
            BracketGenerator equipo = equiposIniciales.get(i);
            StackPane nodo = crearNodoVisual(equipo);
            nodo.setLayoutX(x);
            nodo.setLayoutY(i * (NODE_HEIGHT + V_GAP));
            bracketContainer.getChildren().add(nodo);
            rondaActual.add(nodo);
        }

        rondasVisuales.add(rondaActual);

        while (rondaActual.size() > 1) {
            List<StackPane> siguienteRonda = new ArrayList<>();
            x += H_GAP;
            int i = 0;

            while (i < rondaActual.size()) {
                StackPane equipo1 = rondaActual.get(i);
                StackPane equipo2 = (i + 1 < rondaActual.size()) ? rondaActual.get(i + 1) : null;

                StackPane nodoGanador;
                double y;

                if (equipo2 == null) {
                    nodoGanador = equipo1;
                    siguienteRonda.add(nodoGanador);
                    i++;
                    continue;
                } else {
                    nodoGanador = crearNodoVisualVacio();
                    y = (equipo1.getLayoutY() + equipo2.getLayoutY()) / 2;
                    nodoGanador.setLayoutX(x + 30 * rondasVisuales.size());
                    nodoGanador.setLayoutY(y);

                    bracketContainer.getChildren().add(nodoGanador);
                    siguienteRonda.add(nodoGanador);

                    dibujarConexionesBracket(equipo1, equipo2, nodoGanador);
                    i += 2;
                }
            }

            rondasVisuales.add(siguienteRonda);
            rondaActual = siguienteRonda;
        }
    }

    private StackPane crearNodoVisual(BracketGenerator equipo) {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 5; -fx-border-radius: 5;");

        ImageView escudo = new ImageView();
        escudo.setFitHeight(40);
        escudo.setFitWidth(40);
        try {
            escudo.setImage(new Image(equipo.getLogoPath()));
        } catch (Exception e) {
            escudo.setImage(new Image("file:teamsPhotos/default.png"));
        }

        Label nombre = new Label(equipo.getTeamName());
        nombre.setFont(new Font("Arial", 14));

        HBox box = new HBox(10, escudo, nombre);
        box.setAlignment(Pos.CENTER_LEFT);
        contenedor.getChildren().add(box);

        return contenedor;
    }

    private StackPane crearNodoVisualVacio() {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.setStyle(
                "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: black;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-padding: 10 0 10 5;"
        );
        contenedor.getChildren().add(new Label(""));
        return contenedor;
    }

    private void dibujarConexionesBracket(StackPane nodo1, StackPane nodo2, StackPane destino) {
        double x1 = nodo1.getLayoutX() + NODE_WIDTH;
        double y1 = nodo1.getLayoutY() + NODE_HEIGHT / 2;

        double x2 = nodo2.getLayoutX() + NODE_WIDTH;
        double y2 = nodo2.getLayoutY() + NODE_HEIGHT / 2;

        double midX = x1 + 40;
        double midY = (y1 + y2) / 2;

        Line l1 = new Line(x1, y1, midX, y1);
        Line l2 = new Line(x2, y2, midX, y2);
        Line l3 = new Line(midX, y1, midX, y2);
        Line l4 = new Line(midX, midY, destino.getLayoutX(), midY);

        for (Line l : List.of(l1, l2, l3, l4)) {
            l.setStroke(Color.GRAY);
            l.setStrokeWidth(2);
            bracketContainer.getChildren().add(l);
        }
    }

    @Override
    public void initialize() {
    }
}

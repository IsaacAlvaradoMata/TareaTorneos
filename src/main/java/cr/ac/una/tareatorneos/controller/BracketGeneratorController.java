package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.BracketMatchService;
import cr.ac.una.tareatorneos.service.TeamService;
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
import javafx.scene.layout.VBox;
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
        cargarBracketDesdePartidos(matchService.getTodosLosPartidos());
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
            cargarBracketDesdePartidos(matchService.getTodosLosPartidos());
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

    public void cargarBracketDesdePartidos(List<BracketMatch> partidos) {
        bracketContainer.getChildren().clear();
        List<List<StackPane>> rondasVisuales = new ArrayList<>();

        // Agrupar partidos por ronda
        int maxRonda = partidos.stream().mapToInt(BracketMatch::getRonda).max().orElse(1);
        for (int i = 0; i < maxRonda; i++) {
            rondasVisuales.add(new ArrayList<>());
        }

        for (int ronda = 1; ronda <= maxRonda; ronda++) {
            List<BracketMatch> rondaPartidos = matchService.getPartidosPorRonda(ronda);
            double x = (ronda - 1) * H_GAP;

            for (int i = 0; i < rondaPartidos.size(); i++) {
                BracketMatch match = rondaPartidos.get(i);
                StackPane nodo = crearNodoMatch(match);
                double y = i * (NODE_HEIGHT + V_GAP) * Math.pow(2, ronda - 1);
                nodo.setLayoutX(x);
                nodo.setLayoutY(y);
                bracketContainer.getChildren().add(nodo);
                rondasVisuales.get(ronda - 1).add(nodo);
            }
        }

        // Dibujar conexiones visuales entre rondas
        for (int ronda = 0; ronda < rondasVisuales.size() - 1; ronda++) {
            List<StackPane> actual = rondasVisuales.get(ronda);
            List<StackPane> siguiente = rondasVisuales.get(ronda + 1);

            for (int i = 0; i < actual.size(); i += 2) {
                if (i / 2 < siguiente.size()) {
                    dibujarConexionesBracket(actual.get(i), actual.get(i + 1), siguiente.get(i / 2));
                }
            }
        }
    }

    private StackPane crearNodoMatch(BracketMatch match) {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 5; -fx-border-radius: 5;");

        String equipo1 = match.getEquipo1();
        String equipo2 = match.getEquipo2();

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);

        if (equipo1 != null) box.getChildren().add(crearItemEquipo(equipo1));
        if (equipo2 != null) box.getChildren().add(crearItemEquipo(equipo2));

        if (equipo1 == null && equipo2 == null) {
            Label label = new Label("??? vs ???");
            label.setFont(new Font("Arial", 13));
            box.getChildren().add(label);
        }

        if (match.isJugado()) {
            Label ganador = new Label("🏆 " + match.getGanador());
            ganador.setFont(new Font("Arial", 12));
            box.getChildren().add(ganador);
        }

        contenedor.getChildren().add(box);
        return contenedor;
    }

    private HBox crearItemEquipo(String nombreEquipo) {
        ImageView escudo = new ImageView();
        escudo.setFitHeight(25);
        escudo.setFitWidth(25);

        String logoPath = "file:teamsPhotos/default.png";
        try {
            String rawPath = new TeamService().getTeamByName(nombreEquipo).getTeamImage();
            if (rawPath != null && !rawPath.isBlank()) {
                if (!rawPath.startsWith("file:") && !rawPath.startsWith("http")) {
                    logoPath = "file:teamsPhotos/" + rawPath;
                } else {
                    logoPath = rawPath;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠ Error obteniendo logo de " + nombreEquipo);
        }

        try {
            escudo.setImage(new Image(logoPath));
        } catch (Exception e) {
            escudo.setImage(new Image("file:teamsPhotos/default.png"));
        }

        Label nombre = new Label(nombreEquipo);
        nombre.setFont(new Font("Arial", 13));

        HBox item = new HBox(6, escudo, nombre);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
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

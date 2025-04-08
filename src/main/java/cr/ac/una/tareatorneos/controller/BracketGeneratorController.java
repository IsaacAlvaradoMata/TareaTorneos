package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.BracketMatchService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    private Label lblPartidoActual;
    @FXML
    private MFXButton btnPlay;
    @FXML
    private ScrollPane scrollBracket;
    @FXML
    private ImageView leftBracket;
    @FXML
    private Label lblNombreTorneoBracket;
    @FXML
    private ImageView rightBracket;

    private BracketMatchService matchService = new BracketMatchService();
    private Tournament torneoActual;

    private static final double NODE_WIDTH = 160;
    private static final double NODE_HEIGHT = 60;
    private static final double H_GAP = 200;
    private static final double V_GAP = 30;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void inicializarBracketDesdeTorneo(Tournament torneo) {
        this.torneoActual = torneo; // ✅ Asignar torneo actual
        matchService.generarPartidosDesdeEquipos(torneo); // Cargar desde archivo si ya existen
        cargarBracketDesdePartidos(matchService.getTodosLosPartidos());
        actualizarLabelPartidoPendiente();
        lblNombreTorneoBracket.setText(torneo.getNombre());
    }

    public void actualizarLabelPartidoPendiente() {
        torneoActual = new TournamentService().getTournamentByName(torneoActual.getNombre());

        // Filtra solo partidos válidos
        List<BracketMatch> pendientes = matchService.getPartidosPendientes().stream()
                .filter(p -> p.getEquipo1() != null || p.getEquipo2() != null)
                .toList();

        if (pendientes.isEmpty()) {
            if ("Finalizado".equalsIgnoreCase(torneoActual.getEstado())) {
                lblPartidoActual.setText("🏆 El torneo ha finalizado.");
            } else {
                lblPartidoActual.setText("✅ Todos los partidos han sido jugados.");
            }
            btnPlay.setDisable(true);
            return;
        }

        BracketMatch siguiente = pendientes.get(0);
        String equipo1 = siguiente.getEquipo1();
        String equipo2 = siguiente.getEquipo2();

        boolean esUltimo = pendientes.size() == 1;

        if (equipo1 != null && equipo2 == null && esUltimo && "Finalizado".equalsIgnoreCase(torneoActual.getEstado())) {
            lblPartidoActual.setText("🏆 El torneo ha finalizado.");
            btnPlay.setDisable(true);
            return;
        }

        if (equipo1 != null && equipo2 == null) {
            lblPartidoActual.setText("⚠ " + equipo1 + " pasa automáticamente");
            btnPlay.setDisable(false);
            return;
        }

        if (equipo1 != null && equipo2 != null) {
            lblPartidoActual.setText("🎯 Partido pendiente: " + equipo1 + " vs " + equipo2);
            btnPlay.setDisable(false);
            return;
        }

        // 🔒 Caso de seguridad
        lblPartidoActual.setText("⌛ Esperando equipos para siguiente partido...");
        btnPlay.setDisable(true);
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

        boolean esUltimo = matchService.getPartidosPendientes().size() == 1;

        // 🏆 Si es el último partido y solo hay un equipo → declarar campeón y refrescar todo
        if (equipo1 != null && equipo2 == null && esUltimo) {
            matchService.registrarGanador(siguientePartido, equipo1);

            // 🔁 Refrescar estado del torneo para que diga "Finalizado"
            this.torneoActual = new TournamentService().getTournamentByName(torneoActual.getNombre());

            // 🔄 Redibujar todo y actualizar texto
            cargarBracketDesdePartidos(matchService.getTodosLosPartidos());
            actualizarLabelPartidoPendiente();
            return;
        }

        // ⚠️ Caso normal de BYE (no final)
        if (equipo1 != null && equipo2 == null && !siguientePartido.isJugado()) {
            matchService.registrarGanador(siguientePartido, equipo1);
            this.torneoActual = new TournamentService().getTournamentByName(torneoActual.getNombre());
            cargarBracketDesdePartidos(matchService.getTodosLosPartidos());
            actualizarLabelPartidoPendiente();
            return;
        }

        // 🎯 Partido regular
        lblPartidoActual.setText("🎯 Partido pendiente: " + equipo1 + " vs " + equipo2);

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

    public void setTorneoActual(Tournament torneoActual) {
        this.torneoActual = torneoActual;
    }

    public void cargarBracketDesdePartidos(List<BracketMatch> partidos) {
        matchService.cargarPartidosDesdeArchivo(torneoActual.getNombre());
        bracketContainer.getChildren().clear();
        List<List<StackPane>> rondasVisuales = new ArrayList<>();

        int maxRonda = partidos.stream().mapToInt(BracketMatch::getRonda).max().orElse(1);
        for (int i = 0; i < maxRonda; i++) {
            rondasVisuales.add(new ArrayList<>());
        }

        double totalAltura = matchService.getPartidosPorRonda(1).size() * (NODE_HEIGHT + V_GAP);

        for (int ronda = 1; ronda <= maxRonda; ronda++) {
            List<BracketMatch> rondaPartidos = matchService.getPartidosPorRonda(ronda);
            List<StackPane> nodos = new ArrayList<>();
            double espacioVertical = totalAltura / rondaPartidos.size();

            for (int i = 0; i < rondaPartidos.size(); i++) {
                BracketMatch match = rondaPartidos.get(i);
                StackPane nodo = crearNodoMatch(match);
                if (nodo == null) continue;

                double offsetX = 30 + (ronda - 1) * 20;
                double x = (ronda - 1) * H_GAP + offsetX;
                double y = i * espacioVertical + (espacioVertical - NODE_HEIGHT) / 2;

                nodo.setLayoutX(x);
                nodo.setLayoutY(y);
                bracketContainer.getChildren().add(nodo);
                nodos.add(nodo);
            }
            rondasVisuales.set(ronda - 1, nodos); // ← actualizar correctamente
        }

        for (int ronda = 0; ronda < rondasVisuales.size() - 1; ronda++) {
            List<StackPane> actual = rondasVisuales.get(ronda);
            List<StackPane> siguiente = rondasVisuales.get(ronda + 1);

            for (int i = 0; i < actual.size(); i += 2) {
                StackPane nodo1 = actual.get(i);
                StackPane nodo2 = (i + 1 < actual.size()) ? actual.get(i + 1) : null;

                int indexDestino = i / 2;
                if (indexDestino >= siguiente.size()) continue;

                StackPane destino = siguiente.get(indexDestino);

                if (nodo2 != null) {
                    dibujarConexionesBracket(nodo1, nodo2, destino);
                } else {
                    dibujarLineaSimple(nodo1, destino);
                }
            }
        }

        torneoActual = new TournamentService().getTournamentByName(torneoActual.getNombre());

        if ("Finalizado".equalsIgnoreCase(torneoActual.getEstado())) {
            BracketMatch finalMatch = matchService.getFinalMatch();
            if (finalMatch != null && finalMatch.getGanador() != null) {
                if (!bracketContainer.getChildren().stream().anyMatch(n ->
                        n instanceof StackPane &&
                                ((StackPane) n).getChildren().stream()
                                        .anyMatch(c -> c instanceof VBox &&
                                                ((VBox) c).getChildren().stream()
                                                        .anyMatch(l -> l instanceof Label && ((Label) l).getText().contains("🏆 " + finalMatch.getGanador()))
                                        )
                )) {
                    StackPane nodoCampeon = crearNodoCampeon(finalMatch.getGanador());
                    double x = maxRonda * H_GAP;
                    double y = 0;
                    nodoCampeon.setLayoutX(x);
                    nodoCampeon.setLayoutY(y);
                    bracketContainer.getChildren().add(nodoCampeon);
                }
            }
        }
    }

    private void dibujarLineaSimple(StackPane origen, StackPane destino) {
        double x1 = origen.getLayoutX() + NODE_WIDTH;
        double y1 = origen.getLayoutY() + NODE_HEIGHT / 2;

        double x2 = destino.getLayoutX();
        double y2 = destino.getLayoutY() + NODE_HEIGHT / 2;

        double midX = x1 + 40;

        Line l1 = new Line(x1, y1, midX, y1);
        Line l2 = new Line(midX, y1, midX, y2);
        Line l3 = new Line(midX, y2, x2, y2);

        for (Line l : List.of(l1, l2, l3)) {
            l.setStroke(Color.GOLD);
            l.setStrokeWidth(2);
            bracketContainer.getChildren().add(l);
        }
    }

    private StackPane crearNodoCampeon(String nombreEquipo) {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.setStyle("-fx-background-color: gold; -fx-border-color: black; -fx-background-radius: 5; -fx-border-radius: 5;");

        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        ImageView escudo = new ImageView();
        escudo.setFitHeight(30);
        escudo.setFitWidth(30);

        try {
            String rawPath = new TeamService().getTeamByName(nombreEquipo).getTeamImage();
            String logoPath = rawPath != null ? "file:teamsPhotos/" + rawPath : "file:teamsPhotos/default.png";
            escudo.setImage(new Image(logoPath));
        } catch (Exception e) {
            escudo.setImage(new Image("file:teamsPhotos/default.png"));
        }

        Label label = new Label("🏆 " + nombreEquipo);
        label.setFont(new Font("Arial Bold", 14));
        label.setTextFill(Color.DARKBLUE);

        box.getChildren().addAll(escudo, label);
        contenedor.getChildren().add(box);
        return contenedor;
    }

    private StackPane crearNodoMatch(BracketMatch match) {
        String equipo1 = match.getEquipo1();
        String equipo2 = match.getEquipo2();

        // ✅ Evitar nodos basura en ronda 1 (ambos null)
        if (match.getRonda() == 1 && equipo1 == null && equipo2 == null) {
            return null;
        }

        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-background-radius: 5; -fx-border-radius: 5;");

        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 4 0 4 6;");

        int score1 = match.getPuntajeEquipo1();
        int score2 = match.getPuntajeEquipo2();

        boolean esGanador1 = match.isJugado() && match.getGanador() != null && match.getGanador().equals(match.getEquipo1());
        boolean esGanador2 = match.isJugado() && match.getGanador() != null && match.getGanador().equals(match.getEquipo2());

        box.getChildren().add(crearItemEquipoConExtras(match.getEquipo1(), score1, esGanador1));
        box.getChildren().add(crearItemEquipoConExtras(match.getEquipo2(), score2, esGanador2));

        // 🏆 Mostrar ganador si aplica
        if (match.isJugado() && match.getGanador() != null) {

        }

        contenedor.getChildren().add(box);
        return contenedor;
    }

    private HBox crearItemEquipoConExtras(String nombreEquipo, int puntaje, boolean esGanador) {
        ImageView escudo = new ImageView();
        escudo.setFitHeight(25);
        escudo.setFitWidth(25);

        String logoPath = "file:teamsPhotos/default.png";
        if (nombreEquipo != null) {
            try {
                String rawPath = new TeamService().getTeamByName(nombreEquipo).getTeamImage();
                if (rawPath != null && !rawPath.isBlank()) {
                    logoPath = rawPath.startsWith("file:") ? rawPath : "file:teamsPhotos/" + rawPath;
                }
            } catch (Exception e) {
                System.out.println("⚠ Error obteniendo logo de " + nombreEquipo);
            }
        }

        escudo.setImage(new Image(logoPath));

        Label nombreLbl = new Label(nombreEquipo != null ? nombreEquipo : "???");
        nombreLbl.setFont(new Font("Arial", 13));
        nombreLbl.setWrapText(false);
        nombreLbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nombreLbl, Priority.ALWAYS);

        Label puntajeLbl = new Label("(" + puntaje + ")");
        puntajeLbl.setFont(new Font("Arial", 12));

        HBox info = new HBox(5, nombreLbl, puntajeLbl);
        if (esGanador) {
            Label trofeo = new Label("🏆");
            trofeo.setFont(new Font("Arial", 13));
            info.getChildren().add(trofeo);
        }

        HBox item = new HBox(8, escudo, info);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-padding: 4 0 4 8;");
        return item;
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
        item.setStyle("-fx-padding: 0 0 0 8;");
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
            l.setStroke(Color.GOLD);
            l.setStrokeWidth(2);
            bracketContainer.getChildren().add(l);
        }
    }

    @Override
    public void initialize() {
    }
}

package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.BracketMatch;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.TeamTournamentStats;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.BracketMatchService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TeamTournamentStatsService;
import cr.ac.una.tareatorneos.service.TournamentService;
import cr.ac.una.tareatorneos.util.AnimationDepartment;
import cr.ac.una.tareatorneos.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
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
    @FXML
    private MFXButton btnSave;

    private BracketMatchService matchService = new BracketMatchService();
    private Tournament torneoActual;

    private static final double NODE_WIDTH = 200;
    private static final double NODE_HEIGHT = 110;
    private static final double H_GAP = 250;
    private static final double V_GAP = 40;

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
            lblPartidoActual.setText("  🎯 Partido pendiente: " + equipo1 + " vs " + equipo2 + "  ");
            btnPlay.setDisable(false);
            return;
        }

        // 🔒 Caso de seguridad
        lblPartidoActual.setText("  ⌛ Esperando equipos para siguiente partido...  ");
        btnPlay.setDisable(true);
    }

    private boolean esModoVisualizacion = false;

    public void setModoVisualizacion(boolean modo) {
        this.esModoVisualizacion = modo;

        if (modo) {
            btnSave.setText("Cerrar");
        } else {
            btnSave.setText("Guardar y Salir");
        }
    }

    @FXML
    private void onActionBtnSave(ActionEvent event) {
        if (esModoVisualizacion) {
            // 🔐 Cerrar solo si fue abierto desde "Ver Torneo Finalizado"
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) stage.close();
        } else {
            // 🔁 Comportamiento normal
            FlowController.getInstance().goView("ActiveTournamentsView");
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
        lblPartidoActual.setText("➙Partido pendiente: " + equipo1 + " vs " + equipo2);

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

                double yPadre1 = nodo1.getLayoutY();
                double yPadre2 = (nodo2 != null) ? nodo2.getLayoutY() : yPadre1;

                double centroYPadre1 = yPadre1 + NODE_HEIGHT / 2;
                double centroYPadre2 = yPadre2 + NODE_HEIGHT / 2;

                double centroDestino = (centroYPadre1 + centroYPadre2) / 2;
                double yDestinoAjustado = centroDestino - NODE_HEIGHT / 2;

                destino.setLayoutY(yDestinoAjustado);

                if (nodo2 != null) {
                    dibujarConexionesBracket(nodo1, nodo2, destino);
                } else {
                    dibujarLineaSimple(nodo1, destino);
                }
            }
        }

        torneoActual = new TournamentService().getTournamentByName(torneoActual.getNombre());

        if ("Finalizado".equalsIgnoreCase(torneoActual.getEstado())) {
            int rondaFinal = matchService.getTodosLosPartidos().stream()
                    .mapToInt(BracketMatch::getRonda)
                    .max()
                    .orElse(1);

            List<BracketMatch> rondaFinalPartidos = matchService.getPartidosPorRonda(rondaFinal);
            BracketMatch finalMatch = rondaFinalPartidos.stream()
                    .filter(p -> p.isJugado() && p.getEquipo1() != null && p.getEquipo2() != null && p.getGanador() != null)
                    .findFirst()
                    .orElse(null);

            // ⚠️ Verifica que todos los partidos de la ronda final están jugados
            boolean todosJugados = finalMatch != null &&
                    matchService.getPartidosPorRonda(finalMatch.getRonda()).stream()
                            .filter(p -> p.getEquipo1() != null && p.getEquipo2() != null)
                            .allMatch(BracketMatch::isJugado);

            if (finalMatch != null &&
                    finalMatch.getEquipo1() != null &&
                    finalMatch.getEquipo2() != null &&
                    finalMatch.getGanador() != null &&
                    finalMatch.isJugado() &&
                    todosJugados) {

                if (!bracketContainer.getChildren().stream().anyMatch(n ->
                        n instanceof StackPane &&
                                ((StackPane) n).getChildren().stream()
                                        .anyMatch(c -> c instanceof VBox &&
                                                ((VBox) c).getChildren().stream()
                                                        .anyMatch(l -> l instanceof Label && ((Label) l).getText().contains("🏆 " + finalMatch.getGanador()))
                                        )
                )) {
                    StackPane nodoCampeon = crearNodoCampeon(finalMatch.getGanador());
                    double x = (maxRonda + 1) * H_GAP;

                    // ✅ alinear con el nodo final en Y
                    List<StackPane> ultimaRonda = rondasVisuales.get(maxRonda - 1);
                    StackPane nodoFinal = ultimaRonda.get(0);
                    double yCentroFinal = nodoFinal.getLayoutY() + NODE_HEIGHT / 2;
                    double y = yCentroFinal - NODE_HEIGHT / 2;

                    nodoCampeon.setLayoutX(x);
                    nodoCampeon.setLayoutY(y);
                    bracketContainer.getChildren().add(nodoCampeon);
                    if (!esModoVisualizacion) {
                        FlowController.getInstance().goViewInWindowModal("WinnerAnimationView", this.getStage(), false);
                        WinnerAnimationController controller = (WinnerAnimationController)
                                FlowController.getInstance().getController("WinnerAnimationView");
                        controller.resetAndRunAnimations(finalMatch.getGanador());
                        actualizarEstadisticasGenerales();

                    }
                }
            }
        }
    }

    private void dibujarLineaSimple(StackPane origen, StackPane destino) {
        double x1 = origen.getLayoutX() + NODE_WIDTH;
        double y1 = origen.getLayoutY() + NODE_HEIGHT / 2;

        double x2 = destino.getLayoutX();
        double y2 = destino.getLayoutY() + NODE_HEIGHT / 2;

        Line linea = new Line(x1, y1, x2, y2);
        linea.setStroke(Color.GOLD);
        linea.setStrokeWidth(2);
        bracketContainer.getChildren().add(linea);
    }

    private StackPane crearNodoCampeon(String nombreEquipo) {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
        contenedor.getStyleClass().add("bracket-contenedor-campeon");
        Platform.runLater(() -> AnimationDepartment.glowBorder(contenedor));

        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        ImageView escudo = new ImageView();
        escudo.setFitHeight(40);
        escudo.setFitWidth(40);
        StackPane imageWrapper = new StackPane(escudo);
        imageWrapper.setMaxHeight(40);
        imageWrapper.setMaxWidth(40);
        imageWrapper.getStyleClass().add("img-bracket");

        try {
            String rawPath = new TeamService().getTeamByName(nombreEquipo).getTeamImage();
            String logoPath = rawPath != null ? "file:teamsPhotos/" + rawPath : "file:teamsPhotos/default.png";
            escudo.setImage(new Image(logoPath));
        } catch (Exception e) {
            escudo.setImage(new Image("file:teamsPhotos/default.png"));
        }

        Label label = new Label("🏆 " + nombreEquipo);
        label.getStyleClass().add("label-bracket");

        box.getChildren().addAll(imageWrapper, label);
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
        contenedor.getStyleClass().add("bracket-contenedor");
        Platform.runLater(() -> AnimationDepartment.glowBorder(contenedor));
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 4 0 4 6;");

        int score1 = match.getPuntajeEquipo1();
        int score2 = match.getPuntajeEquipo2();

        boolean esGanador1 = match.isJugado() && match.getGanador() != null && match.getGanador().equals(match.getEquipo1());
        boolean esGanador2 = match.isJugado() && match.getGanador() != null && match.getGanador().equals(match.getEquipo2());

        box.getChildren().add(crearItemEquipoConExtras(match.getEquipo1(), score1, esGanador1));
        box.getChildren().add(crearItemEquipoConExtras(match.getEquipo2(), score2, esGanador2));
        contenedor.getChildren().add(box);

        if (esGanador1 || esGanador2) {
            Platform.runLater(() -> AnimationDepartment.glowBorder(contenedor));
        }

        return contenedor;
    }

    private HBox crearItemEquipoConExtras(String nombreEquipo, int puntaje, boolean esGanador) {
        ImageView escudo = new ImageView();
        escudo.setFitHeight(35);
        escudo.setFitWidth(35);

        StackPane imageWrapper = new StackPane(escudo);
        imageWrapper.getStyleClass().add("img-bracket");

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
        nombreLbl.getStyleClass().add("label-bracket");
        nombreLbl.setWrapText(false);
        nombreLbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nombreLbl, Priority.ALWAYS);

        Label puntajeLbl = new Label("(" + puntaje + ")");
        puntajeLbl.getStyleClass().add("label-bracket");

        HBox info = new HBox(5, nombreLbl, puntajeLbl);
        info.setAlignment(Pos.CENTER_LEFT); // Asegura alineación centrada vertical

        if (esGanador) {
            Label trofeo = new Label("🏆");
            trofeo.setFont(new Font("Arial", 13));
            info.getChildren().add(trofeo);
        }

        HBox item = new HBox(8, imageWrapper, info);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-padding: 4 0 4 8;");
        return item;
    }

    private void dibujarConexionesBracket(StackPane nodo1, StackPane nodo2, StackPane destino) {
        double x1 = nodo1.getLayoutX() + NODE_WIDTH;
        double y1 = nodo1.getLayoutY() + NODE_HEIGHT / 2;

        double x2 = nodo2.getLayoutX() + NODE_WIDTH;
        double y2 = nodo2.getLayoutY() + NODE_HEIGHT / 2;

        double offsetCorto = 12;     // para nodo1
        double offsetLargo = 32;     // para nodo2

        double midX1 = x1 + offsetCorto;
        double midX2 = x2 + offsetLargo;
        double midX = Math.max(midX1, midX2); // punto de encuentro
        double midY = (y1 + y2) / 2;

        Line l1 = new Line(x1, y1, midX, y1); // nodo1 →
        Line l2 = new Line(x2, y2, midX, y2); // nodo2 → (más largo ahora)
        Line l3 = new Line(midX, y1, midX, y2); // unión vertical
        Line l4 = new Line(midX, midY, destino.getLayoutX(), midY); // hacia el nodo destino

        for (Line l : List.of(l1, l2, l3, l4)) {
            l.setStroke(Color.GOLD);
            l.setStrokeWidth(2);
            bracketContainer.getChildren().add(l);
        }
    }

    private void actualizarEstadisticasGenerales() {
        TeamService teamService = new TeamService();
        TeamTournamentStatsService statsService = new TeamTournamentStatsService(); // necesitas esta clase

        for (Team team : teamService.getAllTeams()) {
            TeamTournamentStats statsAvanzadas = statsService.getStatsByTeamName(team.getNombre());
            if (statsAvanzadas == null) continue;

            // Acumuladores
            int partidosTotales = 0;
            int ganados = 0;
            int perdidos = 0;
            int empatados = 0;
            int anotaciones = 0;
            int enContra = 0;
            int torneos = 0;
            int torneosGanados = 0;
            int torneosPerdidos = 0;
            int puntosGlobales = 0;

            for (TeamTournamentStats.TournamentStat torneo : statsAvanzadas.getTorneos()) {
                torneos++;

                // Torneos Ganados/Perdidos
                if ("Ganador".equalsIgnoreCase(torneo.getResultadoTorneo())) torneosGanados++;
                if ("Perdedor".equalsIgnoreCase(torneo.getResultadoTorneo())) torneosPerdidos++;

                puntosGlobales += torneo.getPuntos();

                for (var partido : torneo.getPartidos()) {
                    partidosTotales++;

                    switch (partido.getResultadoReal().toLowerCase()) {
                        case "ganado" -> ganados++;
                        case "perdido" -> perdidos++;
                        case "empatado" -> empatados++;
                    }

                    anotaciones += partido.getAnotaciones();
                    enContra += partido.getAnotacionesEnContra();
                }
            }

            // Actualiza stats generales
            var est = team.getEstadisticas();
            est.setPartidosTotales(partidosTotales);
            est.setPartidosGanados(ganados);
            est.setPartidosPerdidos(perdidos);
            est.setPartidosEmpatados(empatados);
            est.setTorneosTotales(torneos);
            est.setTorneosGanados(torneosGanados);
            est.setTorneosPerdidos(torneosPerdidos);
            est.setPuntosGlobales(puntosGlobales);
            est.setAnotaciones(anotaciones);
            est.setAnotacionesEnContra(enContra);

            teamService.updateTeam(team.getNombre(), team);
        }

        System.out.println("✔️ Estadísticas generales actualizadas para todos los equipos.");
    }


    @Override
    public void initialize() {
    }
}

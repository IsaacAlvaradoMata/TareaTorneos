package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.BracketGenerator;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.BracketGeneratorService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BracketGeneratorController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane bracketContainer;

    private static final double NODE_WIDTH = 160;
    private static final double NODE_HEIGHT = 60;
    private static final double H_GAP = 200;
    private static final double V_GAP = 30;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Se usará cuando se invoque desde otra vista
    }

    public void inicializarBracketDesdeTorneo(Tournament torneo) {
        BracketGeneratorService service = new BracketGeneratorService();
        List<BracketGenerator> equiposVisuales = service.getBracketTeamsByTournament(torneo.getNombre());
        cargarBracket(equiposVisuales);
    }

    public void cargarBracket(List<BracketGenerator> equiposIniciales) {
        bracketContainer.getChildren().clear();

        // Esta lista guardará cada ronda
        List<List<StackPane>> rondasVisuales = new ArrayList<>();

        // 🟡 Ronda inicial (dibuja los equipos originales)
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

        // 🔁 Generar rondas hasta que quede 1 equipo
        while (rondaActual.size() > 1) {
            List<StackPane> siguienteRonda = new ArrayList<>();
            x += H_GAP;

            for (int i = 0; i < rondaActual.size(); i += 2) {
                StackPane equipo1 = rondaActual.get(i);
                StackPane equipo2 = (i + 1 < rondaActual.size()) ? rondaActual.get(i + 1) : null;

                // Posición promedio de los 2
                double y = equipo2 != null
                        ? (equipo1.getLayoutY() + equipo2.getLayoutY()) / 2
                        : equipo1.getLayoutY();

                StackPane nodoGanador = crearNodoVisualVacio();
                double offset = 30;
                nodoGanador.setLayoutX(x);
                nodoGanador.setLayoutY(y);

                bracketContainer.getChildren().add(nodoGanador);
                siguienteRonda.add(nodoGanador);

                // 🧩 Conexiones visuales
                dibujarConexionesBracket(equipo1, equipo2, nodoGanador);
            }

            rondasVisuales.add(siguienteRonda);
            rondaActual = siguienteRonda;
        }
    }

    private StackPane crearNodoVisualVacio() {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(NODE_WIDTH, NODE_HEIGHT);

        // Estilo más claro y con margen interno (padding)
        contenedor.setStyle(
                "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: black;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-padding: 10 0 10 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 0);"
        );

        // Label vacío para mantener estructura uniforme
        Label label = new Label("");
        contenedor.getChildren().add(label);

        return contenedor;
    }

    private void dibujarConexionesBracket(StackPane nodo1, StackPane nodo2, StackPane destino) {
        double x1 = nodo1.getLayoutX() + NODE_WIDTH;
        double y1 = nodo1.getLayoutY() + NODE_HEIGHT / 2;

        double x2 = nodo2 != null ? nodo2.getLayoutX() + NODE_WIDTH : x1;
        double y2 = nodo2 != null ? nodo2.getLayoutY() + NODE_HEIGHT / 2 : y1;

        double midX = x1 + 40;
        double midY = (y1 + y2) / 2;

        // Líneas horizontales + vertical + siguiente
        Line l1 = new Line(x1, y1, midX, y1);
        Line l2 = new Line(x2, y2, midX, y2);
        Line l3 = new Line(midX, y1, midX, y2);
        Line l4 = new Line(midX, midY, destino.getLayoutX() + 10, midY);

        for (Line l : List.of(l1, l2, l3, l4)) {
            l.setStroke(Color.GRAY);
            l.setStrokeWidth(2);
            bracketContainer.getChildren().add(l);
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

    private void dibujarLinea(StackPane desde, StackPane hacia) {
        double startX = desde.getLayoutX() + NODE_WIDTH;
        double startY = desde.getLayoutY() + NODE_HEIGHT / 2;

        double endX = startX + 40; // Línea horizontal recta
        double endY = startY;

        Line linea = new Line(startX, startY, endX, endY);
        linea.setStroke(Color.GRAY);
        linea.setStrokeWidth(2);

        bracketContainer.getChildren().add(linea);
    }

    @Override
    public void initialize() {

    }
}

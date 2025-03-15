
package cr.ac.una.tareatorneos.controller;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXStepper.MFXStepperEvent;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.materialfx.validation.Validated;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TournamentCreationController extends Controller implements Initializable {
    private final MFXTextField nombreTorneo;
    private final MFXTextField tiempoTorneo;
    private final MFXTextField cantidadTorneo;
    private final MFXComboBox<String> deporteTorneo;
    private final MFXCheckbox checkbox;
    private final MFXCheckListView<String> equiposTorneo;
    private final MFXListView<String> seleccionadosTorneo;



    @FXML
    private MFXStepper stepper;
    @FXML

    private AnchorPane root;


    public TournamentCreationController() {
        nombreTorneo = new MFXTextField();
        deporteTorneo = new MFXComboBox<>();
        checkbox = new MFXCheckbox("Confirmar?");
        tiempoTorneo = new MFXTextField();
        cantidadTorneo = new MFXTextField();
        equiposTorneo = new MFXCheckListView();
        seleccionadosTorneo = new MFXListView();

    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        nombreTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        tiempoTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        cantidadTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        deporteTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        checkbox.getStyleClass().add("mfx-checkbox");
        equiposTorneo.getStyleClass().add("mfx-checklist-view");
        seleccionadosTorneo.getStyleClass().add("mfx-list-view");

        nombreTorneo.setPromptText("Escriba el Nombre del Torneo");
        tiempoTorneo.setPromptText("Digite el Tiempo por Partido en Minutos");
        cantidadTorneo.setPromptText("Digite la Cantidad de Equipos");
        nombreTorneo.getValidator().constraint("Debe de Incluir el Nombre del Torneo", nombreTorneo.textProperty().length().greaterThanOrEqualTo(1));
        nombreTorneo.setLeadingIcon(new MFXIconWrapper("fas-medal", 16, Color.web("#4D4D4D"), 15));
        deporteTorneo.setPromptText("Escoja el Deporte");
        deporteTorneo.setItems(FXCollections.observableArrayList("Basket", "Futbol", "PingPong"));
        equiposTorneo.setItems(FXCollections.observableArrayList("los temerarios", "los panas", "niggers", "los gallos", "famosos", "soculentos","los temerarios", "los panas", "niggers", "los gallos", "famosos", "soculentos"));
        seleccionadosTorneo.setItems(FXCollections.observableArrayList("los temerarios", "los panas", "niggers", "los gallos", "famosos", "soculentos","los temerarios", "los panas", "niggers", "los gallos", "famosos", "soculentos"));

        List<MFXStepperToggle> stepperToggles = createSteps();
        stepper.getStepperToggles().addAll(stepperToggles);

        Platform.runLater(() -> {
            stepper.getScene().getStylesheets().add(
                    getClass().getResource("/cr/ac/una/tareatorneos/view/StepperButtons.css").toExternalForm());


            nombreTorneo.setMaxWidth(265);
            tiempoTorneo.setMaxWidth(265);
            cantidadTorneo.setMaxWidth(265);
            deporteTorneo.setMaxWidth(265);


            stepper.lookupAll(".mfx-button").forEach(node -> {

                if (node instanceof MFXButton) {
                    MFXButton button = (MFXButton) node;
                    button.getStyleClass().removeAll("mfx-button");
                    button.getStyleClass().add("stepper-button");

                    if (button.getText().equals("Next")) {
                        button.setText("Siguiente");

                    } else if (button.getText().equals("Previous")) {
                        button.setText("Atrás");
                    }
                    button.setStyle(
                            "-fx-background-color: #690093 !important;" +
                                    "-fx-text-fill: white !important;" +
                                    "-fx-font-weight: bold !important;" +
                                    "-fx-border-radius: 4px !important;" +
                                    "-fx-background-radius: 4px !important;" +
                                    "-fx-padding: 5px 10px !important;"

                    );
                }
            });
        });


    }

    private List<MFXStepperToggle> createSteps() {
        MFXStepperToggle step1 = new MFXStepperToggle("Step 1", new MFXFontIcon("fas-1", 16, Color.web("#f1c40f")));
        VBox step1Box = new VBox(20, deporteTorneo, wrapNodeForValidation(nombreTorneo), tiempoTorneo);
        step1Box.setAlignment(Pos.CENTER);
        step1.setContent(step1Box);
        step1.getValidator().dependsOn(nombreTorneo.getValidator());

        MFXStepperToggle step2 = new MFXStepperToggle("Step 2", new MFXFontIcon("fas-2", 16, Color.web("#49a6d7")));
        VBox step2Box = new VBox(20, cantidadTorneo, equiposTorneo);
        step2Box.setAlignment(Pos.CENTER);
        step2.setContent(step2Box);

        MFXStepperToggle step3 = new MFXStepperToggle("Step 3", new MFXFontIcon("fas-3", 16, Color.web("#85CB33")));
        Node step3Grid = createGrid();
        step3.setContent(step3Grid);
        step3.getValidator().constraint("Se debe de confirmar la informacion", checkbox.selectedProperty());



        return List.of(step1, step2, step3);
    }

    private <T extends Node & Validated> Node wrapNodeForValidation(T node) {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setManaged(false);
        stepper.addEventHandler(MFXStepperEvent.VALIDATION_FAILED_EVENT, event -> {
            MFXValidator validator = node.getValidator();
            List<Constraint> validate = validator.validate();
            if (!validate.isEmpty()) {
                errorLabel.setText(validate.get(0).getMessage());
            }
        });
        stepper.addEventHandler(MFXStepperEvent.NEXT_EVENT, event -> errorLabel.setText(""));
        VBox wrap = new VBox(3, node, errorLabel) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();

                double x = node.getBoundsInParent().getMinX();
                double y = node.getBoundsInParent().getMaxY() + getSpacing();
                double width = getWidth();
                double height = errorLabel.prefHeight(-1);
                errorLabel.resizeRelocate(x, y, width, height);
            }

            @Override
            protected double computePrefHeight(double width) {
                return super.computePrefHeight(width) + errorLabel.getHeight() + getSpacing();
            }
        };
        wrap.setAlignment(Pos.CENTER);
        return wrap;
    }

    private Node createGrid() {
        MFXTextField nombreTorneo1 = createLabel("Nombre del Torneo: ");
        MFXTextField nombreTorneo2 = createLabel("");
        nombreTorneo2.textProperty().bind(nombreTorneo.textProperty());
        nombreTorneo2.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");

        MFXTextField tiempoTorneo1 = createLabel("Duracion en Minutos por Partido: ");
        MFXTextField tiempoTorneo2 = createLabel("");
        tiempoTorneo2.textProperty().bind(tiempoTorneo.textProperty());
        tiempoTorneo2.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");

        MFXTextField cantidadTorneo1 = createLabel("Cantidad de Equipos Participantes: ");
        MFXTextField cantidadTorneo2 = createLabel("");
        cantidadTorneo2.textProperty().bind(cantidadTorneo.textProperty());
        cantidadTorneo2.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");

        MFXTextField deporteTorneo1 = createLabel("Deporte: ");
        MFXTextField deporteTorneo2 = createLabel("");
        deporteTorneo2.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        deporteTorneo2.textProperty().bind(Bindings.createStringBinding(
                () -> deporteTorneo.getValue() != null ? deporteTorneo.getValue() : "",
                deporteTorneo.valueProperty()
        ));

        MFXTextField seleccionadosLabel = createLabel("Equipos Seleccionados:");

        seleccionadosTorneo.setPrefHeight(350);
        seleccionadosTorneo.setPrefWidth(300);

        seleccionadosLabel.getStyleClass().add("header-label");
        nombreTorneo1.getStyleClass().add("header-label");
        tiempoTorneo1.getStyleClass().add("header-label");
        cantidadTorneo1.getStyleClass().add("header-label");
        deporteTorneo1.getStyleClass().add("header-label");

        MFXTextField completedLabel = MFXTextField.asLabel("Torneo Creado!");
        completedLabel.getStyleClass().add("completed-label");

        VBox b1 = new VBox(nombreTorneo1, nombreTorneo2);
        VBox b2 = new VBox(tiempoTorneo1, tiempoTorneo2);
        VBox b3 = new VBox(cantidadTorneo1, cantidadTorneo2);
        VBox b4 = new VBox(deporteTorneo1, deporteTorneo2);
        VBox b5 = new VBox(seleccionadosLabel, seleccionadosTorneo, checkbox);

        b1.setMaxWidth(Region.USE_PREF_SIZE);
        b2.setMaxWidth(Region.USE_PREF_SIZE);
        b3.setMaxWidth(Region.USE_PREF_SIZE);
        b4.setMaxWidth(Region.USE_PREF_SIZE);
        b5.setMaxWidth(Region.USE_PREF_SIZE);
        b5.setAlignment(Pos.CENTER);
        b5.setSpacing(10);

        VBox vBox = new VBox(10, b1, b2, b3, b4);
        HBox box = new HBox(10, vBox, b5);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(50);
        StackPane.setAlignment(box, Pos.CENTER);
        StackPane.setMargin(box, new Insets(30));

        stepper.setOnLastNext(event -> {
            box.getChildren().setAll(completedLabel);
            stepper.setMouseTransparent(true);
        });
        stepper.setOnBeforePrevious(event -> {
            if (stepper.isLastToggle()) {
                checkbox.setSelected(false);
                box.getChildren().setAll(b1, b2, b3, b4, checkbox);
            }
        });

        return box;
    }

    private MFXTextField createLabel(String text) {
        MFXTextField label = MFXTextField.asLabel(text);
        label.setAlignment(Pos.CENTER);
        label.setMinWidth(270);
        label.setStyle(" -fx-text-fill: #690093;");
        return label;
    }

    @Override
    public void initialize() {

    }
}

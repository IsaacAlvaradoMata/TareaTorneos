package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXStepper.MFXStepperEvent;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.materialfx.validation.Validated;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import cr.ac.una.tareatorneos.util.Mensaje;
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

    private final SportService sportService = new SportService();
    private final TeamService teamService = new TeamService();
    private List<Team> todosLosEquipos;
    private Mensaje mensajeUtil = new Mensaje();
    private final ObservableList<String> selectedEquipos = FXCollections.observableArrayList();
    private MFXStepper nuevoStepper;



    @FXML
    private MFXStepper stepper;
    @FXML

    private AnchorPane root1;

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
        configurarEstilosCampos();
        configurarPrompts();
        configurarIconos();
        cargarDeportes();
        configurarFiltroEquipos();
        agregarPasosAlStepper();
        configurarEstiloBotonesStepper();
        configurarEventosStepper();
        configurarValidaciones();
    }

    private void configurarEstilosCampos() {
        nombreTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        tiempoTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        cantidadTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        deporteTorneo.setStyle("-fx-background-color: #d9a2fd; -fx-text-fill: #690093; -fx-border-color: #690093;");
        checkbox.getStyleClass().add("mfx-checkbox");
        equiposTorneo.getStyleClass().add("mfx-checklist-view");
        seleccionadosTorneo.getStyleClass().add("mfx-list-view");

        nombreTorneo.setMaxWidth(265);
        tiempoTorneo.setMaxWidth(265);
        cantidadTorneo.setMaxWidth(265);
        deporteTorneo.setMaxWidth(265);
    }

    private void configurarPrompts() {
        nombreTorneo.setPromptText("Escriba el Nombre del Torneo");
        tiempoTorneo.setPromptText("Digite el Tiempo por Partido en Minutos");
        cantidadTorneo.setPromptText("Digite la Cantidad de Equipos");
        deporteTorneo.setPromptText("Escoja el Deporte");
    }

    private void configurarIconos() {
        nombreTorneo.setLeadingIcon(new MFXIconWrapper("fas-medal", 16, Color.web("#4D4D4D"), 15));
    }

    private void cargarDeportes() {
        List<Sport> deportes = sportService.getAllSports();
        List<String> nombresDeportes = deportes.stream().map(Sport::getNombre).toList();
        deporteTorneo.setItems(FXCollections.observableArrayList(nombresDeportes));
        todosLosEquipos = teamService.getAllTeams();
    }

    private void configurarFiltroEquipos() {
        deporteTorneo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<String> equiposFiltrados = todosLosEquipos.stream()
                        .filter(team -> team.getDeporte() != null && team.getDeporte().equalsIgnoreCase(newVal))
                        .map(Team::getNombre)
                        .distinct()
                        .toList();

                equiposTorneo.setItems(FXCollections.observableArrayList(equiposFiltrados));
                seleccionadosTorneo.getItems().clear();

// 🔄 Actualizar lista observable al cambiar la selección
                equiposTorneo.getSelectionModel().selectionProperty().addListener((obs2, anterior, actual) -> {
                    selectedEquipos.setAll(equiposTorneo.getSelectionModel().getSelectedValues());
                    System.out.println("✅ Equipos seleccionados: " + selectedEquipos.size() + " => " + selectedEquipos);
                });
            }
        });
    }

    private void agregarPasosAlStepper() {
        List<MFXStepperToggle> stepperToggles = createSteps();
        stepper.getStepperToggles().addAll(stepperToggles);
    }

    private void configurarEstiloBotonesStepper() {
        Platform.runLater(() -> {
            stepper.getScene().getStylesheets().add(getClass().getResource("/cr/ac/una/tareatorneos/view/StepperButtons.css").toExternalForm());

            stepper.lookupAll(".mfx-button").forEach(node -> {
                if (node instanceof MFXButton button) {
                    button.getStyleClass().removeAll("mfx-button");
                    button.getStyleClass().add("stepper-button");

                    if (button.getText().equals("Next")) {
                        button.setText("Siguiente");
                    } else if (button.getText().equals("Previous")) {
                        button.setText("Atrás");
                    }

                    button.setStyle("-fx-background-color: #690093 !important;" +
                            "-fx-text-fill: white !important;" +
                            "-fx-font-weight: bold !important;" +
                            "-fx-border-radius: 4px !important;" +
                            "-fx-background-radius: 4px !important;" +
                            "-fx-padding: 5px 10px !important;");
                }
            });
        });
    }

    private void configurarEventosStepper() {
        // Actualiza la lista observable cada vez que el usuario hace click
        equiposTorneo.getSelectionModel().selectionProperty().addListener((obs, oldSel, newSel) -> {
            selectedEquipos.setAll(equiposTorneo.getSelectionModel().getSelectedValues());
            System.out.println("📌 Checkboxes seleccionados (" + selectedEquipos.size() + "): " + selectedEquipos);
        });

        stepper.addEventHandler(MFXStepperEvent.NEXT_EVENT, event -> {
            if (stepper.getCurrentIndex() == 2) { // Paso 3 es índice 2
                List<String> seleccionados = equiposTorneo.getSelectionModel().getSelectedValues();
                seleccionadosTorneo.getItems().setAll(seleccionados);
            }
        });
    }

    private void configurarValidaciones() {
        nombreTorneo.getValidator().constraint(
                "Debe de Incluir el Nombre del Torneo", nombreTorneo.textProperty().length().greaterThanOrEqualTo(1));

        tiempoTorneo.getValidator().constraint(
                "El tiempo debe ser numérico y obligatorio",
                Bindings.createBooleanBinding(() -> {
                    String text = tiempoTorneo.getText();
                    return text != null && !text.isEmpty() && text.matches("\\d+");
                }, tiempoTorneo.textProperty())
        );

        cantidadTorneo.getValidator().constraint(
                "La cantidad debe ser un número entre 2 y 32",
                Bindings.createBooleanBinding(() -> {
                    String text = cantidadTorneo.getText();
                    if (text == null || text.isEmpty()) return false;
                    try {
                        int cantidad = Integer.parseInt(text);
                        return cantidad >= 2 && cantidad <= 32;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }, cantidadTorneo.textProperty())
        );

        deporteTorneo.getValidator().constraint(
                "Debe seleccionar un deporte",
                Bindings.createBooleanBinding(() -> deporteTorneo.getValue() != null, deporteTorneo.valueProperty())
        );


    }

    private List<MFXStepperToggle> createSteps() {
        MFXStepperToggle step1 = new MFXStepperToggle("Step 1", new MFXFontIcon("fas-1", 16, Color.web("#f1c40f")));
        VBox step1Box = new VBox(20, wrapNodeForValidation(deporteTorneo), wrapNodeForValidation(nombreTorneo), wrapNodeForValidation(tiempoTorneo));
        step1Box.setAlignment(Pos.CENTER);
        step1.setContent(step1Box);
        step1.getValidator().dependsOn(deporteTorneo.getValidator());
        step1.getValidator().dependsOn(nombreTorneo.getValidator());
        step1.getValidator().dependsOn(tiempoTorneo.getValidator());

        MFXStepperToggle step2 = new MFXStepperToggle("Step 2", new MFXFontIcon("fas-2", 16, Color.web("#49a6d7")));
        MFXTextField seleccionEquipos = createLabel("Seleccione los Equipos Participantes:");
        seleccionEquipos.getStyleClass().add("header-label");
        VBox step2Box = new VBox(20, wrapNodeForValidation(cantidadTorneo),seleccionEquipos ,equiposTorneo);
        step2Box.setAlignment(Pos.CENTER);
        step2.setContent(step2Box);
        step2.getValidator().dependsOn(cantidadTorneo.getValidator());

        // 👀 Detectar cambios en la selección al hacer click
        equiposTorneo.setOnMouseClicked(event -> {
            selectedEquipos.setAll(equiposTorneo.getSelectionModel().getSelectedValues());
            System.out.println("🧠 Equipos seleccionados (" + selectedEquipos.size() + "): " + selectedEquipos);
        });

        // 🧠 Binding para la cantidad seleccionada
        IntegerBinding cantidadSeleccionados = Bindings.size(selectedEquipos);

        // 🚫 Validar cantidad permitida
        step2.getValidator().constraint(
                "No puede seleccionar más equipos de los permitidos",
                Bindings.createBooleanBinding(() -> {
                    String cantidadTxt = cantidadTorneo.getText();
                    if (cantidadTxt == null || cantidadTxt.isEmpty()) return true;
                    try {
                        int cantidadPermitida = Integer.parseInt(cantidadTxt);
                        boolean valido = cantidadSeleccionados.get() <= cantidadPermitida;
                        System.out.println("🔍 Validación - Seleccionados: " + cantidadSeleccionados.get() +
                                ", Permitidos: " + cantidadPermitida + " => " + (valido ? "✔️ OK" : "❌ BLOQUEADO"));
                        return valido;
                    } catch (NumberFormatException e) {
                        return true; // No valida si aún no es número
                    }
                }, cantidadTorneo.textProperty(), cantidadSeleccionados)
        );

        // Paso 3 - Confirmación
        MFXStepperToggle step3 = new MFXStepperToggle("Paso 3", new MFXFontIcon("fas-3", 16, Color.web("#85CB33")));
        Node step3Grid = createGrid();
        step3.setContent(step3Grid);
        step3.getValidator().constraint("Se debe de confirmar la información", checkbox.selectedProperty());

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

//    private void reiniciarFormulario() {
//        // Limpiar campos de texto
//        nombreTorneo.clear();
//        tiempoTorneo.clear();
//        cantidadTorneo.clear();
//        deporteTorneo.clearSelection();
//        checkbox.setSelected(false);
//
//        // Limpiar listas
//        equiposTorneo.getSelectionModel().clearSelection();
//        equiposTorneo.setItems(FXCollections.observableArrayList()); // también limpia items mostrados
//        seleccionadosTorneo.getItems().clear();
//        selectedEquipos.clear();
//
//        // Volver al paso 1
//        stepper.getSelectionModel().select(0);
//
//        // Reactivar botones
//        stepper.setMouseTransparent(false);
//        stepper.setDisable(false);
//    }

    private void cargarVistaDesdeCero() {
//        // Limpiar contenedor raíz
        root1.getChildren().clear();

        // Reset componentes
        nombreTorneo.clear();
        tiempoTorneo.clear();
        cantidadTorneo.clear();
        deporteTorneo.clearSelection();
        checkbox.setSelected(false);

        equiposTorneo.getItems().clear();
        equiposTorneo.getSelectionModel().clearSelection();
        seleccionadosTorneo.getItems().clear();
        selectedEquipos.clear();

        // Crear y configurar nuevo stepper
        stepper = new MFXStepper();  // 👉 NUEVO steper para reiniciar todo
        agregarPasosAlStepper();
        configurarEstilosCampos();
        configurarPrompts();
        configurarIconos();
        cargarDeportes();
        configurarFiltroEquipos();
        configurarValidaciones();
        configurarEventosStepper();

        // Estilo botones
        Platform.runLater(this::configurarEstiloBotonesStepper);

        // Agregar stepper al root
        AnchorPane.setTopAnchor(stepper, 0.0);
        AnchorPane.setBottomAnchor(stepper, 0.0);
        AnchorPane.setLeftAnchor(stepper, 0.0);
        AnchorPane.setRightAnchor(stepper, 0.0);
        root1.getChildren().add(stepper);
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
        deporteTorneo2.textProperty().bind(Bindings.createStringBinding(() -> deporteTorneo.getValue() != null ? deporteTorneo.getValue() : "", deporteTorneo.valueProperty()));

        MFXTextField seleccionadosLabel = createLabel("Equipos Seleccionados:");

        seleccionadosTorneo.setPrefHeight(350);
        seleccionadosTorneo.setPrefWidth(300);
        seleccionadosTorneo.getStyleClass().add("custom-list-view");

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
            Tournament nuevoTorneo = new Tournament(
                    nombreTorneo.getText().trim(),
                    deporteTorneo.getValue(),
                    Integer.parseInt(tiempoTorneo.getText().trim()),
                    Integer.parseInt(cantidadTorneo.getText().trim()),
                    seleccionadosTorneo.getItems(),
                    "iniciado"
            );

            TournamentService tournamentService = new TournamentService();
            boolean success = tournamentService.addTournament(nuevoTorneo);
            if (!success) {
                mensajeUtil.show(Alert.AlertType.ERROR, "Error", "No se pudo guardar el torneo.");
                return;
            }
            MFXButton crearOtro = new MFXButton("Crear Otro Torneo");
            crearOtro.setStyle("-fx-background-color: #690093; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            crearOtro.getStyleClass().add("stepper-button");
            crearOtro.setOnAction(e -> cargarVistaDesdeCero());


            VBox contenedorFinal = new VBox(20, completedLabel, crearOtro);
            contenedorFinal.setAlignment(Pos.CENTER);

            box.getChildren().setAll(contenedorFinal);
        });

        stepper.setOnBeforePrevious(event -> {
            if (stepper.isLastToggle()) {
                checkbox.setSelected(false);
                box.getChildren().setAll(vBox, b5);
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

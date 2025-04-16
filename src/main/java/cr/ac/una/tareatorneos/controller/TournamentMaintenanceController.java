package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import cr.ac.una.tareatorneos.util.FlowController;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * FXML Controller class
 *
 * @author josue_5njzopn
 */
public class TournamentMaintenanceController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label lblTournamentCreationTitulo;
    @FXML
    private Separator sprTournamentMaintenance;
    @FXML
    private MFXButton btnGuardarTorneo;
    @FXML
    private MFXFilterComboBox<String> cmbMantenimientoTorneo;
    @FXML
    private MFXTableView<Tournament> tbvMantenimientoTorneo;
    @FXML
    private MFXButton btnEliminarTorneo;
    @FXML
    private MFXTextField txtfieldNombreMantenimiento;
    @FXML
    private MFXFilterComboBox<String> cmbDeportesRegistradosMantenimiento;
    @FXML
    private MFXTextField txtfieldTiempoMantenimiento;
    @FXML
    private MFXTextField txtfieldCantidadMantenimiento;
    @FXML
    private MFXButton btnEliminarEquipos;
    @FXML
    private TabPane tabPanePrincipal;
    @FXML
    private Tab tabMantenimientoTorneos;
    @FXML
    private ImageView imgLupaMorada;
    @FXML
    private ImageView imgInfoTorneos;
    @FXML
    private MFXButton btnAgregarEquiposMantenimientoTorneo;
    @FXML
    private MFXButton btnModificarTorneo;
    @FXML
    private MFXButton btnBarrrerInfoTorneo;
    @FXML
    private ImageView imgInfoCreacionTorneo;
    @FXML
    private Tab tabSeleccionEquipos;
    @FXML
    private Label lblNombreTorneoSeleccionEquipos;
    @FXML
    private Label lblCantidadEquiposSeleccionEquipos;
    @FXML
    private MFXCheckListView<String> chklistviewEquiposSeleccionados1;
    @FXML
    private MFXTextField txtfieldBuscarPorNombre;

    @FXML
    private MFXCheckListView<String> chklistviewEquiposDisponibles1;
    @FXML
    private MFXButton btnAgregarEquiposSeleccionEquipos;
    @FXML
    private MFXButton btnVolverMantenimiento;
    @FXML
    private ImageView imgBuscarPorNombre;
    @FXML
    private ImageView imgLimpiarBusqueda;

    private Mensaje mensajeUtil = new Mensaje();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColumnasTabla();
        populateComboBoxMantenimientoDeporte();
        cmbMantenimientoTorneo.selectFirst();

        populateComboBoxRegistroDeporte();
        cargarTodosLosTorneos();
        cmbMantenimientoTorneo.setOnAction(e -> {
            cargarTorneosEnTabla();

            Platform.runLater(() -> {
                root.requestFocus();
            });
        });
        toolTipInfo();
        bloquearCambioManualDeTabs();
        animarImagenComoBoton(imgBuscarPorNombre);
        animarImagenComoBoton(imgLimpiarBusqueda);

        tbvMantenimientoTorneo.getSelectionModel().selectionProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !newSelection.isEmpty()) {
                Tournament torneoSeleccionado = newSelection.values().iterator().next();


                cmbDeportesRegistradosMantenimiento.selectItem(torneoSeleccionado.getDeporte());
                txtfieldNombreMantenimiento.setText(torneoSeleccionado.getNombre());
                txtfieldTiempoMantenimiento.setText(String.valueOf(torneoSeleccionado.getTiempoPorPartido()));
                txtfieldCantidadMantenimiento.setText(String.valueOf(torneoSeleccionado.getCantidadEquipos()));
                lblNombreTorneoSeleccionEquipos.setText(torneoSeleccionado.getNombre());
                lblCantidadEquiposSeleccionEquipos.setText(String.valueOf(torneoSeleccionado.getCantidadEquipos()));
                txtfieldBuscarPorNombre.clear();
            }
        });

    }


    @FXML
    private void OnActionBtnGuardarTorneo(ActionEvent event) {
        String deporte = (String) cmbDeportesRegistradosMantenimiento.getSelectedItem();
        String nombre = txtfieldNombreMantenimiento.getText().trim();
        String tiempoStr = txtfieldTiempoMantenimiento.getText().trim();
        String cantidadStr = txtfieldCantidadMantenimiento.getText().trim();

        if (deporte == null || nombre.isEmpty() || tiempoStr.isEmpty() || cantidadStr.isEmpty()) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "No se pudo guardar",
                    "Todos los campos son obligatorios.");
            return;
        }

        int tiempoPorPartido;
        int cantidadEquipos;

        try {
            tiempoPorPartido = Integer.parseInt(tiempoStr);
            cantidadEquipos = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Datos inválidos",
                    "Tiempo y cantidad deben ser números enteros.");
            return;
        }

        if (tiempoPorPartido < 1 || tiempoPorPartido > 10) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Tiempo inválido",
                    "El tiempo por partido debe estar entre 1 y 10 minutos.");
            return;
        }

        if (cantidadEquipos < 2 || cantidadEquipos > 32) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Cantidad inválida",
                    "La cantidad de equipos debe estar entre 2 y 32.");
            return;
        }

        TournamentService service = new TournamentService();

        if (service.tournamentExists(nombre)) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                    "Torneo duplicado",
                    "Ya existe un torneo con este nombre.");
            return;
        }

        Tournament nuevoTorneo = new Tournament(nombre, deporte, tiempoPorPartido, cantidadEquipos, new ArrayList<>(), "Por comenzar");

        boolean guardado = service.addTournament(nuevoTorneo);

        if (guardado) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Torneo guardado exitosamente.");
            limpiarFormulario();
            cargarTorneosEnTabla();
        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo guardar el torneo.");
        }
    }


    @FXML
    private void OnActionBtnEliminarTorneo(ActionEvent event) {
        TournamentService service = new TournamentService();
        Tournament torneoSeleccionado = (Tournament) tbvMantenimientoTorneo.getSelectionModel().getSelectedValue();

        if (torneoSeleccionado == null) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                    "Eliminación inválida",
                    "Debe seleccionar un torneo de la tabla para eliminar.");
            return;
        }

        Tournament torneoActual = service.getTournamentByName(torneoSeleccionado.getNombre());
        String estado = torneoActual.getEstado();
        int equiposAgregados = torneoActual.getEquiposParticipantes().size();

        if (!"Finalizado".equalsIgnoreCase(estado) && equiposAgregados > 0) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Eliminación no permitida",
                    "No se puede eliminar este torneo porque tiene equipos agregados y aún no ha finalizado.");
            return;
        }

        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar el torneo: " + torneoSeleccionado.getNombre() + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        boolean eliminado = service.deleteTournament(torneoSeleccionado.getNombre());

        if (eliminado) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Torneo eliminado correctamente.");
            limpiarFormulario();
            cargarTorneosEnTabla();
            File archivoPartidos = new File("data/matches_" + torneoSeleccionado.getNombre() + ".json");
            if (archivoPartidos.exists()) {
                archivoPartidos.delete();
            }
        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar el torneo.");
        }
    }


    @FXML
    private void OnActionBtnEliminarEquipos(ActionEvent event) {
        TournamentService service = new TournamentService();
        TeamService teamService = new TeamService();
        Tournament torneoSeleccionado = (Tournament) tbvMantenimientoTorneo.getSelectionModel().getSelectedValue();

        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();
        Tournament torneo = service.getTournamentByName(nombreTorneo);
        if (torneo == null) return;

        List<String> aEliminar = new ArrayList<>(chklistviewEquiposSeleccionados1.getSelectionModel().getSelectedValues());

        if (aEliminar.isEmpty()) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Sin selección",
                    "Debe seleccionar al menos un equipo para eliminar.");
            return;
        }

        torneo.getEquiposParticipantes().removeAll(aEliminar);
        service.updateTournament(torneo.getNombre(), torneo);

        List<Tournament> todosLosTorneos = service.getAllTournaments();

        for (String nombre : aEliminar) {
            Team equipo = teamService.getTeamByName(nombre);
            if (equipo != null) {
                boolean participaEnOtro = todosLosTorneos.stream()
                        .filter(t -> !t.getNombre().equalsIgnoreCase(torneo.getNombre()))
                        .anyMatch(t -> t.getEquiposParticipantes().contains(nombre));

                if (!participaEnOtro) {
                    equipo.setEstado("disponible");
                    teamService.updateTeam(equipo.getNombre(), equipo);
                }
            }
        }

        chklistviewEquiposSeleccionados1.getItems().removeAll(aEliminar);
        chklistviewEquiposDisponibles1.getItems().addAll(aEliminar);

        TeamsMaintenanceController controller = (TeamsMaintenanceController)
                FlowController.getInstance().getController("TeamsMaintenanceView");

        if (controller != null) {
            controller.recargarEquiposDesdeJSON();
        }
        chklistviewEquiposSeleccionados1.getSelectionModel().clearSelection();

    }

    @FXML
    private void handleTableClickTorneosMantenimiento(MouseEvent event) {

    }

    @FXML
    private void OnActionBtnAgregarEquiposMantenimientoTorneo(ActionEvent event) {
        TournamentService service = new TournamentService();
        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();

        Tournament torneo = service.getTournamentByName(nombreTorneo);
        if (torneo == null) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                    "Advertencia", "Debe seleccionar un torneo existente para ir a seleccion de equipos.");
            return;
        }
        tabPanePrincipal.getSelectionModel().select(tabSeleccionEquipos);

        Platform.runLater(() -> {
            tabSeleccionEquipos.getContent().requestFocus();
        });

        chklistviewEquiposSeleccionados1.getItems().setAll(torneo.getEquiposParticipantes());

        cargarEquiposDisponiblesPorDeporte(torneo.getDeporte(), torneo.getEquiposParticipantes());
    }

    @FXML
    private void OnActionBtnModificarTorneo(ActionEvent event) {
        Tournament torneoSeleccionado = (Tournament) tbvMantenimientoTorneo.getSelectionModel().getSelectedValue();

        if (torneoSeleccionado == null) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Modificación inválida",
                    "Debe seleccionar un torneo de la tabla para modificarlo.");
            return;
        }

        String nuevoNombre = txtfieldNombreMantenimiento.getText().trim();
        String nuevoDeporte = (String) cmbDeportesRegistradosMantenimiento.getSelectedItem();
        String nuevoTiempoStr = txtfieldTiempoMantenimiento.getText().trim();
        String nuevaCantidadStr = txtfieldCantidadMantenimiento.getText().trim();

        if (nuevoNombre.isEmpty() || nuevoDeporte == null || nuevoTiempoStr.isEmpty() || nuevaCantidadStr.isEmpty()) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Campos vacíos",
                    "Todos los campos deben estar completos para modificar.");
            return;
        }

        int nuevoTiempo;
        int nuevaCantidad;

        try {
            nuevoTiempo = Integer.parseInt(nuevoTiempoStr);
            nuevaCantidad = Integer.parseInt(nuevaCantidadStr);
        } catch (NumberFormatException e) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Datos inválidos",
                    "Tiempo y cantidad deben ser valores numéricos válidos.");
            return;
        }

        if (nuevoTiempo < 1 || nuevoTiempo > 10) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Tiempo inválido",
                    "El tiempo por partido debe estar entre 1 y 10 minutos.");
            return;
        }

        if (nuevaCantidad < 2 || nuevaCantidad > 32) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Cantidad inválida",
                    "La cantidad de equipos debe estar entre 2 y 32.");
            return;
        }

        TournamentService service = new TournamentService();
        Tournament torneoActual = service.getTournamentByName(torneoSeleccionado.getNombre());


        boolean sinCambios = torneoActual.getNombre().equals(nuevoNombre) &&
                torneoActual.getDeporte().equals(nuevoDeporte) &&
                torneoActual.getTiempoPorPartido() == nuevoTiempo &&
                torneoActual.getCantidadEquipos() == nuevaCantidad;

        if (sinCambios) {
            mensajeUtil.show(Alert.AlertType.INFORMATION,
                    "Sin cambios detectados",
                    "No se han realizado modificaciones al torneo.");
            return;
        }


        if (torneoActual == null) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Error de datos",
                    "No se encontró el torneo original.");
            return;
        }

        int equiposAgregados = torneoActual.getEquiposParticipantes().size();

        if (nuevaCantidad < equiposAgregados) {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Cantidad inválida",
                    "Ya hay " + equiposAgregados + " equipos agregados al torneo.\n" +
                            "No se puede reducir la cantidad por debajo de esa cifra.");
            return;
        }

        if (!torneoActual.getDeporte().equalsIgnoreCase(nuevoDeporte) && equiposAgregados > 0) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Cambio de deporte no permitido",
                    "Este torneo ya tiene equipos agregados y no se puede cambiar el deporte.");
            return;
        }

        torneoActual.setNombre(nuevoNombre);
        torneoActual.setDeporte(nuevoDeporte);
        torneoActual.setTiempoPorPartido(nuevoTiempo);
        torneoActual.setCantidadEquipos(nuevaCantidad);

        boolean modificado = service.updateTournament(torneoSeleccionado.getNombre(), torneoActual);

        if (modificado) {
            mensajeUtil.show(Alert.AlertType.INFORMATION,
                    "Torneo actualizado",
                    "Los datos del torneo fueron modificados correctamente.");
            limpiarFormulario();
            cargarTorneosEnTabla();
        } else {
            mensajeUtil.show(Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo modificar el torneo.");
        }
    }


    @FXML
    private void OnActionBtnBarrerInfoTorneo(ActionEvent event) {
        limpiarFormulario();
    }


    @FXML
    private void imgBuscarPorNombre(MouseEvent event) {
        String filtro = txtfieldBuscarPorNombre.getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Ingrese una letra o nombre para buscar equipos.");
            return;
        }

        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();
        TournamentService torneoService = new TournamentService();
        Tournament torneo = torneoService.getTournamentByName(nombreTorneo);

        if (torneo == null) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Torneo no seleccionado",
                    "Debe seleccionar un torneo para buscar equipos.");
            return;
        }

        String deporte = torneo.getDeporte();
        List<String> yaSeleccionados = torneo.getEquiposParticipantes();

        TeamService teamService = new TeamService();
        List<Team> todos = teamService.getAllTeams();

        List<String> resultados = todos.stream()
                .filter(team -> team.getDeporte().equalsIgnoreCase(deporte))
                .filter(team -> team.getNombre().toLowerCase().contains(filtro))
                .map(Team::getNombre)
                .filter(nombre -> !yaSeleccionados.contains(nombre))
                .toList();

        if (resultados.isEmpty()) {
            chklistviewEquiposDisponibles1.getItems().clear();
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "No encontrado",
                    "No se encontraron equipos que coincidan con ese texto para este deporte.");
        } else {
            chklistviewEquiposDisponibles1.getItems().setAll(resultados);
        }
    }


    @FXML
    void imgLimpiarBusqueda(MouseEvent event) {
        txtfieldBuscarPorNombre.clear();

        String nombreTorneoActual = lblNombreTorneoSeleccionEquipos.getText();
        TournamentService torneoService = new TournamentService();
        Tournament torneo = torneoService.getTournamentByName(nombreTorneoActual);

        if (torneo == null) return;

        cargarEquiposDisponiblesPorDeporte(torneo.getDeporte(), torneo.getEquiposParticipantes());

    }

    @FXML
    private void OnActionBtnAgregarEquiposSeleccionEquipos(ActionEvent event) {
        TournamentService service = new TournamentService();
        TeamService teamService = new TeamService();

        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();
        Tournament torneo = service.getTournamentByName(nombreTorneo);
        if (torneo == null) return;

        List<String> seleccionados = new ArrayList<>(chklistviewEquiposDisponibles1.getSelectionModel().getSelectedValues());

        if (seleccionados.isEmpty()) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Sin selección",
                    "Debe seleccionar al menos un equipo disponible para agregar.");
            return;
        }

        int yaAgregados = torneo.getEquiposParticipantes().size();
        int cantidadPermitida = torneo.getCantidadEquipos();
        int totalDespuesDeAgregar = yaAgregados + seleccionados.size();

        if (totalDespuesDeAgregar > cantidadPermitida) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Cantidad excedida",
                    "No se pueden agregar " + seleccionados.size() + " equipos.\n" +
                            "Actualmente hay " + yaAgregados + " equipos registrados.\n" +
                            "El máximo permitido es " + cantidadPermitida + ".");
            return;
        }

        torneo.getEquiposParticipantes().addAll(seleccionados);
        service.updateTournament(torneo.getNombre(), torneo);

        for (String nombre : seleccionados) {
            Team equipo = teamService.getTeamByName(nombre);
            if (equipo != null) {
                equipo.setEstado("participante");
                teamService.updateTeam(equipo.getNombre(), equipo);
            }
        }

        chklistviewEquiposSeleccionados1.getItems().addAll(seleccionados);
        chklistviewEquiposDisponibles1.getItems().removeAll(seleccionados);

        TeamsMaintenanceController controller = (TeamsMaintenanceController)
                FlowController.getInstance().getController("TeamsMaintenanceView");

        if (controller != null) {
            controller.recargarEquiposDesdeJSON();
        }

        chklistviewEquiposDisponibles1.getSelectionModel().clearSelection();

    }


    @FXML
    void OnActionBtnVolverMantenimiento(ActionEvent event) {
        tabPanePrincipal.getSelectionModel().select(tabMantenimientoTorneos);

    }

    private void populateComboBoxMantenimientoDeporte() {
        SportService service = new SportService();
        List<Sport> deportes = service.getAllSports();

        Set<String> nombresDeportesUnicos = new TreeSet<>();
        for (Sport deporte : deportes) {
            if (deporte.getNombre() != null && !deporte.getNombre().isBlank()) {
                nombresDeportesUnicos.add(deporte.getNombre());
            }
        }

        ObservableList<String> deportesList = FXCollections.observableArrayList();
        deportesList.add("Todos");
        deportesList.addAll(nombresDeportesUnicos);

        cmbMantenimientoTorneo.setItems(deportesList);
    }


    private void populateComboBoxRegistroDeporte() {
        SportService service = new SportService();
        List<Sport> deportes = service.getAllSports();

        Set<String> nombresDeportesUnicos = new HashSet<>();
        for (Sport deporte : deportes) {
            if (deporte.getNombre() != null && !deporte.getNombre().isBlank()) {
                nombresDeportesUnicos.add(deporte.getNombre());
            }
        }

        ObservableList<String> deportesList = FXCollections.observableArrayList(nombresDeportesUnicos);
        cmbDeportesRegistradosMantenimiento.setItems(deportesList);
    }

    private void limpiarFormulario() {
        cmbDeportesRegistradosMantenimiento.clearSelection();
        txtfieldNombreMantenimiento.clear();
        txtfieldTiempoMantenimiento.clear();
        txtfieldCantidadMantenimiento.clear();
        lblNombreTorneoSeleccionEquipos.setText("");
        lblCantidadEquiposSeleccionEquipos.setText("");
        tbvMantenimientoTorneo.getSelectionModel().clearSelection();

    }

    public void cargarTorneosEnTabla() {
        TournamentService service = new TournamentService();

        String deporteSeleccionado = cmbMantenimientoTorneo.getSelectedItem();
        List<Tournament> torneos;

        if (deporteSeleccionado == null || deporteSeleccionado.isBlank() || deporteSeleccionado.equals("Todos")) {
            torneos = service.getAllTournaments();
        } else {
            torneos = service.getTournamentsBySport(deporteSeleccionado);
        }

        tbvMantenimientoTorneo.getSelectionModel().clearSelection();
        tbvMantenimientoTorneo.getItems().setAll(torneos);
    }


    private void configurarColumnasTabla() {
        tbvMantenimientoTorneo.getTableColumns().clear();

        MFXTableColumn<Tournament> colNombre = new MFXTableColumn<>("Nombre", false, Comparator.comparing(Tournament::getNombre));
        colNombre.setMinWidth(150);
        colNombre.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getNombre));

        MFXTableColumn<Tournament> colDeporte = new MFXTableColumn<>("Deporte", false, Comparator.comparing(Tournament::getDeporte));
        colDeporte.setMinWidth(120);
        colDeporte.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getDeporte));

        MFXTableColumn<Tournament> colEstado = new MFXTableColumn<>("Estado", false, Comparator.comparing(Tournament::getEstado));
        colEstado.setMinWidth(120);
        colEstado.setRowCellFactory(t -> new MFXTableRowCell<>(Tournament::getEstado));

        tbvMantenimientoTorneo.getTableColumns().addAll(colNombre, colDeporte, colEstado);
    }

    private void toolTipInfo() {
        Tooltip tooltip = new Tooltip("INFORMACION IMPORTANTE:\n\n" +
                "➜ Solo podra agregar equipos despues de crear el torneo.\n" +
                "➜ Una vez que haya agregado equipos al torneo no podra modificar su tipo de deporte.\n" +
                "➜ El tiempo por partido debe de ser igual o mayor a 1 minuto, o menor o igual a 10 minutos.\n" +
                "➜ La cantidad de equipos participantes debe de ser igual o mayor a 2 equipos, o igual o menor a 32 equipos.");
        tooltip.setStyle("-fx-background-color: rgba(217, 162, 253, 0.9); " +
                "-fx-text-fill: #690093; " +
                "fx-min-width: 150px; " +
                "-fx-wrap-text: true;" +
                "-fx-padding: 8px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px; " +
                "-fx-font-family: \"Trebuchet MS\", sans-serif; " +
                "-fx-border-color: #690093; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px;");

        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));

        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        imgInfoCreacionTorneo.setOnMouseEntered(event -> {
            double x = imgInfoCreacionTorneo.localToScene(imgInfoCreacionTorneo.getBoundsInLocal()).getMinX();
            double y = imgInfoCreacionTorneo.localToScene(imgInfoCreacionTorneo.getBoundsInLocal()).getMinY();

            tooltip.show(imgInfoCreacionTorneo, imgInfoCreacionTorneo.getScene().getWindow().getX() + x - 645,
                    imgInfoCreacionTorneo.getScene().getWindow().getY() + y - 55);
        });

        imgInfoCreacionTorneo.setOnMouseExited(event -> tooltip.hide());
    }

    private void cargarEquiposDisponiblesPorDeporte(String deporte, List<String> yaSeleccionados) {
        TeamService service = new TeamService();
        List<Team> todos = service.getAllTeams();

        List<String> filtrados = todos.stream()
                .filter(equipo -> equipo.getDeporte().equalsIgnoreCase(deporte))
                .map(Team::getNombre)
                .filter(nombre -> !yaSeleccionados.contains(nombre))
                .toList();

        chklistviewEquiposDisponibles1.getItems().setAll(filtrados);
    }

    private void bloquearCambioManualDeTabs() {
        Platform.runLater(() -> {
            Set<Node> tabHeaders = tabPanePrincipal.lookupAll(".tab");

            for (Node tabHeader : tabHeaders) {
                tabHeader.setMouseTransparent(true);
                tabHeader.setFocusTraversable(false);
            }
        });
    }

    private void cargarTodosLosTorneos() {
        TournamentService service = new TournamentService();
        List<Tournament> todos = service.getAllTournaments();
        tbvMantenimientoTorneo.getItems().setAll(todos);
    }


    private void animarImagenComoBoton(ImageView imageView) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#9a3aff"));
        glow.setRadius(20);
        glow.setSpread(0.3);

        imageView.setOnMouseEntered(e -> {
            imageView.setEffect(glow);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), imageView);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        imageView.setOnMouseExited(e -> {
            imageView.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), imageView);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        imageView.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), imageView);
            st.setToX(0.9);
            st.setToY(0.9);
            st.play();
        });

        imageView.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), imageView);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        imageView.setCursor(Cursor.HAND);
    }

    public void actualizarComboBoxesDeportes() {
        populateComboBoxMantenimientoDeporte();
        populateComboBoxRegistroDeporte();
    }


    @Override
    public void initialize() {

    }
}

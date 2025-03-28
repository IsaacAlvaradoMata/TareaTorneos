package cr.ac.una.tareatorneos.controller;

import cr.ac.una.tareatorneos.model.Sport;
import cr.ac.una.tareatorneos.model.Team;
import cr.ac.una.tareatorneos.model.Tournament;
import cr.ac.una.tareatorneos.service.SportService;
import cr.ac.una.tareatorneos.service.TeamService;
import cr.ac.una.tareatorneos.service.TournamentService;
import cr.ac.una.tareatorneos.util.Mensaje;
import io.github.palexdev.materialfx.controls.*;
import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

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
        populateComboBoxRegistroDeporte();
        cargarTodosLosTorneos();
        cmbMantenimientoTorneo.setOnAction(e -> {
            cargarTorneosEnTabla();

            // 🧠 Evitar que quede el focus atrapado en el combo box
            Platform.runLater(() -> {
                root.requestFocus(); // root es tu AnchorPane padre
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
            cargarTorneosEnTabla(); // (Implementar)
        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo guardar el torneo.");
        }
    }



    @FXML
    private void OnActionBtnEliminarTorneo(ActionEvent event) {
        // Asegúrate de castear correctamente el tipo si usas generics personalizados
        Tournament torneoSeleccionado = (Tournament) tbvMantenimientoTorneo.getSelectionModel().getSelectedValue();

        if (torneoSeleccionado == null) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.WARNING,
                    "Eliminación inválida",
                    "Debe seleccionar un torneo de la tabla para eliminar.");
            return;
        }

        // Confirmación (opcional)
        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar el torneo: " + torneoSeleccionado.getNombre() + "?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        TournamentService service = new TournamentService();
        boolean eliminado = service.deleteTournament(torneoSeleccionado.getNombre());

        if (eliminado) {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Torneo eliminado correctamente.");
            limpiarFormulario();
            cargarTorneosEnTabla();
        } else {
            mensajeUtil.show(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar el torneo.");
        }
    }



    @FXML
    private void OnActionBtnEliminarEquipos(ActionEvent event) {
        TournamentService service = new TournamentService();
        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();

        Tournament torneo = service.getTournamentByName(nombreTorneo);
        if (torneo == null) return;

        List<String> aEliminar = new ArrayList<>(chklistviewEquiposSeleccionados1.getSelectionModel().getSelectedValues());

        torneo.getEquiposParticipantes().removeAll(aEliminar);
        service.updateTournament(torneo.getNombre(), torneo);

        // Refrescar listas
        chklistviewEquiposSeleccionados1.getItems().removeAll(aEliminar);
        chklistviewEquiposDisponibles1.getItems().addAll(aEliminar);
    }



    @FXML
    private void handleTableClickTorneosMantenimiento(MouseEvent event) {
//        tbvMantenimientoTorneo.getSelectionModel().selectionProperty().addListener((obs, oldSelection, newSelection) -> {
//            if (newSelection != null && !newSelection.isEmpty()) {
//                Tournament torneoSeleccionado = newSelection.values().iterator().next();
//
//
//                cmbDeportesRegistradosMantenimiento.selectItem(torneoSeleccionado.getDeporte());
//                txtfieldNombreMantenimiento.setText(torneoSeleccionado.getNombre());
//                txtfieldTiempoMantenimiento.setText(String.valueOf(torneoSeleccionado.getTiempoPorPartido()));
//                txtfieldCantidadMantenimiento.setText(String.valueOf(torneoSeleccionado.getCantidadEquipos()));
//                lblNombreTorneoSeleccionEquipos.setText(torneoSeleccionado.getNombre());
//                lblCantidadEquiposSeleccionEquipos.setText(String.valueOf(torneoSeleccionado.getCantidadEquipos()));
//                txtfieldBuscarPorNombre.clear();
//            }
//        });
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

        chklistviewEquiposSeleccionados1.getItems().setAll(torneo.getEquiposParticipantes());

        // Cargar disponibles (por deporte)
        cargarEquiposDisponiblesPorDeporte(torneo.getDeporte(), torneo.getEquiposParticipantes());
    }

    @FXML
    private void OnActionBtnModificarTorneo(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnBarrerInfoTorneo(ActionEvent event) {
        limpiarFormulario();
    }


    @FXML
    private void imgBuscarPorNombre(MouseEvent event) {
        String nombreBuscado = txtfieldBuscarPorNombre.getText().trim();

        if (nombreBuscado.isEmpty()) {

            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Ingrese un nombre para buscar un equipo.");
            return;
        }

        String nombreTorneoActual = lblNombreTorneoSeleccionEquipos.getText();
        TournamentService torneoService = new TournamentService();
        Tournament torneo = torneoService.getTournamentByName(nombreTorneoActual);

        if (torneo == null) {
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "Torneo no seleccionado",
                    "Debe seleccionar un torneo para buscar equipos.");
            return;
        }

        String deporteReal = torneo.getDeporte();

        TeamService service = new TeamService();
        List<Team> equipos = service.getAllTeams();

        Optional<Team> equipoEncontrado = equipos.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombreBuscado))
                .filter(e -> e.getDeporte().equalsIgnoreCase(deporteReal))
                .findFirst();

        if (equipoEncontrado.isPresent()) {
            Team equipo = equipoEncontrado.get();

            if (chklistviewEquiposSeleccionados1.getItems().contains(equipo.getNombre())) {
                mensajeUtil.show(Alert.AlertType.INFORMATION,
                        "Equipo ya seleccionado",
                        "Este equipo ya ha sido agregado al torneo.");
                chklistviewEquiposDisponibles1.getItems().clear(); // Ocultarlo si ya está seleccionado
            } else {
                chklistviewEquiposDisponibles1.getItems().setAll(equipo.getNombre());
            }

        } else {
            chklistviewEquiposDisponibles1.getItems().clear();
            mensajeUtil.show(Alert.AlertType.WARNING,
                    "No encontrado",
                    "No se encontró ningún equipo con ese nombre en el deporte del torneo.");
        }
    }

    @FXML
    void imgLimpiarBusqueda(MouseEvent event) {
        txtfieldBuscarPorNombre.clear();

        // Obtener torneo actual
        String nombreTorneoActual = lblNombreTorneoSeleccionEquipos.getText();
        TournamentService torneoService = new TournamentService();
        Tournament torneo = torneoService.getTournamentByName(nombreTorneoActual);

        if (torneo == null) return;

        // Recargar equipos disponibles para ese deporte
        cargarEquiposDisponiblesPorDeporte(torneo.getDeporte(), torneo.getEquiposParticipantes());

    }

    @FXML
    private void OnActionBtnAgregarEquiposSeleccionEquipos(ActionEvent event) {
        TournamentService service = new TournamentService();
        String nombreTorneo = lblNombreTorneoSeleccionEquipos.getText();

        Tournament torneo = service.getTournamentByName(nombreTorneo);
        if (torneo == null) return;

        List<String> seleccionados = new ArrayList<>(chklistviewEquiposDisponibles1.getSelectionModel().getSelectedValues());

        torneo.getEquiposParticipantes().addAll(seleccionados);
        service.updateTournament(torneo.getNombre(), torneo);

        // Refrescar listas
        chklistviewEquiposSeleccionados1.getItems().addAll(seleccionados);
        chklistviewEquiposDisponibles1.getItems().removeAll(seleccionados);
    }

    @FXML
    void OnActionBtnVolverMantenimiento(ActionEvent event) {
        tabPanePrincipal.getSelectionModel().select(tabMantenimientoTorneos);

    }

    private void populateComboBoxMantenimientoDeporte() {
        SportService service = new SportService();
        List<Sport> deportes = service.getAllSports();

        Set<String> nombresDeportesUnicos = new TreeSet<>(); // ordenado alfabéticamente
        for (Sport deporte : deportes) {
            if (deporte.getNombre() != null && !deporte.getNombre().isBlank()) {
                nombresDeportesUnicos.add(deporte.getNombre());
            }
        }

        ObservableList<String> deportesList = FXCollections.observableArrayList();
        deportesList.add("Todos"); // 🟣 Opción especial
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
    }

    private void cargarTorneosEnTabla() {
        String deporteSeleccionado = cmbMantenimientoTorneo.getSelectedItem();

        TournamentService service = new TournamentService();

        if (deporteSeleccionado == null || deporteSeleccionado.isBlank() || deporteSeleccionado.equals("Todos")) {
            // Mostrar todos los torneos
            tbvMantenimientoTorneo.getItems().setAll(service.getAllTournaments());
            return;
        }

        // Mostrar filtrados por deporte
        List<Tournament> torneosFiltrados = service.getTournamentsBySport(deporteSeleccionado);
        tbvMantenimientoTorneo.getItems().setAll(torneosFiltrados);
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

    private void toolTipInfo(){
        Tooltip tooltip = new Tooltip("Solo podra agregar equipos despues de crear el torneo.\n" +
                "Una vez que haya agregado equipos al torneo no podra modificar su tipo de deporte.");
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

        // 🔹 Configurar la duración de la visibilidad
        tooltip.setShowDelay(Duration.millis(200)); // Aparece después de 200ms
        tooltip.setHideDelay(Duration.millis(100)); // Desaparece rápido al salir

        // 🔹 Asociar el Tooltip al icono
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);

        // 🔹 Manejar manualmente la posición del Tooltip
        imgInfoCreacionTorneo.setOnMouseEntered(event -> {
            double x = imgInfoCreacionTorneo.localToScene(imgInfoCreacionTorneo.getBoundsInLocal()).getMinX();
            double y = imgInfoCreacionTorneo.localToScene(imgInfoCreacionTorneo.getBoundsInLocal()).getMinY();

            tooltip.show(imgInfoCreacionTorneo, imgInfoCreacionTorneo.getScene().getWindow().getX() + x - 510,
                    imgInfoCreacionTorneo.getScene().getWindow().getY() + y +5 ); // Ajusta posición arriba del icono
        });

        imgInfoCreacionTorneo.setOnMouseExited(event -> tooltip.hide()); // Ocultar tooltip al salir del icono
    }

    private void cargarEquiposDisponiblesPorDeporte(String deporte, List<String> yaSeleccionados) {
        TeamService service = new TeamService(); // o como se llame tu clase
        List<Team> todos = service.getAllTeams();

        List<String> filtrados = todos.stream()
                .filter(equipo -> equipo.getDeporte().equalsIgnoreCase(deporte))
                .map(Team::getNombre)
                .filter(nombre -> !yaSeleccionados.contains(nombre)) // Ignorar los ya seleccionados
                .toList();

        chklistviewEquiposDisponibles1.getItems().setAll(filtrados);
    }
    private void bloquearCambioManualDeTabs() {
        Platform.runLater(() -> {
            Set<Node> tabHeaders = tabPanePrincipal.lookupAll(".tab");

            for (Node tabHeader : tabHeaders) {
                tabHeader.setMouseTransparent(true); // 🛡️ Ignora clicks, pero se ve igual
                tabHeader.setFocusTraversable(false); // Evita foco por tabulación
            }
        });
    }

    private void cargarTodosLosTorneos() {
        TournamentService service = new TournamentService();
        List<Tournament> todos = service.getAllTournaments();
        tbvMantenimientoTorneo.getItems().setAll(todos);
    }

    private void animarImagenComoBoton(ImageView imageView) {
        // 🔮 Efecto Glow (DropShadow)
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#9a3aff")); // 💜 Purpura eléctrico
        glow.setRadius(20);
        glow.setSpread(0.3);

        // 🧠 Hover IN: Aplica glow + zoom
        imageView.setOnMouseEntered(e -> {
            imageView.setEffect(glow);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), imageView);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        // 👋 Hover OUT: Quita efecto y vuelve a tamaño normal
        imageView.setOnMouseExited(e -> {
            imageView.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), imageView);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        // 🖱️ Click PRESSED: Achica imagen momentáneamente
        imageView.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), imageView);
            st.setToX(0.9);
            st.setToY(0.9);
            st.play();
        });

        // ✅ Click RELEASED: vuelve al tamaño hover
        imageView.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(80), imageView);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        // 🖱️ Cambia cursor a "hand" como un botón
        imageView.setCursor(Cursor.HAND);
    }

    @Override
    public void initialize() {

    }
}

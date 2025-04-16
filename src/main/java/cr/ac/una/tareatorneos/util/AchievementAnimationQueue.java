package cr.ac.una.tareatorneos.util;

import cr.ac.una.tareatorneos.controller.UnlockAchievementController;
import cr.ac.una.tareatorneos.model.Achievement;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AchievementAnimationQueue {

    private static final Queue<Achievement> cola = new LinkedList<>();

    private static boolean mostrando = false;

    private static Runnable accionDespuesDeLogros = null;

    public static void mostrarCuandoPosible(List<Achievement> nuevosLogros) {
        if (!permitidoMostrar || nuevosLogros == null || nuevosLogros.isEmpty()) return;

        for (Achievement logro : nuevosLogros) {
            if (!cola.contains(logro)) cola.offer(logro);
        }

        if (!mostrando) {
            mostrarSiguiente();
        }
    }

    public static void agregarALaCola(Achievement logro) {
        if (!cola.contains(logro)) {
            cola.offer(logro);
        }
    }

    private static boolean permitidoMostrar = true;

    public static void setPermitirMostrar(boolean permitir) {
        permitidoMostrar = permitir;
    }

    public static void ejecutarLuegoDeMostrarTodos(Runnable accionFinal) {
        accionDespuesDeLogros = accionFinal;
    }

    private static void mostrarSiguiente() {
        if (!permitidoMostrar || cola.isEmpty()) {
            mostrando = false;
            return;
        }

        mostrando = true;

        Achievement siguienteLogro = cola.poll();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(AchievementAnimationQueue.class.getResource("/cr/ac/una/tareatorneos/view/UnlockAchievementView.fxml"));
                Parent root = loader.load();
                UnlockAchievementController controller = loader.getController();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setOnCloseRequest(e -> e.consume());

                controller.mostrarLogrosEnCadena(List.of(siguienteLogro), () -> {
                    stage.close();

                    Platform.runLater(() -> {
                        mostrando = false;

                        if (!cola.isEmpty()) {
                            mostrarSiguiente();
                        } else if (accionDespuesDeLogros != null) {
                            Runnable temp = accionDespuesDeLogros;
                            accionDespuesDeLogros = null;
                            temp.run();
                        }
                    });
                });

                stage.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                mostrando = false;
            }
        });
    }

    public static List<Achievement> obtenerLogrosPendientes() {
        return new ArrayList<>(cola);
    }


}

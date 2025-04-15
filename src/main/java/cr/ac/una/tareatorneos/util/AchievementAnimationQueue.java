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

    // 🧠 Cola interna de logros pendientes
    private static final Queue<Achievement> cola = new LinkedList<>();

    // 🔒 Evita mostrar más de una ventana al mismo tiempo
    private static boolean mostrando = false;

    private static Runnable accionDespuesDeLogros = null;

    // ✅ Llama esto cuando quieras intentar mostrar la cola
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

    // 🧨 Método interno: muestra una ventana con el siguiente logro
    private static void mostrarSiguiente() {
        if (!permitidoMostrar || cola.isEmpty()) {
            mostrando = false;
            return;
        }

        mostrando = true;

        List<Achievement> paraMostrar = new ArrayList<>(cola);
        cola.clear();
        try {
            FXMLLoader loader = new FXMLLoader(AchievementAnimationQueue.class.getResource("/cr/ac/una/tareatorneos/view/UnlockAchievementView.fxml"));
            Parent root = loader.load();
            UnlockAchievementController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> e.consume());

            controller.mostrarLogrosEnCadena(paraMostrar, () -> {
                stage.close();
                mostrando = false;

                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(() -> {
                        if (!cola.isEmpty()) {
                            mostrarSiguiente();
                        } else {
                            if (accionDespuesDeLogros != null) {
                                Runnable temp = accionDespuesDeLogros;
                                accionDespuesDeLogros = null;
                                temp.run();
                            }
                        }
                    });
                }).start();
            });

            // ✅ Esta línea es clave para evitar solapamiento
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrando = false;
        }
    }

    // 📦 Método de respaldo para mostrar después de la vista del campeón
    public static List<Achievement> obtenerLogrosPendientes() {
        return new ArrayList<>(cola); // Copia segura
    }
}

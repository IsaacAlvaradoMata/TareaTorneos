package cr.ac.una.tareatorneos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cr.ac.una.tareatorneos.util.FlowController;
import java.io.IOException;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(scene);
        stage.setMinWidth(1350);
        stage.setMinHeight(800);
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain("MainMenuView");

    }
    public static void main(String[] args) {
        launch();
    }
}
package cr.ac.una.tareatorneos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.stage.StageStyle;

import java.io.IOException;
import static javafx.application.Application.launch;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(scene);
        stage.setMinWidth(1350); // Ancho mínimo
        stage.setMinHeight(800); // Alto mínimo
//        stage.initStyle(StageStyle.UNDECORATED);
        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain("MainMenuView");

    }
    public static void main(String[] args) {
        launch();
    }
}
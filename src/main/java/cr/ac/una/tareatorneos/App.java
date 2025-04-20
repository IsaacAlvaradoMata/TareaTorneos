package cr.ac.una.tareatorneos;

import cr.ac.una.tareatorneos.util.DirectoryCreator;
import cr.ac.una.tareatorneos.util.FlowController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        DirectoryCreator.createRequiredDirectories();

        stage.setTitle("BracketChamp");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/cr/ac/una/tareatorneos/resources/BracketChampIcon.png")));
        stage.setScene(scene);
        stage.setMinWidth(1350);
        stage.setMinHeight(800);

        FlowController.getInstance().InitializeFlow(stage, null);
        FlowController.getInstance().goMain("MainMenuView");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

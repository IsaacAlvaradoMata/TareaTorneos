package cr.ac.una.tareatorneos.controller;

import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public abstract class Controller {

    public Stage stage;
    private String accion;

    public static void iconChanger(Stage stage, Image image) {
        stage.getIcons().clear();
        stage.getIcons().add(image);
    }

    public static void nameChanger(Stage stage, String name) {
        stage.setTitle(name);
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void sendTabEvent(KeyEvent event) {
        event.consume();
        KeyEvent keyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.TAB, false, false, false, false);
        ((Control) event.getSource()).fireEvent(keyEvent);
    }

    public abstract void initialize();
}

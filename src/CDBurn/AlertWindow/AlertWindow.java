package CDBurn.AlertWindow;

import javafx.scene.control.Alert;

public class AlertWindow {
    public static void showErrorAlert(String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.showAndWait();
    }
}

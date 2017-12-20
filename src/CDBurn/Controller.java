package CDBurn;

import CDBurn.AlertWindow.AlertWindow;
import CDBurn.CDBurn.CDBurn;
import CDBurn.CDBurn.CDBurnWatcher;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class Controller implements CDBurnWatcher {
    @FXML
    private ListView<String> listView;
    @FXML
    private Button fileChooserButton;
    @FXML
    private Button write;
    @FXML
    private Label  progress;

    private CDBurn cdBurn = new CDBurn();
    private ObservableList<String> fileNames = FXCollections.observableArrayList();
    private FileChooser fileChooser = new FileChooser();
    private ActionListener notificationListener;

    @FXML
    private void initialize() {
        listView.setItems(fileNames);
        fileChooser.setTitle("Choose file");
        cdBurn.setWatcher(this);
    }

    public void chooseFile() {
        File file = fileChooser.showOpenDialog(new Stage());
        if(file != null) {
            fileNames.add(file.getName());
            cdBurn.addFile(file);
        }
    }

    public void writeFiles() {
        fileChooserButton.setDisable(true);
        write.setDisable(true);
        runAsynchronouslyWriteProcess();
    }

    private void runAsynchronouslyWriteProcess() {
        new Thread(() -> cdBurn.burnFiles()).start();
    }

    @Override
    public void setProgressValue(String message) {
        Platform.runLater(() -> {
            progress.setText(message);
            notificationListener.actionPerformed(new ActionEvent(this, 0, message));
        });
    }

    @Override
    public void endRecord() {
        Platform.runLater(() -> {
            fileChooserButton.setDisable(false);
            write.setDisable(false);
            fileNames.clear();
        });
    }

    @Override
    public void showError(String message) {
        Platform.runLater(() -> AlertWindow.showErrorAlert(message));
    }

    public void setNotificationListener(ActionListener notificationListener) {
        this.notificationListener = notificationListener;
//        cdBurn.setNotificationListener(notificationListener);
    }
}

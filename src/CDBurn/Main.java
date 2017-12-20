package CDBurn;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionListener;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CDBurn.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("CDBurn");
        primaryStage.setScene(new Scene(root, 445, 305));

        if (SystemTray.isSupported()) {
            Platform.setImplicitExit(false);
            setTray(primaryStage, loader);
        }
        primaryStage.show();
    }

    public void setTray(Stage primaryStage, FXMLLoader loader) {
        SystemTray sTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("src/resources/icon.png");
        ActionListener listenerShow = actionEvent -> Platform.runLater(primaryStage::show);
        ActionListener listenerClose = actionEvent -> System.exit(0);
        primaryStage.setOnCloseRequest(windowEvent -> primaryStage.hide());

        PopupMenu popup = new PopupMenu();

        MenuItem showItem = new MenuItem("Show");
        MenuItem exitItem = new MenuItem("Close");

        showItem.addActionListener(listenerShow);
        exitItem.addActionListener(listenerClose);

        popup.add(showItem);
        popup.add(exitItem);

        TrayIcon icon = new TrayIcon(image, "I'm not a virus", popup);
        ActionListener listenerNotifications = actionEvent -> {
            if (!primaryStage.isShowing())
                icon.displayMessage("Application", actionEvent.getActionCommand(), TrayIcon.MessageType.INFO);
        };
        Controller controller = loader.getController();
        controller.setNotificationListener(listenerNotifications);
        try {
            sTray.add(icon);
        } catch (AWTException e) {
            System.err.println(e);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}

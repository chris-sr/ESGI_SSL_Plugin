package com.esgi.sslmanager;

import java.io.IOException;

import com.esgi.sslmanager.core.views.MainViewController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SSLManagerApp extends Application {

	public static final String TITLE = "ESGI SSL Plugin";
	private static StringBuilder console = new StringBuilder();

	private Stage mainStage;
	private Scene mainScene;
    private BorderPane rootLayout;

    @Override
    public void start(Stage mainStage) {
        this.mainStage = mainStage;
        this.mainStage.setTitle(TITLE);
        this.mainStage.setResizable(false);

        initRootLayout();
        initMainView();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SSLManagerApp.class.getResource("core/views/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            mainScene = new Scene(rootLayout);
            mainStage.setScene(mainScene);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SSLManagerApp.class.getResource("core/views/MainView.fxml"));
            AnchorPane mainView = (AnchorPane) loader.load();

            rootLayout.setCenter(mainView);

            MainViewController controller = loader.getController();
            controller.setSSLManagerApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDialogMessage(String title, String contentText) {
    	Alert alert = new Alert(AlertType.INFORMATION);
//    	alert.initOwner(mainStage);
    	alert.setTitle(title);
    	alert.setHeaderText(null);
    	alert.setGraphic(null);
    	alert.setContentText(contentText);

    	alert.showAndWait();
    }

    public static void showError(String title, String headerText, String contentText) {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle(title);
    	alert.setHeaderText(headerText);
    	alert.setContentText(contentText);

    	alert.showAndWait();
    }

    public Stage getMainStage() {
        return mainStage;
    }

	public static void main(String[] args) {
		SSLManagerApp.launch(args);
	}

	public static void appendConsole(String text) {
		console.append(text + System.lineSeparator());
	}

	public String getConsoleText() {
		return console.toString();
	}

	public static void clearConsole() {
		console.setLength(0);
	}
}

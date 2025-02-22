package org.taskmanager.taskmanager;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.taskmanager.taskmanager.controller.MediaLabController;

/**
 * Initial class that starts the application and opens the first controller
 * and the first scene of the MediaLab Assistant.
 */
public class MediaLabAssistant extends Application {

    /**
     * The constructor of the MediaLabAssistant which is never used or called.
     */
    public MediaLabAssistant(){}

    /**
     * Launches the application.
     * @param args The arguments passed on to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the mediaLabController and opens the MediaLab Assistant window.
     * @param primaryStage The primary stage of the application, MediaLab Assistant.
     */
    @Override
    public void start(Stage primaryStage) {

        try {

            // Load the FXML file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/taskmanager/taskmanager/views/MediaLabAssistant.fxml"));
            VBox root = loader.load();

            // Load the controller responding to the FXML file.
            MediaLabController controller = loader.getController();
            controller.initialize();

            primaryStage.setTitle("MediaLab Assistant");

            // Create the first scene and give it the proper style sheet.
            Scene scene = new Scene(root, 1000, 800);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/taskmanager/taskmanager/style/MediaLabAssistant.css")).toExternalForm());
            primaryStage.setScene(scene);

            // The onCloseRequest helps us add actions that we want to happen when the application closes.
            primaryStage.setOnCloseRequest(event -> controller.onClose(primaryStage));

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load FXML.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to show an alert to the user.
     * @param title The title of the alert notification.
     * @param message The message of the alert notification.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

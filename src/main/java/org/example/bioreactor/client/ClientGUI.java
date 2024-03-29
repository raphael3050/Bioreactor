package org.example.bioreactor.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class ClientGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientGUI.class.getResource("/org/example/bioreactor/bioreactor-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        // Bootstrap css stylesheet for JavaFX
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("Bioreactor");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch();
    }
}
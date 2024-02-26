package org.example.bioreactor.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public Button connexionButton;
    public Button deconnexionButton;
    public TableView dataTable;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connexionButton.getStyleClass().add("btn");
        deconnexionButton.getStyleClass().add("btn");
        dataTable.getStyleClass().add("table");
        //dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
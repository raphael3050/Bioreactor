package org.example.bioreactor.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public Button connexionButton;
    public Button deconnexionButton;
    public TableView dataTable;
    public Text connectionStatus;
    public Circle connectionStatusCircle;
    public HBox messageLoggerHbox;
    public TextFlow connectionErrorMsg;
    public Button playButton;
    public Text errorMsgContent;
    private ClientTCP myClt;

    @FXML
    protected void onDeconnectionButtonClick() {
        this.myClt.deconnecterDuServeur();
        this.connectionStatus.setText("déconnecté");
        this.connectionStatusCircle.setFill(Color.RED);
    }

    @FXML
    protected void onConnectionButtonClick() {
        this.myClt = new ClientTCP(this,"localhost", 6666, DataStorage.DataType.JSON);
        if (myClt.connecterAuServeur()) {
            this.connectionStatus.setText("connecté");
            this.connectionStatusCircle.setFill(Color.GREEN);
            myClt.transmettreChaine("PING");
        } else {
            this.displayError("Impossible de se connecter au serveur");
        }
    }


    @FXML
    protected void displayError(String errorMsg) {
        this.connectionErrorMsg.setVisible(true);
        this.connectionErrorMsg.setDisable(false);
        this.errorMsgContent.setText(errorMsg);

        // Timeline pour masquer le message d'erreur après 5 secondes
        Timeline error_display = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            connectionErrorMsg.setVisible(false);
            connectionErrorMsg.setDisable(true);
        }));
        error_display.setCycleCount(1);
        error_display.play();
    }

    @FXML
    protected void onPlayButtonClick() {
        // TODO implement
        if (myClt.connecterAuServeur()){
            myClt.transmettreChaine(String.valueOf(ClientTCP.Command.PLAY));
        } else {
            this.displayError("Connectez-vous au serveur pour lancer la simulation.");
        }
    }

    @FXML
    protected void addDataToTable(String data) {
        // TODO implement
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.connexionButton.getStyleClass().add("btn");
        this.deconnexionButton.getStyleClass().add("btn");
        this.connectionStatus.setText("deconnecté");
        this.connectionStatusCircle.setFill(Color.RED);

        // Error message TextFlow
        this.connectionErrorMsg.setVisible(false);
        this.connectionErrorMsg.setDisable(true);
        this.errorMsgContent.setText("");
    }
}
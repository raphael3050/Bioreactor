package org.example.bioreactor.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable, PropertyChangeListener {
    public Button connexionButton;
    public Button deconnexionButton;

    public TableView<Data> dataTable;
    public TableColumn<Data, String> dateColumn;
    public TableColumn<Data, String> temperatureColumn;
    public TableColumn<Data, String> oxygeneColumn;
    public TableColumn<Data, String> phColumn;

    public Text connectionStatus;
    public Circle connectionStatusCircle;
    public HBox messageLoggerHbox;
    public TextFlow connectionErrorMsg;
    public Button playButton;
    public Text errorMsgContent;
    private ClientTCP myClt;

    @FXML
    protected void onDeconnectionButtonClick() {
        if(this.myClt.isConnected()){
            this.myClt.deconnecterDuServeur();
            this.connectionStatus.setText("déconnecté");
            this.connectionStatusCircle.setFill(Color.RED);
        }
    }

    @FXML
    protected void onConnectionButtonClick() {
        if(this.myClt.isConnected()) {
            this.displayError("Vous êtes déjà connecté.");
        }else{
            if (myClt.connecterAuServeur()) {
                this.connectionStatus.setText("connecté");
                this.connectionStatusCircle.setFill(Color.GREEN);
            } else {
                this.displayError("Impossible de se connecter au serveur");
            }
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
        if (myClt.isConnected()){
            new Thread(() -> {
                myClt.transmettreChaine(String.valueOf(ClientTCP.Command.PLAY)+" 3");
            }).start();
        } else {
            this.displayError("Connectez-vous au serveur pour lancer la simulation.");
        }
    }

    @FXML
    protected void addDataToTable(Data data) {
        this.dataTable.getItems().add(data);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.myClt = new ClientTCP("localhost", 6666, DataStorage.DataType.JSON);
        this.myClt.getDataStorage().getPropertyChangeSupport().addPropertyChangeListener(this);
        this.connexionButton.getStyleClass().add("btn");
        this.deconnexionButton.getStyleClass().add("btn");
        this.connectionStatus.setText("deconnecté");
        this.connectionStatusCircle.setFill(Color.RED);

        // Error message TextFlow
        this.connectionErrorMsg.setVisible(false);
        this.connectionErrorMsg.setDisable(true);
        this.errorMsgContent.setText("");

        // Table

        // Configurer les CellValueFactory pour chaque colonne
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        temperatureColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTemperature().toString()));
        oxygeneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOxygen().toString()));
        phColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPh().toString()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof DataStorage) {
            if (evt.getPropertyName().equals("new_data")) {
                Data data = (Data) evt.getNewValue();
                this.addDataToTable(data);
                this.dataTable.refresh();
            }
        }
    }

}
module org.example.bioreactor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires json.simple;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;

    exports org.example.bioreactor.client;
    opens org.example.bioreactor.client to javafx.fxml;
}
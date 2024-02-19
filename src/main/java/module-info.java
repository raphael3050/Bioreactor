module org.example.bioreactor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    exports org.example.bioreactor.client;
    opens org.example.bioreactor.client to javafx.fxml;
}
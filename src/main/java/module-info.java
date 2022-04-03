module com.example.windowchatlesson4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires lombok;
    requires org.xerial.sqlitejdbc;
    requires java.sql;

    opens com.example.windowchatlesson4 to javafx.fxml;
    exports com.example.windowchatlesson4;
    exports com.example.windowchatlesson4.controllers;
    opens com.example.windowchatlesson4.controllers to javafx.fxml;
}
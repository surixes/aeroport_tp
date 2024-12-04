module org.project.aeroport.app.aeroport_tp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    //requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens org.project.aeroport.app.aeroport_tp to javafx.fxml;
    exports org.project.aeroport.app.aeroport_tp;
    exports org.project.aeroport.app.aeroport_tp.app;
    opens org.project.aeroport.app.aeroport_tp.app to javafx.fxml;
    exports org.project.aeroport.app.aeroport_tp.controller;
    opens org.project.aeroport.app.aeroport_tp.controller to javafx.fxml;
}
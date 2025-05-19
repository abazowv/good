module org.example.autos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens org.example.autos to javafx.fxml;
    exports org.example.autos;
}
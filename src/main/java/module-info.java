module org.example.musannif {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens org.example.musannif to javafx.fxml;
    exports org.example.musannif;
}
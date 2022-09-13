module com.example.klient_witt {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.klient_witt to javafx.fxml;
    exports com.example.klient_witt;
}
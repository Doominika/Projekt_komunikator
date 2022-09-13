module com.example.serwer_witt {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.serwer_witt to javafx.fxml;
    exports com.example.serwer_witt;
}
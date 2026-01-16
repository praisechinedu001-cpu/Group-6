module com.example.main1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.main1 to javafx.fxml;
    exports com.example.main1;
}
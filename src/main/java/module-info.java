module com.example.burvancad {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.burvancad to javafx.fxml;
    exports com.example.burvancad;
}
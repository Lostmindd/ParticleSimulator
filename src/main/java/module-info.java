module com.main.particlesimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.main.particlesimulator to javafx.fxml;
    exports com.main.particlesimulator;
}
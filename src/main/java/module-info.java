module at.wifi.notenmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.base;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.graphics;

    opens at.wifi.notenmanager to javafx.fxml;
    opens at.wifi.notenmanager.model to javafx.fxml, javafx.base;
    opens at.wifi.notenmanager.controller to javafx.fxml;


    exports at.wifi.notenmanager;
    exports at.wifi.notenmanager.model to javafx.fxml;
    exports at.wifi.notenmanager.controller to javafx.fxml;
}

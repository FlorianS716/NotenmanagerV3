package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void handleRegister() {
        loadScene("views/register-view.fxml", "Registrierung");
    }

    @FXML
    private void handleLogin() {
        loadScene("views/login-view.fxml", "Login");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0); // Aktuelles Fenster
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.Main;
import at.wifi.notenmanager.dao.TeacherDAO;
import at.wifi.notenmanager.model.Teacher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static at.wifi.notenmanager.util.PasswordValidator.checkPassword;

public class LoginController {
    @FXML public TextField emailField;
    @FXML public PasswordField passwordField;
    @FXML public Label feedbackLabel;

    private final TeacherDAO teacherDAO = new TeacherDAO();

    public void handleLogin(ActionEvent actionEvent) throws SQLException {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            Teacher teacher = teacherDAO.findByEmail(email);

            if (teacher != null && checkPassword(password, teacher.getPassword())) {
                feedbackLabel.setText("Login erfolgreich.");
                loadDashboard();
            } else {
                feedbackLabel.setText("Fehler bei Anmeldung.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

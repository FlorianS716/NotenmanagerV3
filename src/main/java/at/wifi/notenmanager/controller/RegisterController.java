package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.Main;
import at.wifi.notenmanager.dao.TeacherDAO;
import at.wifi.notenmanager.model.Teacher;
import at.wifi.notenmanager.util.PasswordValidator;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static at.wifi.notenmanager.util.PasswordValidator.hashPassword;

public class RegisterController {
    @FXML public TextField emailField;
    @FXML public PasswordField passwordField;
    @FXML public Label statusLabel;

    private final TeacherDAO teacherDAO = new TeacherDAO();

    public void handleRegister(ActionEvent actionEvent) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!PasswordValidator.isValid(password)){
            statusLabel.setText("Passwort ungültig.");
            return;
        }
        String hashedPassword = hashPassword(password);
        Teacher teacher = new Teacher();
        teacher.setEmail(email);
        teacher.setPassword(hashedPassword);
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        teacher.setCreatedAt(timeStamp);

        try {
            teacherDAO.createTeacher(teacher);
            statusLabel.setText("Registrierung erfolgreich.");
        } catch (SQLException e) {
            statusLabel.setText("Registrierung fehlgeschlagen." + e.getMessage());
        }
    }

    public void handleRedirect(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/welcome-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Notenmanager");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

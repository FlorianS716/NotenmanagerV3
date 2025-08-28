package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.Main;
import at.wifi.notenmanager.dao.TeacherDAO;
import at.wifi.notenmanager.model.Teacher;
import at.wifi.notenmanager.util.PasswordValidator;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static at.wifi.notenmanager.util.PasswordValidator.hashPassword;

public class RegisterController {
    @FXML public TextField emailField;
    @FXML public PasswordField passwordField;
    @FXML public Label statusLabel;
    @FXML public PasswordField passwordFieldConfirm;

    @FXML public CheckBox lenCheck;
    @FXML public CheckBox lowerCheck;
    @FXML public CheckBox upperCheck;
    @FXML public CheckBox digitCheck;
    @FXML public CheckBox specialCheck;
    @FXML public CheckBox matchCheck;

    private final TeacherDAO teacherDAO = new TeacherDAO();

    private static final Pattern lower = Pattern.compile(".*[a-z].*");
    private static final Pattern upper = Pattern.compile(".*[A-Z].*");
    private static final Pattern digit = Pattern.compile(".*\\d.*");
    private static final Pattern special = Pattern.compile(".*[@#$%^!?&*\\-].*");
    private static final Pattern eMail = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    private void initialize(){
        //Live Update bei Eingabe:
        passwordField.textProperty().addListener((obs,o,n) -> updateCheckList());
        passwordFieldConfirm.textProperty().addListener((obs,o,n) -> updateCheckList());
    }

    private void updateCheckList() {
        final String pwd = passwordField.getText() == null ? "" : passwordField.getText();
        final String confirm = passwordFieldConfirm.getText() == null ? "" : passwordFieldConfirm.getText();

        lenCheck.setSelected(pwd.length() >= 5 && pwd.length() <= 20);
        lowerCheck.setSelected(lower.matcher(pwd).matches());
        upperCheck.setSelected(upper.matcher(pwd).matches());
        digitCheck.setSelected(digit.matcher(pwd).matches());
        specialCheck.setSelected(special.matcher(pwd).matches());
        matchCheck.setSelected(!pwd.isEmpty() && pwd.equals(confirm));

        if (!matchCheck.isSelected() && !confirm.isEmpty()) {
            statusLabel.setText("Passwörter stimmen nicht überein.");
        } else if (!pwd.isEmpty() && !PasswordValidator.isValid(pwd)) {
            statusLabel.setText("Passwort erfüllt nicht alle Kriterien.");
        } else {
            statusLabel.setText("");
        }

    }


    public void handleRegister(ActionEvent actionEvent) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = passwordFieldConfirm.getText();

        if (email == null || email.trim().isEmpty()) {
            statusLabel.setText("E-Mail Feld ist leer.");
            return;
        }

        if (!eMail.matcher(email).matches()){
            statusLabel.setText("Bitte eine gültige E-Mail Adresse eingeben.");
            return;
        }


        if (!PasswordValidator.isValid(password)){
            statusLabel.setText("Passwortkriterien nicht erfüllt.");
            return;
        }

        if (!password.equals(confirmPassword)){
            statusLabel.setText("Passwörter stimmen nicht überein.");
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

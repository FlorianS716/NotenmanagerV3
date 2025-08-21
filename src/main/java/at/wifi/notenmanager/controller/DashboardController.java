package at.wifi.notenmanager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML public ToolBar toolbar;
    @FXML public Region spacer;

    @FXML
    private TabPane tabPane;

    @FXML
    private void openStudentsView() {
        loadTab("Schüler", "views/students-view.fxml");
    }

    @FXML
    private void openSubjectView() {
        loadTab("Fächer", "views/subjects-view.fxml");
    }

    @FXML
    private void openGradesView() {
        loadTab("Noten", "views/grades-view.fxml");
    }

    @FXML
    private void openBehaviorView() {
        loadTab("Verhalten", "views/behavior-view.fxml");
    }

    @FXML
    private void openNotesView() {
        loadTab("Notizen", "views/notes-view.fxml");
    }

    @FXML
    private void openStatisticsView() {
        loadTab("Statistik", "views/statistics-view.fxml");
    }

    private void loadTab(String title, String fxmlFile) {
        // Prüfen, ob Tab schon offen ist
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(title)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/wifi/notenmanager/" + fxmlFile));
            Parent content = loader.load();
            Tab tab = new Tab(title);
            tab.setContent(content);
            tab.setClosable(true);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Fehler beim Laden der Ansicht: " + fxmlFile);
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Logout erfolgreich.");
        alert.showAndWait();

        // Anwendung beenden
        Stage stage = (Stage) tabPane.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

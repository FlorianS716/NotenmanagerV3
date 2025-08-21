package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.model.GradingComponent;
import at.wifi.notenmanager.model.Students;
import at.wifi.notenmanager.model.Subject;
import at.wifi.notenmanager.service.GradingComponentServiceImpl;
import at.wifi.notenmanager.service.SubjectService;
import at.wifi.notenmanager.service.SubjectServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SubjectController {

    @FXML public TextField nameField;
    @FXML public Label statusLabel;
    @FXML public Button saveButton;
    @FXML public Button deleteButton;
    @FXML public Button reloadButton;
    @FXML public Button editDetailsButton;
    @FXML public TableView<Subject> subjectTable;
    @FXML public TableColumn<Subject, Number> idColumn;
    @FXML public TableColumn<Subject, String> nameColumn;
    @FXML public TableColumn<Subject, String> componentsColumn;

    private final SubjectService subjectService = new SubjectServiceImpl();
    private final GradingComponentServiceImpl gradingComponentService = new GradingComponentServiceImpl();
    private Subject selectedForEdit;


    @FXML
    public void initialize() {
        // Tabellenbindung
        idColumn.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        componentsColumn.setCellValueFactory(c -> new SimpleStringProperty(getComponentSummary(c.getValue())));

        // Zeilenauswahl -> Formular
        subjectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, neu) -> {
            selectedForEdit = neu;
            if (neu != null) nameField.setText(neu.getName());
        });

        refreshTable();
    }

    @FXML
    private void onReload() {
        refreshTable();
        setStatus("");
    }

    public void onSave(ActionEvent actionEvent) throws SQLException {
        String name = nameField.getText();

        try {
            Subject subject = new Subject();
            if (selectedForEdit != null){
                subject.setId(selectedForEdit.getId());
            }
            subject.setName(name);

            if (selectedForEdit == null){
                subjectService.createSubject(subject);
                statusLabel.setText("Fach wurde angelegt.");
            } else {
                subjectService.updateSubject(subject);
                statusLabel.setText("Fach wurde aktualisiert.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void onDelete(ActionEvent actionEvent) {
        Subject subject = subjectTable.getSelectionModel().getSelectedItem();
        if (subject == null){
            setStatus("Wähle zuerst ein Fach aus der Tabelle aus.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Fach wirklich löschen?");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK){
                try {
                    subjectService.deleteSubject(subject.getId());
                    setStatus("Fach wurde gelöscht.");
                    clearForm();
                    refreshTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onReload(ActionEvent actionEvent) {
    }

    public void onEditDetails(ActionEvent actionEvent) {
        Subject sel = subjectTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStatus("Bitte zuerst ein Fach auswählen.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/wifi/notenmanager/views/grading-component-view.fxml"));
            Parent root = loader.load();

            GradingCompController controller = loader.getController();
            controller.setSubject(sel);

            Stage stage = new Stage();
            stage.setTitle("Bewertungskomponenten – " + sel.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            setStatus("Fehler beim Öffnen: " + e.getMessage());
        }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void clearForm(){
        nameField.clear();
    }

    private void refreshTable() {
        try {
            List<Subject> list = subjectService.findAll();
            subjectTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            setStatus("Fehler beim Laden: " + e.getMessage());
        }
    }

    private String getComponentSummary(Subject subject) {
        try {
            List<GradingComponent> comps = gradingComponentService.findBySubjectId(subject.getId());
            if (comps.isEmpty()) return "Keine Komponenten";
            return comps.stream()
                    .map(c -> c.getName() + " (" + (int)(c.getWeight() * 100) + "%)")
                    .collect(Collectors.joining(", "));
        } catch (SQLException e) {
            return "Fehler beim Laden";
        }
    }

}

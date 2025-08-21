package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.model.GradingComponent;
import at.wifi.notenmanager.model.Subject;
import at.wifi.notenmanager.service.GradingComponentService;
import at.wifi.notenmanager.service.GradingComponentServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class GradingCompController {


    @FXML public TableView<GradingComponent> componentTable;
    @FXML public TableColumn<GradingComponent, Number> idColumn;
    @FXML public TableColumn<GradingComponent, String> nameColumn;
    @FXML public TableColumn<GradingComponent, Number> weightColumn;
    @FXML public Label statusLabel;
    @FXML public Button addButton;
    @FXML public Button updateButton;
    @FXML public Button deleteButton;
    @FXML public Button saveButton;
    @FXML public Label totalLabel;
    @FXML public TextField nameField;

    private final GradingComponentService componentService = new GradingComponentServiceImpl();
    private final ObservableList<GradingComponent> components = FXCollections.observableArrayList();

    private Subject subject;
    private GradingComponent selected;

    public void setSubject(Subject subject) {
        this.subject = subject;
        loadComponents();
    }

    @FXML
    public void initialize(){
        idColumn.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        weightColumn.setCellValueFactory(c-> new SimpleIntegerProperty((int) c.getValue().getWeight()));
        weightColumn.setCellFactory(column -> new TableCell<>(){
            private final Slider slider = new Slider(0,100,50);

            {
                slider.setMajorTickUnit(5);
                slider.setMinorTickCount(0);
                slider.setShowTickLabels(true);
                slider.setShowTickMarks(true);
                slider.setSnapToTicks(true);
                slider.setPrefWidth(140);
                slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    GradingComponent component = getTableView().getItems().get(getIndex());
                    component.setWeight((float) (newVal.doubleValue() / 100));
                    updateTotalLabel();
                });
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    slider.setValue(item.doubleValue());
                    setGraphic(slider);
                }
            }
        });

        componentTable.setItems(components);

        componentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, neu) -> {
            selected = neu;
            if (neu != null) {
                nameField.setText(neu.getName());
            }
        });
    }

    public void onAdd(ActionEvent actionEvent) {
        String name = trim(nameField.getText());

        if (name.isEmpty()) {
            setStatus("Bitte einen Namen für die Komponente eingeben.");
            return;
        }

        try {
            GradingComponent gc = new GradingComponent(0, subject.getId(), name, 0.0f);
            componentService.save(gc);
            loadComponents();
            clearForm();
            setStatus("Komponente hinzugefügt.");
        } catch (SQLException e) {
            setStatus("Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void onUpdate(ActionEvent actionEvent) {
            if (selected == null) {
                setStatus("Keine Komponente ausgewählt.");
                return;
            }

            String name = trim(nameField.getText());
            if (name.isEmpty()) {
                setStatus("Bitte gültigen Namen eingeben.");
                return;
            }

            try {
                selected.setName(name);
                componentService.update(selected);
                loadComponents();
                clearForm();
                setStatus("Komponente aktualisiert.");
            } catch (SQLException e) {
                setStatus("Update-Fehler: " + e.getMessage());
            }
        }


    public void onDelete(ActionEvent actionEvent) {
        if (selected == null) {
            setStatus("Bitte Komponente auswählen.");
            return;
        }

        try {
            componentService.delete(selected.getId());
            loadComponents();
            clearForm();
            setStatus("Komponente gelöscht.");
        } catch (SQLException e) {
            setStatus("Löschfehler: " + e.getMessage());
        }

    }

    public void onSave(ActionEvent actionEvent) {
        double sum = components.stream()
                .mapToDouble(GradingComponent::getWeight)
                .sum();

        if (Math.abs(sum - 1.0) > 0.1) {
            setStatus("Die Gewichtung muss exakt 100 % ergeben.");
            return;
        }

        try {
            for (GradingComponent gc : components) {
                componentService.update(gc);
            }
            setStatus("Alle Gewichtungen gespeichert.");
        } catch (SQLException e) {
            setStatus("Fehler beim Speichern: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameField.clear();
        componentTable.getSelectionModel().clearSelection();
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private void loadComponents() {
        try {
            components.setAll(componentService.findBySubjectId(subject.getId()));
            updateTotalLabel();
            componentTable.refresh();
        } catch (SQLException e) {
            setStatus("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void updateTotalLabel() {
        double sum = components.stream()
                .mapToDouble(GradingComponent::getWeight)
                .sum();

        int percent = (int) Math.round(sum * 100);
        totalLabel.setText("Summe: " + percent + " %");

        boolean valid = (percent == 100);
        saveButton.setDisable(!valid);
    }
}

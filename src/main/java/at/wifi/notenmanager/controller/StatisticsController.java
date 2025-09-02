package at.wifi.notenmanager.controller;

import at.wifi.notenmanager.dao.BehaviorDAO;
import at.wifi.notenmanager.dao.GradesDAO;
import at.wifi.notenmanager.dao.SubjectDAO;
import at.wifi.notenmanager.model.*;
import at.wifi.notenmanager.service.GradingComponentServiceImpl;
import at.wifi.notenmanager.service.StudentsServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private ComboBox<Students> studentComboBox;
    @FXML private TableView<Grades> gradesTable;
    @FXML private TableColumn<Grades, String> gradeSubjectColumn;
    @FXML private TableColumn<Grades, String> gradeDateColumn;
    @FXML private TableColumn<Grades, String> gradeValueColumn;
    @FXML private TableColumn<Grades, String> gradeTypeColumn;

    @FXML private TableView<Behavior> behaviorTable;
    @FXML private TableColumn<Behavior, String> behaviorSubjectColumn;
    @FXML private TableColumn<Behavior, String> behaviorDateColumn;
    @FXML private TableColumn<Behavior, String> behaviorRatingColumn;
    @FXML private TableColumn<Behavior, String> behaviorCommentColumn;

    @FXML private VBox finalGradesBox;
    @FXML private VBox chartsContainer;

    private final StudentsServiceImpl studentService = new StudentsServiceImpl();
    private final GradesDAO gradesDAO = new GradesDAO();
    private final BehaviorDAO behaviorDAO = new BehaviorDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    private final Map<Integer, String> subjectMap = new HashMap<>();
    private final ObservableList<Grades> gradesItems = FXCollections.observableArrayList();
    private final ObservableList<Behavior> behaviorItems = FXCollections.observableArrayList();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @FXML
    public void initialize() throws SQLException {
       try {
           //Schülerliste
           List<Students> students = studentService.getAllStudents();
           studentComboBox.setItems(FXCollections.observableArrayList(students));

           //Fächer
           for (Subject subject : subjectDAO.findAll()) {
               subjectMap.put(subject.getId(), subject.getName());
           }

           //Noten-Spalten
           gradeSubjectColumn.setCellValueFactory(c -> new SimpleStringProperty(getSubjectName(c.getValue().getSubjectId())));
           gradeDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().format(dateTimeFormatter)));
           gradeValueColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRating())));
           gradeTypeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
           gradesTable.setItems(gradesItems);

           //Verhalten-Spalten
           behaviorSubjectColumn.setCellValueFactory(c -> new SimpleStringProperty(getSubjectName(c.getValue().getSubjectId())));
           behaviorDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().format(dateTimeFormatter)));
           behaviorRatingColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRating())));

           // Wichtig: PropertyValueFactory bindet an getComment()
           behaviorCommentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

           // Darstellung: falls null/leer, ersatzweise "Kein Kommentar"
           behaviorCommentColumn.setCellFactory(col -> new TableCell<>() {
               @Override
               protected void updateItem(String item, boolean empty) {
                   super.updateItem(item, empty);
                   if (empty) {
                       setText(null);
                   } else {
                       setText(item == null || item.isBlank() ? "Kein Kommentar" : item);
                   }
               }
           });

           behaviorTable.setItems(behaviorItems);

           // Beim Schülerwechsel automatisch laden
           studentComboBox.setOnAction(e -> loadData());

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }

    public void loadData() {
        Students selected = studentComboBox.getValue();
        if (selected == null) {
            gradesItems.clear();
            behaviorItems.clear();
            finalGradesBox.getChildren().clear();
            chartsContainer.getChildren().clear();
            return;
        }

        try {
            gradesItems.setAll(gradesDAO.getGradesByStudentId(selected.getId()));
            behaviorItems.setAll(behaviorDAO.getBehaviorByStudentId(selected.getId()));

            showFinalGrades(selected);

        } catch (SQLException e) {
            showError("Fehler beim Laden der Übersicht: " + e.getMessage());
        }
    }

    private void showFinalGrades(Students student) {
        finalGradesBox.getChildren().clear();
        chartsContainer.getChildren().clear();

        try {
            List<Subject> subjects = subjectDAO.findAll();

            // Ergebnisliste: Fach -> Endnote + (optional) Breakdown-Text
            List<Map.Entry<String, Double>> subjectAverages = new java.util.ArrayList<>();
            Map<String, String> subjectDetails = new java.util.HashMap<>();

            for (Subject subject : subjects) {
                List<Grades> grades = gradesDAO.getGradesByStudentAndSubject(student.getId(), subject.getId());
                List<GradingComponent> components = new GradingComponentServiceImpl().findBySubjectId(subject.getId());

                double weightedSum = 0.0;
                double totalWeight = 0.0;

                StringBuilder details = new StringBuilder(subject.getName()).append(": ");

                for (GradingComponent component : components) {
                    String type = component.getName();
                    double weight = component.getWeight();

                    var matching = grades.stream()
                            .filter(g -> g.getType() != null && g.getType().equalsIgnoreCase(type))
                            .toList();

                    if (!matching.isEmpty()) {
                        double avg = matching.stream().mapToInt(Grades::getRating).average().orElse(0.0);
                        weightedSum += avg * weight;
                        totalWeight += weight;
                        details.append(String.format("%.0f%% %s (⌀ %.2f), ", weight * 100, type, avg));
                    }
                }

                if (totalWeight > 0.0) {
                    double finalNote = weightedSum; // Gewichte sind 0..1 -> weightedSum ist bereits Endnote
                    details.append(String.format("Gesamtnote: %.2f", finalNote));
                    subjectAverages.add(Map.entry(subject.getName(), finalNote));
                    subjectDetails.put(subject.getName(), details.toString());

                    // Optional: Textausgabe pro Fach unter dem Chart
                    finalGradesBox.getChildren().add(new Label(details.toString()));
                }
            }

            if (subjectAverages.isEmpty()) return;

            // Aufsteigend sortieren (besser = niedriger)
            subjectAverages.sort(java.util.Comparator.comparingDouble(Map.Entry::getValue));

            // Achsen
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Fächer");
            xAxis.setCategories(FXCollections.observableArrayList(
                    subjectAverages.stream().map(Map.Entry::getKey).toList()
            ));

            NumberAxis yAxis = new NumberAxis(1, 5, 1);
            yAxis.setLabel("Durchschnitt (1 = sehr gut)");

            // Einfache, robuste Darstellung: ein BarChart, eine Serie
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle("Durchschnittsnoten nach Fach");
            chart.setLegendVisible(false);
            chart.setAnimated(false);
            chart.setCategoryGap(12);
            chart.setBarGap(4);
            chart.setPrefHeight(320);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (var e : subjectAverages) {
                XYChart.Data<String, Number> d = new XYChart.Data<>(e.getKey(), e.getValue());
                series.getData().add(d);
            }
            chart.getData().add(series);

            // Farben & Tooltips setzen (grün -> rot je nach Note)
            chart.applyCss(); // Nodes sicherstellen
            for (XYChart.Data<String, Number> d : series.getData()) {
                double val = d.getYValue().doubleValue();
                String color = colorForGrade(val);
                if (d.getNode() != null) {
                    d.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    String tipText = subjectDetails.getOrDefault(d.getXValue(),
                            d.getXValue() + " – Durchschnitt: " + String.format("%.2f", val));
                    Tooltip.install(d.getNode(), new Tooltip(tipText));
                }
            }

            chartsContainer.getChildren().add(chart);

        } catch (SQLException e) {
            showError("Fehler beim Berechnen der Gesamtnoten: " + e.getMessage());
        }
    }

    /** Grün (1.0) -> Rot (5.0) */
    private String colorForGrade(double grade) {
        double t = Math.min(1.0, Math.max(0.0, (grade - 1.0) / 4.0)); // 1..5 -> 0..1
        int r = (int) Math.round(46 + (220 - 46) * t);
        int g = (int) Math.round(160 + (53  - 160) * t);
        int b = (int) Math.round(67 + (69  - 67) * t);
        return "rgb(" + r + "," + g + "," + b + ")";
    }


    @FXML
    private void exportToExcel() {
        Students student = studentComboBox.getValue();
        if (student == null) {
            showError("Bitte zuerst einen Schüler auswählen.");
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            DataFormat format = workbook.createDataFormat();
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(format.getFormat("0.000"));

            Sheet sheet = workbook.createSheet("Notenübersicht");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Fach");
            header.createCell(1).setCellValue("Komponente");
            header.createCell(2).setCellValue("Gewichtung (%)");
            header.createCell(3).setCellValue("Durchschnitt");
            header.createCell(4).setCellValue("Berechneter Beitrag");

            int rowIndex = 1;

            List<Subject> subjects = subjectDAO.findAll();

            for (Subject subject : subjects) {
                List<Grades> grades = gradesDAO.getGradesByStudentAndSubject(student.getId(), subject.getId());
                List<GradingComponent> components = new GradingComponentServiceImpl().findBySubjectId(subject.getId());

                double weightedSum = 0.0;

                for (GradingComponent component : components) {
                    String type = component.getName();
                    double weight = component.getWeight();

                    var matching = grades.stream()
                            .filter(g -> g.getType() != null && g.getType().equalsIgnoreCase(type))
                            .toList();

                    if (!matching.isEmpty()) {
                        double avg = matching.stream().mapToInt(Grades::getRating).average().orElse(0);
                        double contribution = avg * weight;
                        weightedSum += contribution;

                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(subject.getName());
                        row.createCell(1).setCellValue(type);

                        Cell cWeight = row.createCell(2);
                        cWeight.setCellValue(weight * 100);
                        cWeight.setCellStyle(numberStyle);

                        Cell cAvg = row.createCell(3);
                        cAvg.setCellValue(avg);
                        cAvg.setCellStyle(numberStyle);

                        Cell cContribution = row.createCell(4);
                        cContribution.setCellValue(contribution);
                        cContribution.setCellStyle(numberStyle);
                    }
                }

                Row summaryRow = sheet.createRow(rowIndex++);
                summaryRow.createCell(0).setCellValue("Gesamtnote " + subject.getName());

                Cell cFinal = summaryRow.createCell(4);
                cFinal.setCellValue(weightedSum);
                cFinal.setCellStyle(numberStyle);

                rowIndex++;
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            //Speicherort wählen:

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Excel speichern");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel-Datei (*.xlsx)", "*.xlsx"));
            fileChooser.setInitialFileName("Notenübersicht_" + student.getFirstName() + "_" + student.getLastName() + ".xlsx");
            File home = new File(System.getProperty("user.home"));
            if (home.exists()){
                fileChooser.setInitialDirectory(home);
            }

            Window window = studentComboBox.getScene() != null ? studentComboBox.getScene().getWindow() : null;
            File target = fileChooser.showSaveDialog(window);

            if (target == null) {
                return;
            }

            // Sicherstellen, dass die Datei die richtige Endung hat
            String path = target.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) {
                target = new File(path + ".xlsx");
            }

            try (FileOutputStream fileOut = new FileOutputStream(target)) {
                workbook.write(fileOut);
            }

            showInfo("Excel-Datei erfolgreich exportiert:\n" + target.getAbsolutePath());

        } catch (SQLException | IOException e) {
            showError("Export fehlgeschlagen: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getSubjectName(int subjectId) {
        return subjectMap.getOrDefault(subjectId, "Unbekannt");
    }
}

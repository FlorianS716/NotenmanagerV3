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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

            for (Subject subject : subjects) {
                List<Grades> grades = gradesDAO.getGradesByStudentAndSubject(student.getId(), subject.getId());
                List<GradingComponent> components = new GradingComponentServiceImpl().findBySubjectId(subject.getId());
                double weightedSum = 0.0;
                double totalWeight = 0.0;

                //Beitrag je Komponente
                Map<String, Double> contributions = new LinkedHashMap<>();

                StringBuilder details = new StringBuilder(subject.getName()).append(": ");

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
                        totalWeight += weight;
                        contributions.put(type, contribution);
                        details.append(String.format("%.0f%% %s (⌀ %.2f), ", weight * 100, type, avg));
                    }
                }

                if (totalWeight == 0.0) continue;

                double finalNote = weightedSum;
                details.append(String.format("Gesamtnote: %.2f", finalNote));

                Label label = new Label(details.toString());
                finalGradesBox.getChildren().add(label);

                // Diagramm
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("");

                NumberAxis yAxis = new NumberAxis(1, 5, 1);
                yAxis.setLabel("1 = Sehr gut");

                StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
                chart.setTitle("Gesamtbeitrag der Komponenten – " + subject.getName());
                chart.setLegendVisible(true);
                chart.setAnimated(false);
                chart.setCategoryGap(20);
                chart.setPrefHeight(200);

                //Für jeden Eintrag eine Serie mit genau einem Datenpunkt
                contributions.forEach((String type, Double contrib) -> {
                    XYChart.Series<String, Number> s = new XYChart.Series<>();
                    s.setName(type);
                    s.getData().add(new XYChart.Data<>(subject.getName(), contrib));
                    chart.getData().add(s);
                });
                chartsContainer.getChildren().add(chart);
            }

        } catch (SQLException e) {
            showError("Fehler beim Berechnen der Gesamtnoten: " + e.getMessage());
        }
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
                summaryRow.createCell(0).setCellValue("→ Gesamtnote " + subject.getName());

                Cell cFinal = summaryRow.createCell(4);
                cFinal.setCellValue(weightedSum);
                cFinal.setCellStyle(numberStyle);

                rowIndex++;
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            String filename = "Notenübersicht_" + student.getLastName() + "_" + student.getFirstName() + ".xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }

            showInfo("Excel-Datei erfolgreich exportiert:\n" + filename);

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

package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.Grades;

import java.sql.SQLException;
import java.util.List;

public interface GradesService {

    void createGrade(Grades grade) throws SQLException;
    boolean deleteGrade(int id) throws SQLException;
    List<Grades> getGradesByStudentId(int studentId) throws SQLException;
    void updateGrade(Grades grade) throws SQLException;
    double getAverageGradeByType(int studentId, int subjectId) throws SQLException;
}

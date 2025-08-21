package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.GradesDAO;
import at.wifi.notenmanager.model.Grades;

import java.sql.SQLException;
import java.util.List;

public class GradesServiceImpl implements GradesService{

    private final GradesDAO gradesDAO;

    public GradesServiceImpl(GradesDAO gradesDAO) {
        this.gradesDAO = gradesDAO;
    }


    @Override
    public void createGrade(Grades grade) throws SQLException {
        gradesDAO.createGrade(grade);
    }

    @Override
    public boolean deleteGrade(int id) throws SQLException {
        return gradesDAO.deleteGrade(id);
    }

    @Override
    public List<Grades> getGradesByStudentId(int studentId) throws SQLException {
        return gradesDAO.getGradesByStudentId(studentId);
    }

    @Override
    public void updateGrade(Grades grade) throws SQLException {
        gradesDAO.updateGrade(grade);
    }

    @Override
    public double getAverageGradeByType(int studentId, int subjectId) throws SQLException {
        return gradesDAO.getAverageGrade(studentId, subjectId);
    }

    @Override
    public List<Grades> getGradesByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        return gradesDAO.getGradesByStudentAndSubject(studentId, subjectId);
    }
}

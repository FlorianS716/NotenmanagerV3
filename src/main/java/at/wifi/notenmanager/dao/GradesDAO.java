package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Grades;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GradesDAO {

    public void createGrade(Grades grade) throws SQLException {
        String sql = "INSERT INTO grades (student_id, subject_id, date, rating, type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, grade.getStudentId());
            preparedStatement.setInt(2, grade.getSubjectId());
            preparedStatement.setString(3, grade.getDate().toString());
            preparedStatement.setInt(4, grade.getRating());
            preparedStatement.setString(5, grade.getType());

            preparedStatement.executeUpdate();
        }
    }
    public boolean deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            int deleteRow = preparedStatement.executeUpdate();
            return deleteRow > 0;
        }
    }
    public List<Grades> getGradesByStudentId(int studentId) throws SQLException {
        List<Grades> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? ORDER BY date DESC";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Grades grade = new Grades();
                grade.setId(resultSet.getInt("id"));
                grade.setRating(resultSet.getInt("rating"));
                grade.setDate(LocalDate.parse(resultSet.getString("date")));
                grade.setStudentId(resultSet.getInt("student_id"));
                grade.setSubjectId(resultSet.getInt("subject_id"));
                grade.setType(resultSet.getString("type"));


                grades.add(grade);
            }
        }

        return grades;
    }
    public void updateGrade(Grades grade) throws SQLException {
        String sql = "UPDATE grades SET student_id = ?, subject_id = ?, date = ?, rating = ?, type = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, grade.getStudentId());
            preparedStatement.setInt(2, grade.getSubjectId());
            preparedStatement.setString(3, grade.getDate().toString());
            preparedStatement.setInt(4, grade.getRating());
            preparedStatement.setString(5, grade.getType());
            preparedStatement.setInt(6, grade.getId());

            preparedStatement.executeUpdate();
        }
    }
    public float getAverageGrade(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM grades WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getFloat(1);
            }
        }
        return 0.0f;
    }
    public List<Grades> getGradesByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        List<Grades> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Grades g = new Grades();
                g.setId(rs.getInt("id"));
                g.setStudentId(rs.getInt("student_id"));
                g.setSubjectId(rs.getInt("subject_id"));
                g.setRating(rs.getInt("rating"));
                g.setType(rs.getString("type"));
                g.setDate(LocalDate.parse(rs.getString("date")));
                grades.add(g);
            }
        }

        return grades;
    }


}

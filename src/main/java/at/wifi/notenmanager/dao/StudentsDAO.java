package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Students;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentsDAO {

    public void createStudents(Students students) throws SQLException {
        String sql = "INSERT INTO students (class_id, first_name, last_name, dob, parent_name, strengths, weaknesses, health_info, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, students.getClassId());
            preparedStatement.setString(2, students.getFirstName());
            preparedStatement.setString(3, students.getLastName());
            preparedStatement.setString(4, students.getDob());
            preparedStatement.setString(5, students.getParentName());
            preparedStatement.setString(6, students.getStrengths());
            preparedStatement.setString(7, students.getWeaknesses());
            preparedStatement.setString(8, students.getHealthInfo());
            preparedStatement.setString(9,students.getPhoneNumber());

            preparedStatement.executeUpdate();
        }
    }
    public void deleteById(int id) throws SQLException{
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        }
    }
    public List<Students> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students";
        List<Students> students = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Students student = new Students(
                        resultSet.getInt("id"),
                        resultSet.getString("class_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("dob"),
                        resultSet.getString("parent_name"),
                        resultSet.getString("strengths"),
                        resultSet.getString("weaknesses"),
                        resultSet.getString("health_info"),
                        resultSet.getString("phone_number")
                );
                students.add(student);
            }
        }
        return students;
    }
    public void updateStudent(Students student) throws SQLException {
        String sql = "UPDATE students SET class_id = ?, first_name = ?, last_name = ?, dob = ?, parent_name = ?, strengths = ?, weaknesses = ?, health_info = ?, phone_number = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, student.getClassId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setString(4, student.getDob());
            preparedStatement.setString(5, student.getParentName());
            preparedStatement.setString(6, student.getStrengths());
            preparedStatement.setString(7, student.getWeaknesses());
            preparedStatement.setString(8, student.getHealthInfo());
            preparedStatement.setString(9,student.getPhoneNumber());
            preparedStatement.setInt(10, student.getId());
            preparedStatement.executeUpdate();
        }
    }

}

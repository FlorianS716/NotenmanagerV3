package at.wifi.notenmanager.dao;

import at.wifi.notenmanager.model.Teacher;
import at.wifi.notenmanager.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherDAO {

    public void createTeacher(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teacher (email, password) VALUES (?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, teacher.getEmail());
            preparedStatement.setString(2, teacher.getPassword());
            preparedStatement.executeUpdate();
        }
    }
    public Teacher findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM teacher WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getInt("id"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPassword(rs.getString("password"));
                return teacher;
            }
        }
        return null;
    }
}

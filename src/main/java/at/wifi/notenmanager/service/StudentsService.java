package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.Students;

import java.sql.SQLException;
import java.util.List;

public interface StudentsService {

    void createStudents(Students students) throws SQLException;
    void deleteById(int id) throws SQLException;
    List<Students> getAllStudents() throws SQLException;
    void updateStudent(Students student) throws SQLException;
}

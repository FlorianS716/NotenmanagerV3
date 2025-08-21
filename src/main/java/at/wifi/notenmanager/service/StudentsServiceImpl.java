package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.StudentsDAO;
import at.wifi.notenmanager.model.Students;

import java.sql.SQLException;
import java.util.List;

public class StudentsServiceImpl implements StudentsService{

    private final StudentsDAO studentsDAO;

    public StudentsServiceImpl() {
        this.studentsDAO = new StudentsDAO();
    }

    @Override
    public void createStudents(Students students) throws SQLException {
        studentsDAO.createStudents(students);
    }

    @Override
    public void deleteById(int id) throws SQLException {
        studentsDAO.deleteById(id);
    }

    @Override
    public List<Students> getAllStudents() throws SQLException {
        return studentsDAO.getAllStudents();
    }

    @Override
    public void updateStudent(Students student) throws SQLException {
        studentsDAO.updateStudent(student);
    }
}

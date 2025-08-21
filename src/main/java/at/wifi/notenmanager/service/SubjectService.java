package at.wifi.notenmanager.service;

import at.wifi.notenmanager.model.Subject;

import java.sql.SQLException;
import java.util.List;

public interface SubjectService {

    void createSubject(Subject subject) throws SQLException;
    boolean deleteSubject(int id) throws SQLException;
    void updateSubject(Subject subject) throws SQLException;
    Subject findById(int id) throws SQLException;
    List<Subject> findAll() throws SQLException;

}

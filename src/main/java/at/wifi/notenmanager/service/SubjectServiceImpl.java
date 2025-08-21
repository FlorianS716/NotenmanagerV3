package at.wifi.notenmanager.service;

import at.wifi.notenmanager.dao.SubjectDAO;
import at.wifi.notenmanager.model.Subject;

import java.sql.SQLException;
import java.util.List;

public class SubjectServiceImpl implements SubjectService{

    private final SubjectDAO subjectDAO;

    public SubjectServiceImpl() {
        this.subjectDAO = new SubjectDAO();
    }

    @Override
    public void createSubject(Subject subject) throws SQLException {
        subjectDAO.createSubject(subject);
    }

    @Override
    public boolean deleteSubject(int id) throws SQLException {
        return subjectDAO.deleteSubject(id);
    }

    @Override
    public void updateSubject(Subject subject) throws SQLException {
        subjectDAO.updateSubject(subject);
    }

    @Override
    public Subject findById(int id) throws SQLException {
        return subjectDAO.findById(id);
    }

    @Override
    public List<Subject> findAll() throws SQLException {
        return subjectDAO.findAll();
    }
}

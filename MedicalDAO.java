package dao;

import model.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    // Submit medical request
    public boolean submitMedical(Medical medical) {
        String query = "INSERT INTO medicals (student_id, submission_date, medical_date, reason, document_path, status) " +
                "VALUES (?, CURDATE(), ?, ?, ?, 'pending')";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, medical.getStudentId());
            pstmt.setDate(2, new java.sql.Date(medical.getMedicalDate().getTime()));
            pstmt.setString(3, medical.getReason());
            pstmt.setString(4, medical.getDocumentPath());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get medical requests for a student
    public List<Medical> getStudentMedicals(int studentId) {
        List<Medical> medicals = new ArrayList<>();
        String query = "SELECT m.*, u.full_name as approved_by_name FROM medicals m " +
                "LEFT JOIN users u ON m.approved_by = u.user_id " +
                "WHERE m.student_id = ? ORDER BY m.submission_date DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Medical medical = new Medical();
                medical.setMedicalId(rs.getInt("medical_id"));
                medical.setStudentId(rs.getInt("student_id"));
                medical.setSubmissionDate(rs.getDate("submission_date"));
                medical.setMedicalDate(rs.getDate("medical_date"));
                medical.setReason(rs.getString("reason"));
                medical.setDocumentPath(rs.getString("document_path"));
                medical.setStatus(rs.getString("status"));
                medical.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                medical.setApprovedByName(rs.getString("approved_by_name"));
                medical.setApprovalDate(rs.getDate("approval_date"));
                medical.setRemarks(rs.getString("remarks"));
                medicals.add(medical);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicals;
    }

    // Get all pending medical requests (for technical officer)
    public List<Medical> getPendingMedicals() {
        List<Medical> medicals = new ArrayList<>();
        String query = "SELECT m.*, s.registration_no, u.full_name as student_name " +
                "FROM medicals m " +
                "INNER JOIN students s ON m.student_id = s.student_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "WHERE m.status = 'pending' ORDER BY m.submission_date";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Medical medical = new Medical();
                medical.setMedicalId(rs.getInt("medical_id"));
                medical.setStudentId(rs.getInt("student_id"));
                medical.setStudentName(rs.getString("student_name"));
                medical.setRegistrationNo(rs.getString("registration_no"));
                medical.setSubmissionDate(rs.getDate("submission_date"));
                medical.setMedicalDate(rs.getDate("medical_date"));
                medical.setReason(rs.getString("reason"));
                medical.setStatus(rs.getString("status"));
                medical.setDocumentPath(rs.getString("document_path"));
                medicals.add(medical);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicals;
    }

    // Approve/reject medical request
    public boolean updateMedicalStatus(int medicalId, String status, int approvedBy, String remarks) {
        String query = "UPDATE medicals SET status = ?, approved_by = ?, approval_date = CURDATE(), remarks = ? " +
                "WHERE medical_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, approvedBy);
            pstmt.setString(3, remarks);
            pstmt.setInt(4, medicalId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
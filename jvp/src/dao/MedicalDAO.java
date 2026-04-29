package dao;

import model.Medical;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    // Called once when the app starts to make sure the table has all needed columns.
    // Safe to run even if columns already exist (uses IF NOT EXISTS / ignores errors).
    private static boolean migrated = false;
    private static void ensureSchema() {
        if (migrated) return;
        migrated = true;
        String[] alters = {
            "ALTER TABLE medicals ADD COLUMN IF NOT EXISTS end_date     DATE",
            "ALTER TABLE medicals ADD COLUMN IF NOT EXISTS course_id    INT",
            "ALTER TABLE medicals ADD COLUMN IF NOT EXISTS session_type VARCHAR(20) DEFAULT 'both'",
            "ALTER TABLE medicals MODIFY COLUMN document_path VARCHAR(500)"
        };
        try (Statement st = DatabaseConnection.getConnection().createStatement()) {
            for (String sql : alters) {
                try { st.execute(sql); } catch (SQLException ignored) {}
            }
        } catch (Exception ignored) {}
    }

    // Submit medical request (student module enhanced version)
    public boolean submitMedical(Medical m) {
        ensureSchema();

        String sql =
            "INSERT INTO medicals " +
            "(student_id, submission_date, medical_date, end_date, " +
            " reason, document_path, status, course_id, session_type) " +
            "VALUES (?, CURDATE(), ?, ?, ?, ?, 'pending', ?, ?)";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt   (1, m.getStudentId());
            ps.setDate  (2, toSqlDate(m.getMedicalDate()));
            ps.setDate  (3, m.getEndDate() != null ? toSqlDate(m.getEndDate()) : toSqlDate(m.getMedicalDate()));
            ps.setString(4, m.getReason());
            ps.setString(5, m.getDocumentPath());       // nullable

            if (m.getCourseId() > 0) ps.setInt (6, m.getCourseId());
            else                     ps.setNull(6, Types.INTEGER);

            ps.setString(7, m.getSessionType() != null ? m.getSessionType() : "both");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("submitMedical SQL error: " + e.getMessage());
        }
        return false;
    }

    // Get medical records for a student (student module enhanced version - joins courses)
    public List<Medical> getStudentMedicals(int studentId) {
        ensureSchema();
        List<Medical> list = new ArrayList<>();
        String sql =
            "SELECT m.medical_id, m.student_id, m.submission_date, m.medical_date, " +
            "       m.reason, m.document_path, m.status, m.remarks, " +
            "       IFNULL(m.end_date, m.medical_date) AS end_date, " +
            "       IFNULL(m.session_type,'both')       AS session_type, " +
            "       c.course_code, " +
            "       u.full_name AS approved_by_name, " +
            "       m.approved_by, m.approval_date " +
            "FROM medicals m " +
            "LEFT JOIN courses c ON m.course_id = c.course_id " +
            "LEFT JOIN users u ON m.approved_by = u.user_id " +
            "WHERE m.student_id = ? " +
            "ORDER BY m.medical_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get all pending medical requests (for technical officer / admin)
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

    // Approve/reject medical request (for technical officer / admin)
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

    private java.sql.Date toSqlDate(java.util.Date d) {
        return new java.sql.Date(d.getTime());
    }

    private Medical mapRow(ResultSet rs) throws SQLException {
        Medical m = new Medical();
        m.setMedicalId    (rs.getInt   ("medical_id"));
        m.setStudentId    (rs.getInt   ("student_id"));
        m.setSubmissionDate(rs.getDate ("submission_date"));
        m.setMedicalDate  (rs.getDate  ("medical_date"));
        m.setEndDate      (rs.getDate  ("end_date"));
        m.setReason       (rs.getString("reason"));
        m.setDocumentPath (rs.getString("document_path"));
        m.setStatus       (rs.getString("status"));
        m.setRemarks      (rs.getString("remarks"));
        m.setSessionType  (rs.getString("session_type"));
        try { m.setCourseCode    (rs.getString("course_code"));      } catch (SQLException ignored) {}
        try { m.setApprovedByName(rs.getString("approved_by_name")); } catch (SQLException ignored) {}
        try {
            Object ab = rs.getObject("approved_by");
            if (ab != null) m.setApprovedBy(rs.getInt("approved_by"));
        } catch (SQLException ignored) {}
        try { m.setApprovalDate(rs.getDate("approval_date")); } catch (SQLException ignored) {}
        return m;
    }
}

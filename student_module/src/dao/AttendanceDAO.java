package dao;

import model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<Attendance> getStudentCourseAttendance(int studentId, int courseId) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE student_id = ? AND course_id = ? ORDER BY session_date";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setAttendanceId(rs.getInt("attendance_id"));
                a.setStudentId(rs.getInt("student_id"));
                a.setCourseId(rs.getInt("course_id"));
                a.setSessionDate(rs.getDate("session_date"));
                a.setSessionType(rs.getString("session_type"));
                a.setSessionNumber(rs.getInt("session_number"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getAttendancePercentageWithMedical(int studentId, int courseId, String type) {
        String baseQuery;
        if (type.equals("all")) {
            baseQuery = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND course_id = ?";
        } else {
            baseQuery = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND course_id = ? AND session_type = '" + type + "'";
        }

        String presentQuery;
        if (type.equals("all")) {
            presentQuery = "SELECT COUNT(*) FROM attendance a WHERE a.student_id = ? AND a.course_id = ? AND " +
                    "(a.status = 'present' OR EXISTS (SELECT 1 FROM medicals m WHERE m.student_id = a.student_id " +
                    "AND m.medical_date = a.session_date AND m.status = 'approved'))";
        } else {
            presentQuery = "SELECT COUNT(*) FROM attendance a WHERE a.student_id = ? AND a.course_id = ? " +
                    "AND a.session_type = '" + type + "' AND " +
                    "(a.status = 'present' OR EXISTS (SELECT 1 FROM medicals m WHERE m.student_id = a.student_id " +
                    "AND m.medical_date = a.session_date AND m.status = 'approved'))";
        }

        try {
            int total = 0, present = 0;
            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(baseQuery)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }
            try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(presentQuery)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) present = rs.getInt(1);
            }
            return total == 0 ? 0.0 : (present * 100.0 / total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

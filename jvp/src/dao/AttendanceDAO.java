package dao;

import model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Record attendance
    public boolean recordAttendance(Attendance attendance) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String query = "INSERT INTO attendance (student_id, course_id, session_date, session_type, session_number, status) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getCourseId());
            pstmt.setDate(3, new java.sql.Date(attendance.getSessionDate().getTime()));
            pstmt.setString(4, attendance.getSessionType());
            pstmt.setInt(5, attendance.getSessionNumber());
            pstmt.setString(6, attendance.getStatus());
            pstmt.setString(7, attendance.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get attendance for a student in a course
    public List<Attendance> getStudentCourseAttendance(int studentId, int courseId) {
        List<Attendance> attendances = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return attendances;

        String query = "SELECT a.*, s.registration_no, u.full_name as student_name, c.course_code, c.course_name " +
                "FROM attendance a " +
                "INNER JOIN students s ON a.student_id = s.student_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "INNER JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.student_id = ? AND a.course_id = ? " +
                "ORDER BY a.session_date, a.session_number";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getInt("attendance_id"));
                attendance.setStudentId(rs.getInt("student_id"));
                attendance.setStudentName(rs.getString("student_name"));
                attendance.setRegistrationNo(rs.getString("registration_no"));
                attendance.setCourseId(rs.getInt("course_id"));
                attendance.setCourseCode(rs.getString("course_code"));
                attendance.setCourseName(rs.getString("course_name"));
                attendance.setSessionDate(rs.getDate("session_date"));
                attendance.setSessionType(rs.getString("session_type"));
                attendance.setSessionNumber(rs.getInt("session_number"));
                attendance.setStatus(rs.getString("status"));
                attendances.add(attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendances;
    }

    // Get attendance percentage for a student in a course
    public double getAttendancePercentage(int studentId, int courseId, String sessionType) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        String query;
        if (sessionType.equals("all")) {
            query = "SELECT COUNT(*) as total, SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) as present " +
                    "FROM attendance WHERE student_id = ? AND course_id = ?";
        } else {
            query = "SELECT COUNT(*) as total, SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) as present " +
                    "FROM attendance WHERE student_id = ? AND course_id = ? AND session_type = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            if (!sessionType.equals("all")) {
                pstmt.setString(3, sessionType);
            }
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                if (total > 0) {
                    return (present * 100.0) / total;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get attendance summary for whole batch for a course
    public List<Object[]> getBatchAttendanceSummary(int courseId) {
        List<Object[]> summaries = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return summaries;

        String query = "SELECT s.student_id, u.full_name as student_name, s.registration_no, " +
                "COUNT(a.attendance_id) as total_sessions, " +
                "SUM(CASE WHEN a.status = 'present' THEN 1 ELSE 0 END) as present_sessions " +
                "FROM students s " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN attendance a ON s.student_id = a.student_id AND a.course_id = ? " +
                "GROUP BY s.student_id, u.full_name, s.registration_no";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] summary = new Object[5];
                summary[0] = rs.getInt("student_id");
                summary[1] = rs.getString("student_name");
                summary[2] = rs.getString("registration_no");
                int total = rs.getInt("total_sessions");
                int present = rs.getInt("present_sessions");
                summary[3] = total;
                summary[4] = total > 0 ? (present * 100.0) / total : 0;
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    // Initialize attendance for a course (15 sessions for theory and practical)
    public boolean initializeAttendance(int courseId, int studentId) {
        boolean success = true;
        // Generate 15 theory sessions and 15 practical sessions
        for (int sessionNum = 1; sessionNum <= 15; sessionNum++) {
            // Theory sessions
            Attendance theoryAttendance = new Attendance();
            theoryAttendance.setStudentId(studentId);
            theoryAttendance.setCourseId(courseId);
            theoryAttendance.setSessionType("theory");
            theoryAttendance.setSessionNumber(sessionNum);
            theoryAttendance.setStatus("absent");
            // Set a date (assuming weekly sessions starting from current date)
            java.util.Date date = new java.util.Date();
            date.setDate(date.getDate() + (sessionNum - 1) * 7);
            theoryAttendance.setSessionDate(date);
            if (!recordAttendance(theoryAttendance)) {
                success = false;
            }

            // Practical sessions
            Attendance practicalAttendance = new Attendance();
            practicalAttendance.setStudentId(studentId);
            practicalAttendance.setCourseId(courseId);
            practicalAttendance.setSessionType("practical");
            practicalAttendance.setSessionNumber(sessionNum);
            practicalAttendance.setStatus("absent");
            practicalAttendance.setSessionDate(date);
            if (!recordAttendance(practicalAttendance)) {
                success = false;
            }
        }
        return success;
    }

    // Get attendance with medical consideration
    public double getAttendancePercentageWithMedical(int studentId, int courseId, String sessionType) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        // Get all approved medical date ranges for this student
        String medicalQuery = "SELECT medical_date, IFNULL(end_date, medical_date) AS end_date " +
                "FROM medicals WHERE student_id = ? AND status = 'approved'";
        List<java.sql.Date[]> medicalRanges = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(medicalQuery)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                medicalRanges.add(new java.sql.Date[]{
                    rs.getDate("medical_date"),
                    rs.getDate("end_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Then get attendance considering medical as present
        String query;
        if (sessionType.equals("all")) {
            query = "SELECT a.session_date, a.status FROM attendance a " +
                    "WHERE a.student_id = ? AND a.course_id = ?";
        } else {
            query = "SELECT a.session_date, a.status FROM attendance a " +
                    "WHERE a.student_id = ? AND a.course_id = ? AND a.session_type = ?";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            if (!sessionType.equals("all")) {
                pstmt.setString(3, sessionType);
            }
            ResultSet rs = pstmt.executeQuery();

            int total = 0;
            int present = 0;

            while (rs.next()) {
                total++;
                java.sql.Date sessionDate = rs.getDate("session_date");
                String status = rs.getString("status");

                // Check if this date falls within any approved medical range
                boolean hasMedical = false;
                for (java.sql.Date[] range : medicalRanges) {
                    if (sessionDate != null && range[0] != null && range[1] != null
                            && !sessionDate.before(range[0]) && !sessionDate.after(range[1])) {
                        hasMedical = true;
                        break;
                    }
                }

                if (status.equals("present") || hasMedical) {
                    present++;
                }
            }

            return total > 0 ? (present * 100.0) / total : 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
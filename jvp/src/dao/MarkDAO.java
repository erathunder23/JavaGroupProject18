package dao;

import model.Mark;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    // Upload marks
    public boolean uploadMark(Mark mark) {
        String grade = Mark.calculateGrade(mark.getMarksObtained());
        double gradePoints = Mark.calculateGradePoints(mark.getMarksObtained());

        String query = "INSERT INTO marks (student_id, course_id, exam_type, marks_obtained, max_marks, grade, grade_points) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE marks_obtained = ?, grade = ?, grade_points = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, mark.getStudentId());
            pstmt.setInt(2, mark.getCourseId());
            pstmt.setString(3, mark.getExamType());
            pstmt.setDouble(4, mark.getMarksObtained());
            pstmt.setDouble(5, mark.getMaxMarks());
            pstmt.setString(6, grade);
            pstmt.setDouble(7, gradePoints);
            pstmt.setDouble(8, mark.getMarksObtained());
            pstmt.setString(9, grade);
            pstmt.setDouble(10, gradePoints);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get student marks for a course
    public Mark getStudentCourseMark(int studentId, int courseId, String examType) {
        String query = "SELECT m.*, s.registration_no, u.full_name as student_name " +
                "FROM marks m " +
                "INNER JOIN students s ON m.student_id = s.student_id " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "WHERE m.student_id = ? AND m.course_id = ? AND m.exam_type = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, examType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Mark mark = new Mark();
                mark.setMarkId(rs.getInt("mark_id"));
                mark.setStudentId(rs.getInt("student_id"));
                mark.setStudentName(rs.getString("student_name"));
                mark.setRegistrationNo(rs.getString("registration_no"));
                mark.setCourseId(rs.getInt("course_id"));
                mark.setExamType(rs.getString("exam_type"));
                mark.setMarksObtained(rs.getDouble("marks_obtained"));
                mark.setMaxMarks(rs.getDouble("max_marks"));
                mark.setGrade(rs.getString("grade"));
                mark.setGradePoints(rs.getDouble("grade_points"));
                return mark;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all marks for a student
    public List<Mark> getStudentAllMarks(int studentId) {
        List<Mark> marks = new ArrayList<>();
        String query = "SELECT m.*, c.course_code, c.course_name, c.credits " +
                "FROM marks m " +
                "LEFT JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.student_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Mark mark = new Mark();
                mark.setMarkId(rs.getInt("mark_id"));
                mark.setStudentId(rs.getInt("student_id"));
                mark.setCourseId(rs.getInt("course_id"));
                mark.setCourseCode(rs.getString("course_code"));
                mark.setCourseName(rs.getString("course_name"));
                mark.setExamType(rs.getString("exam_type"));
                mark.setMarksObtained(rs.getDouble("marks_obtained"));
                mark.setMaxMarks(rs.getDouble("max_marks"));
                mark.setGrade(rs.getString("grade"));
                mark.setGradePoints(rs.getDouble("grade_points"));
                marks.add(mark);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marks;
    }

    // Calculate CA eligibility (CA marks >= 40%)
    public boolean isCAEligibleForExam(int studentId, int courseId) {
        Mark caMark = getStudentCourseMark(studentId, courseId, "CA");
        if (caMark != null) {
            return caMark.getMarksObtained() >= 40;
        }
        return false;
    }

    // Get batch marks summary for a course
    public List<Object[]> getBatchMarksSummary(int courseId, String examType) {
        List<Object[]> summaries = new ArrayList<>();
        String query = "SELECT s.student_id, u.full_name as student_name, s.registration_no, " +
                "m.marks_obtained, m.grade " +
                "FROM students s " +
                "INNER JOIN users u ON s.user_id = u.user_id " +
                "LEFT JOIN marks m ON s.student_id = m.student_id " +
                "AND m.course_id = ? AND m.exam_type = ? " +
                "ORDER BY s.student_id";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            pstmt.setString(2, examType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] summary = new Object[5];
                summary[0] = rs.getInt("student_id");
                summary[1] = rs.getString("student_name");
                summary[2] = rs.getString("registration_no");
                summary[3] = rs.getObject("marks_obtained") != null ? rs.getDouble("marks_obtained") : null;
                summary[4] = rs.getString("grade");
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    // Calculate SGPA for a student in a semester
    public double calculateSGPA(int studentId, int semester) {
        String query = "SELECT SUM(c.credits * m.grade_points) as total_points, SUM(c.credits) as total_credits " +
                "FROM marks m " +
                "INNER JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.student_id = ? AND c.semester = ? AND m.exam_type = 'final'";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, semester);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double totalPoints = rs.getDouble("total_points");
                double totalCredits = rs.getDouble("total_credits");
                if (totalCredits > 0) {
                    return totalPoints / totalCredits;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
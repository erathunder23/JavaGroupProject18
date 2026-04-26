package dao;

import model.Mark;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarkDAO {

    public List<Mark> getStudentAllMarks(int studentId) {
        List<Mark> marks = new ArrayList<>();
        String query = "SELECT m.*, c.course_code FROM marks m " +
                "JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.student_id = ? ORDER BY c.course_code, m.exam_type";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Mark mark = new Mark();
                mark.setMarkId(rs.getInt("mark_id"));
                mark.setStudentId(rs.getInt("student_id"));
                mark.setCourseId(rs.getInt("course_id"));
                mark.setCourseCode(rs.getString("course_code"));
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

    public double calculateSGPA(int studentId, int semester) {
        String query = "SELECT SUM(m.grade_points * c.credits) / SUM(c.credits) AS sgpa " +
                "FROM marks m JOIN courses c ON m.course_id = c.course_id " +
                "WHERE m.student_id = ? AND c.semester = ? AND m.exam_type = 'final'";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, semester);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double sgpa = rs.getDouble("sgpa");
                return rs.wasNull() ? 0.0 : sgpa;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}

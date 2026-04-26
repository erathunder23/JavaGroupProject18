package dao;

import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getCoursesBySemester(int semester) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.*, CONCAT(u.full_name) AS lecturer_name " +
                "FROM courses c LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id " +
                "WHERE c.semester = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, semester);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setTheoryCredits(rs.getInt("theory_credits"));
                course.setPracticalCredits(rs.getInt("practical_credits"));
                course.setSemester(rs.getInt("semester"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDescription(rs.getString("description"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseById(int courseId) {
        String query = "SELECT c.*, u.full_name AS lecturer_name FROM courses c " +
                "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id " +
                "WHERE c.course_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setTheoryCredits(rs.getInt("theory_credits"));
                course.setPracticalCredits(rs.getInt("practical_credits"));
                course.setSemester(rs.getInt("semester"));
                course.setLecturerName(rs.getString("lecturer_name"));
                return course;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

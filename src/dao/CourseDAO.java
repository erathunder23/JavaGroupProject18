package dao;

import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // Get all courses
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as lecturer_name FROM courses c " +
                "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                course.setCredits(rs.getInt("credits"));
                course.setTheoryCredits(rs.getInt("theory_credits"));
                course.setPracticalCredits(rs.getInt("practical_credits"));
                course.setSemester(rs.getInt("semester"));
                course.setLecturerId(rs.getInt("lecturer_id"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDescription(rs.getString("description"));
                course.setMaterials(rs.getString("materials"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Get courses by semester
    public List<Course> getCoursesBySemester(int semester) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as lecturer_name FROM courses c " +
                "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
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
                course.setLecturerId(rs.getInt("lecturer_id"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDescription(rs.getString("description"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Get course by ID
    public Course getCourseById(int courseId) {
        String query = "SELECT c.*, u.full_name as lecturer_name FROM courses c " +
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
                course.setLecturerId(rs.getInt("lecturer_id"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDescription(rs.getString("description"));
                course.setMaterials(rs.getString("materials"));
                return course;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Create course
    public boolean createCourse(Course course) {
        String query = "INSERT INTO courses (course_code, course_name, credits, theory_credits, " +
                "practical_credits, semester, lecturer_id, description, materials) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setInt(4, course.getTheoryCredits());
            pstmt.setInt(5, course.getPracticalCredits());
            pstmt.setInt(6, course.getSemester());
            pstmt.setObject(7, course.getLecturerId() == 0 ? null : course.getLecturerId());
            pstmt.setString(8, course.getDescription());
            pstmt.setString(9, course.getMaterials());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update course
    public boolean updateCourse(Course course) {
        String query = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, " +
                "theory_credits = ?, practical_credits = ?, semester = ?, lecturer_id = ?, " +
                "description = ?, materials = ? WHERE course_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            pstmt.setInt(4, course.getTheoryCredits());
            pstmt.setInt(5, course.getPracticalCredits());
            pstmt.setInt(6, course.getSemester());
            pstmt.setObject(7, course.getLecturerId() == 0 ? null : course.getLecturerId());
            pstmt.setString(8, course.getDescription());
            pstmt.setString(9, course.getMaterials());
            pstmt.setInt(10, course.getCourseId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete course
    public boolean deleteCourse(int courseId) {
        String query = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add materials to course
    public boolean addMaterials(int courseId, String materials) {
        String query = "UPDATE courses SET materials = CONCAT(IFNULL(materials, ''), ?) WHERE course_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, materials);
            pstmt.setInt(2, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get courses for a student
    public List<Course> getStudentCourses(int studentId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as lecturer_name FROM courses c " +
                "INNER JOIN student_courses sc ON c.course_id = sc.course_id " +
                "LEFT JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                "LEFT JOIN users u ON l.user_id = u.user_id " +
                "WHERE sc.student_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
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
                course.setLecturerId(rs.getInt("lecturer_id"));
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDescription(rs.getString("description"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Enroll student in course
    public boolean enrollStudent(int studentId, int courseId) {
        String query = "INSERT INTO student_courses (student_id, course_id, enrollment_date) VALUES (?, ?, CURDATE())";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
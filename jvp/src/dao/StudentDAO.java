package dao;

import model.Student;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - Handles all database operations related to Students.
 * Extracted from UserDAO to keep the student module self-contained.
 */
public class StudentDAO {

    // ─── Authentication ────────────────────────────────────────────────────────

    public User authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setProfilePicture(rs.getString("profile_picture"));
                user.setContactDetails(rs.getString("contact_details"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT u.*, s.student_id, s.registration_no, s.batch_year, s.current_semester, " +
                "s.is_repeat, s.is_batch_missed FROM users u " +
                "INNER JOIN students s ON u.user_id = s.user_id " +
                "WHERE u.role = 'student'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentByUserId(int userId) {
        String query = "SELECT u.*, s.student_id, s.registration_no, s.batch_year, s.current_semester, " +
                "s.is_repeat, s.is_batch_missed FROM users u " +
                "INNER JOIN students s ON u.user_id = s.user_id " +
                "WHERE u.user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    public boolean createStudent(Student student) {
        String userQuery = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) " +
                "VALUES (?, ?, ?, ?, ?, 'student', ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection()
                .prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getUsername());
            pstmt.setString(2, student.getPassword());
            pstmt.setString(3, student.getFullName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getContactDetails());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) student.setUserId(keys.getInt(1));
                return insertStudentRecord(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean insertStudentRecord(Student student) {
        String query = "INSERT INTO students (user_id, registration_no, batch_year, current_semester, is_repeat, is_batch_missed) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getRegistrationNo());
            pstmt.setInt(3, student.getBatchYear());
            pstmt.setInt(4, student.getCurrentSemester());
            pstmt.setBoolean(5, student.isRepeat());
            pstmt.setBoolean(6, student.isBatchMissed());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    public boolean updateStudent(Student student) {
        String query = "UPDATE users SET full_name = ?, email = ?, phone = ?, contact_details = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setString(4, student.getContactDetails());
            pstmt.setInt(5, student.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfilePicture(int userId, String picturePath) {
        String query = "UPDATE users SET profile_picture = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, picturePath);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteStudent(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── Helper ────────────────────────────────────────────────────────────────

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setUserId(rs.getInt("user_id"));
        student.setUsername(rs.getString("username"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setRole(rs.getString("role"));
        student.setProfilePicture(rs.getString("profile_picture"));
        student.setContactDetails(rs.getString("contact_details"));
        student.setStudentId(rs.getInt("student_id"));
        student.setRegistrationNo(rs.getString("registration_no"));
        student.setBatchYear(rs.getInt("batch_year"));
        student.setCurrentSemester(rs.getInt("current_semester"));
        student.setRepeat(rs.getBoolean("is_repeat"));
        student.setBatchMissed(rs.getBoolean("is_batch_missed"));
        return student;
    }
}

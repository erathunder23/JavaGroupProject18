package dao;

import model.User;
import model.Student;
import model.Lecturer;
import model.TechnicalOfficer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ==================== AUTHENTICATION ====================
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
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==================== CREATE USER (base) ====================
    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getContactDetails());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) user.setUserId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ==================== STUDENT CRUD ====================
    public boolean createStudent(Student student) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into users
            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int userId;
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, student.getUsername());
                pstmt.setString(2, student.getPassword());
                pstmt.setString(3, student.getFullName());
                pstmt.setString(4, student.getEmail());
                pstmt.setString(5, student.getPhone());
                pstmt.setString(6, "student");
                pstmt.setString(7, student.getContactDetails());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Failed to get user ID");
                userId = rs.getInt(1);
                student.setUserId(userId);
            }

            // Insert into students
            String studentSql = "INSERT INTO students (user_id, registration_no, batch_year, current_semester, is_repeat, is_batch_missed) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, student.getRegistrationNo());
                pstmt.setInt(3, student.getBatchYear());
                pstmt.setInt(4, student.getCurrentSemester());
                pstmt.setBoolean(5, student.isRepeat());
                pstmt.setBoolean(6, student.isBatchMissed());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean updateStudent(Student student) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                pstmt.setString(1, student.getFullName());
                pstmt.setString(2, student.getEmail());
                pstmt.setString(3, student.getPhone());
                pstmt.setString(4, student.getContactDetails());
                pstmt.setInt(5, student.getUserId());
                pstmt.executeUpdate();
            }

            String studentSql = "UPDATE students SET registration_no=?, batch_year=?, current_semester=?, is_repeat=?, is_batch_missed=? WHERE student_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setString(1, student.getRegistrationNo());
                pstmt.setInt(2, student.getBatchYear());
                pstmt.setInt(3, student.getCurrentSemester());
                pstmt.setBoolean(4, student.isRepeat());
                pstmt.setBoolean(5, student.isBatchMissed());
                pstmt.setInt(6, student.getStudentId());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean deleteStudent(int studentId) {
        String getUserId = "SELECT user_id FROM students WHERE student_id = ?";
        Integer userId = null;
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String delStudent = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(delStudent)) {
            pstmt.setInt(1, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        return deleteUser(userId);
    }

    // ==================== LECTURER CRUD ====================
    public boolean createLecturer(Lecturer lecturer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int userId;
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, lecturer.getUsername());
                pstmt.setString(2, lecturer.getPassword());
                pstmt.setString(3, lecturer.getFullName());
                pstmt.setString(4, lecturer.getEmail());
                pstmt.setString(5, lecturer.getPhone());
                pstmt.setString(6, "lecturer");
                pstmt.setString(7, lecturer.getContactDetails());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Failed to get user ID");
                userId = rs.getInt(1);
                lecturer.setUserId(userId);
            }

            String lecturerSql = "INSERT INTO lecturers (user_id, employee_no, department) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(lecturerSql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, lecturer.getEmployeeNo());
                pstmt.setString(3, lecturer.getDepartment());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean updateLecturer(Lecturer lecturer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                pstmt.setString(1, lecturer.getFullName());
                pstmt.setString(2, lecturer.getEmail());
                pstmt.setString(3, lecturer.getPhone());
                pstmt.setString(4, lecturer.getContactDetails());
                pstmt.setInt(5, lecturer.getUserId());
                pstmt.executeUpdate();
            }

            String lecturerSql = "UPDATE lecturers SET employee_no=?, department=? WHERE lecturer_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(lecturerSql)) {
                pstmt.setString(1, lecturer.getEmployeeNo());
                pstmt.setString(2, lecturer.getDepartment());
                pstmt.setInt(3, lecturer.getLecturerId());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean deleteLecturer(int lecturerId) {
        String getUserId = "SELECT user_id FROM lecturers WHERE lecturer_id = ?";
        Integer userId = null;
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            pstmt.setInt(1, lecturerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String delLecturer = "DELETE FROM lecturers WHERE lecturer_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(delLecturer)) {
            pstmt.setInt(1, lecturerId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        return deleteUser(userId);
    }

    // ==================== TECHNICAL OFFICER CRUD ====================
    public boolean createTechnicalOfficer(TechnicalOfficer officer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int userId;
            try (PreparedStatement pstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, officer.getUsername());
                pstmt.setString(2, officer.getPassword());
                pstmt.setString(3, officer.getFullName());
                pstmt.setString(4, officer.getEmail());
                pstmt.setString(5, officer.getPhone());
                pstmt.setString(6, "technical_officer");
                pstmt.setString(7, officer.getContactDetails());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Failed to get user ID");
                userId = rs.getInt(1);
                officer.setUserId(userId);
            }

            String officerSql = "INSERT INTO technical_officers (user_id, employee_no, department) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(officerSql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, officer.getEmployeeNo());
                pstmt.setString(3, officer.getDepartment());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean updateTechnicalOfficer(TechnicalOfficer officer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                pstmt.setString(1, officer.getFullName());
                pstmt.setString(2, officer.getEmail());
                pstmt.setString(3, officer.getPhone());
                pstmt.setString(4, officer.getContactDetails());
                pstmt.setInt(5, officer.getUserId());
                pstmt.executeUpdate();
            }

            String officerSql = "UPDATE technical_officers SET employee_no=?, department=? WHERE officer_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(officerSql)) {
                pstmt.setString(1, officer.getEmployeeNo());
                pstmt.setString(2, officer.getDepartment());
                pstmt.setInt(3, officer.getOfficerId());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean deleteTechnicalOfficer(int officerId) {
        String getUserId = "SELECT user_id FROM technical_officers WHERE officer_id = ?";
        Integer userId = null;
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            pstmt.setInt(1, officerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String delOfficer = "DELETE FROM technical_officers WHERE officer_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(delOfficer)) {
            pstmt.setInt(1, officerId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        return deleteUser(userId);
    }

    // ==================== CORE DELETE USER ====================
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== UPDATE USER (generic) ====================
    public boolean updateUser(User user) {
        String query = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getContactDetails());
            pstmt.setInt(5, user.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== UPDATE STUDENT CONTACT (for profile picture) ====================
    public boolean updateStudentContact(Student student) {
        String query = "UPDATE users SET contact_details=?, profile_picture=? WHERE user_id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, student.getContactDetails());
            pstmt.setString(2, student.getProfilePicture());
            pstmt.setInt(3, student.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== UPDATE PROFILE PICTURE ====================
    public boolean updateProfilePicture(int userId, String picturePath) {
        String query = "UPDATE users SET profile_picture = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, picturePath);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== GET ALL STUDENTS ====================
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT u.*, s.student_id, s.registration_no, s.batch_year, s.current_semester, " +
                "s.is_repeat, s.is_batch_missed FROM users u " +
                "INNER JOIN students s ON u.user_id = s.user_id WHERE u.role = 'student'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Student s = new Student();
                s.setUserId(rs.getInt("user_id"));
                s.setUsername(rs.getString("username"));
                s.setFullName(rs.getString("full_name"));
                s.setEmail(rs.getString("email"));
                s.setPhone(rs.getString("phone"));
                s.setRole(rs.getString("role"));
                s.setContactDetails(rs.getString("contact_details"));
                s.setStudentId(rs.getInt("student_id"));
                s.setRegistrationNo(rs.getString("registration_no"));
                s.setBatchYear(rs.getInt("batch_year"));
                s.setCurrentSemester(rs.getInt("current_semester"));
                s.setRepeat(rs.getBoolean("is_repeat"));
                s.setBatchMissed(rs.getBoolean("is_batch_missed"));
                students.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    // ==================== GET ALL LECTURERS ====================
    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String query = "SELECT u.*, l.lecturer_id, l.employee_no, l.department FROM users u " +
                "INNER JOIN lecturers l ON u.user_id = l.user_id WHERE u.role = 'lecturer'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Lecturer l = new Lecturer();
                l.setUserId(rs.getInt("user_id"));
                l.setUsername(rs.getString("username"));
                l.setFullName(rs.getString("full_name"));
                l.setEmail(rs.getString("email"));
                l.setPhone(rs.getString("phone"));
                l.setRole(rs.getString("role"));
                l.setContactDetails(rs.getString("contact_details"));
                l.setLecturerId(rs.getInt("lecturer_id"));
                l.setEmployeeNo(rs.getString("employee_no"));
                l.setDepartment(rs.getString("department"));
                lecturers.add(l);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lecturers;
    }

    // ==================== GET ALL TECHNICAL OFFICERS ====================
    public List<TechnicalOfficer> getAllTechnicalOfficers() {
        List<TechnicalOfficer> officers = new ArrayList<>();
        String query = "SELECT u.*, t.officer_id, t.employee_no, t.department FROM users u " +
                "INNER JOIN technical_officers t ON u.user_id = t.user_id WHERE u.role = 'technical_officer'";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                TechnicalOfficer o = new TechnicalOfficer();
                o.setUserId(rs.getInt("user_id"));
                o.setUsername(rs.getString("username"));
                o.setFullName(rs.getString("full_name"));
                o.setEmail(rs.getString("email"));
                o.setPhone(rs.getString("phone"));
                o.setRole(rs.getString("role"));
                o.setContactDetails(rs.getString("contact_details"));
                o.setOfficerId(rs.getInt("officer_id"));
                o.setEmployeeNo(rs.getString("employee_no"));
                o.setDepartment(rs.getString("department"));
                officers.add(o);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return officers;
    }
}

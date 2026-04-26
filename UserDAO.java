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
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
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

    // ==================== GET USER BY ID (for profile) ====================
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
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

    // ==================== UPDATE USER (profile info) ====================
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getContactDetails());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== UPDATE PROFILE PICTURE ====================
    public boolean updateProfilePicture(int userId, String picturePath) {
        String sql = "UPDATE users SET profile_picture = ? WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, picturePath);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== CREATE STUDENT ====================
    public boolean createStudent(Student student) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            // Insert into users
            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?,?,?,?,?,?,?)";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, student.getUsername());
                ps.setString(2, student.getPassword());
                ps.setString(3, student.getFullName());
                ps.setString(4, student.getEmail());
                ps.setString(5, student.getPhone());
                ps.setString(6, "student");
                ps.setString(7, student.getContactDetails());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("User insert failed");
                userId = rs.getInt(1);
                student.setUserId(userId);
            }
            // Insert into students
            String studentSql = "INSERT INTO students (user_id, registration_no, batch_year, current_semester, is_repeat, is_batch_missed) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(studentSql)) {
                ps.setInt(1, userId);
                ps.setString(2, student.getRegistrationNo());
                ps.setInt(3, student.getBatchYear());
                ps.setInt(4, student.getCurrentSemester());
                ps.setBoolean(5, student.isRepeat());
                ps.setBoolean(6, student.isBatchMissed());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== UPDATE STUDENT ====================
    public boolean updateStudent(Student student) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, student.getFullName());
                ps.setString(2, student.getEmail());
                ps.setString(3, student.getPhone());
                ps.setString(4, student.getContactDetails());
                ps.setInt(5, student.getUserId());
                ps.executeUpdate();
            }
            String studentSql = "UPDATE students SET registration_no=?, batch_year=?, current_semester=?, is_repeat=?, is_batch_missed=? WHERE student_id=?";
            try (PreparedStatement ps = conn.prepareStatement(studentSql)) {
                ps.setString(1, student.getRegistrationNo());
                ps.setInt(2, student.getBatchYear());
                ps.setInt(3, student.getCurrentSemester());
                ps.setBoolean(4, student.isRepeat());
                ps.setBoolean(5, student.isBatchMissed());
                ps.setInt(6, student.getStudentId());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== DELETE STUDENT ====================
    public boolean deleteStudent(int studentId) {
        Integer userId = null;
        String getUserId = "SELECT user_id FROM students WHERE student_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        // Delete from students (optional but safe)
        String delStudent = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(delStudent)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return deleteUser(userId);
    }

    // ==================== CREATE LECTURER ====================
    public boolean createLecturer(Lecturer lecturer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?,?,?,?,?,?,?)";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, lecturer.getUsername());
                ps.setString(2, lecturer.getPassword());
                ps.setString(3, lecturer.getFullName());
                ps.setString(4, lecturer.getEmail());
                ps.setString(5, lecturer.getPhone());
                ps.setString(6, "lecturer");
                ps.setString(7, lecturer.getContactDetails());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException();
                userId = rs.getInt(1);
                lecturer.setUserId(userId);
            }
            String lecturerSql = "INSERT INTO lecturers (user_id, employee_no, department) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(lecturerSql)) {
                ps.setInt(1, userId);
                ps.setString(2, lecturer.getEmployeeNo());
                ps.setString(3, lecturer.getDepartment());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== UPDATE LECTURER ====================
    public boolean updateLecturer(Lecturer lecturer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, lecturer.getFullName());
                ps.setString(2, lecturer.getEmail());
                ps.setString(3, lecturer.getPhone());
                ps.setString(4, lecturer.getContactDetails());
                ps.setInt(5, lecturer.getUserId());
                ps.executeUpdate();
            }
            String lecturerSql = "UPDATE lecturers SET employee_no=?, department=? WHERE lecturer_id=?";
            try (PreparedStatement ps = conn.prepareStatement(lecturerSql)) {
                ps.setString(1, lecturer.getEmployeeNo());
                ps.setString(2, lecturer.getDepartment());
                ps.setInt(3, lecturer.getLecturerId());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== DELETE LECTURER ====================
    public boolean deleteLecturer(int lecturerId) {
        Integer userId = null;
        String getUserId = "SELECT user_id FROM lecturers WHERE lecturer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        String delLecturer = "DELETE FROM lecturers WHERE lecturer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(delLecturer)) {
            ps.setInt(1, lecturerId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return deleteUser(userId);
    }

    // ==================== CREATE TECHNICAL OFFICER ====================
    public boolean createTechnicalOfficer(TechnicalOfficer officer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String userSql = "INSERT INTO users (username, password, full_name, email, phone, role, contact_details) VALUES (?,?,?,?,?,?,?)";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, officer.getUsername());
                ps.setString(2, officer.getPassword());
                ps.setString(3, officer.getFullName());
                ps.setString(4, officer.getEmail());
                ps.setString(5, officer.getPhone());
                ps.setString(6, "technical_officer");
                ps.setString(7, officer.getContactDetails());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException();
                userId = rs.getInt(1);
                officer.setUserId(userId);
            }
            String officerSql = "INSERT INTO technical_officers (user_id, employee_no, department) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(officerSql)) {
                ps.setInt(1, userId);
                ps.setString(2, officer.getEmployeeNo());
                ps.setString(3, officer.getDepartment());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== UPDATE TECHNICAL OFFICER ====================
    public boolean updateTechnicalOfficer(TechnicalOfficer officer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String userSql = "UPDATE users SET full_name=?, email=?, phone=?, contact_details=? WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, officer.getFullName());
                ps.setString(2, officer.getEmail());
                ps.setString(3, officer.getPhone());
                ps.setString(4, officer.getContactDetails());
                ps.setInt(5, officer.getUserId());
                ps.executeUpdate();
            }
            String officerSql = "UPDATE technical_officers SET employee_no=?, department=? WHERE officer_id=?";
            try (PreparedStatement ps = conn.prepareStatement(officerSql)) {
                ps.setString(1, officer.getEmployeeNo());
                ps.setString(2, officer.getDepartment());
                ps.setInt(3, officer.getOfficerId());
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // ==================== DELETE TECHNICAL OFFICER ====================
    public boolean deleteTechnicalOfficer(int officerId) {
        Integer userId = null;
        String getUserId = "SELECT user_id FROM technical_officers WHERE officer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(getUserId)) {
            ps.setInt(1, officerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) userId = rs.getInt("user_id");
            else return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        String delOfficer = "DELETE FROM technical_officers WHERE officer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(delOfficer)) {
            ps.setInt(1, officerId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return deleteUser(userId);
    }

    // ==================== CORE DELETE USER ====================
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== GET ALL STUDENTS ====================
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT u.*, s.student_id, s.registration_no, s.batch_year, s.current_semester, s.is_repeat, s.is_batch_missed " +
                "FROM users u INNER JOIN students s ON u.user_id = s.user_id WHERE u.role = 'student'";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ==================== GET ALL LECTURERS ====================
    public List<Lecturer> getAllLecturers() {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT u.*, l.lecturer_id, l.employee_no, l.department FROM users u " +
                "INNER JOIN lecturers l ON u.user_id = l.user_id WHERE u.role = 'lecturer'";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
                list.add(l);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ==================== GET ALL TECHNICAL OFFICERS ====================
    public List<TechnicalOfficer> getAllTechnicalOfficers() {
        List<TechnicalOfficer> list = new ArrayList<>();
        String sql = "SELECT u.*, t.officer_id, t.employee_no, t.department FROM users u " +
                "INNER JOIN technical_officers t ON u.user_id = t.user_id WHERE u.role = 'technical_officer'";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
                list.add(o);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}

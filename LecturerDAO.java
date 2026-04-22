// dao/LecturerDAO.java
package dao;

import model.Lecturer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    private Connection conn;

    public LecturerDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    // CREATE
    public boolean addLecturer(Lecturer lec) {
        String sql = "INSERT INTO Lecturer (LecturerID, First_name, Last_name, " +
                "Email, Gender, DOB, Telephone, DeptID, Password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lec.getId());
            ps.setString(2, lec.getFirstName());
            ps.setString(3, lec.getLastName());
            ps.setString(4, lec.getEmail());
            ps.setString(5, lec.getGender());
            ps.setString(6, lec.getDob());
            ps.setString(7, lec.getTelephone());
            ps.setString(8, lec.getDeptID());
            ps.setString(9, lec.getPassword());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding lecturer: " + e.getMessage());
            return false;
        }
    }

    // READ ALL
    public List<Lecturer> getAllLecturers() {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM Lecturer";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToLecturer(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching lecturers: " + e.getMessage());
        }
        return list;
    }

    // READ BY ID
    public Lecturer getLecturerByID(String id) {
        String sql = "SELECT * FROM Lecturer WHERE LecturerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToLecturer(rs);
        } catch (SQLException e) {
            System.err.println("Error fetching lecturer: " + e.getMessage());
        }
        return null;
    }

    // LOGIN - Authenticate lecturer
    public Lecturer authenticateLecturer(String email, String password) {
        String sql = "SELECT * FROM Lecturer WHERE Email = ? AND Password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToLecturer(rs);
        } catch (SQLException e) {
            System.err.println("Auth error: " + e.getMessage());
        }
        return null;
    }

    // UPDATE Profile (not password)
    public boolean updateLecturerProfile(Lecturer lec) {
        String sql = "UPDATE Lecturer SET First_name=?, Last_name=?, " +
                "Email=?, Gender=?, DOB=?, Telephone=? WHERE LecturerID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lec.getFirstName());
            ps.setString(2, lec.getLastName());
            ps.setString(3, lec.getEmail());
            ps.setString(4, lec.getGender());
            ps.setString(5, lec.getDob());
            ps.setString(6, lec.getTelephone());
            ps.setString(7, lec.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating lecturer: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteLecturer(String id) {
        String sql = "DELETE FROM Lecturer WHERE LecturerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting lecturer: " + e.getMessage());
            return false;
        }
    }

    // Get courses assigned to a lecturer
    public List<String> getCoursesByLecturer(String lecturerID) {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT CourseCode FROM Lecturer_Course WHERE LecturerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lecturerID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("CourseCode"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return courses;
    }

    // Helper: Map ResultSet to Lecturer object
    private Lecturer mapResultSetToLecturer(ResultSet rs) throws SQLException {
        return new Lecturer(
                rs.getString("LecturerID"),
                rs.getString("First_name"),
                rs.getString("Last_name"),
                rs.getString("Email"),
                rs.getString("Gender"),
                rs.getString("DOB"),
                rs.getString("Telephone"),
                rs.getString("DeptID"),
                rs.getString("Password")
        );
    }
    // Add to LecturerDAO.java

    // View students in a course the lecturer teaches
    public List<String[]> getStudentsInCourse(String courseCode) {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT s.StuID, s.First_name, s.Last_name " +
                "FROM Student s JOIN Student_Course sc ON s.StuID = sc.StuID " +
                "WHERE sc.CourseCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(new String[]{
                        rs.getString("StuID"),
                        rs.getString("First_name"),
                        rs.getString("Last_name")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return students;
    }

}


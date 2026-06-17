// dao/CourseUnitDAO.java
package dao;

import model.CourseUnit;
import java.sql.*;
import java.util.*;

public class CourseUnitDAO {

    private Connection conn;

    public CourseUnitDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public boolean addCourseUnit(CourseUnit cu) {
        String sql = "INSERT INTO Course_Unit (CourseCode, Title, Credit, " +
                "Theory_hrs, Practical_hrs, ModuleID, LecturerID) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cu.getCourseCode());
            ps.setString(2, cu.getTitle());
            ps.setInt(3, cu.getCredit());
            ps.setInt(4, cu.getTheoryHrs());
            ps.setInt(5, cu.getPracticalHrs());
            ps.setString(6, cu.getModuleID());
            ps.setString(7, cu.getLecturerID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public List<CourseUnit> getAllCourseUnits() {
        List<CourseUnit> list = new ArrayList<>();
        String sql = "SELECT * FROM Course_Unit";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;
    }

    // Get course units assigned to a specific lecturer
    public List<CourseUnit> getCoursesByLecturerID(String lecturerID) {
        List<CourseUnit> list = new ArrayList<>();
        String sql = "SELECT cu.* FROM Course_Unit cu " +
                "JOIN Lecturer_Course lc ON cu.CourseCode = lc.CourseCode " +
                "WHERE lc.LecturerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lecturerID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateCourseUnit(CourseUnit cu) {
        String sql = "UPDATE Course_Unit SET Title=?, Credit=?, Theory_hrs=?, " +
                "Practical_hrs=?, ModuleID=?, LecturerID=? WHERE CourseCode=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cu.getTitle());
            ps.setInt(2, cu.getCredit());
            ps.setInt(3, cu.getTheoryHrs());
            ps.setInt(4, cu.getPracticalHrs());
            ps.setString(5, cu.getModuleID());
            ps.setString(6, cu.getLecturerID());
            ps.setString(7, cu.getCourseCode());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourseUnit(String courseCode) {
        String sql = "DELETE FROM Course_Unit WHERE CourseCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    private CourseUnit mapRow(ResultSet rs) throws SQLException {
        return new CourseUnit(
                rs.getString("CourseCode"),
                rs.getString("Title"),
                rs.getInt("Credit"),
                rs.getInt("Theory_hrs"),
                rs.getInt("Practical_hrs"),
                rs.getString("ModuleID"),
                rs.getString("LecturerID")
        );
    }
}


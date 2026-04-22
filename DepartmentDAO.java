// dao/DepartmentDAO.java
package dao;

import model.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    private Connection conn;

    public DepartmentDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    // CREATE - Add a department
    public boolean addDepartment(Department dept) {
        String sql = "INSERT INTO Department (DeptID, DeptName) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getDeptID());
            ps.setString(2, dept.getDeptName());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding department: " + e.getMessage());
            return false;
        }
    }

    // READ - Get all departments
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Department dept = new Department(
                        rs.getString("DeptID"),
                        rs.getString("DeptName")
                );
                list.add(dept);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching departments: " + e.getMessage());
        }
        return list;
    }

    // READ - Get department by ID
    public Department getDepartmentByID(String deptID) {
        String sql = "SELECT * FROM Department WHERE DeptID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deptID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Department(rs.getString("DeptID"), rs.getString("DeptName"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching department: " + e.getMessage());
        }
        return null;
    }

    // UPDATE
    public boolean updateDepartment(Department dept) {
        String sql = "UPDATE Department SET DeptName = ? WHERE DeptID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getDeptName());
            ps.setString(2, dept.getDeptID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating department: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteDepartment(String deptID) {
        String sql = "DELETE FROM Department WHERE DeptID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deptID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
            return false;
        }
    }
}

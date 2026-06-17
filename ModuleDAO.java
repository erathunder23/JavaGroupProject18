// dao/ModuleDAO.java
package dao;

import model.Module;
import java.sql.*;
import java.util.*;

public class ModuleDAO {

    private Connection conn;

    public ModuleDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public boolean addModule(Module m) {
        String sql = "INSERT INTO Module (ModuleID, ModuleName, Credit, Type, Hours) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getModuleID());
            ps.setString(2, m.getModuleName());
            ps.setInt(3, m.getCredit());
            ps.setString(4, m.getType());
            ps.setInt(5, m.getHours());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public List<Module> getAllModules() {
        List<Module> list = new ArrayList<>();
        String sql = "SELECT * FROM Module";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Module(
                        rs.getString("ModuleID"),
                        rs.getString("ModuleName"),
                        rs.getInt("Credit"),
                        rs.getString("Type"),
                        rs.getInt("Hours")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateModule(Module m) {
        String sql = "UPDATE Module SET ModuleName=?, Credit=?, Type=?, Hours=? WHERE ModuleID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getModuleName());
            ps.setInt(2, m.getCredit());
            ps.setString(3, m.getType());
            ps.setInt(4, m.getHours());
            ps.setString(5, m.getModuleID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteModule(String moduleID) {
        String sql = "DELETE FROM Module WHERE ModuleID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moduleID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
}

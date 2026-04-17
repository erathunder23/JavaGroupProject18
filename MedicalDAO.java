import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalDAO {

    public static boolean insert(Medical m) throws SQLException {
        String sql = "INSERT INTO Medical VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, m.getMedicalID());
        ps.setString(2, m.getStuID());
        ps.setString(3, m.getCourseCode());
        ps.setDate(4, m.getSubmissionDate());
        ps.setString(5, m.getDescription());
        ps.setString(6, m.getStatus());

        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public static boolean update(Medical m) throws SQLException {
        String sql = "UPDATE Medical SET Description=?, Status=? WHERE MedicalID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, m.getDescription());
        ps.setString(2, m.getStatus());
        ps.setString(3, m.getMedicalID());

        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public static boolean delete(String medicalID) throws SQLException {
        String sql = "DELETE FROM Medical WHERE MedicalID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, medicalID);

        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public static Medical getById(String id) throws SQLException {
        String sql = "SELECT * FROM Medical WHERE MedicalID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Medical m = new Medical(
                    rs.getString("MedicalID"),
                    rs.getString("StuID"),
                    rs.getString("CourseCode"),
                    rs.getDate("SubmissionDate"),
                    rs.getString("Description"),
                    rs.getString("Status")
            );
            con.close();
            return m;
        }

        con.close();
        return null;
    }

    public static List<Medical> getAll() throws SQLException {
        List<Medical> list = new ArrayList<>();
        String sql = "SELECT * FROM Medical";
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(new Medical(
                    rs.getString("MedicalID"),
                    rs.getString("StuID"),
                    rs.getString("CourseCode"),
                    rs.getDate("SubmissionDate"),
                    rs.getString("Description"),
                    rs.getString("Status")
            ));
        }

        con.close();
        return list;
    }
}
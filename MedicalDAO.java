import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class MedicalDAO implements Service {

    private Medical m;

    public MedicalDAO(Medical m) {
        this.m = m;
    }

    @Override
    public void add() throws Exception {
        String sql = "INSERT INTO Medical VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, m.getMedicalID());
        ps.setString(2, m.getStuID());
        ps.setString(3, m.getCourseCode());
        ps.setString(4, m.getDate());
        ps.setString(5, m.getDescription());
        ps.setString(6, m.getStatus());
        ps.executeUpdate();
    }

    @Override
    public void update() throws Exception {
        String sql = "UPDATE Medical SET Status=? WHERE MedicalID=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, m.getStatus());
        ps.setString(2, m.getMedicalID());
        ps.executeUpdate();
    }

    @Override
    public void delete() throws Exception {
        String sql = "DELETE FROM Medical WHERE MedicalID=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, m.getMedicalID());
        ps.executeUpdate();
    }

    public static DefaultTableModel view() throws Exception {
        String sql = "SELECT * FROM Medical";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MedicalID");
        model.addColumn("StuID");
        model.addColumn("Course");
        model.addColumn("Date");
        model.addColumn("Description");
        model.addColumn("Status");

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6)
            });
        }
        return model;
    }
}
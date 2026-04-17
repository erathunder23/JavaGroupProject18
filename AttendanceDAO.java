import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AttendanceDAO implements Service {

    private Attendance a;

    public AttendanceDAO(Attendance a) {
        this.a = a;
    }

    @Override
    public void add() throws Exception {
        String sql = "INSERT INTO Attendance(StuID,CourseCode,Date,Type,Status) VALUES(?,?,?,?,?)";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, a.getStuID());
        ps.setString(2, a.getCourseCode());
        ps.setString(3, a.getDate());
        ps.setString(4, a.getType());
        ps.setString(5, a.getStatus());
        ps.executeUpdate();
    }

    @Override
    public void update() throws Exception {
        String sql = "UPDATE Attendance SET Status=? WHERE StuID=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, a.getStatus());
        ps.setString(2, a.getStuID());
        ps.executeUpdate();
    }

    @Override
    public void delete() throws Exception {
        String sql = "DELETE FROM Attendance WHERE StuID=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, a.getStuID());
        ps.executeUpdate();
    }

    // VIEW METHOD (IMPORTANT FOR DEMO)
    public static DefaultTableModel view() throws Exception {
        String sql = "SELECT * FROM Attendance";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("StuID");
        model.addColumn("Course");
        model.addColumn("Date");
        model.addColumn("Type");
        model.addColumn("Status");

        while (rs.next()) {
            model.addRow(new Object[]{
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
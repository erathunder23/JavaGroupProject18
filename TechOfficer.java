import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechOfficer {

    private String techID;
    private String name;
    private String email;
    private String gender;
    private Date dob;
    private String telephone;
    private String password;

    public TechOfficer(String techID, String name, String email, String gender,
                       Date dob, String telephone, String password) {
        this.techID = techID;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.telephone = telephone;
        this.password = password;
    }

    public String getTechID() { return techID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public Date getDob() { return dob; }
    public String getTelephone() { return telephone; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDob(Date dob) { this.dob = dob; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setPassword(String password) { this.password = password; }

    public boolean insert() throws SQLException {
        String sql = "INSERT INTO Tech_Officer VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, techID);
        ps.setString(2, name);
        ps.setString(3, email);
        ps.setString(4, gender);
        ps.setDate(5, dob);
        ps.setString(6, telephone);
        ps.setString(7, password);

        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public boolean update() throws SQLException {
        String sql = "UPDATE Tech_Officer SET Name=?, Email=?, Gender=?, DOB=?, Telephone=?, Password=? WHERE TechID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, gender);
        ps.setDate(4, dob);
        ps.setString(5, telephone);
        ps.setString(6, password);
        ps.setString(7, techID);

        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public static boolean delete(String techID) throws SQLException {
        String sql = "DELETE FROM Tech_Officer WHERE TechID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, techID);
        int rows = ps.executeUpdate();
        con.close();
        return rows > 0;
    }

    public static TechOfficer getById(String id) throws SQLException {
        String sql = "SELECT * FROM Tech_Officer WHERE TechID=?";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            TechOfficer t = new TechOfficer(
                    rs.getString("TechID"),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Gender"),
                    rs.getDate("DOB"),
                    rs.getString("Telephone"),
                    rs.getString("Password")
            );
            con.close();
            return t;
        }

        con.close();
        return null;
    }

    public static List<TechOfficer> getAll() throws SQLException {
        List<TechOfficer> list = new ArrayList<>();
        String sql = "SELECT * FROM Tech_Officer";
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            list.add(new TechOfficer(
                    rs.getString("TechID"),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Gender"),
                    rs.getDate("DOB"),
                    rs.getString("Telephone"),
                    rs.getString("Password")
            ));
        }

        con.close();
        return list;
    }
}
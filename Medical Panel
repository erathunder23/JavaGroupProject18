import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

// 1. Data moniterin class
class Medical {
    private String medicalId, stuId, courseCode, description, status;
    private java.sql.Date submissionDate;

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tecmis_db";
        String user = "root";
        String password = "Apu1723";
        return DriverManager.getConnection(url, user, password);
    }

    public void setMedicalId(String id) { this.medicalId = id; }
    public void setStuId(String id) { this.stuId = id; }
    public void setCourseCode(String code) { this.courseCode = code; }
    public void setSubmissionDate(java.sql.Date date) { this.submissionDate = date; }
    public void setDescription(String desc) { this.description = desc; }
    public void setStatus(String status) { this.status = status; }

    public boolean add() {
        String sql = "INSERT INTO Medical (MedicalID, StuID, CourseCode, SubmissionDate, Description, Status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, medicalId);
            ps.setString(2, stuId);
            ps.setString(3, courseCode);
            ps.setDate(4, submissionDate);
            ps.setString(5, description);
            ps.setString(6, (status == null) ? "Pending" : status);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public ResultSet getStudentRecords(String sId) throws SQLException {
        Connection conn = getConnection();
        String sql = "SELECT * FROM Medical WHERE StuID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, sId);
        return ps.executeQuery();
    }
}

// 2. GUI Class
public class MedicalPortal extends JPanel {

    private String currentStudentId;

    // select clear colors
    private Color bg = Color.WHITE;
    private Color labelBlue = new Color(0, 0, 255); // (Pure Blue)
    private Color textColor = Color.BLACK;

    public MedicalPortal(String studentId) {
        this.currentStudentId = studentId;
        setLayout(new BorderLayout());
        setBackground(bg);

        UIManager.put("TabbedPane.background", Color.WHITE);
        UIManager.put("TabbedPane.selected", new Color(230, 240, 255));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("Submit Medical", createSubmitPanel());
        tabs.addTab("View My Records", createViewPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createSubmitPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labelTexts = {"Medical ID:", "Course Code:", "Date (YYYY-MM-DD):", "Description:"};
        JTextField[] fields = new JTextField[4];

        for (int i = 0; i < labelTexts.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labelTexts[i]);

            // create label in to dark blue
            lbl.setForeground(labelBlue);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); // clearly display font
            p.add(lbl, gbc);

            fields[i] = new JTextField(20);
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fields[i].setForeground(textColor);
            fields[i].setBorder(BorderFactory.createLineBorder(labelBlue, 1)); //blue border for label
            gbc.gridx = 1;
            p.add(fields[i], gbc);
        }

        JButton btn = new JButton("Submit Record");
        btn.setBackground(labelBlue);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        p.add(btn, gbc);

        btn.addActionListener(e -> {
            try {
                if(fields[0].getText().trim().isEmpty()) throw new Exception("ID is empty!");

                Medical m = new Medical();
                m.setMedicalId(fields[0].getText().trim());
                m.setStuId(currentStudentId);
                m.setCourseCode(fields[1].getText().trim());
                m.setSubmissionDate(java.sql.Date.valueOf(fields[2].getText().trim()));
                m.setDescription(fields[3].getText().trim());
                m.setStatus("Submitted");

                if (m.add()) {
                    JOptionPane.showMessageDialog(p, "Successfully Submitted!");
                    for(JTextField f : fields) f.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage());
            }
        });
        return p;
    }

    private JPanel createViewPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] cols = {"Medical ID", "Course", "Date", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.getTableHeader().setBackground(labelBlue);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh Records");
        refresh.setBackground(labelBlue);
        refresh.setForeground(Color.WHITE);
        p.add(refresh, BorderLayout.SOUTH);

        refresh.addActionListener(e -> {
            model.setRowCount(0);
            Medical m = new Medical();
            try (ResultSet rs = m.getStudentRecords(currentStudentId)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("MedicalID"), rs.getString("CourseCode"),
                            rs.getDate("SubmissionDate"), rs.getString("Description"),
                            rs.getString("Status")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return p;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Student Medical Management");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(850, 550);
        f.setLocationRelativeTo(null);

        // To test give your database ID which include there
        f.add(new MedicalPortal("TG001"));
        f.setVisible(true);
    }
}

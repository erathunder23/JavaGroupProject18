import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.sql.*;

// 1. ABSTRACTION
interface Actions {
    void loadData(String id);
    void updateContact();
    void updatePhoto();
}

public class StudentDashboard extends JFrame implements Actions {
    private StudentModel stu;
    private JPanel mainPanel;
    private JLabel nameLbl, regLbl, lvlLbl, gpaLbl, photoDisplay;
    private JTextField emailField, phoneField;

    public StudentDashboard(String loginID) {
        stu = new StudentModel(loginID); // ඔයාගේ Student ID එක

        setTitle("Student LMS Dashboard" + loginID);
        setSize(1050, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupSidebar();
        setupMainPanel();

        loadData(stu.id); // දත්ත load කිරීම
    }

    private void setupSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(8, 1));
        sidebar.setBackground(new Color(52, 94, 141));
        sidebar.setPreferredSize(new Dimension(220, 700));

        String[] menu = {"Profile", "Attendance", "Medical", "Courses", "Grades & GPA", "Timetable", "Notices"};
        for (String m : menu) {
            JButton btn = new JButton(m);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(52, 94, 141));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

            btn.addActionListener(e -> {
                if (m.equals("Grades & GPA")) {
                ((CardLayout) mainPanel.getLayout()).show(mainPanel, m);
            } else {
                ((CardLayout) mainPanel.getLayout()).show(mainPanel, m);
            }
            });
            sidebar.add(btn);
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(180, 40, 40)); // රතු පැහැයට හුරු වර්ණයක්
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Dashboard එක වසන්න
                new LoginGUI().setVisible(true); // නැවත Login window එක විවෘත කරන්න
            }
        });

        sidebar.add(logoutBtn);
        add(sidebar, BorderLayout.WEST);
    }

    private void setupMainPanel() {
        mainPanel = new JPanel(new CardLayout());

        // Profile Panel එක සෑදීම
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 20, 8, 20);

        // රවුම් පින්තූරය
        photoDisplay = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Shape circle = new Ellipse2D.Double(5, 5, 160, 160);
                g2.setClip(circle);
                if (stu.getProfilePic() != null) {
                    g2.drawImage(new ImageIcon(stu.getProfilePic()).getImage(), 5, 5, 160, 160, this);
                } else {
                    g2.setColor(new Color(230, 230, 230)); g2.fill(circle);
                }
                g2.setClip(null); g2.setColor(Color.LIGHT_GRAY); g2.draw(circle); g2.dispose();
            }
        };
        photoDisplay.setPreferredSize(new Dimension(175, 175));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        p.add(photoDisplay, g);

        g.gridy = 1; JButton phBtn = new JButton("Update Photo");
        phBtn.addActionListener(e -> updatePhoto());
        p.add(phBtn, g);

        // Labels සහ Input fields (ඔයාගේ screenshot එකේ විදිහටම)
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL;

        addLabelRow(p, g, 2, "Name:", nameLbl = new JLabel("Loading..."));
        addLabelRow(p, g, 3, "Reg No:", regLbl = new JLabel(stu.id));
        addLabelRow(p, g, 4, "Level:", lvlLbl = new JLabel("-"));
        addLabelRow(p, g, 5, "GPA:", gpaLbl = new JLabel("0.0"));

        g.gridy = 6; g.gridx = 0; p.add(new JLabel("Email Address:"), g);
        g.gridx = 1; emailField = new JTextField(20); p.add(emailField, g);

        g.gridy = 7; g.gridx = 0; p.add(new JLabel("Phone Number:"), g);
        g.gridx = 1; phoneField = new JTextField(20); p.add(phoneField, g);

        g.gridy = 8; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(30, 0, 0, 0);
        JButton upBtn = new JButton("Update Contact Details");
        upBtn.setBackground(new Color(28, 54, 86)); upBtn.setForeground(Color.WHITE);
        upBtn.setPreferredSize(new Dimension(0, 45));
        upBtn.addActionListener(e -> updateContact());
        p.add(upBtn, g);

        mainPanel.add(p, "Profile");

        Color whiteBg = Color.WHITE;
        Color primaryBlue = new Color(0, 102,204);

        JTabbedPane attendanceTabs = new JTabbedPane();
        // AttendanceDashboard හි ඇති static methods මගින් panels ලබා ගැනීම
        attendanceTabs.addTab("View Records", AttendanceManagementSystem.createViewAttendancePanel(whiteBg, primaryBlue));
        attendanceTabs.addTab("Statistics", AttendanceManagementSystem.createAttendancePercentagePanel(whiteBg, primaryBlue));

        mainPanel.add(attendanceTabs, "Attendance"); // CardLayout එකට Attendance එකතු කිරීම

        StudentReportCardGUI reportCard = new StudentReportCardGUI(stu.id);
        mainPanel.add(reportCard.getReportCardPanel(), "Grades & GPA");
        // අලුතින් හදපු NoticePanel එක card layout එකට එකතු කිරීම
        NoticePanel noticePanel = new NoticePanel();
        mainPanel.add(noticePanel, "Notices");

        // Add this near where you added NoticePanel
        CourseModulePanel coursePanel = new CourseModulePanel();
        mainPanel.add(coursePanel, "Courses");

        MedicalPortal medicalPortal = new MedicalPortal(stu.id); // දැනට සිටින ශිෂ්‍යයාගේ ID එක ලබා දෙයි
        mainPanel.add(medicalPortal, "Medical");

        // Locate this part in your setupMainPanel() method and change it:
        Batch8Timetable timetablePanel = new Batch8Timetable();
        mainPanel.add(timetablePanel, "Timetable"); // Use the exact string "Timetable"


        // වෙනත් පිටු placeholder ලෙස
        for(String s : new String[]{})
            mainPanel.add(new JLabel(s, JLabel.CENTER), s);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addLabelRow(JPanel p, GridBagConstraints g, int y, String title, JLabel val) {
        g.gridy = y; g.gridx = 0; p.add(new JLabel(title), g);
        g.gridx = 1; val.setFont(new Font("Segoe UI", Font.BOLD, 14)); p.add(val, g);
    }

    // --- පින්තූරය තෝරාගෙන DB එකට Save කිරීම ---
    @Override
    public void updatePhoto() {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File f = ch.getSelectedFile();
                byte[] b = new FileInputStream(f).readAllBytes();
                try (Connection c = DBManager.getConnection()) {
                    PreparedStatement ps = c.prepareStatement("UPDATE Student SET ProfilePic=? WHERE StuID= ?");
                    ps.setBytes(1, b); ps.setString(2, stu.id);
                    if(ps.executeUpdate() > 0) {
                        stu.setProfilePic(b);
                        photoDisplay.repaint();
                        JOptionPane.showMessageDialog(this, "Photo Updated!");
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // --- Email සහ Phone Update කිරීම ---
    @Override
    public void updateContact() {
        try (Connection c = DBManager.getConnection()) {
            PreparedStatement ps = c.prepareStatement("UPDATE Student SET Email=?, Telephone=? WHERE StuID= ?");
            ps.setString(1, emailField.getText());
            ps.setString(2, phoneField.getText());
            ps.setString(3, stu.id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Contact details updated!");
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // --- දත්ත load කර UI එක refresh කිරීම ---
    @Override
    public void loadData(String id) {
        try (Connection c = DBManager.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM Student WHERE StuID= ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stu.setName(rs.getString("First_name") + " " + rs.getString("Last_name"));
                stu.setLevel(rs.getString("Level"));
                stu.setGpa(rs.getString("GPA"));
                stu.setEmail(rs.getString("Email"));
                stu.setPhone(rs.getString("Telephone"));
                stu.setProfilePic(rs.getBytes("ProfilePic"));

                // UI එකට අලුත් දත්ත ලබා දීම
                nameLbl.setText(stu.getName());
                lvlLbl.setText(stu.getLevel());
                gpaLbl.setText(stu.getGpa());
                emailField.setText(stu.getEmail());
                phoneField.setText(stu.getPhone());
                photoDisplay.repaint();
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard("TG001").setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.*;

public class StudentDashboardConnections extends JFrame {
    private JPanel mainPanel;
    private Image profileImage = null;

    // Database තොරතුරු
    private final String DB_URL = "jdbc:mysql://localhost:3306/tecmis_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "Apu1723"; // keep your password here

    // GUI සංරචක (Components)
    private JLabel nameLabel, regLabel;
    private JTextField emailField, phoneField;
    private JLabel profilePicDisplay;

    public StudentDashboardConnections() {
        // Load the driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver එක හමු නොවීය!");
        }

        setTitle("Student LMS Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(7, 1, 5, 5));
        sidebar.setBackground(new Color(40, 80, 120));
        sidebar.setPreferredSize(new Dimension(200, 600));

        String[] sections = {"Profile", "Attendance", "Medical", "Courses", "Grades & GPA", "Timetable", "Notices"};
        for (String sec : sections) {
            JButton btn = new JButton(sec);
            btn.setBackground(new Color(60, 100, 150));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            sidebar.add(btn);
            btn.addActionListener(e -> switchPanel(sec));
        }
        add(sidebar, BorderLayout.WEST);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(createProfilePanel(), "Profile");

        for(int i = 1; i < sections.length; i++) {
            mainPanel.add(new JLabel(sections[i] + " Details (View Only)", JLabel.CENTER), sections[i]);
        }

        add(mainPanel, BorderLayout.CENTER);

        // Load data from the database
        loadStudentData("TG001");
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        profilePicDisplay = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight()) - 4;
                Shape circle = new Ellipse2D.Double(2, 2, size, size);
                g2.setClip(circle);
                if (profileImage != null) g2.drawImage(profileImage, 2, 2, size, size, this);
                else {
                    g2.setColor(new Color(230, 230, 230));
                    g2.fill(circle);
                    g2.setColor(Color.GRAY);
                    g2.drawString("No Photo", getWidth()/2 - 30, getHeight()/2 + 5);
                }
                g2.setClip(null);
                g2.setColor(Color.DARK_GRAY);
                g2.draw(circle);
                g2.dispose();
            }
        };
        profilePicDisplay.setPreferredSize(new Dimension(150, 150));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        panel.add(profilePicDisplay, gbc);

        gbc.gridy = 1;
        JButton photoBtn = new JButton("Update Photo");
        panel.add(photoBtn, gbc);

        // Fields Setup
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; nameLabel = new JLabel("Loading..."); panel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Reg No:"), gbc);
        gbc.gridx = 1; regLabel = new JLabel("TG001"); panel.add(regLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1; emailField = new JTextField(20); panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; phoneField = new JTextField(20); panel.add(phoneField, gbc);

        // Update Action
        JButton updateBtn = new JButton("Update Contact Details");
        updateBtn.setBackground(new Color(30, 60, 90));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> updateStudentData());

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(updateBtn, gbc);

        return panel;
    }

    // Read data from the database
    private void loadStudentData(String stuID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT First_name, Last_name, Email, Telephone FROM Student WHERE StuID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stuID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameLabel.setText(rs.getString("First_name") + " " + rs.getString("Last_name"));
                emailField.setText(rs.getString("Email"));
                phoneField.setText(rs.getString("Telephone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update data of the database
    private void updateStudentData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "UPDATE Student SET Email = ?, Telephone = ? WHERE StuID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, emailField.getText());
            pstmt.setString(2, phoneField.getText());
            pstmt.setString(3, regLabel.getText());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Data Updated Successfully!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboardConnections().setVisible(true));
    }
}

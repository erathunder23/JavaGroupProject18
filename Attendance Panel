import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AttendanceManagementSystem {

    // 1. DATABASE CONNECTION CLASS
    public static class DatabaseConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/tecmis_db";
        private static final String USER = "root";
        private static final String PASSWORD = "Apu1723";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    // 2. ATTENDANCE LOGIC CLASS
    public static class Attendance {
        public double calculateAttendancePercentage(String studentId, String courseCode) {
            int totalClasses = 0;
            int presentClasses = 0;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT Status FROM Attendance WHERE StuID = ? AND CourseCode = ?")) {

                ps.setString(1, studentId);
                ps.setString(2, courseCode);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    totalClasses++;
                    if (rs.getString("Status").equalsIgnoreCase("Present")) {
                        presentClasses++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (totalClasses == 0) return 0.0;
            return ((double) presentClasses / totalClasses) * 100;
        }
    }

    // 3. UI DASHBOARD METHODS
    public static JPanel createViewAttendancePanel(Color bg, Color primaryBlue) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topPanel.setBackground(bg);

        // Label in Blue
        JLabel lbl = new JLabel("Student ID:");
        lbl.setForeground(primaryBlue);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Text Field with Black Letters
        JTextField stuField = new JTextField(18);
        stuField.setBackground(Color.WHITE);
        stuField.setForeground(Color.BLACK); // Black text for input
        stuField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        stuField.setBorder(BorderFactory.createLineBorder(primaryBlue));

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(primaryBlue);
        searchBtn.setForeground(Color.WHITE);

        JButton loadAllBtn = new JButton("Load All Records");
        loadAllBtn.setBackground(new Color(30, 30, 100));
        loadAllBtn.setForeground(Color.WHITE);

        topPanel.add(lbl);
        topPanel.add(stuField);
        topPanel.add(searchBtn);
        topPanel.add(loadAllBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"ID", "Student ID", "Course Code", "Date", "Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK); // Table data in Black
        table.setGridColor(new Color(230, 230, 230));
        table.setRowHeight(25);

        table.getTableHeader().setBackground(primaryBlue);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Logic
        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM Attendance WHERE StuID = ?")) {
                ps.setString(1, stuField.getText().trim());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("AttendanceID"), rs.getString("StuID"),
                            rs.getString("CourseCode"), rs.getDate("Date"), rs.getString("Type"), rs.getString("Status")});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        loadAllBtn.addActionListener(e -> {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Attendance ORDER BY Date DESC")) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt("AttendanceID"), rs.getString("StuID"),
                            rs.getString("CourseCode"), rs.getDate("Date"), rs.getString("Type"), rs.getString("Status")});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    public static JPanel createAttendancePercentagePanel(Color bg, Color primaryBlue) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels in Blue
        JLabel lbl1 = new JLabel("Student ID:");
        lbl1.setForeground(primaryBlue);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lbl2 = new JLabel("Course Code:");
        lbl2.setForeground(primaryBlue);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Fields with Black Letters
        JTextField stuField = new JTextField(20);
        stuField.setForeground(Color.BLACK);
        stuField.setBorder(BorderFactory.createLineBorder(primaryBlue));

        JTextField courseField = new JTextField(20);
        courseField.setForeground(Color.BLACK);
        courseField.setBorder(BorderFactory.createLineBorder(primaryBlue));

        JButton calcBtn = new JButton("Calculate Percentage");
        calcBtn.setBackground(primaryBlue);
        calcBtn.setForeground(Color.WHITE);

        JLabel resultLbl = new JLabel("Attendance: - %", SwingConstants.CENTER);
        resultLbl.setForeground(primaryBlue); // Result Label in Blue
        resultLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lbl1, gbc);
        gbc.gridx = 1; panel.add(stuField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lbl2, gbc);
        gbc.gridx = 1; panel.add(courseField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(calcBtn, gbc);
        gbc.gridy = 3; panel.add(resultLbl, gbc);

        calcBtn.addActionListener(e -> {
            Attendance attendance = new Attendance();
            double percentage = attendance.calculateAttendancePercentage(stuField.getText(), courseField.getText());
            resultLbl.setText(String.format("Attendance: %.2f%%", percentage));
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Attendance Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);

            Color whiteBg = Color.WHITE;
            Color primaryBlue = new Color(0, 102, 204); // A clean blue

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("View Records", createViewAttendancePanel(whiteBg, primaryBlue));
            tabs.addTab("Statistics", createAttendancePercentagePanel(whiteBg, primaryBlue));

            frame.add(tabs);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

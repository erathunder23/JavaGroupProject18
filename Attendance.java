import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Attendance implements Manageable {

    private int attendanceId;
    private String stuId;
    private String courseCode;
    private java.sql.Date date;
    private String type;
    private String status;

    public Attendance() {}

    public Attendance(int attendanceId, String stuId, String courseCode,
                      java.sql.Date date, String type, String status) {
        this.attendanceId = attendanceId;
        this.stuId = stuId;
        this.courseCode = courseCode;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public String getStuId() { return stuId; }
    public void setStuId(String stuId) { this.stuId = stuId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public java.sql.Date getDate() { return date; }
    public void setDate(java.sql.Date date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean add() {
        try {
            if (stuId == null || stuId.isEmpty()) throw new InvalidDataException("Student ID cannot be empty");
            if (courseCode == null || courseCode.isEmpty()) throw new InvalidDataException("Course Code cannot be empty");
            if (date == null) throw new InvalidDataException("Date cannot be empty");
            if (status == null || status.isEmpty()) throw new InvalidDataException("Status cannot be empty");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Attendance (StuID, CourseCode, Date, Type, Status) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, stuId);
            ps.setString(2, courseCode);
            ps.setDate(3, date);
            ps.setString(4, type);
            ps.setString(5, status);
            return ps.executeUpdate() > 0;

        } catch (InvalidDataException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean update() {
        try {
            if (attendanceId <= 0) throw new InvalidDataException("Invalid Attendance ID");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Attendance SET StuID=?, CourseCode=?, Date=?, Type=?, Status=? WHERE AttendanceID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, stuId);
            ps.setString(2, courseCode);
            ps.setDate(3, date);
            ps.setString(4, type);
            ps.setString(5, status);
            ps.setInt(6, attendanceId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Attendance record not found with ID: " + attendanceId);
            return true;

        } catch (InvalidDataException | RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean delete() {
        try {
            if (attendanceId <= 0) throw new InvalidDataException("Invalid Attendance ID");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Attendance WHERE AttendanceID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, attendanceId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Attendance record not found with ID: " + attendanceId);
            return true;

        } catch (InvalidDataException | RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public String view() {
        return "Attendance[ID=" + attendanceId + ", StuID=" + stuId + ", Course=" + courseCode + ", Status=" + status + "]";
    }

    public String viewAttendance(String studentId) {
        StringBuilder sb = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Attendance WHERE StuID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("AttendanceID"))
                        .append(" | Course: ").append(rs.getString("CourseCode"))
                        .append(" | Date: ").append(rs.getDate("Date"))
                        .append(" | Type: ").append(rs.getString("Type"))
                        .append(" | Status: ").append(rs.getString("Status"))
                        .append("\n");
            }
            if (sb.length() == 0) sb.append("No attendance records found for student: ").append(studentId);
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        return sb.toString();
    }

    public double calculateAttendancePercentage(String studentId, String courseCode) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String totalSql = "SELECT COUNT(*) FROM Attendance WHERE StuID = ? AND CourseCode = ?";
            PreparedStatement totalPs = conn.prepareStatement(totalSql);
            totalPs.setString(1, studentId);
            totalPs.setString(2, courseCode);
            ResultSet totalRs = totalPs.executeQuery();
            int total = 0;
            if (totalRs.next()) total = totalRs.getInt(1);

            String presentSql = "SELECT COUNT(*) FROM Attendance WHERE StuID = ? AND CourseCode = ? AND Status = 'Present'";
            PreparedStatement presentPs = conn.prepareStatement(presentSql);
            presentPs.setString(1, studentId);
            presentPs.setString(2, courseCode);
            ResultSet presentRs = presentPs.executeQuery();
            int present = 0;
            if (presentRs.next()) present = presentRs.getInt(1);

            if (total == 0) return 0.0;
            return ((double) present / total) * 100;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error calculating attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    public static void showGUI() {
        JFrame frame = new JFrame("Attendance Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(950, 680);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(20, 20, 35);
        Color panelColor = new Color(28, 28, 48);
        Color accent = new Color(255, 183, 77);
        Color textColor = Color.WHITE;
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);

        frame.getContentPane().setBackground(bg);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("  Attendance Management System", SwingConstants.LEFT);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(accent);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(panelColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        frame.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(panelColor);
        tabs.setForeground(textColor);
        tabs.setFont(labelFont);

        tabs.addTab("Add Attendance", buildAddPanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("View Attendance", buildViewPanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("Update Attendance", buildUpdatePanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("Delete Attendance", buildDeletePanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("Attendance %", buildPercentagePanel(bg, panelColor, accent, textColor, labelFont));

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel buildAddPanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Student ID:", "Course Code:", "Date (YYYY-MM-DD):", "Type (Theory/Practical):"};
        JTextField[] fields = new JTextField[labels.length];
        String[] statuses = {"Present", "Absent", "Medical Leave"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setBackground(new Color(35, 35, 60));
        statusBox.setForeground(text);

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(text);
            lbl.setFont(lf);
            gbc.gridx = 0; gbc.gridy = i;
            p.add(lbl, gbc);

            fields[i] = new JTextField(22);
            fields[i].setBackground(new Color(35, 35, 60));
            fields[i].setForeground(text);
            fields[i].setCaretColor(text);
            fields[i].setBorder(BorderFactory.createLineBorder(accent));
            gbc.gridx = 1;
            p.add(fields[i], gbc);
        }

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setForeground(text);
        statusLbl.setFont(lf);
        gbc.gridx = 0; gbc.gridy = labels.length;
        p.add(statusLbl, gbc);
        gbc.gridx = 1;
        p.add(statusBox, gbc);

        JButton addBtn = new JButton("Add Attendance");
        addBtn.setBackground(accent);
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = labels.length + 1; gbc.gridwidth = 2;
        p.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            try {
                Attendance a = new Attendance();
                a.setStuId(fields[0].getText().trim());
                a.setCourseCode(fields[1].getText().trim());
                a.setDate(java.sql.Date.valueOf(fields[2].getText().trim()));
                a.setType(fields[3].getText().trim());
                a.setStatus((String) statusBox.getSelectedItem());
                if (a.add()) {
                    JOptionPane.showMessageDialog(p, "Attendance added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    for (JTextField f : fields) f.setText("");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(p, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildViewPanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(bg);
        JLabel lbl = new JLabel("Student ID:");
        lbl.setForeground(text); lbl.setFont(lf);
        JTextField stuField = new JTextField(15);
        stuField.setBackground(new Color(35, 35, 60));
        stuField.setForeground(text);
        stuField.setCaretColor(text);
        stuField.setBorder(BorderFactory.createLineBorder(accent));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(accent);
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        JButton loadAllBtn = new JButton("Load All");
        loadAllBtn.setBackground(new Color(100, 149, 237));
        loadAllBtn.setForeground(Color.WHITE);
        loadAllBtn.setBorderPainted(false);
        loadAllBtn.setFocusPainted(false);
        topPanel.add(lbl); topPanel.add(stuField); topPanel.add(searchBtn); topPanel.add(loadAllBtn);
        p.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "StuID", "CourseCode", "Date", "Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(new Color(28, 28, 48));
        table.setForeground(text);
        table.setGridColor(new Color(50, 50, 80));
        table.setFont(lf);
        table.getTableHeader().setBackground(accent);
        table.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(28, 28, 48));
        p.add(scroll, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM Attendance WHERE StuID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, stuField.getText().trim());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("AttendanceID"), rs.getString("StuID"),
                            rs.getString("CourseCode"), rs.getDate("Date"),
                            rs.getString("Type"), rs.getString("Status")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadAllBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Attendance");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("AttendanceID"), rs.getString("StuID"),
                            rs.getString("CourseCode"), rs.getDate("Date"),
                            rs.getString("Type"), rs.getString("Status")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildUpdatePanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLbl = new JLabel("Attendance ID:");
        idLbl.setForeground(text); idLbl.setFont(lf);
        JTextField idField = new JTextField(20);
        idField.setBackground(new Color(35, 35, 60));
        idField.setForeground(text);
        idField.setCaretColor(text);
        idField.setBorder(BorderFactory.createLineBorder(accent));

        JLabel statusLbl = new JLabel("New Status:");
        statusLbl.setForeground(text); statusLbl.setFont(lf);
        String[] statuses = {"Present", "Absent", "Medical Leave"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setBackground(new Color(35, 35, 60));
        statusBox.setForeground(text);

        JButton updateBtn = new JButton("Update Attendance");
        updateBtn.setBackground(accent);
        updateBtn.setForeground(Color.BLACK);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updateBtn.setBorderPainted(false);
        updateBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 0; p.add(idLbl, gbc);
        gbc.gridx = 1; p.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(statusLbl, gbc);
        gbc.gridx = 1; p.add(statusBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; p.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Attendance a = new Attendance();
                a.setAttendanceId(id);
                a.setStatus((String) statusBox.getSelectedItem());
                if (a.update()) {
                    JOptionPane.showMessageDialog(p, "Attendance updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    idField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Attendance ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildDeletePanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLbl = new JLabel("Attendance ID:");
        idLbl.setForeground(text); idLbl.setFont(lf);
        JTextField idField = new JTextField(20);
        idField.setBackground(new Color(35, 35, 60));
        idField.setForeground(text);
        idField.setCaretColor(text);
        idField.setBorder(BorderFactory.createLineBorder(accent));

        JButton delBtn = new JButton("Delete Attendance");
        delBtn.setBackground(new Color(220, 80, 80));
        delBtn.setForeground(Color.WHITE);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        delBtn.setBorderPainted(false);
        delBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 0; p.add(idLbl, gbc);
        gbc.gridx = 1; p.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; p.add(delBtn, gbc);

        delBtn.addActionListener(e -> {
            try {
                int confirm = JOptionPane.showConfirmDialog(p,
                        "Delete Attendance ID: " + idField.getText().trim() + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = Integer.parseInt(idField.getText().trim());
                    Attendance a = new Attendance();
                    a.setAttendanceId(id);
                    if (a.delete()) {
                        JOptionPane.showMessageDialog(p, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        idField.setText("");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(p, "Attendance ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildPercentagePanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel stuLbl = new JLabel("Student ID:");
        stuLbl.setForeground(text); stuLbl.setFont(lf);
        JTextField stuField = new JTextField(20);
        stuField.setBackground(new Color(35, 35, 60));
        stuField.setForeground(text);
        stuField.setCaretColor(text);
        stuField.setBorder(BorderFactory.createLineBorder(accent));

        JLabel courseLbl = new JLabel("Course Code:");
        courseLbl.setForeground(text); courseLbl.setFont(lf);
        JTextField courseField = new JTextField(20);
        courseField.setBackground(new Color(35, 35, 60));
        courseField.setForeground(text);
        courseField.setCaretColor(text);
        courseField.setBorder(BorderFactory.createLineBorder(accent));

        JButton calcBtn = new JButton("Calculate %");
        calcBtn.setBackground(accent);
        calcBtn.setForeground(Color.BLACK);
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calcBtn.setBorderPainted(false);
        calcBtn.setFocusPainted(false);

        JLabel resultLbl = new JLabel("Attendance: -");
        resultLbl.setForeground(accent);
        resultLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));

        gbc.gridx = 0; gbc.gridy = 0; p.add(stuLbl, gbc);
        gbc.gridx = 1; p.add(stuField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(courseLbl, gbc);
        gbc.gridx = 1; p.add(courseField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; p.add(calcBtn, gbc);
        gbc.gridy = 3; p.add(resultLbl, gbc);

        calcBtn.addActionListener(e -> {
            Attendance a = new Attendance();
            double pct = a.calculateAttendancePercentage(stuField.getText().trim(), courseField.getText().trim());
            resultLbl.setText(String.format("Attendance: %.2f%%", pct));
            if (pct < 80) resultLbl.setForeground(new Color(220, 80, 80));
            else resultLbl.setForeground(accent);
        });

        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Attendance::showGUI);
    }
}
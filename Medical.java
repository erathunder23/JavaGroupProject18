import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Medical implements Manageable {

    private String medicalId;
    private String stuId;
    private String courseCode;
    private java.sql.Date submissionDate;
    private String description;
    private String status;

    public Medical() {}

    public Medical(String medicalId, String stuId, String courseCode,
                   java.sql.Date submissionDate, String description, String status) {
        this.medicalId = medicalId;
        this.stuId = stuId;
        this.courseCode = courseCode;
        this.submissionDate = submissionDate;
        this.description = description;
        this.status = status;
    }

    public String getMedicalId() { return medicalId; }
    public void setMedicalId(String medicalId) { this.medicalId = medicalId; }

    public String getStuId() { return stuId; }
    public void setStuId(String stuId) { this.stuId = stuId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public java.sql.Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(java.sql.Date submissionDate) { this.submissionDate = submissionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean add() {
        try {
            if (medicalId == null || medicalId.isEmpty()) throw new InvalidDataException("Medical ID cannot be empty");
            if (stuId == null || stuId.isEmpty()) throw new InvalidDataException("Student ID cannot be empty");
            if (courseCode == null || courseCode.isEmpty()) throw new InvalidDataException("Course Code cannot be empty");

            Connection conn = DatabaseConnection.getConnection();
            String check = "SELECT MedicalID FROM Medical WHERE MedicalID = ?";
            PreparedStatement checkPs = conn.prepareStatement(check);
            checkPs.setString(1, medicalId);
            if (checkPs.executeQuery().next()) throw new DuplicateRecordException("Medical ID already exists");

            String sql = "INSERT INTO Medical (MedicalID, StuID, CourseCode, SubmissionDate, Description, Status) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, medicalId);
            ps.setString(2, stuId);
            ps.setString(3, courseCode);
            ps.setDate(4, submissionDate);
            ps.setString(5, description);
            ps.setString(6, status != null ? status : "Pending");
            return ps.executeUpdate() > 0;

        } catch (InvalidDataException | DuplicateRecordException e) {
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
            if (medicalId == null || medicalId.isEmpty()) throw new InvalidDataException("Medical ID cannot be empty");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Medical SET StuID=?, CourseCode=?, SubmissionDate=?, Description=?, Status=? WHERE MedicalID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, stuId);
            ps.setString(2, courseCode);
            ps.setDate(3, submissionDate);
            ps.setString(4, description);
            ps.setString(5, status);
            ps.setString(6, medicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Medical record not found: " + medicalId);
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
            if (medicalId == null || medicalId.isEmpty()) throw new InvalidDataException("Medical ID cannot be empty");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Medical WHERE MedicalID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, medicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Medical record not found: " + medicalId);
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
        return "Medical[ID=" + medicalId + ", StuID=" + stuId + ", Status=" + status + "]";
    }

    public void submitMedical() {
        this.status = "Submitted";
        add();
    }

    public void updateMedical(String newStatus) {
        this.status = newStatus;
        update();
    }

    public void deleteMedical() {
        delete();
    }

    public static void showGUI() {
        JFrame frame = new JFrame("Medical Records Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(950, 680);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(18, 30, 25);
        Color panelColor = new Color(25, 45, 35);
        Color accent = new Color(72, 199, 142);
        Color textColor = Color.WHITE;
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);

        frame.getContentPane().setBackground(bg);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("  Medical Records Management", SwingConstants.LEFT);
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

        tabs.addTab("Submit Medical", buildSubmitPanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("View Records", buildViewPanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("Update Status", buildUpdateStatusPanel(bg, panelColor, accent, textColor, labelFont));
        tabs.addTab("Delete Record", buildDeletePanel(bg, panelColor, accent, textColor, labelFont));

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel buildSubmitPanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Medical ID:", "Student ID:", "Course Code:", "Submission Date (YYYY-MM-DD):", "Description:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(text);
            lbl.setFont(lf);
            gbc.gridx = 0; gbc.gridy = i;
            p.add(lbl, gbc);

            fields[i] = new JTextField(22);
            fields[i].setBackground(new Color(30, 55, 40));
            fields[i].setForeground(text);
            fields[i].setCaretColor(text);
            fields[i].setBorder(BorderFactory.createLineBorder(accent));
            gbc.gridx = 1;
            p.add(fields[i], gbc);
        }

        JButton submitBtn = new JButton("Submit Medical");
        submitBtn.setBackground(accent);
        submitBtn.setForeground(Color.BLACK);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setBorderPainted(false);
        submitBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        p.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                Medical m = new Medical();
                m.setMedicalId(fields[0].getText().trim());
                m.setStuId(fields[1].getText().trim());
                m.setCourseCode(fields[2].getText().trim());
                m.setSubmissionDate(java.sql.Date.valueOf(fields[3].getText().trim()));
                m.setDescription(fields[4].getText().trim());
                m.submitMedical();
                JOptionPane.showMessageDialog(p, "Medical submitted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                for (JTextField f : fields) f.setText("");
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

        String[] cols = {"MedicalID", "StuID", "CourseCode", "Submission Date", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(new Color(25, 45, 35));
        table.setForeground(text);
        table.setGridColor(new Color(40, 80, 60));
        table.setFont(lf);
        table.getTableHeader().setBackground(accent);
        table.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(25, 45, 35));
        p.add(scroll, BorderLayout.CENTER);

        JButton loadBtn = new JButton("Load Records");
        loadBtn.setBackground(accent);
        loadBtn.setForeground(Color.BLACK);
        loadBtn.setBorderPainted(false);
        loadBtn.setFocusPainted(false);
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(loadBtn, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Medical");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("MedicalID"),
                            rs.getString("StuID"),
                            rs.getString("CourseCode"),
                            rs.getDate("SubmissionDate"),
                            rs.getString("Description"),
                            rs.getString("Status")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildUpdateStatusPanel(Color bg, Color panel, Color accent, Color text, Font lf) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLbl = new JLabel("Medical ID:");
        idLbl.setForeground(text); idLbl.setFont(lf);
        JTextField idField = new JTextField(20);
        idField.setBackground(new Color(30, 55, 40));
        idField.setForeground(text);
        idField.setCaretColor(text);
        idField.setBorder(BorderFactory.createLineBorder(accent));

        JLabel statusLbl = new JLabel("New Status:");
        statusLbl.setForeground(text); statusLbl.setFont(lf);
        String[] statuses = {"Pending", "Approved", "Rejected", "Under Review"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        statusBox.setBackground(new Color(30, 55, 40));
        statusBox.setForeground(text);

        JButton updateBtn = new JButton("Update Status");
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
            Medical m = new Medical();
            m.setMedicalId(idField.getText().trim());
            m.setStatus((String) statusBox.getSelectedItem());
            if (m.update()) {
                JOptionPane.showMessageDialog(p, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                idField.setText("");
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

        JLabel idLbl = new JLabel("Medical ID:");
        idLbl.setForeground(text); idLbl.setFont(lf);
        JTextField idField = new JTextField(20);
        idField.setBackground(new Color(30, 55, 40));
        idField.setForeground(text);
        idField.setCaretColor(text);
        idField.setBorder(BorderFactory.createLineBorder(accent));

        JButton delBtn = new JButton("Delete Record");
        delBtn.setBackground(new Color(220, 80, 80));
        delBtn.setForeground(Color.WHITE);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        delBtn.setBorderPainted(false);
        delBtn.setFocusPainted(false);

        gbc.gridx = 0; gbc.gridy = 0; p.add(idLbl, gbc);
        gbc.gridx = 1; p.add(idField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; p.add(delBtn, gbc);

        delBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(p, "Delete Medical ID: " + idField.getText().trim() + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Medical m = new Medical();
                m.setMedicalId(idField.getText().trim());
                if (m.delete()) {
                    JOptionPane.showMessageDialog(p, "Record deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    idField.setText("");
                }
            }
        });

        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Medical::showGUI);
    }
}
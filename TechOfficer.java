import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class TechOfficer extends User implements Manageable {

    private String techId;
    private String techName;
    private String techEmail;

    public TechOfficer() {
        super();
    }

    public TechOfficer(String techId, String techName, String techEmail,
                       String gender, Date dob, String telephone, String password) {
        super(techId, techName, "", techEmail, gender, dob, telephone, password);
        this.techId = techId;
        this.techName = techName;
        this.techEmail = techEmail;
    }

    public String getTechId() { return techId; }
    public void setTechId(String techId) { this.techId = techId; }

    public String getTechName() { return techName; }
    public void setTechName(String techName) { this.techName = techName; }

    public String getTechEmail() { return techEmail; }
    public void setTechEmail(String techEmail) { this.techEmail = techEmail; }

    @Override
    public boolean loginUser() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Tech_Officer WHERE Email = ? AND Password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, getTechEmail());
            ps.setString(2, getPassword());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Login failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean logoutUser() {
        return true;
    }

    @Override
    public String viewTimeTable() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM Timetable";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Batch: ").append(rs.getString("Batch"))
                        .append(" | Dept: ").append(rs.getString("DeptID"))
                        .append("\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public boolean updateUser() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Tech_Officer SET Name=?, Email=?, Telephone=? WHERE TechID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, techName);
            ps.setString(2, techEmail);
            ps.setString(3, getTelephone());
            ps.setString(4, techId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Update failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean add() {
        try {
            if (techId == null || techId.isEmpty()) throw new InvalidDataException("Tech ID cannot be empty");
            if (techName == null || techName.isEmpty()) throw new InvalidDataException("Name cannot be empty");
            if (techEmail == null || !techEmail.contains("@")) throw new InvalidDataException("Invalid email address");

            Connection conn = DatabaseConnection.getConnection();
            String check = "SELECT TechID FROM Tech_Officer WHERE TechID = ?";
            PreparedStatement checkPs = conn.prepareStatement(check);
            checkPs.setString(1, techId);
            if (checkPs.executeQuery().next()) throw new DuplicateRecordException("TechID already exists");

            String sql = "INSERT INTO Tech_Officer (TechID, Name, Email, Gender, DOB, Telephone, Password) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, techId);
            ps.setString(2, techName);
            ps.setString(3, techEmail);
            ps.setString(4, getGender());
            ps.setDate(5, getDob() != null ? new java.sql.Date(getDob().getTime()) : null);
            ps.setString(6, getTelephone());
            ps.setString(7, getPassword());
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
        return updateUser();
    }

    @Override
    public boolean delete() {
        try {
            if (techId == null || techId.isEmpty()) throw new InvalidDataException("Tech ID cannot be empty");
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Tech_Officer WHERE TechID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, techId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Tech Officer not found with ID: " + techId);
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
        return "TechOfficer[ID=" + techId + ", Name=" + techName + ", Email=" + techEmail + "]";
    }

    public void uploadAttendance(String stuId, String courseCode, String date, String type, String status) {
        try {
            if (stuId == null || stuId.isEmpty()) throw new InvalidDataException("Student ID cannot be empty");
            if (courseCode == null || courseCode.isEmpty()) throw new InvalidDataException("Course Code cannot be empty");

            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Attendance (StuID, CourseCode, Date, Type, Status) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, stuId);
            ps.setString(2, courseCode);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.setString(4, type);
            ps.setString(5, status);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Attendance uploaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (InvalidDataException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateAttendance(int attendanceId, String status) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Attendance SET Status = ? WHERE AttendanceID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, attendanceId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Attendance record not found with ID: " + attendanceId);
            JOptionPane.showMessageDialog(null, "Attendance updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteAttendance(int attendanceId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Attendance WHERE AttendanceID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, attendanceId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Attendance record not found with ID: " + attendanceId);
            JOptionPane.showMessageDialog(null, "Attendance deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addMedicals(String medicalId, String stuId, String courseCode, String submissionDate, String description) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Medical (MedicalID, StuID, CourseCode, SubmissionDate, Description, Status) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, medicalId);
            ps.setString(2, stuId);
            ps.setString(3, courseCode);
            ps.setDate(4, java.sql.Date.valueOf(submissionDate));
            ps.setString(5, description);
            ps.setString(6, "Pending");
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medical record added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateMedicals(String medicalId, String status) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Medical SET Status = ? WHERE MedicalID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, medicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Medical record not found: " + medicalId);
            JOptionPane.showMessageDialog(null, "Medical updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMedicals(String medicalId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Medical WHERE MedicalID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, medicalId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RecordNotFoundException("Medical record not found: " + medicalId);
            JOptionPane.showMessageDialog(null, "Medical deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RecordNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Not Found", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createNotice(String noticeId, String title, String description, String date, String adminId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Notice (NoticeID, Title, Description, Date, AdminID) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, noticeId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setDate(4, java.sql.Date.valueOf(date));
            ps.setString(5, adminId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Notice created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showGUI() {
        JFrame frame = new JFrame("Tech Officer Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(18, 18, 30);
        Color panel = new Color(30, 30, 50);
        Color accent = new Color(100, 149, 237);
        Color text = Color.WHITE;
        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        frame.getContentPane().setBackground(bg);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("  Tech Officer Management", SwingConstants.LEFT);
        title.setFont(titleFont);
        title.setForeground(accent);
        title.setOpaque(true);
        title.setBackground(panel);
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        frame.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(panel);
        tabs.setForeground(text);
        tabs.setFont(labelFont);

        JPanel addPanel        = buildTechOfficerFormPanel(bg, panel, accent, text, labelFont);
        JPanel viewPanel       = buildTechOfficerViewPanel(bg, panel, accent, text, labelFont);
        JPanel attendancePanel = buildAttendanceUploadPanel(bg, panel, accent, text, labelFont);
        JPanel deletePanel     = buildDeleteOfficerPanel(bg, panel, accent, text, labelFont);  // NEW TAB

        tabs.addTab("Add Officer",        addPanel);
        tabs.addTab("View Officers",      viewPanel);
        tabs.addTab("Upload Attendance",  attendancePanel);
        tabs.addTab("Delete Officer",     deletePanel);                                        // NEW TAB

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // -----------------------------------------------------------------------
    // NEW: Delete Officer Panel
    // -----------------------------------------------------------------------
    private static JPanel buildDeleteOfficerPanel(Color bg, Color panel, Color accent, Color text, Font labelFont) {

        // ---- colours ----
        Color dangerRed   = new Color(220, 53, 69);   // delete button
        Color warningAmber = new Color(255, 193, 7);  // confirm highlight

        // ---- outer panel ----
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ---- Tech ID field ----
        JLabel idLabel = new JLabel("Tech ID:");
        idLabel.setForeground(text);
        idLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        p.add(idLabel, gbc);

        JTextField idField = new JTextField(20);
        idField.setBackground(new Color(40, 40, 65));
        idField.setForeground(text);
        idField.setCaretColor(text);
        idField.setBorder(BorderFactory.createLineBorder(accent));
        gbc.gridx = 1;
        p.add(idField, gbc);

        // ---- Lookup button ----
        JButton lookupBtn = new JButton("Look Up Officer");
        lookupBtn.setBackground(accent);
        lookupBtn.setForeground(Color.WHITE);
        lookupBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lookupBtn.setBorderPainted(false);
        lookupBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        p.add(lookupBtn, gbc);

        // ---- Info display area ----
        JTextArea infoArea = new JTextArea(5, 30);
        infoArea.setBackground(new Color(25, 25, 42));
        infoArea.setForeground(warningAmber);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 100)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        infoArea.setEditable(false);
        infoArea.setText("Officer details will appear here after lookup.");
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        p.add(infoScroll, gbc);

        // ---- Delete button (disabled until lookup succeeds) ----
        JButton deleteBtn = new JButton("Delete Officer");
        deleteBtn.setBackground(dangerRed);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setEnabled(false);   // disabled until a valid record is found
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        p.add(deleteBtn, gbc);

        // ---- Status label ----
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(warningAmber);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        p.add(statusLabel, gbc);

        // ---- Lookup action ----
        // Stores whether a valid record was found so the delete button knows what to delete
        final String[] foundTechId = { null };

        lookupBtn.addActionListener(e -> {
            String inputId = idField.getText().trim();
            if (inputId.isEmpty()) {
                infoArea.setText("Please enter a Tech ID.");
                deleteBtn.setEnabled(false);
                foundTechId[0] = null;
                return;
            }
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM Tech_Officer WHERE TechID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, inputId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    // Build a readable summary of the record
                    String name      = rs.getString("Name");
                    String email     = rs.getString("Email");
                    String gender    = rs.getString("Gender");
                    String telephone = rs.getString("Telephone");

                    infoArea.setText(
                            "Tech ID   : " + inputId   + "\n" +
                                    "Name      : " + name      + "\n" +
                                    "Email     : " + email     + "\n" +
                                    "Gender    : " + gender    + "\n" +
                                    "Telephone : " + telephone + "\n\n" +
                                    "Review the details above before deleting."
                    );
                    deleteBtn.setEnabled(true);
                    foundTechId[0] = inputId;
                    statusLabel.setText("Record found. Click 'Delete Officer' to remove permanently.");
                } else {
                    infoArea.setText("No Tech Officer found with ID: " + inputId);
                    deleteBtn.setEnabled(false);
                    foundTechId[0] = null;
                    statusLabel.setText("");
                }
            } catch (SQLException ex) {
                infoArea.setText("Database error: " + ex.getMessage());
                deleteBtn.setEnabled(false);
                foundTechId[0] = null;
                JOptionPane.showMessageDialog(p, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---- Delete action ----
        deleteBtn.addActionListener(e -> {
            if (foundTechId[0] == null) return;

            // Confirmation dialog to prevent accidental deletions
            int confirm = JOptionPane.showConfirmDialog(
                    p,
                    "Are you sure you want to permanently delete Tech Officer with ID: " + foundTechId[0] + "?\n"
                            + "This action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            TechOfficer to = new TechOfficer();
            to.setTechId(foundTechId[0]);

            if (to.delete()) {
                JOptionPane.showMessageDialog(p,
                        "Tech Officer '" + foundTechId[0] + "' deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                // Reset the panel
                idField.setText("");
                infoArea.setText("Officer details will appear here after lookup.");
                deleteBtn.setEnabled(false);
                foundTechId[0] = null;
                statusLabel.setText("");
            }
            // delete() already shows an error dialog on failure, so no else needed here
        });

        return p;
    }

    // -----------------------------------------------------------------------
    // Existing panel builders (unchanged)
    // -----------------------------------------------------------------------
    private static JPanel buildTechOfficerFormPanel(Color bg, Color panel, Color accent, Color text, Font labelFont) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Tech ID:", "Name:", "Email:", "Gender:", "Telephone:", "Password:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(text);
            lbl.setFont(labelFont);
            gbc.gridx = 0; gbc.gridy = i;
            p.add(lbl, gbc);

            fields[i] = new JTextField(20);
            fields[i].setBackground(new Color(40, 40, 65));
            fields[i].setForeground(text);
            fields[i].setCaretColor(text);
            fields[i].setBorder(BorderFactory.createLineBorder(accent));
            gbc.gridx = 1;
            p.add(fields[i], gbc);
        }

        JButton addBtn = new JButton("Add Tech Officer");
        addBtn.setBackground(accent);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        p.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            TechOfficer to = new TechOfficer();
            to.setTechId(fields[0].getText().trim());
            to.setTechName(fields[1].getText().trim());
            to.setTechEmail(fields[2].getText().trim());
            to.setGender(fields[3].getText().trim());
            to.setTelephone(fields[4].getText().trim());
            to.setPassword(fields[5].getText().trim());
            if (to.add()) {
                JOptionPane.showMessageDialog(p, "Tech Officer added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                for (JTextField f : fields) f.setText("");
            }
        });

        return p;
    }

    private static JPanel buildTechOfficerViewPanel(Color bg, Color panel, Color accent, Color text, Font labelFont) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"TechID", "Name", "Email", "Gender", "Telephone"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setBackground(new Color(30, 30, 50));
        table.setForeground(text);
        table.setGridColor(new Color(60, 60, 90));
        table.setFont(labelFont);
        table.getTableHeader().setBackground(accent);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(30, 30, 50));
        p.add(scroll, BorderLayout.CENTER);

        JButton loadBtn = new JButton("Load Officers");
        loadBtn.setBackground(accent);
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setBorderPainted(false);
        loadBtn.setFocusPainted(false);
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(loadBtn, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Tech_Officer");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("TechID"),
                            rs.getString("Name"),
                            rs.getString("Email"),
                            rs.getString("Gender"),
                            rs.getString("Telephone")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private static JPanel buildAttendanceUploadPanel(Color bg, Color panel, Color accent, Color text, Font labelFont) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Student ID:", "Course Code:", "Date (YYYY-MM-DD):", "Type:", "Status:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(text);
            lbl.setFont(labelFont);
            gbc.gridx = 0; gbc.gridy = i;
            p.add(lbl, gbc);

            fields[i] = new JTextField(20);
            fields[i].setBackground(new Color(40, 40, 65));
            fields[i].setForeground(text);
            fields[i].setCaretColor(text);
            fields[i].setBorder(BorderFactory.createLineBorder(accent));
            gbc.gridx = 1;
            p.add(fields[i], gbc);
        }

        JButton uploadBtn = new JButton("Upload Attendance");
        uploadBtn.setBackground(accent);
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.setBorderPainted(false);
        uploadBtn.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        p.add(uploadBtn, gbc);

        uploadBtn.addActionListener(e -> {
            TechOfficer to = new TechOfficer();
            to.uploadAttendance(
                    fields[0].getText().trim(),
                    fields[1].getText().trim(),
                    fields[2].getText().trim(),
                    fields[3].getText().trim(),
                    fields[4].getText().trim()
            );
            for (JTextField f : fields) f.setText("");
        });

        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TechOfficer::showGUI);
    }
}
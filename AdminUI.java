package ui;

import dao.*;
import model.*;
import utils.SessionManager;
import utils.ButtonStyleUtil;
import utils.LogoUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Time;
import java.util.List;

public class AdminUI extends JFrame {
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private NoticeDAO noticeDAO;
    private TimetableDAO timetableDAO;

    private JTable studentTable, lecturerTable, officerTable, courseTable, noticeTable, timetableTable;
    private DefaultTableModel studentModel, lecturerModel, officerModel, courseModel, noticeModel, timetableModel;

    private static final Color BG_COLOR = new Color(240, 248, 255);
    private static final Color HEADER_BG = Color.BLACK;
    private static final Color HEADER_FG = Color.WHITE;
    private static final Color ROW_COLOR_1 = Color.WHITE;
    private static final Color ROW_COLOR_2 = new Color(245, 245, 245);

    public AdminUI() {
        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        noticeDAO = new NoticeDAO();
        timetableDAO = new TimetableDAO();
        initComponents();
        loadData();
        LogoUtil.setFrameIcon(this);
    }

    private void initComponents() {
        setTitle("👑 Admin Dashboard - Faculty of Technology Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 86, 179));
        menuBar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JMenu fileMenu = new JMenu("📁 File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem logoutItem = new JMenuItem("🚪 Logout");
        logoutItem.setBackground(new Color(231, 76, 60));
        logoutItem.setForeground(Color.WHITE);
        logoutItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutItem.setOpaque(true);
        logoutItem.addActionListener(e -> logout());
        JMenuItem exitItem = new JMenuItem("❌ Exit");
        exitItem.setBackground(new Color(231, 76, 60));
        exitItem.setForeground(Color.WHITE);
        exitItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exitItem.setOpaque(true);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("❓ Help");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JMenuItem aboutItem = new JMenuItem("ℹ️ About");
        aboutItem.setBackground(new Color(155, 89, 182));
        aboutItem.setForeground(Color.WHITE);
        aboutItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        aboutItem.setOpaque(true);
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createHorizontalGlue());

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(0, 86, 179));
        JLabel userIcon = new JLabel("👑");
        userIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel userLabel = new JLabel(" " + SessionManager.getCurrentUser().getFullName() + " (Admin) ");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.BLACK);
        userLabel.setBackground(new Color(255, 193, 7));
        userLabel.setOpaque(true);
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        userPanel.add(userIcon);
        userPanel.add(userLabel);
        menuBar.add(userPanel);

        setJMenuBar(menuBar);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("👥 User Management", createUserManagementPanel());
        tabbedPane.addTab("📚 Course Management", createCourseManagementPanel());
        tabbedPane.addTab("📢 Notice Management", createNoticeManagementPanel());
        tabbedPane.addTab("📅 Timetable Management", createTimetableManagementPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("✅ Logged in as: " + SessionManager.getCurrentUser().getFullName() + " (Administrator)");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ==================== CUSTOM HEADER RENDERER FOR PERMANENT STYLING ====================
    private void styleTable(JTable table) {
        // Custom header renderer to force black background & white text
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(HEADER_BG);
                label.setForeground(HEADER_FG);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                return label;
            }
        });
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 35));

        // Row styling
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        table.setAutoCreateRowSorter(true);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ROW_COLOR_1 : ROW_COLOR_2);
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                return c;
            }
        });
    }

    // ==================== USER MANAGEMENT PANEL ====================
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);
        JButton addStudentBtn = ButtonStyleUtil.createSuccessButton("👨‍🎓 Add Student");
        JButton addLecturerBtn = ButtonStyleUtil.createInfoButton("👩‍🏫 Add Lecturer");
        JButton addOfficerBtn = ButtonStyleUtil.createPurpleButton("🔧 Add Technical Officer");
        JButton refreshBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh All");
        buttonPanel.add(addStudentBtn);
        buttonPanel.add(addLecturerBtn);
        buttonPanel.add(addOfficerBtn);
        buttonPanel.add(refreshBtn);

        addStudentBtn.addActionListener(e -> addStudent());
        addLecturerBtn.addActionListener(e -> addLecturer());
        addOfficerBtn.addActionListener(e -> addTechnicalOfficer());
        refreshBtn.addActionListener(e -> loadData());

        JTabbedPane userTabs = new JTabbedPane();
        userTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Students
        studentModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Reg No", "Batch", "Semester", "Repeat"}, 0);
        studentTable = new JTable(studentModel);
        styleTable(studentTable);
        JPanel studentPanel = createEntityPanel(studentTable, "✏️ Edit Student", "🗑 Delete Student",
                e -> editStudent(), e -> deleteStudent());
        userTabs.addTab("👨‍🎓 Students", studentPanel);

        // Lecturers
        lecturerModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Email", "Phone", "Emp No", "Department"}, 0);
        lecturerTable = new JTable(lecturerModel);
        styleTable(lecturerTable);
        JPanel lecturerPanel = createEntityPanel(lecturerTable, "✏️ Edit Lecturer", "🗑 Delete Lecturer",
                e -> editLecturer(), e -> deleteLecturer());
        userTabs.addTab("👩‍🏫 Lecturers", lecturerPanel);

        // Technical Officers
        officerModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Email", "Phone", "Emp No", "Department"}, 0);
        officerTable = new JTable(officerModel);
        styleTable(officerTable);
        JPanel officerPanel = createEntityPanel(officerTable, "✏️ Edit Officer", "🗑 Delete Officer",
                e -> editTechnicalOfficer(), e -> deleteTechnicalOfficer());
        userTabs.addTab("🔧 Technical Officers", officerPanel);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(userTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEntityPanel(JTable table, String editText, String deleteText, java.awt.event.ActionListener editAction, java.awt.event.ActionListener deleteAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(BG_COLOR);
        JButton editBtn = ButtonStyleUtil.createWarningButton(editText);
        JButton delBtn = ButtonStyleUtil.createDangerButton(deleteText);
        editBtn.addActionListener(editAction);
        delBtn.addActionListener(deleteAction);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ==================== COURSE MANAGEMENT ====================
    private JPanel createCourseManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);
        JButton addBtn = ButtonStyleUtil.createSuccessButton("➕ Add Course");
        JButton editBtn = ButtonStyleUtil.createWarningButton("✏️ Edit Course");
        JButton delBtn = ButtonStyleUtil.createDangerButton("🗑 Delete Course");
        JButton refreshBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(refreshBtn);

        addBtn.addActionListener(e -> addCourse());
        editBtn.addActionListener(e -> editCourse());
        delBtn.addActionListener(e -> deleteCourse());
        refreshBtn.addActionListener(e -> loadCourses());

        courseModel = new DefaultTableModel(new String[]{"ID", "Course Code", "Course Name", "Credits", "Semester", "Lecturer"}, 0);
        courseTable = new JTable(courseModel);
        styleTable(courseTable);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        return panel;
    }

    // ==================== NOTICE MANAGEMENT ====================
    private JPanel createNoticeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);
        JButton addBtn = ButtonStyleUtil.createSuccessButton("📢 Add Notice");
        JButton editBtn = ButtonStyleUtil.createWarningButton("✏️ Edit Notice");
        JButton delBtn = ButtonStyleUtil.createDangerButton("🗑 Delete Notice");
        JButton refreshBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(refreshBtn);

        addBtn.addActionListener(e -> addNotice());
        editBtn.addActionListener(e -> editNotice());
        delBtn.addActionListener(e -> deleteNotice());
        refreshBtn.addActionListener(e -> loadNotices());

        noticeModel = new DefaultTableModel(new String[]{"ID", "Title", "Content", "Target", "Created By", "Date", "Attachment"}, 0);
        noticeTable = new JTable(noticeModel);
        styleTable(noticeTable);
        noticeTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        noticeTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(noticeTable), BorderLayout.CENTER);
        return panel;
    }

    // ==================== TIMETABLE MANAGEMENT ====================
    private JPanel createTimetableManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);
        JButton addBtn = ButtonStyleUtil.createSuccessButton("➕ Add Entry");
        JButton editBtn = ButtonStyleUtil.createWarningButton("✏️ Edit Entry");
        JButton delBtn = ButtonStyleUtil.createDangerButton("🗑 Delete Entry");
        JButton refreshBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(refreshBtn);

        addBtn.addActionListener(e -> addTimetableEntry());
        editBtn.addActionListener(e -> editTimetableEntry());
        delBtn.addActionListener(e -> deleteTimetableEntry());
        refreshBtn.addActionListener(e -> loadTimetables());

        timetableModel = new DefaultTableModel(new String[]{"ID", "Course", "Day", "Start", "End", "Type", "Venue", "Semester"}, 0);
        timetableTable = new JTable(timetableModel);
        styleTable(timetableTable);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(timetableTable), BorderLayout.CENTER);
        return panel;
    }

    // ==================== DATA LOADING ====================
    private void loadData() {
        loadStudents();
        loadLecturers();
        loadOfficers();
        loadCourses();
        loadNotices();
        loadTimetables();
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            studentModel.addRow(new Object[]{s.getStudentId(), s.getUsername(), s.getFullName(),
                    s.getRegistrationNo(), s.getBatchYear(), s.getCurrentSemester(), s.isRepeat() ? "Yes" : "No"});
        }
    }

    private void loadLecturers() {
        lecturerModel.setRowCount(0);
        List<Lecturer> lecturers = userDAO.getAllLecturers();
        for (Lecturer l : lecturers) {
            lecturerModel.addRow(new Object[]{l.getLecturerId(), l.getUsername(), l.getFullName(),
                    l.getEmail(), l.getPhone(), l.getEmployeeNo(), l.getDepartment()});
        }
    }

    private void loadOfficers() {
        officerModel.setRowCount(0);
        List<TechnicalOfficer> officers = userDAO.getAllTechnicalOfficers();
        for (TechnicalOfficer o : officers) {
            officerModel.addRow(new Object[]{o.getOfficerId(), o.getUsername(), o.getFullName(),
                    o.getEmail(), o.getPhone(), o.getEmployeeNo(), o.getDepartment()});
        }
    }

    private void loadCourses() {
        courseModel.setRowCount(0);
        for (Course c : courseDAO.getAllCourses()) {
            courseModel.addRow(new Object[]{c.getCourseId(), c.getCourseCode(), c.getCourseName(),
                    c.getCredits(), c.getSemester(), c.getLecturerName()});
        }
    }

    private void loadNotices() {
        noticeModel.setRowCount(0);
        for (Notice n : noticeDAO.getAllNotices()) {
            String attach = (n.getAttachmentPath() != null && !n.getAttachmentPath().isEmpty()) ? "📎" : "";
            noticeModel.addRow(new Object[]{n.getNoticeId(), n.getTitle(), n.getContent(),
                    n.getTargetRole(), n.getCreatedByName(), n.getCreatedDate(), attach});
        }
    }

    private void loadTimetables() {
        timetableModel.setRowCount(0);
        for (Timetable t : timetableDAO.getTimetableBySemester(2)) {
            timetableModel.addRow(new Object[]{t.getTimetableId(), t.getCourseCode() + " - " + t.getCourseName(),
                    t.getDayOfWeek(), t.getStartTime(), t.getEndTime(), t.getSessionType(), t.getVenue(), t.getSemester()});
        }
    }

    // ==================== CRUD: STUDENT ====================
    private void addStudent() {
        JDialog dialog = createDialog("Add Student", 550, 620);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JTextField fullName = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField regNo = new JTextField();
        JTextField batch = new JTextField();
        JTextField semester = new JTextField();
        JCheckBox repeat = new JCheckBox();
        JCheckBox batchMissed = new JCheckBox();
        JTextArea contact = new JTextArea(3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Password:", password);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Registration No:", regNo);
        addField(panel, "Batch Year:", batch);
        addField(panel, "Current Semester:", semester);
        addField(panel, "Repeat Student:", repeat);
        addField(panel, "Batch Missed:", batchMissed);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton save = ButtonStyleUtil.createSuccessButton("💾 Save Student");
        save.addActionListener(e -> {
            try {
                Student s = new Student();
                s.setUsername(username.getText());
                s.setPassword(password.getText());
                s.setFullName(fullName.getText());
                s.setEmail(email.getText());
                s.setPhone(phone.getText());
                s.setRole("student");
                s.setContactDetails(contact.getText());
                s.setRegistrationNo(regNo.getText());
                s.setBatchYear(Integer.parseInt(batch.getText()));
                s.setCurrentSemester(Integer.parseInt(semester.getText()));
                s.setRepeat(repeat.isSelected());
                s.setBatchMissed(batchMissed.isSelected());

                if (userDAO.createStudent(s)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Student added successfully!");
                    dialog.dispose();
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Failed to add student!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for Batch and Semester.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }
        int studentId = (int) studentModel.getValueAt(row, 0);
        Student s = userDAO.getAllStudents().stream().filter(st -> st.getStudentId() == studentId).findFirst().orElse(null);
        if (s == null) return;

        JDialog dialog = createDialog("Edit Student", 550, 620);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField(s.getUsername());
        username.setEditable(false);
        JTextField fullName = new JTextField(s.getFullName());
        JTextField email = new JTextField(s.getEmail());
        JTextField phone = new JTextField(s.getPhone());
        JTextField regNo = new JTextField(s.getRegistrationNo());
        JTextField batch = new JTextField(String.valueOf(s.getBatchYear()));
        JTextField semester = new JTextField(String.valueOf(s.getCurrentSemester()));
        JCheckBox repeat = new JCheckBox("Repeat", s.isRepeat());
        JCheckBox batchMissed = new JCheckBox("Batch Missed", s.isBatchMissed());
        JTextArea contact = new JTextArea(s.getContactDetails(), 3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Registration No:", regNo);
        addField(panel, "Batch Year:", batch);
        addField(panel, "Current Semester:", semester);
        addField(panel, "Repeat Student:", repeat);
        addField(panel, "Batch Missed:", batchMissed);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Student");
        update.addActionListener(e -> {
            try {
                s.setFullName(fullName.getText());
                s.setEmail(email.getText());
                s.setPhone(phone.getText());
                s.setRegistrationNo(regNo.getText());
                s.setBatchYear(Integer.parseInt(batch.getText()));
                s.setCurrentSemester(Integer.parseInt(semester.getText()));
                s.setRepeat(repeat.isSelected());
                s.setBatchMissed(batchMissed.isSelected());
                s.setContactDetails(contact.getText());

                if (userDAO.updateStudent(s)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Student updated!");
                    dialog.dispose();
                    loadStudents();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }
        int studentId = (int) studentModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student? This action cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Check database constraints.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== CRUD: LECTURER ====================
    private void addLecturer() {
        JDialog dialog = createDialog("Add Lecturer", 550, 550);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JTextField fullName = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField empNo = new JTextField();
        JTextField dept = new JTextField();
        JTextArea contact = new JTextArea(3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Password:", password);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Employee No:", empNo);
        addField(panel, "Department:", dept);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton save = ButtonStyleUtil.createSuccessButton("💾 Save Lecturer");
        save.addActionListener(e -> {
            Lecturer l = new Lecturer();
            l.setUsername(username.getText());
            l.setPassword(password.getText());
            l.setFullName(fullName.getText());
            l.setEmail(email.getText());
            l.setPhone(phone.getText());
            l.setRole("lecturer");
            l.setContactDetails(contact.getText());
            l.setEmployeeNo(empNo.getText());
            l.setDepartment(dept.getText());

            if (userDAO.createLecturer(l)) {
                JOptionPane.showMessageDialog(dialog, "✅ Lecturer added!");
                dialog.dispose();
                loadLecturers();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Failed to add lecturer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editLecturer() {
        int row = lecturerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a lecturer to edit.");
            return;
        }
        int lecturerId = (int) lecturerModel.getValueAt(row, 0);
        Lecturer l = userDAO.getAllLecturers().stream().filter(lec -> lec.getLecturerId() == lecturerId).findFirst().orElse(null);
        if (l == null) return;

        JDialog dialog = createDialog("Edit Lecturer", 550, 550);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField(l.getUsername());
        username.setEditable(false);
        JTextField fullName = new JTextField(l.getFullName());
        JTextField email = new JTextField(l.getEmail());
        JTextField phone = new JTextField(l.getPhone());
        JTextField empNo = new JTextField(l.getEmployeeNo());
        JTextField dept = new JTextField(l.getDepartment());
        JTextArea contact = new JTextArea(l.getContactDetails(), 3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Employee No:", empNo);
        addField(panel, "Department:", dept);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Lecturer");
        update.addActionListener(e -> {
            l.setFullName(fullName.getText());
            l.setEmail(email.getText());
            l.setPhone(phone.getText());
            l.setEmployeeNo(empNo.getText());
            l.setDepartment(dept.getText());
            l.setContactDetails(contact.getText());

            if (userDAO.updateLecturer(l)) {
                JOptionPane.showMessageDialog(dialog, "✅ Lecturer updated!");
                dialog.dispose();
                loadLecturers();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteLecturer() {
        int row = lecturerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a lecturer to delete.");
            return;
        }
        int lecturerId = (int) lecturerModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this lecturer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteLecturer(lecturerId)) {
                JOptionPane.showMessageDialog(this, "Lecturer deleted.");
                loadLecturers();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== CRUD: TECHNICAL OFFICER ====================
    private void addTechnicalOfficer() {
        JDialog dialog = createDialog("Add Technical Officer", 550, 550);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JTextField fullName = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField empNo = new JTextField();
        JTextField dept = new JTextField();
        JTextArea contact = new JTextArea(3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Password:", password);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Employee No:", empNo);
        addField(panel, "Department:", dept);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton save = ButtonStyleUtil.createSuccessButton("💾 Save Officer");
        save.addActionListener(e -> {
            TechnicalOfficer o = new TechnicalOfficer();
            o.setUsername(username.getText());
            o.setPassword(password.getText());
            o.setFullName(fullName.getText());
            o.setEmail(email.getText());
            o.setPhone(phone.getText());
            o.setRole("technical_officer");
            o.setContactDetails(contact.getText());
            o.setEmployeeNo(empNo.getText());
            o.setDepartment(dept.getText());

            if (userDAO.createTechnicalOfficer(o)) {
                JOptionPane.showMessageDialog(dialog, "✅ Officer added!");
                dialog.dispose();
                loadOfficers();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Failed to add officer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editTechnicalOfficer() {
        int row = officerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an officer to edit.");
            return;
        }
        int officerId = (int) officerModel.getValueAt(row, 0);
        TechnicalOfficer o = userDAO.getAllTechnicalOfficers().stream().filter(of -> of.getOfficerId() == officerId).findFirst().orElse(null);
        if (o == null) return;

        JDialog dialog = createDialog("Edit Technical Officer", 550, 550);
        JPanel panel = createFormPanel();

        JTextField username = new JTextField(o.getUsername());
        username.setEditable(false);
        JTextField fullName = new JTextField(o.getFullName());
        JTextField email = new JTextField(o.getEmail());
        JTextField phone = new JTextField(o.getPhone());
        JTextField empNo = new JTextField(o.getEmployeeNo());
        JTextField dept = new JTextField(o.getDepartment());
        JTextArea contact = new JTextArea(o.getContactDetails(), 3, 20);

        addField(panel, "Username:", username);
        addField(panel, "Full Name:", fullName);
        addField(panel, "Email:", email);
        addField(panel, "Phone:", phone);
        addField(panel, "Employee No:", empNo);
        addField(panel, "Department:", dept);
        addField(panel, "Contact Details:", new JScrollPane(contact));

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Officer");
        update.addActionListener(e -> {
            o.setFullName(fullName.getText());
            o.setEmail(email.getText());
            o.setPhone(phone.getText());
            o.setEmployeeNo(empNo.getText());
            o.setDepartment(dept.getText());
            o.setContactDetails(contact.getText());

            if (userDAO.updateTechnicalOfficer(o)) {
                JOptionPane.showMessageDialog(dialog, "✅ Officer updated!");
                dialog.dispose();
                loadOfficers();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteTechnicalOfficer() {
        int row = officerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an officer to delete.");
            return;
        }
        int officerId = (int) officerModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this officer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteTechnicalOfficer(officerId)) {
                JOptionPane.showMessageDialog(this, "Officer deleted.");
                loadOfficers();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== COURSE CRUD ====================
    private void addCourse() {
        JDialog dialog = createDialog("Add Course", 500, 550);
        JPanel panel = createFormPanel();

        JTextField code = new JTextField();
        JTextField name = new JTextField();
        JTextField credits = new JTextField();
        JTextField theory = new JTextField();
        JTextField practical = new JTextField();
        JTextField semester = new JTextField();
        JTextField lecturerId = new JTextField();
        JTextArea desc = new JTextArea(3, 20);

        addField(panel, "Course Code:", code);
        addField(panel, "Course Name:", name);
        addField(panel, "Total Credits:", credits);
        addField(panel, "Theory Credits:", theory);
        addField(panel, "Practical Credits:", practical);
        addField(panel, "Semester:", semester);
        addField(panel, "Lecturer ID:", lecturerId);
        addField(panel, "Description:", new JScrollPane(desc));

        JButton save = ButtonStyleUtil.createSuccessButton("💾 Save Course");
        save.addActionListener(e -> {
            try {
                Course c = new Course();
                c.setCourseCode(code.getText());
                c.setCourseName(name.getText());
                c.setCredits(Integer.parseInt(credits.getText()));
                c.setTheoryCredits(Integer.parseInt(theory.getText()));
                c.setPracticalCredits(Integer.parseInt(practical.getText()));
                c.setSemester(Integer.parseInt(semester.getText()));
                c.setLecturerId(lecturerId.getText().isEmpty() ? 0 : Integer.parseInt(lecturerId.getText()));
                c.setDescription(desc.getText());

                if (courseDAO.createCourse(c)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Course added!");
                    dialog.dispose();
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Failed to add course!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course to edit.");
            return;
        }
        int courseId = (int) courseModel.getValueAt(row, 0);
        Course c = courseDAO.getCourseById(courseId);
        if (c == null) return;

        JDialog dialog = createDialog("Edit Course", 500, 550);
        JPanel panel = createFormPanel();

        JTextField code = new JTextField(c.getCourseCode());
        JTextField name = new JTextField(c.getCourseName());
        JTextField credits = new JTextField(String.valueOf(c.getCredits()));
        JTextField theory = new JTextField(String.valueOf(c.getTheoryCredits()));
        JTextField practical = new JTextField(String.valueOf(c.getPracticalCredits()));
        JTextField semester = new JTextField(String.valueOf(c.getSemester()));
        JTextField lecturerId = new JTextField(String.valueOf(c.getLecturerId()));
        JTextArea desc = new JTextArea(c.getDescription(), 3, 20);

        addField(panel, "Course Code:", code);
        addField(panel, "Course Name:", name);
        addField(panel, "Total Credits:", credits);
        addField(panel, "Theory Credits:", theory);
        addField(panel, "Practical Credits:", practical);
        addField(panel, "Semester:", semester);
        addField(panel, "Lecturer ID:", lecturerId);
        addField(panel, "Description:", new JScrollPane(desc));

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Course");
        update.addActionListener(e -> {
            try {
                c.setCourseCode(code.getText());
                c.setCourseName(name.getText());
                c.setCredits(Integer.parseInt(credits.getText()));
                c.setTheoryCredits(Integer.parseInt(theory.getText()));
                c.setPracticalCredits(Integer.parseInt(practical.getText()));
                c.setSemester(Integer.parseInt(semester.getText()));
                c.setLecturerId(lecturerId.getText().isEmpty() ? 0 : Integer.parseInt(lecturerId.getText()));
                c.setDescription(desc.getText());

                if (courseDAO.updateCourse(c)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Course updated!");
                    dialog.dispose();
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.");
            return;
        }
        int courseId = (int) courseModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (courseDAO.deleteCourse(courseId)) {
                JOptionPane.showMessageDialog(this, "Course deleted.");
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== NOTICE CRUD ====================
    private void addNotice() {
        JDialog dialog = createDialog("Add Notice", 600, 500);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(BG_COLOR);
        JTextField title = new JTextField();
        JComboBox<String> target = new JComboBox<>(new String[]{"all", "student", "lecturer", "technical_officer", "admin"});
        JTextArea content = new JTextArea(5, 30);
        form.add(new JLabel("Title:"));
        form.add(title);
        form.add(new JLabel("Target Role:"));
        form.add(target);
        form.add(new JLabel("Content:"));

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(content), BorderLayout.CENTER);

        JButton save = ButtonStyleUtil.createSuccessButton("📢 Publish Notice");
        save.addActionListener(e -> {
            Notice n = new Notice();
            n.setTitle(title.getText());
            n.setContent(content.getText());
            n.setTargetRole((String) target.getSelectedItem());
            n.setCreatedBy(SessionManager.getCurrentUserId());

            if (noticeDAO.createNotice(n)) {
                JOptionPane.showMessageDialog(dialog, "✅ Notice published!");
                dialog.dispose();
                loadNotices();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Failed to publish notice!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editNotice() {
        int row = noticeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a notice to edit.");
            return;
        }
        int noticeId = (int) noticeModel.getValueAt(row, 0);
        Notice n = noticeDAO.getAllNotices().stream().filter(no -> no.getNoticeId() == noticeId).findFirst().orElse(null);
        if (n == null) return;

        JDialog dialog = createDialog("Edit Notice", 600, 500);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(BG_COLOR);
        JTextField title = new JTextField(n.getTitle());
        JComboBox<String> target = new JComboBox<>(new String[]{"all", "student", "lecturer", "technical_officer", "admin"});
        target.setSelectedItem(n.getTargetRole());
        JTextArea content = new JTextArea(n.getContent(), 5, 30);
        form.add(new JLabel("Title:"));
        form.add(title);
        form.add(new JLabel("Target Role:"));
        form.add(target);
        form.add(new JLabel("Content:"));

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(content), BorderLayout.CENTER);

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Notice");
        update.addActionListener(e -> {
            n.setTitle(title.getText());
            n.setContent(content.getText());
            n.setTargetRole((String) target.getSelectedItem());

            if (noticeDAO.updateNotice(n)) {
                JOptionPane.showMessageDialog(dialog, "✅ Notice updated!");
                dialog.dispose();
                loadNotices();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteNotice() {
        int row = noticeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a notice to delete.");
            return;
        }
        int noticeId = (int) noticeModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this notice?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (noticeDAO.deleteNotice(noticeId)) {
                JOptionPane.showMessageDialog(this, "Notice deleted.");
                loadNotices();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== TIMETABLE CRUD ====================
    private void addTimetableEntry() {
        JDialog dialog = createDialog("Add Timetable Entry", 600, 500);
        JPanel panel = createFormPanel();

        JComboBox<String> courseCombo = new JComboBox<>();
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode());

        JComboBox<String> dayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        JTextField start = new JTextField("09:00:00");
        JTextField end = new JTextField("11:00:00");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"theory", "practical"});
        JTextField venue = new JTextField();
        JTextField semester = new JTextField("2");

        addField(panel, "Course:", courseCombo);
        addField(panel, "Day:", dayCombo);
        addField(panel, "Start Time (HH:MM:SS):", start);
        addField(panel, "End Time (HH:MM:SS):", end);
        addField(panel, "Session Type:", typeCombo);
        addField(panel, "Venue:", venue);
        addField(panel, "Semester:", semester);

        JButton save = ButtonStyleUtil.createSuccessButton("💾 Save Entry");
        save.addActionListener(e -> {
            try {
                Timetable t = new Timetable();
                int cid = Integer.parseInt(((String) courseCombo.getSelectedItem()).split(" - ")[0]);
                t.setCourseId(cid);
                t.setDayOfWeek((String) dayCombo.getSelectedItem());
                t.setStartTime(Time.valueOf(start.getText()));
                t.setEndTime(Time.valueOf(end.getText()));
                t.setSessionType((String) typeCombo.getSelectedItem());
                t.setVenue(venue.getText());
                t.setSemester(Integer.parseInt(semester.getText()));

                if (timetableDAO.createTimetableEntry(t)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Timetable entry added!");
                    dialog.dispose();
                    loadTimetables();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid time format. Use HH:MM:SS", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(save);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editTimetableEntry() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an entry to edit.");
            return;
        }
        int id = (int) timetableModel.getValueAt(row, 0);
        Timetable t = timetableDAO.getTimetableBySemester(2).stream().filter(tt -> tt.getTimetableId() == id).findFirst().orElse(null);
        if (t == null) return;

        JDialog dialog = createDialog("Edit Timetable Entry", 600, 500);
        JPanel panel = createFormPanel();

        JComboBox<String> courseCombo = new JComboBox<>();
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode());
        courseCombo.setSelectedItem(t.getCourseId() + " - " + t.getCourseCode());

        JComboBox<String> dayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        dayCombo.setSelectedItem(t.getDayOfWeek());

        JTextField start = new JTextField(t.getStartTime().toString());
        JTextField end = new JTextField(t.getEndTime().toString());
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"theory", "practical"});
        typeCombo.setSelectedItem(t.getSessionType());

        JTextField venue = new JTextField(t.getVenue());
        JTextField semester = new JTextField(String.valueOf(t.getSemester()));

        addField(panel, "Course:", courseCombo);
        addField(panel, "Day:", dayCombo);
        addField(panel, "Start Time:", start);
        addField(panel, "End Time:", end);
        addField(panel, "Session Type:", typeCombo);
        addField(panel, "Venue:", venue);
        addField(panel, "Semester:", semester);

        JButton update = ButtonStyleUtil.createWarningButton("✏️ Update Entry");
        update.addActionListener(e -> {
            try {
                int cid = Integer.parseInt(((String) courseCombo.getSelectedItem()).split(" - ")[0]);
                t.setCourseId(cid);
                t.setDayOfWeek((String) dayCombo.getSelectedItem());
                t.setStartTime(Time.valueOf(start.getText()));
                t.setEndTime(Time.valueOf(end.getText()));
                t.setSessionType((String) typeCombo.getSelectedItem());
                t.setVenue(venue.getText());
                t.setSemester(Integer.parseInt(semester.getText()));

                if (timetableDAO.updateTimetableEntry(t)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Entry updated!");
                    dialog.dispose();
                    loadTimetables();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid time format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(update);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteTimetableEntry() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an entry to delete.");
            return;
        }
        int id = (int) timetableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this timetable entry?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (timetableDAO.deleteTimetableEntry(id)) {
                JOptionPane.showMessageDialog(this, "Entry deleted.");
                loadTimetables();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== UTILITY METHODS ====================
    private JDialog createDialog(String title, int width, int height) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(width, height);
        d.setLocationRelativeTo(this);
        d.setBackground(BG_COLOR);
        return d;
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_COLOR);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        return p;
    }

    private void addField(JPanel panel, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = panel.getComponentCount();
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.clearSession();
            dispose();
            new LoginUI().setVisible(true);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "═══════════════════════════════════════\n" +
                        "   FACULTY OF TECHNOLOGY\n" +
                        "   MANAGEMENT SYSTEM\n" +
                        "═══════════════════════════════════════\n\n" +
                        "Admin Module v2.0\n\n" +
                        "Full CRUD Operations:\n" +
                        "• Manage Users (Add/Edit/Delete)\n" +
                        "• Manage Courses (Add/Edit/Delete)\n" +
                        "• Manage Notices (Add/Edit/Delete)\n" +
                        "• Manage Timetable (Add/Edit/Delete)\n\n" +
                        "© 2024 Faculty of Technology",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}

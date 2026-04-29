package ui;

import dao.*;
import model.*;
import utils.SessionManager;
import utils.ButtonStyleUtil;
import utils.LogoUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class LecturerUI extends JFrame {
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    private MarkDAO markDAO;
    private AttendanceDAO attendanceDAO;
    private NoticeDAO noticeDAO;
    private MedicalDAO medicalDAO;
    private CourseMaterialDAO materialDAO;

    private JTable studentTable, attendanceTable, marksTable, materialsTable, noticesTable;
    private DefaultTableModel studentModel, attendanceModel, marksModel, materialsModel, noticesModel;
    private Lecturer currentLecturer;

    public LecturerUI() {
        userDAO = new UserDAO();
        courseDAO = new CourseDAO();
        markDAO = new MarkDAO();
        attendanceDAO = new AttendanceDAO();
        noticeDAO = new NoticeDAO();
        medicalDAO = new MedicalDAO();
        materialDAO = new CourseMaterialDAO();

        loadCurrentLecturer();
        initComponents();
        loadData();
        LogoUtil.setFrameIcon(this);
    }

    private void loadCurrentLecturer() {
        User user = SessionManager.getCurrentUser();
        List<Lecturer> lecturers = userDAO.getAllLecturers();
        for (Lecturer l : lecturers) {
            if (l.getUserId() == user.getUserId()) {
                currentLecturer = l;
                break;
            }
        }
    }

    private void initComponents() {
        setTitle("Lecturer Dashboard - Faculty of Technology Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);

        // ============ HIGHLY VISIBLE MENU BAR ============
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0, 86, 179)); // Dark Blue background
        menuBar.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 1));

        // Profile Menu with bright background
        JMenu profileMenu = new JMenu("👤  PROFILE");
        profileMenu.setForeground(Color.WHITE);
        profileMenu.setFont(new Font("Arial", Font.BOLD, 14));
        profileMenu.setBackground(new Color(0, 86, 179));
        profileMenu.setOpaque(true);

        // Edit Profile Menu Item - Bright Green
        JMenuItem editProfileItem = new JMenuItem("✏️  Edit Profile");
        editProfileItem.setBackground(new Color(39, 174, 96)); // Bright Green
        editProfileItem.setForeground(Color.WHITE);
        editProfileItem.setFont(new Font("Arial", Font.BOLD, 13));
        editProfileItem.setOpaque(true);
        editProfileItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        editProfileItem.addActionListener(e -> editProfile());

        // Logout Menu Item - Bright Red
        JMenuItem logoutItem = new JMenuItem("🚪  Logout");
        logoutItem.setBackground(new Color(231, 76, 60)); // Bright Red
        logoutItem.setForeground(Color.WHITE);
        logoutItem.setFont(new Font("Arial", Font.BOLD, 13));
        logoutItem.setOpaque(true);
        logoutItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutItem.addActionListener(e -> logout());

        profileMenu.add(editProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);

        // Help Menu with bright background
        JMenu helpMenu = new JMenu("❓  HELP");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Arial", Font.BOLD, 14));
        helpMenu.setBackground(new Color(0, 86, 179));
        helpMenu.setOpaque(true);

        // About Menu Item - Bright Purple
        JMenuItem aboutItem = new JMenuItem("ℹ️  About System");
        aboutItem.setBackground(new Color(155, 89, 182)); // Bright Purple
        aboutItem.setForeground(Color.WHITE);
        aboutItem.setFont(new Font("Arial", Font.BOLD, 13));
        aboutItem.setOpaque(true);
        aboutItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(aboutItem);

        menuBar.add(profileMenu);
        menuBar.add(helpMenu);

        // Add spacing between menus
        menuBar.add(Box.createHorizontalGlue());

        // User Info Panel on the right side
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        userInfoPanel.setBackground(new Color(0, 86, 179));

        JLabel userIconLabel = new JLabel("👨‍🏫");
        userIconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userIconLabel.setForeground(Color.YELLOW);

        JLabel userInfoLabel = new JLabel(" " + currentLecturer.getFullName() + " ");
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userInfoLabel.setBackground(new Color(255, 193, 7)); // Yellow background
        userInfoLabel.setOpaque(true);
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        userInfoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        userInfoPanel.add(userIconLabel);
        userInfoPanel.add(userInfoLabel);
        menuBar.add(userInfoPanel);

        setJMenuBar(menuBar);

        // ============ TABBED PANE ============
        JTabbedPane tabbedPane = new JTabbedPane();

        // Dashboard Tab
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());

        // Student Management Tab
        tabbedPane.addTab("👨‍🎓 Students", createStudentManagementPanel());

        // Attendance Tracking Tab
        tabbedPane.addTab("📋 Attendance", createAttendancePanel());

        // Marks Management Tab
        tabbedPane.addTab("📊 Marks", createMarksPanel());

        // Lecture Materials Tab
        tabbedPane.addTab("📚 Lecture Materials", createMaterialsPanel());

        // Notices Tab
        tabbedPane.addTab("📢 Notices", createNoticesPanel());

        add(tabbedPane);

        // ============ STATUS BAR ============
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JPanel statusLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLeft.setBackground(new Color(52, 73, 94));
        JLabel statusLabel = new JLabel("✅ Logged in as: " + currentLecturer.getFullName() + " (Lecturer) | Department: " + currentLecturer.getDepartment());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLeft.add(statusLabel);

        JPanel statusRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusRight.setBackground(new Color(52, 73, 94));
        JLabel dateLabel = new JLabel(new java.util.Date().toString());
        dateLabel.setForeground(Color.LIGHT_GRAY);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusRight.add(dateLabel);

        statusBar.add(statusLeft, BorderLayout.WEST);
        statusBar.add(statusRight, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadData() {
        loadStudents();
    }

    // Dashboard Panel
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(0, 86, 179));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentLecturer.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        statsPanel.setBackground(new Color(240, 248, 255));

        List<Student> students = userDAO.getAllStudents();
        List<Course> courses = courseDAO.getAllCourses();
        List<Notice> notices = noticeDAO.getAllNotices();

        JPanel studentCard = createStatCard("👨‍🎓 Total Students", String.valueOf(students.size()), new Color(52, 152, 219));
        JPanel courseCard = createStatCard("📚 My Courses", String.valueOf(courses.size()), new Color(46, 204, 113));
        JPanel noticeCard = createStatCard("📢 Notices", String.valueOf(notices.size()), new Color(241, 196, 15));
        JPanel materialCard = createStatCard("📄 Materials", "Uploaded", new Color(155, 89, 182));
        JPanel attendanceCard = createStatCard("📋 Attendance", "Track", new Color(230, 126, 34));
        JPanel markCard = createStatCard("📊 Marks", "Manage", new Color(231, 76, 60));

        statsPanel.add(studentCard);
        statsPanel.add(courseCard);
        statsPanel.add(noticeCard);
        statsPanel.add(materialCard);
        statsPanel.add(attendanceCard);
        statsPanel.add(markCard);

        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(color);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Student Management Panel
    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh Students");
        refreshBtn.addActionListener(e -> loadStudents());
        buttonPanel.add(refreshBtn);

        studentModel = new DefaultTableModel(new String[]{"ID", "Reg No", "Name", "Email", "Phone", "Batch", "Semester"}, 0);
        studentTable = new JTable(studentModel);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        studentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        studentTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "Student List",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            studentModel.addRow(new Object[]{
                    s.getStudentId(), s.getRegistrationNo(), s.getFullName(),
                    s.getEmail(), s.getPhone(), s.getBatchYear(), s.getCurrentSemester()
            });
        }
    }

    // Attendance Panel
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 248, 255));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(new Color(240, 248, 255));
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "Select Course",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(350, 30));

        JButton viewAttendanceBtn = ButtonStyleUtil.createInfoButton("📊 View Attendance Summary");

        selectionPanel.add(new JLabel("Course: "));
        selectionPanel.add(courseCombo);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(viewAttendanceBtn);

        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode() + " - " + c.getCourseName());
        }

        attendanceModel = new DefaultTableModel(new String[]{"Student ID", "Reg No", "Student Name", "Total Sessions", "Attendance %", "Status"}, 0);
        attendanceTable = new JTable(attendanceModel);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 12));

        viewAttendanceBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                loadAttendanceSummary(courseId);
            }
        });

        topPanel.add(selectionPanel);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadAttendanceSummary(int courseId) {
        attendanceModel.setRowCount(0);
        List<Object[]> summaries = attendanceDAO.getBatchAttendanceSummary(courseId);
        for (Object[] summary : summaries) {
            double percentage = (double) summary[4];
            String status;
            if (percentage >= 80) {
                status = "✅ Excellent";
            } else if (percentage >= 60) {
                status = "⚠️ Average";
            } else {
                status = "❌ Poor";
            }
            attendanceModel.addRow(new Object[]{
                    summary[0], summary[2], summary[1], summary[3],
                    String.format("%.2f", percentage) + "%", status
            });
        }
    }

    // Marks Management Panel
    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 248, 255));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(new Color(240, 248, 255));
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "Select Course and Exam Type",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(350, 30));
        JComboBox<String> examTypeCombo = new JComboBox<>(new String[]{"CA", "ESA", "Final"});
        examTypeCombo.setPreferredSize(new Dimension(100, 30));

        JButton viewMarksBtn = ButtonStyleUtil.createInfoButton("👁️ View Marks");
        JButton uploadMarksBtn = ButtonStyleUtil.createSuccessButton("📤 Upload Marks");

        selectionPanel.add(new JLabel("Course: "));
        selectionPanel.add(courseCombo);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(new JLabel("Exam Type: "));
        selectionPanel.add(examTypeCombo);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(viewMarksBtn);
        selectionPanel.add(uploadMarksBtn);

        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode() + " - " + c.getCourseName());
        }

        marksModel = new DefaultTableModel(new String[]{"Student ID", "Reg No", "Student Name", "Marks", "Grade"}, 0);
        marksTable = new JTable(marksModel);
        marksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        marksTable.setFont(new Font("Arial", Font.PLAIN, 12));

        viewMarksBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                String examType = ((String) examTypeCombo.getSelectedItem()).toLowerCase();
                loadMarksSummary(courseId, examType);
            }
        });

        uploadMarksBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                String examType = ((String) examTypeCombo.getSelectedItem()).toLowerCase();
                uploadMarks(courseId, examType);
            }
        });

        topPanel.add(selectionPanel);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(marksTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadMarksSummary(int courseId, String examType) {
        marksModel.setRowCount(0);
        List<Object[]> summaries = markDAO.getBatchMarksSummary(courseId, examType);
        for (Object[] summary : summaries) {
            marksModel.addRow(new Object[]{
                    summary[0], summary[2], summary[1],
                    summary[3] != null ? summary[3] : "Not Uploaded",
                    summary[4] != null ? summary[4] : "-"
            });
        }
    }

    private void uploadMarks(int courseId, String examType) {
        JDialog dialog = new JDialog(this, "Upload Marks - " + examType.toUpperCase(), true);
        dialog.setSize(650, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel headerLabel = new JLabel("Upload " + examType.toUpperCase() + " Marks", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(new Color(0, 86, 179));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel tempModel = new DefaultTableModel(new String[]{"Student ID", "Reg No", "Name", "Marks (out of 100)"}, 0);
        JTable tempTable = new JTable(tempModel);
        tempTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tempTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tempTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            tempModel.addRow(new Object[]{s.getStudentId(), s.getRegistrationNo(), s.getFullName(), ""});
        }

        JButton saveBtn = ButtonStyleUtil.createSuccessButton("💾 Save All Marks");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));

        JButton fillDefaultBtn = ButtonStyleUtil.createWarningButton("📝 Fill Sample Marks");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(fillDefaultBtn);
        buttonPanel.add(saveBtn);

        fillDefaultBtn.addActionListener(e -> {
            for (int i = 0; i < tempModel.getRowCount(); i++) {
                int marks = 60 + (int)(Math.random() * 35);
                tempModel.setValueAt(String.valueOf(marks), i, 3);
            }
            JOptionPane.showMessageDialog(dialog, "Sample marks filled!");
        });

        saveBtn.addActionListener(e -> {
            int saved = 0;
            for (int i = 0; i < tempModel.getRowCount(); i++) {
                int studentId = (int) tempModel.getValueAt(i, 0);
                String marksStr = (String) tempModel.getValueAt(i, 3);
                if (marksStr != null && !marksStr.trim().isEmpty()) {
                    try {
                        double marks = Double.parseDouble(marksStr);
                        if (marks >= 0 && marks <= 100) {
                            Mark mark = new Mark();
                            mark.setStudentId(studentId);
                            mark.setCourseId(courseId);
                            mark.setExamType(examType);
                            mark.setMarksObtained(marks);
                            mark.setMaxMarks(100);
                            if (markDAO.uploadMark(mark)) {
                                saved++;
                            }
                        }
                    } catch (NumberFormatException ex) {
                        // Skip invalid marks
                    }
                }
            }
            JOptionPane.showMessageDialog(dialog, "✅ Saved " + saved + " marks successfully!");
            dialog.dispose();
            loadMarksSummary(courseId, examType);
        });

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tempTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Lecture Materials Panel
    private JPanel createMaterialsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(new Color(240, 248, 255));
        selectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "Upload Lecture Materials",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(350, 30));

        JButton uploadBtn = ButtonStyleUtil.createSuccessButton("📤 Upload PDF Material");

        selectionPanel.add(new JLabel("Course: "));
        selectionPanel.add(courseCombo);
        selectionPanel.add(Box.createHorizontalStrut(20));
        selectionPanel.add(uploadBtn);

        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode() + " - " + c.getCourseName());
        }

        materialsModel = new DefaultTableModel(new String[]{"ID", "Title", "Description", "File Name", "Size", "Upload Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        materialsTable = new JTable(materialsModel);
        materialsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        materialsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        uploadBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                uploadMaterial(courseId);
            }
        });

        topPanel.add(selectionPanel, BorderLayout.NORTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);

        if (!courses.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                loadMaterials(courses.get(0).getCourseId());
            });
        }

        return panel;
    }

    private void loadMaterials(int courseId) {
        materialsModel.setRowCount(0);
        List<CourseMaterial> materials = materialDAO.getMaterialsByCourse(courseId);
        for (CourseMaterial m : materials) {
            materialsModel.addRow(new Object[]{
                    m.getMaterialId(), m.getTitle(), m.getDescription(),
                    m.getFileName(), m.getFormattedFileSize(), m.getUploadedDate()
            });
        }
    }

    private void uploadMaterial(int courseId) {
        JDialog dialog = new JDialog(this, "Upload Lecture Material", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField titleField = new JTextField(30);
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextArea descArea = new JTextArea(3, 30);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("PDF File:*"), gbc);

        gbc.gridx = 1;
        JTextField fileField = new JTextField(20);
        fileField.setEditable(false);
        panel.add(fileField, gbc);

        gbc.gridx = 2;
        JButton browseBtn = ButtonStyleUtil.createInfoButton("Browse");
        panel.add(browseBtn, gbc);

        final File[] selectedFile = {null};

        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                fileField.setText(selectedFile[0].getName());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JButton uploadBtn = ButtonStyleUtil.createSuccessButton("📤 Upload Material");
        uploadBtn.setFont(new Font("Arial", Font.BOLD, 14));

        uploadBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a title!");
                return;
            }
            if (selectedFile[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a PDF file!");
                return;
            }

            try {
                String uploadDir = "uploads/lecture_materials/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String timestamp = String.valueOf(System.currentTimeMillis());
                String newFileName = timestamp + "_" + selectedFile[0].getName();
                String filePath = uploadDir + newFileName;

                Files.copy(selectedFile[0].toPath(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

                CourseMaterial material = new CourseMaterial();
                material.setCourseId(courseId);
                material.setLecturerId(currentLecturer.getLecturerId());
                material.setTitle(titleField.getText().trim());
                material.setDescription(descArea.getText().trim());
                material.setFilePath(filePath);
                material.setFileName(selectedFile[0].getName());
                material.setFileSize(selectedFile[0].length());

                if (materialDAO.uploadMaterial(material)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Material uploaded successfully!");
                    dialog.dispose();
                    loadMaterials(courseId);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error uploading material!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        panel.add(uploadBtn, gbc);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Notices Panel
    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel createPanel = new JPanel(new BorderLayout());
        createPanel.setBackground(new Color(240, 248, 255));
        createPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "📢 Create New Notice with PDF Attachment",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Notice Title:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField titleField = new JTextField(40);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Content:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextArea contentArea = new JTextArea(4, 40);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        formPanel.add(contentScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Target Audience:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JComboBox<String> targetCombo = new JComboBox<>(new String[]{"all", "student", "lecturer", "technical_officer", "admin"});
        formPanel.add(targetCombo, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("PDF Attachment:"), gbc);

        gbc.gridx = 3;
        JTextField attachmentField = new JTextField(20);
        attachmentField.setEditable(false);
        formPanel.add(attachmentField, gbc);

        gbc.gridx = 4;
        JButton browseBtn = ButtonStyleUtil.createInfoButton("📁 Browse PDF");
        formPanel.add(browseBtn, gbc);

        final File[] selectedPdf = {null};

        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedPdf[0] = fileChooser.getSelectedFile();
                attachmentField.setText(selectedPdf[0].getName());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 5;
        JButton publishBtn = ButtonStyleUtil.createSuccessButton("📢 Publish Notice");
        publishBtn.setFont(new Font("Arial", Font.BOLD, 14));

        publishBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a notice title!");
                return;
            }
            if (contentArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter notice content!");
                return;
            }

            try {
                String attachmentPath = null;
                String attachmentName = null;

                if (selectedPdf[0] != null) {
                    String uploadDir = "uploads/notice_attachments/";
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String newFileName = "notice_" + timestamp + "_" + selectedPdf[0].getName();
                    attachmentPath = uploadDir + newFileName;
                    attachmentName = selectedPdf[0].getName();

                    Files.copy(selectedPdf[0].toPath(), Paths.get(attachmentPath), StandardCopyOption.REPLACE_EXISTING);
                }

                Notice notice = new Notice();
                notice.setTitle(titleField.getText().trim());
                notice.setContent(contentArea.getText().trim());
                notice.setTargetRole((String) targetCombo.getSelectedItem());
                notice.setCreatedBy(SessionManager.getCurrentUserId());
                notice.setAttachmentPath(attachmentPath);
                notice.setAttachmentName(attachmentName);

                if (noticeDAO.createNotice(notice)) {
                    JOptionPane.showMessageDialog(panel, "✅ Notice published successfully!");
                    titleField.setText("");
                    contentArea.setText("");
                    attachmentField.setText("");
                    selectedPdf[0] = null;
                    loadNotices();
                } else {
                    JOptionPane.showMessageDialog(panel, "Error publishing notice!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        formPanel.add(publishBtn, gbc);
        createPanel.add(formPanel, BorderLayout.CENTER);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(new Color(240, 248, 255));
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179)),
                "📋 Published Notices",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(0, 86, 179)
        ));

        noticesModel = new DefaultTableModel(new String[]{"ID", "Title", "Content", "Target", "Date", "Attachment"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        noticesTable = new JTable(noticesModel);
        noticesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        noticesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        noticesTable.setRowHeight(35);

        noticesTable.getColumnModel().getColumn(0).setMaxWidth(50);
        noticesTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        noticesTable.getColumnModel().getColumn(2).setPreferredWidth(350);
        noticesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        noticesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        noticesTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        noticesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = noticesTable.getSelectedRow();
                    if (row >= 0) {
                        String attachment = (String) noticesModel.getValueAt(row, 5);
                        if (attachment != null && !attachment.isEmpty() && !attachment.equals("None") && !attachment.equals("📎 No attachment")) {
                            downloadNoticeAttachment(row);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(noticesTable);

        JPanel noticeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        noticeButtonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshNoticesBtn = ButtonStyleUtil.createCyanButton("🔄 Refresh");
        JButton deleteNoticeBtn = ButtonStyleUtil.createDangerButton("🗑 Delete Selected");
        JButton downloadBtn = ButtonStyleUtil.createSuccessButton("📥 Download PDF");

        refreshNoticesBtn.addActionListener(e -> loadNotices());
        deleteNoticeBtn.addActionListener(e -> deleteNotice());
        downloadBtn.addActionListener(e -> {
            int row = noticesTable.getSelectedRow();
            if (row >= 0) {
                String attachment = (String) noticesModel.getValueAt(row, 5);
                if (attachment != null && !attachment.isEmpty() && !attachment.equals("None") && !attachment.equals("📎 No attachment")) {
                    downloadNoticeAttachment(row);
                } else {
                    JOptionPane.showMessageDialog(panel, "No PDF attachment available!");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a notice!");
            }
        });

        noticeButtonPanel.add(refreshNoticesBtn);
        noticeButtonPanel.add(deleteNoticeBtn);
        noticeButtonPanel.add(downloadBtn);

        listPanel.add(noticeButtonPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 200));
        JLabel infoLabel = new JLabel("💡 Tip: Double-click on a notice with 📎 to download the attached PDF file");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoLabel);
        listPanel.add(infoPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createPanel, listPanel);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.4);

        panel.add(splitPane, BorderLayout.CENTER);

        loadNotices();

        return panel;
    }

    private void loadNotices() {
        noticesModel.setRowCount(0);
        List<Notice> notices = noticeDAO.getAllNotices();
        for (Notice n : notices) {
            boolean hasAttachment = n.getAttachmentPath() != null && !n.getAttachmentPath().isEmpty();
            String attachmentDisplay = hasAttachment ? "📎 " + n.getAttachmentName() : "📎 No attachment";
            String contentPreview = n.getContent().length() > 100 ? n.getContent().substring(0, 100) + "..." : n.getContent();

            noticesModel.addRow(new Object[]{
                    n.getNoticeId(),
                    n.getTitle(),
                    contentPreview,
                    n.getTargetRole(),
                    n.getCreatedDate() != null ? n.getCreatedDate().toString() : "",
                    attachmentDisplay
            });
        }

        if (notices.isEmpty()) {
            noticesModel.addRow(new Object[]{"-", "No Notices", "No notices published yet", "-", "-", "📎 No attachment"});
        }
    }

    private void deleteNotice() {
        int row = noticesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete!");
            return;
        }

        int noticeId = (int) noticesModel.getValueAt(row, 0);
        if (noticeId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this notice?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (noticeDAO.deleteNotice(noticeId)) {
                JOptionPane.showMessageDialog(this, "Notice deleted!");
                loadNotices();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting notice!");
            }
        }
    }

    private void downloadNoticeAttachment(int row) {
        try {
            int noticeId = (int) noticesModel.getValueAt(row, 0);
            List<Notice> notices = noticeDAO.getAllNotices();
            Notice selectedNotice = null;
            for (Notice n : notices) {
                if (n.getNoticeId() == noticeId) {
                    selectedNotice = n;
                    break;
                }
            }

            if (selectedNotice == null || selectedNotice.getAttachmentPath() == null) {
                JOptionPane.showMessageDialog(this, "No PDF attachment found!");
                return;
            }

            File sourceFile = new File(selectedNotice.getAttachmentPath());
            if (!sourceFile.exists()) {
                JOptionPane.showMessageDialog(this, "File not found on server!");
                return;
            }

            String fileName = selectedNotice.getAttachmentName();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(fileName));
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File destFile = fileChooser.getSelectedFile();
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "PDF downloaded: " + destFile.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ============ EDIT PROFILE ============
    private void editProfile() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(new Color(240, 248, 255));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("✏️ Edit Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 86, 179));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        User currentUser = SessionManager.getCurrentUser();

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(currentUser.getFullName(), 25);
        nameField.setEditable(false);
        nameField.setBackground(new Color(200, 200, 200));
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail(), 25);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(currentUser.getPhone(), 25);
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        JTextField deptField = new JTextField(currentLecturer.getDepartment(), 25);
        deptField.setEditable(false);
        deptField.setBackground(new Color(200, 200, 200));
        panel.add(deptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Contact Details:"), gbc);
        gbc.gridx = 1;
        JTextArea contactArea = new JTextArea(currentUser.getContactDetails(), 3, 25);
        contactArea.setLineWrap(true);
        JScrollPane contactScroll = new JScrollPane(contactArea);
        panel.add(contactScroll, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton saveBtn = ButtonStyleUtil.createSuccessButton("💾 Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setPreferredSize(new Dimension(140, 35));

        JButton cancelBtn = ButtonStyleUtil.createDangerButton("❌ Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cancelBtn.setPreferredSize(new Dimension(140, 35));

        saveBtn.addActionListener(e -> {
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setContactDetails(contactArea.getText());

            if (userDAO.updateUser(currentUser)) {
                JOptionPane.showMessageDialog(dialog, "✅ Profile updated successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Error updating profile!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
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
                        "Version: 2.0\n" +
                        "Lecturer Module\n\n" +
                        "👨‍🏫 Welcome " + currentLecturer.getFullName() + "\n\n" +
                        "Features:\n" +
                        "• Manage Student Records\n" +
                        "• Track Attendance\n" +
                        "• Upload Marks with Grading\n" +
                        "• Upload Lecture Materials (PDF)\n" +
                        "• Publish Notices with PDF Attachments\n" +
                        "• Download Notice PDFs\n\n" +
                        "© 2024 Faculty of Technology\n" +
                        "All Rights Reserved\n" +
                        "═══════════════════════════════════════",
                "About System",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
package ui;

import dao.*;
import model.*;
import utils.SessionManager;
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
import java.sql.Time;
import java.util.List;

public class TechnicalOfficerUI extends JFrame {
    private UserDAO userDAO;
    private AttendanceDAO attendanceDAO;
    private MedicalDAO medicalDAO;
    private NoticeDAO noticeDAO;
    private TimetableDAO timetableDAO;
    private CourseDAO courseDAO;

    private JTable attendanceTable, medicalTable, noticesTable, timetableTable;
    private DefaultTableModel attendanceModel, medicalModel, noticesModel, timetableModel;
    private TechnicalOfficer currentOfficer;

    // Color constants for buttons
    private final Color SUCCESS_GREEN = new Color(39, 174, 96);
    private final Color SUCCESS_GREEN_HOVER = new Color(29, 132, 73);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color DANGER_RED_HOVER = new Color(192, 57, 43);
    private final Color INFO_BLUE = new Color(52, 152, 219);
    private final Color INFO_BLUE_HOVER = new Color(36, 113, 163);
    private final Color WARNING_ORANGE = new Color(230, 126, 34);
    private final Color WARNING_ORANGE_HOVER = new Color(184, 101, 27);
    private final Color PURPLE = new Color(155, 89, 182);
    private final Color PURPLE_HOVER = new Color(124, 71, 145);
    private final Color CYAN = new Color(26, 188, 156);
    private final Color CYAN_HOVER = new Color(22, 160, 133);
    private final Color DARK_BLUE = new Color(0, 86, 179);

    public TechnicalOfficerUI() {
        userDAO = new UserDAO();
        attendanceDAO = new AttendanceDAO();
        medicalDAO = new MedicalDAO();
        noticeDAO = new NoticeDAO();
        timetableDAO = new TimetableDAO();
        courseDAO = new CourseDAO();

        loadCurrentOfficer();
        initComponents();
        LogoUtil.setFrameIcon(this);
    }

    private void loadCurrentOfficer() {
        User user = SessionManager.getCurrentUser();
        List<TechnicalOfficer> officers = userDAO.getAllTechnicalOfficers();
        for (TechnicalOfficer o : officers) {
            if (o.getUserId() == user.getUserId()) {
                currentOfficer = o;
                break;
            }
        }
    }

    // Method to style buttons
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void initComponents() {
        setTitle("Technical Officer Dashboard - Faculty of Technology Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);

        // ============ MENU BAR ============
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(DARK_BLUE);
        menuBar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JMenu profileMenu = new JMenu("👤 PROFILE");
        profileMenu.setForeground(Color.WHITE);
        profileMenu.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuItem editProfileItem = new JMenuItem("✏️ Edit Profile");
        editProfileItem.setBackground(SUCCESS_GREEN);
        editProfileItem.setForeground(Color.WHITE);
        editProfileItem.setFont(new Font("Arial", Font.BOLD, 12));
        editProfileItem.addActionListener(e -> editProfile());

        JMenuItem logoutItem = new JMenuItem("🚪 Logout");
        logoutItem.setBackground(DANGER_RED);
        logoutItem.setForeground(Color.WHITE);
        logoutItem.setFont(new Font("Arial", Font.BOLD, 12));
        logoutItem.addActionListener(e -> logout());

        profileMenu.add(editProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);

        JMenu helpMenu = new JMenu("❓ HELP");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuItem aboutItem = new JMenuItem("ℹ️ About System");
        aboutItem.setBackground(PURPLE);
        aboutItem.setForeground(Color.WHITE);
        aboutItem.setFont(new Font("Arial", Font.BOLD, 12));
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(aboutItem);

        menuBar.add(profileMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createHorizontalGlue());

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        userInfoPanel.setBackground(DARK_BLUE);

        JLabel userIconLabel = new JLabel("🔧");
        userIconLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userIconLabel.setForeground(Color.YELLOW);

        JLabel userInfoLabel = new JLabel(" " + currentOfficer.getFullName() + " ");
        userInfoLabel.setForeground(Color.BLACK);
        userInfoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userInfoLabel.setBackground(new Color(255, 193, 7));
        userInfoLabel.setOpaque(true);
        userInfoLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        userInfoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        userInfoPanel.add(userIconLabel);
        userInfoPanel.add(userInfoLabel);
        menuBar.add(userInfoPanel);

        setJMenuBar(menuBar);

        // ============ TABBED PANE ============
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📋 Attendance Management", createAttendanceManagementPanel());
        tabbedPane.addTab("🏥 Medical Records", createMedicalManagementPanel());
        tabbedPane.addTab("📅 Timetable Management", createTimetableManagementPanel());
        tabbedPane.addTab("📢 Notices", createNoticesPanel());

        add(tabbedPane);

        // ============ STATUS BAR ============
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JPanel statusLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLeft.setBackground(new Color(52, 73, 94));
        JLabel statusLabel = new JLabel("✅ Logged in as: " + currentOfficer.getFullName() + " (Technical Officer) | Department: " + currentOfficer.getDepartment());
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

    // ============ ATTENDANCE MANAGEMENT PANEL ============
    private JPanel createAttendanceManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 248, 255));

        // Mark Attendance Panel
        JPanel markPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        markPanel.setBackground(new Color(240, 248, 255));
        markPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DARK_BLUE),
                "Mark Attendance",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                DARK_BLUE
        ));

        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(250, 30));
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(250, 30));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"theory", "practical"});
        JSpinner sessionSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        sessionSpinner.setPreferredSize(new Dimension(60, 30));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"present", "absent"});

        JButton markBtn = new JButton("✅ MARK ATTENDANCE");
        styleButton(markBtn, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);

        JButton initBtn = new JButton("📝 INITIALIZE 15 SESSIONS");
        styleButton(initBtn, WARNING_ORANGE, WARNING_ORANGE_HOVER);

        markPanel.add(new JLabel("Course:"));
        markPanel.add(courseCombo);
        markPanel.add(new JLabel("Student:"));
        markPanel.add(studentCombo);
        markPanel.add(new JLabel("Type:"));
        markPanel.add(typeCombo);
        markPanel.add(new JLabel("Session #:"));
        markPanel.add(sessionSpinner);
        markPanel.add(new JLabel("Status:"));
        markPanel.add(statusCombo);
        markPanel.add(markBtn);
        markPanel.add(initBtn);

        // Load courses
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode() + " - " + c.getCourseName());
        }

        // Load students
        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            studentCombo.addItem(s.getStudentId() + " - " + s.getRegistrationNo() + " - " + s.getFullName());
        }

        // Attendance Table
        attendanceModel = new DefaultTableModel(new String[]{"Student", "Reg No", "Course", "Type", "Session #", "Date", "Status"}, 0);
        attendanceTable = new JTable(attendanceModel);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 12));
        attendanceTable.setRowHeight(25);

        // Mark Attendance Action
        markBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() < 0 || studentCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(panel, "Please select course and student!");
                return;
            }

            String selectedCourse = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(selectedCourse.split(" - ")[0]);
            String selectedStudent = (String) studentCombo.getSelectedItem();
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
            String type = (String) typeCombo.getSelectedItem();
            int sessionNum = (int) sessionSpinner.getValue();
            String status = (String) statusCombo.getSelectedItem();

            Attendance attendance = new Attendance();
            attendance.setStudentId(studentId);
            attendance.setCourseId(courseId);
            attendance.setSessionType(type);
            attendance.setSessionNumber(sessionNum);
            attendance.setStatus(status);
            attendance.setSessionDate(new java.util.Date());

            if (attendanceDAO.recordAttendance(attendance)) {
                JOptionPane.showMessageDialog(panel, "✅ Attendance marked successfully!\n" +
                        "Student: " + selectedStudent.split(" - ")[2] + "\n" +
                        "Session: " + type + " " + sessionNum + "\n" +
                        "Status: " + status.toUpperCase());
                loadAllAttendance(courseId);
            } else {
                JOptionPane.showMessageDialog(panel, "❌ Error marking attendance!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Initialize 15 Sessions Action
        initBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() < 0 || studentCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(panel, "Please select course and student!");
                return;
            }

            String selectedCourse = (String) courseCombo.getSelectedItem();
            int courseId = Integer.parseInt(selectedCourse.split(" - ")[0]);
            String selectedStudent = (String) studentCombo.getSelectedItem();
            int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);

            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Initialize 15 theory and 15 practical sessions for this student?\nThis will create all attendance records as ABSENT.\n\nStudent: " + selectedStudent.split(" - ")[2] + "\nCourse: " + selectedCourse.split(" - ")[1],
                    "Confirm Initialization",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (attendanceDAO.initializeAttendance(courseId, studentId)) {
                    JOptionPane.showMessageDialog(panel, "✅ 30 sessions initialized successfully!\n" +
                            "15 Theory sessions + 15 Practical sessions created.\n\nYou can now mark attendance session by session.");
                    loadAllAttendance(courseId);
                } else {
                    JOptionPane.showMessageDialog(panel, "❌ Error initializing sessions!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // View Buttons
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewPanel.setBackground(new Color(240, 248, 255));

        JButton viewAllBtn = new JButton("📊 VIEW ALL STUDENTS");
        styleButton(viewAllBtn, INFO_BLUE, INFO_BLUE_HOVER);

        JButton viewStudentBtn = new JButton("👨‍🎓 VIEW STUDENT DETAILS");
        styleButton(viewStudentBtn, PURPLE, PURPLE_HOVER);

        JButton refreshBtn = new JButton("🔄 REFRESH");
        styleButton(refreshBtn, CYAN, CYAN_HOVER);

        viewPanel.add(viewAllBtn);
        viewPanel.add(viewStudentBtn);
        viewPanel.add(refreshBtn);

        viewAllBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                loadAllAttendance(courseId);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a course first!");
            }
        });

        viewStudentBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0 && studentCombo.getSelectedIndex() >= 0) {
                String selectedCourse = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selectedCourse.split(" - ")[0]);
                String selectedStudent = (String) studentCombo.getSelectedItem();
                int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);
                loadStudentAttendance(courseId, studentId);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select course and student!");
            }
        });

        refreshBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                loadAllAttendance(courseId);
            }
        });

        topPanel.add(markPanel);
        topPanel.add(viewPanel);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 200));
        JLabel infoLabel = new JLabel("💡 Tip: First initialize 15 sessions for each student, then mark attendance session by session (1-15)");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoLabel);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllAttendance(int courseId) {
        attendanceModel.setRowCount(0);
        List<Object[]> summaries = attendanceDAO.getBatchAttendanceSummary(courseId);

        Course course = courseDAO.getCourseById(courseId);
        String courseName = course != null ? course.getCourseCode() : "Course " + courseId;

        if (summaries.isEmpty()) {
            attendanceModel.addRow(new Object[]{"No students", "-", courseName, "-", "-", "-", "⚠️ No data"});
        } else {
            for (Object[] summary : summaries) {
                double percentage = (double) summary[4];
                String statusText;
                if (percentage > 80) {
                    statusText = "✅ Good (>80%)";
                } else if (percentage == 80) {
                    statusText = "⚠️ Exactly 80%";
                } else {
                    statusText = "❌ Needs Improvement (<80%)";
                }
                attendanceModel.addRow(new Object[]{
                        summary[1], summary[2], courseName, "All", "-", "-",
                        statusText + " (" + String.format("%.2f", percentage) + "%)"
                });
            }
        }
    }

    private void loadStudentAttendance(int courseId, int studentId) {
        attendanceModel.setRowCount(0);
        List<Attendance> attendances = attendanceDAO.getStudentCourseAttendance(studentId, courseId);

        Student student = null;
        List<Student> students = userDAO.getAllStudents();
        for (Student s : students) {
            if (s.getStudentId() == studentId) {
                student = s;
                break;
            }
        }

        if (attendances.isEmpty()) {
            attendanceModel.addRow(new Object[]{
                    student != null ? student.getFullName() : "Student " + studentId,
                    student != null ? student.getRegistrationNo() : "-",
                    "Course " + courseId, "-", "-", "-", "⚠️ No attendance records. Click 'Initialize 15 Sessions' first."
            });
        } else {
            for (Attendance a : attendances) {
                String statusIcon = a.getStatus().equals("present") ? "✅ Present" : "❌ Absent";
                attendanceModel.addRow(new Object[]{
                        a.getStudentName(), a.getRegistrationNo(), a.getCourseCode(),
                        a.getSessionType().toUpperCase(), "Session " + a.getSessionNumber(),
                        a.getSessionDate(), statusIcon
                });
            }
        }
    }

    // ============ MEDICAL MANAGEMENT PANEL ============
    private JPanel createMedicalManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshBtn = new JButton("🔄 REFRESH");
        styleButton(refreshBtn, INFO_BLUE, INFO_BLUE_HOVER);

        JButton showAllBtn = new JButton("📋 SHOW ALL MEDICALS");
        styleButton(showAllBtn, PURPLE, PURPLE_HOVER);

        buttonPanel.add(refreshBtn);
        buttonPanel.add(showAllBtn);

        medicalModel = new DefaultTableModel(new String[]{"ID", "Student", "Reg No", "Medical Date", "Reason", "Status"}, 0);
        medicalTable = new JTable(medicalModel);
        medicalTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        medicalTable.setFont(new Font("Arial", Font.PLAIN, 12));
        medicalTable.setRowHeight(30);

        refreshBtn.addActionListener(e -> loadPendingMedicals());
        showAllBtn.addActionListener(e -> loadAllMedicals());

        medicalTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = medicalTable.getSelectedRow();
                    if (row >= 0) {
                        int medicalId = (int) medicalModel.getValueAt(row, 0);
                        String status = (String) medicalModel.getValueAt(row, 5);
                        if (status.contains("Pending")) {
                            approveMedical(medicalId);
                        } else {
                            JOptionPane.showMessageDialog(panel, "This request has already been processed.");
                        }
                    }
                }
            }
        });

        loadPendingMedicals();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(medicalTable), BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 200));
        JLabel infoLabel = new JLabel("💡 Tip: Double-click on a pending medical request to approve/reject it");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoLabel);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPendingMedicals() {
        medicalModel.setRowCount(0);
        List<Medical> medicals = medicalDAO.getPendingMedicals();
        if (medicals.isEmpty()) {
            medicalModel.addRow(new Object[]{"-", "No pending requests", "-", "-", "-", "✅ All processed"});
        } else {
            for (Medical m : medicals) {
                medicalModel.addRow(new Object[]{
                        m.getMedicalId(), m.getStudentName(), m.getRegistrationNo(),
                        m.getMedicalDate(), m.getReason(), "⏳ Pending"
                });
            }
        }
    }

    private void loadAllMedicals() {
        medicalModel.setRowCount(0);
        List<Student> students = userDAO.getAllStudents();
        int count = 0;
        for (Student s : students) {
            List<Medical> medicals = medicalDAO.getStudentMedicals(s.getStudentId());
            for (Medical m : medicals) {
                String status = m.getStatus().equals("pending") ? "⏳ Pending" : (m.getStatus().equals("approved") ? "✅ Approved" : "❌ Rejected");
                medicalModel.addRow(new Object[]{
                        m.getMedicalId(), m.getStudentName(), m.getRegistrationNo(),
                        m.getMedicalDate(), m.getReason(), status
                });
                count++;
            }
        }
        if (count == 0) {
            medicalModel.addRow(new Object[]{"-", "No medical records", "-", "-", "-", "-"});
        } else {
            JOptionPane.showMessageDialog(this, "Found " + count + " medical records");
        }
    }

    private void approveMedical(int medicalId) {
        String[] options = {"✅ Approve", "❌ Reject", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Please review the medical request and make a decision:",
                "Medical Request Review",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        if (choice == 0 || choice == 1) {
            String status = choice == 0 ? "approved" : "rejected";
            String remarks = JOptionPane.showInputDialog(this, "Enter remarks (optional):");
            if (remarks == null) remarks = "";

            if (medicalDAO.updateMedicalStatus(medicalId, status, SessionManager.getCurrentUserId(), remarks)) {
                JOptionPane.showMessageDialog(this, "Medical request " + status + " successfully!");
                loadPendingMedicals();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating medical status!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============ TIMETABLE MANAGEMENT PANEL ============
    private JPanel createTimetableManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DARK_BLUE),
                "Add Timetable Entry",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                DARK_BLUE
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(200, 30));
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(c.getCourseId() + " - " + c.getCourseCode());
        }
        formPanel.add(courseCombo, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 3;
        JComboBox<String> dayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        dayCombo.setPreferredSize(new Dimension(120, 30));
        formPanel.add(dayCombo, gbc);

        gbc.gridx = 4;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 5;
        JTextField startTimeField = new JTextField("09:00", 6);
        formPanel.add(startTimeField, gbc);

        gbc.gridx = 6;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 7;
        JTextField endTimeField = new JTextField("11:00", 6);
        formPanel.add(endTimeField, gbc);

        gbc.gridx = 8;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 9;
        JTextField venueField = new JTextField("ICT Lab", 10);
        formPanel.add(venueField, gbc);

        gbc.gridx = 10;
        JButton addBtn = new JButton("➕ ADD ENTRY");
        styleButton(addBtn, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);
        addBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(panel, "Please select a course!");
                return;
            }
            try {
                String selected = (String) courseCombo.getSelectedItem();
                int courseId = Integer.parseInt(selected.split(" - ")[0]);
                String day = (String) dayCombo.getSelectedItem();
                String startTime = startTimeField.getText().trim();
                String endTime = endTimeField.getText().trim();
                String venue = venueField.getText().trim();

                Timetable tt = new Timetable();
                tt.setCourseId(courseId);
                tt.setDayOfWeek(day);
                tt.setStartTime(Time.valueOf(startTime + ":00"));
                tt.setEndTime(Time.valueOf(endTime + ":00"));
                tt.setVenue(venue);
                tt.setSessionType("theory");
                tt.setSemester(2);

                if (timetableDAO.createTimetableEntry(tt)) {
                    JOptionPane.showMessageDialog(panel, "✅ Timetable entry added successfully!");
                    startTimeField.setText("09:00");
                    endTimeField.setText("11:00");
                    venueField.setText("ICT Lab");
                    loadTimetable();
                } else {
                    JOptionPane.showMessageDialog(panel, "❌ Error adding entry!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Invalid time format! Use HH:MM (e.g., 09:00)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(addBtn, gbc);

        // Timetable Table
        timetableModel = new DefaultTableModel(new String[]{"ID", "Day", "Start", "End", "Course", "Venue"}, 0);
        timetableTable = new JTable(timetableModel);
        timetableTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        timetableTable.setFont(new Font("Arial", Font.PLAIN, 12));
        timetableTable.setRowHeight(25);

        JButton refreshTtBtn = new JButton("🔄 REFRESH TIMETABLE");
        styleButton(refreshTtBtn, INFO_BLUE, INFO_BLUE_HOVER);
        refreshTtBtn.addActionListener(e -> loadTimetable());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 248, 255));
        bottomPanel.add(refreshTtBtn);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(timetableTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadTimetable();

        return panel;
    }

    private void loadTimetable() {
        timetableModel.setRowCount(0);
        List<Timetable> timetables = timetableDAO.getTimetableBySemester(2);
        if (timetables.isEmpty()) {
            timetableModel.addRow(new Object[]{"-", "No entries", "-", "-", "Add new entries", "-"});
        } else {
            for (Timetable t : timetables) {
                timetableModel.addRow(new Object[]{
                        t.getTimetableId(), t.getDayOfWeek(), t.getStartTime(), t.getEndTime(),
                        t.getCourseCode(), t.getVenue()
                });
            }
        }
    }

    // ============ NOTICES PANEL ============
    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Create Notice Panel
        JPanel createPanel = new JPanel(new GridBagLayout());
        createPanel.setBackground(new Color(240, 248, 255));
        createPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DARK_BLUE),
                "Create New Notice with PDF Attachment",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                DARK_BLUE
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        createPanel.add(new JLabel("Title:*"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextField titleField = new JTextField(40);
        createPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        createPanel.add(new JLabel("Content:*"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        JTextArea contentArea = new JTextArea(4, 40);
        contentArea.setLineWrap(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        createPanel.add(contentScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        createPanel.add(new JLabel("Target Audience:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JComboBox<String> targetCombo = new JComboBox<>(new String[]{"all", "student", "lecturer", "technical_officer", "admin"});
        createPanel.add(targetCombo, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        createPanel.add(new JLabel("PDF Attachment:"), gbc);
        gbc.gridx = 3;
        JTextField attachmentField = new JTextField(20);
        attachmentField.setEditable(false);
        createPanel.add(attachmentField, gbc);

        gbc.gridx = 4;
        JButton browseBtn = new JButton("📁 BROWSE PDF");
        styleButton(browseBtn, INFO_BLUE, INFO_BLUE_HOVER);
        createPanel.add(browseBtn, gbc);

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
        JButton publishBtn = new JButton("📢 PUBLISH NOTICE");
        styleButton(publishBtn, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);
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

        createPanel.add(publishBtn, gbc);

        // Notices List Panel
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

        JScrollPane scrollPane = new JScrollPane(noticesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Published Notices"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton refreshNoticesBtn = new JButton("🔄 REFRESH");
        styleButton(refreshNoticesBtn, CYAN, CYAN_HOVER);

        JButton deleteNoticeBtn = new JButton("🗑 DELETE SELECTED");
        styleButton(deleteNoticeBtn, DANGER_RED, DANGER_RED_HOVER);

        JButton downloadBtn = new JButton("📥 DOWNLOAD PDF");
        styleButton(downloadBtn, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);

        refreshNoticesBtn.addActionListener(e -> loadNotices());
        deleteNoticeBtn.addActionListener(e -> deleteNotice());
        downloadBtn.addActionListener(e -> {
            int row = noticesTable.getSelectedRow();
            if (row >= 0) {
                downloadNoticeAttachment(row);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a notice!");
            }
        });

        noticesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = noticesTable.getSelectedRow();
                    if (row >= 0) {
                        downloadNoticeAttachment(row);
                    }
                }
            }
        });

        buttonPanel.add(refreshNoticesBtn);
        buttonPanel.add(deleteNoticeBtn);
        buttonPanel.add(downloadBtn);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 200));
        JLabel infoLabel = new JLabel("💡 Tip: Double-click on a notice with 📎 to download the attached PDF file");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createPanel, new JPanel());
        splitPane.setTopComponent(createPanel);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(buttonPanel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(infoPanel, BorderLayout.SOUTH);
        splitPane.setBottomComponent(listPanel);
        splitPane.setDividerLocation(400);
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
            String attachmentDisplay = hasAttachment ? "📎 " + n.getAttachmentName() : "No attachment";
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
    }

    private void deleteNotice() {
        int row = noticesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete!");
            return;
        }

        int noticeId = (int) noticesModel.getValueAt(row, 0);
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

    private void editProfile() {
        JDialog dialog = new JDialog(this, "Edit Profile", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        User currentUser = SessionManager.getCurrentUser();

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(currentUser.getFullName(), 20);
        nameField.setEditable(false);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail(), 20);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField(currentUser.getPhone(), 20);
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        JTextField deptField = new JTextField(currentOfficer.getDepartment(), 20);
        deptField.setEditable(false);
        panel.add(deptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Contact Details:"), gbc);
        gbc.gridx = 1;
        JTextArea contactArea = new JTextArea(currentUser.getContactDetails(), 3, 20);
        contactArea.setLineWrap(true);
        JScrollPane contactScroll = new JScrollPane(contactArea);
        panel.add(contactScroll, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton saveBtn = new JButton("💾 SAVE CHANGES");
        styleButton(saveBtn, SUCCESS_GREEN, SUCCESS_GREEN_HOVER);
        saveBtn.addActionListener(e -> {
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setContactDetails(contactArea.getText());

            if (userDAO.updateUser(currentUser)) {
                JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error updating profile!");
            }
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
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
                        "Technical Officer Module\n\n" +
                        "🔧 Welcome " + currentOfficer.getFullName() + "\n\n" +
                        "Features:\n" +
                        "• Mark Student Attendance (Sessions 1-15)\n" +
                        "• Initialize 15 Theory + 15 Practical Sessions\n" +
                        "• Manage Medical Requests\n" +
                        "• Full Timetable Management\n" +
                        "• Publish Notices with PDF Attachments\n" +
                        "• Download Notice PDFs\n\n" +
                        "© 2024 Faculty of Technology\n" +
                        "All Rights Reserved\n" +
                        "═══════════════════════════════════════",
                "About System",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
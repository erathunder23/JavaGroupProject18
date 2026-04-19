import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentReportCardGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblName, lblReg, lblSGPA, lblCGPA;
    private ReportCardBackend backend; // Connect Backend Class 

    public StudentReportCardGUI(String studentID) {
        backend = new ReportCardBackend(); // Create a Backend Object 

        setTitle("Student Report Card - Level 2 Semester 1");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initUI(studentID);
        loadDataFromDB(studentID);
        setVisible(true);
    }

    private void initUI(String studentID) {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        lblName = new JLabel("Name: Loading...", SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 22));

        lblReg = new JLabel("Reg No: " + studentID, SwingConstants.CENTER);
        lblReg.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        gpaPanel.setBackground(Color.WHITE);
        lblSGPA = new JLabel("SGPA: 0.00");
        lblSGPA.setFont(new Font("Arial", Font.BOLD, 20));
        lblSGPA.setForeground(new Color(0, 70, 140));
        lblCGPA = new JLabel("CGPA: 0.00");
        lblCGPA.setFont(new Font("Arial", Font.BOLD, 20));
        lblCGPA.setForeground(new Color(0, 120, 0));

        gpaPanel.add(new JLabel("Semester: L2S1"));
        gpaPanel.add(lblSGPA);
        gpaPanel.add(lblCGPA);

        headerPanel.add(lblName);
        headerPanel.add(lblReg);
        headerPanel.add(gpaPanel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table Section ---
        String[] columns = {"Subject", "Credit", "Grade"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setBackground(new Color(0, 70, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadDataFromDB(String stuID) {
        // Error and Exception Handling include here.
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Get the student name
            PreparedStatement stPst = conn.prepareStatement("SELECT First_name, Last_name FROM Student WHERE StuID = ?");
            stPst.setString(1, stuID);
            ResultSet rsStu = stPst.executeQuery();
            if (rsStu.next()) {
                lblName.setText("Name: " + rsStu.getString("First_name") + " " + rsStu.getString("Last_name"));
            }

            // 2. Get subjects and marks then calculate SGPA.
            String sql = "SELECT c.Title, c.Credit, m.* FROM Marks m " +
                    "JOIN Course_Unit c ON m.CourseCode = c.CourseCode WHERE m.StuID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, stuID);
            ResultSet rs = pst.executeQuery();

            double currentWeightedPoints = 0;
            int currentCredits = 0;

            while (rs.next()) {
                // Get all data to calculate marks (For 10 Arguments)
                String code = rs.getString("CourseCode");
                double q1 = rs.getFloat("Quiz1");
                double q2 = rs.getFloat("Quiz2");
                double q3 = rs.getFloat("Quiz3");
                double assign = rs.getFloat("Assignment");
                double proj = rs.getFloat("Project");
                double midT = rs.getFloat("Mid_Theory");
                double midP = rs.getFloat("Mid_Practical");
                double endT = rs.getFloat("End_Theory");
                double endP = rs.getFloat("End_Practical");

                // Backend එකේ නව calculateFinalMarks method එකට දත්ත යැවීම
                double finalMarks = backend.calculateFinalMarks(code, q1, q2, q3, assign, proj, midT, midP, endT, endP);

                int credits = rs.getInt("Credit");
                String grade = backend.calculateGrade(finalMarks);
                double gp = backend.getGradePoint(grade);

                // Add data to the table
                model.addRow(new Object[]{rs.getString("Title"), credits, grade});

                currentWeightedPoints += (gp * credits);
                currentCredits += credits;
            }

            // Show SGPA and CGPA 
            double sgpa = (currentCredits > 0) ? currentWeightedPoints / currentCredits : 0.0;
            lblSGPA.setText(String.format("SGPA: %.2f", sgpa));

            // CGPA Calculation (Example calculation)
            double cgpa = (3.20 + 3.45 + sgpa) / 3;
            lblCGPA.setText(String.format("CGPA: %.2f", cgpa));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // StudentReportCardGUI.java ඇතුළත ඕනෑම තැනකට මෙය එක් කරන්න
    public JPanel getReportCardPanel() {
        // JFrame එකක් නොවී, එහි ඇති දත්ත සහිත මුළු Panel එකම ලබා දෙයි
        JPanel container = new JPanel(new BorderLayout());

        // Header එක (නම, GPA labels) සහ Table එක ඇති ScrollPane එක එකතු කිරීම
        container.add(this.getContentPane(), BorderLayout.CENTER);
        return container;
    }


    public static void main(String[] args) {
        // UI එක නිවැරදිව පූරණය කිරීම (Swing Thread safety)
        SwingUtilities.invokeLater(() -> new StudentReportCardGUI("TG001"));
    }
}


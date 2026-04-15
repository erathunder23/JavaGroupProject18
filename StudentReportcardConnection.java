import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentReportcardConnection extends JFrame {

    // Database Informations
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tecmis_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Apu1723";

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblName, lblReg, lblSGPA, lblCGPA;

    public StudentReportcardConnection(String studentID) {
        setTitle("Student Report Card - Level 2 Semester 1");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

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

        loadData(studentID);
        setVisible(true);
    }

    private String calculateGrade(double finalMarks) {
        if (finalMarks >= 85) return "A+";
        if (finalMarks >= 70) return "A";
        if (finalMarks >= 65) return "A-";
        if (finalMarks >= 60) return "B+";
        if (finalMarks >= 55) return "B";
        if (finalMarks >= 50) return "B-";
        if (finalMarks >= 45) return "C+";
        if (finalMarks >= 40) return "C";
        if (finalMarks >= 35) return "C-";
        if (finalMarks >= 30) return "D+";
        if (finalMarks >= 25) return "D";
        return "E";
    }

    private double getGradePoint(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            default: return 0.0;
        }
    }

    private void loadData(String stuID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // 1. Get student name
            PreparedStatement stPst = conn.prepareStatement("SELECT First_name, Last_name FROM Student WHERE StuID = ?");
            stPst.setString(1, stuID);
            ResultSet rsStu = stPst.executeQuery();
            if (rsStu.next()) {
                lblName.setText("Name: " + rsStu.getString("First_name") + " " + rsStu.getString("Last_name"));
            }

            // 2. calculate current semester gpa
            String sql = "SELECT c.Title, c.Credit, m.* FROM Marks m " +
                    "JOIN Course_Unit c ON m.CourseCode = c.CourseCode WHERE m.StuID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, stuID);
            ResultSet rs = pst.executeQuery();

            double currentWeightedPoints = 0;
            int currentCredits = 0;

            while (rs.next()) {
                double ca = rs.getFloat("Quiz1") + rs.getFloat("Quiz2") + rs.getFloat("Quiz3") +
                        rs.getFloat("Assignment") + rs.getFloat("Mid_Theory") + rs.getFloat("Mid_Practical");
                double finalMarks = ca + rs.getFloat("End_Theory") + rs.getFloat("End_Practical");

                int credits = rs.getInt("Credit");
                String grade = calculateGrade(finalMarks);
                double gp = getGradePoint(grade);

                model.addRow(new Object[]{rs.getString("Title"), credits, grade});

                currentWeightedPoints += (gp * credits);
                currentCredits += credits;
            }

            double sgpaL2S1 = (currentCredits > 0) ? currentWeightedPoints / currentCredits : 0.0;
            lblSGPA.setText(String.format("SGPA: %.2f", sgpaL2S1));

            // 3. CGPA Calculation (Average of Semester 1, 2, and 3)
            // Here, we assume the SGPA values of previous semesters are obtained from the Database or provided as follows
            // According to your requirement: (SGPA_S1 + SGPA_S2 + SGPA_S3) / 3

            double sgpaL1S1 = 3.20; // Example Values
            double sgpaL1S2 = 3.45;

            double cgpa = (sgpaL1S1 + sgpaL1S2 + sgpaL2S1) / 3;
            lblCGPA.setText(String.format("CGPA: %.2f", cgpa));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new StudentReportcardConnection("TG001");
    }
}

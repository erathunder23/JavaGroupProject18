import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentReportCard extends JFrame {

    public StudentReportCard() {
        setTitle("Student Report Card");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Student Info Section
        JPanel studentInfoPanel = new JPanel(new GridLayout(2, 1, 0, 10)); // Added vertical gap
        studentInfoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name: Kasun Kalhara", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel regLabel = new JLabel("Reg No: TG001", SwingConstants.CENTER);
        regLabel.setFont(new Font("Arial", Font.BOLD, 18));

        studentInfoPanel.add(nameLabel);
        studentInfoPanel.add(regLabel);
        add(studentInfoPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Grades & GPA", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        contentPanel.add(title, BorderLayout.NORTH);

        // GPA Info
        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        gpaPanel.setBackground(Color.WHITE);

        JLabel semesterLabel = new JLabel("Semester: 1");
        semesterLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel sgpaLabel = new JLabel("SGPA: 3.76");
        sgpaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        sgpaLabel.setForeground(new Color(0, 70, 140)); // Dark blue for visibility

        JLabel cgpaLabel = new JLabel("CGPA: 3.55");
        cgpaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        cgpaLabel.setForeground(new Color(0, 120, 0)); // Dark green for visibility

        gpaPanel.add(semesterLabel);
        gpaPanel.add(sgpaLabel);
        gpaPanel.add(cgpaLabel);

        contentPanel.add(gpaPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table for Subjects
        String[] columns = {"Subject", "Credit", "Grade"};
        Object[][] data = {
                {"Mathematics", 3, "A"},
                {"Computer Science", 4, "B+"},
                {"English", 2, "A-"},
                {"Physics", 3, "A"},
                {"Total Credits", 12, "44.5"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(0, 70, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new StudentReportCard();
    }
}

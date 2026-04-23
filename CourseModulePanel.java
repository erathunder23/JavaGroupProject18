import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CourseModulePanel extends JPanel {
    private Color pureBlue = new Color(0, 51, 204);
    private Color bg = Color.WHITE;
    private JTable table;
    private DefaultTableModel model;

    private final String URL = "jdbc:mysql://localhost:3306/tecmis_db";
    private final String USER = "root";
    private final String PASS = "Apu1723";

    public CourseModulePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bg);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 1. Blue Main Label
        JLabel title = new JLabel("Course Module Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(pureBlue);
        add(title, BorderLayout.NORTH);

        String[] cols = {"Module ID", "Module Name", "Credits", "Type", "Hours"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        setupTableAlignmentAndColor();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(pureBlue, 2)); // Thicker blue border
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // 2. Blue Button
        JButton refreshBtn = new JButton("Load / Refresh Modules");
        styleButton(refreshBtn);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(bg);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadModules());
    }

    private void setupTableAlignmentAndColor() {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(220, 230, 250)); // Light blue grid lines

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(pureBlue);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));

        // Blue Center Alignment (For IDs and Numbers)
        DefaultTableCellRenderer blueCenterRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                comp.setForeground(pureBlue); // Text color blue
                setHorizontalAlignment(JLabel.CENTER);
                return comp;
            }
        };

        // Standard Left Alignment (For Name and Type)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Apply to columns
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(blueCenterRenderer); // ID in Blue
        columnModel.getColumn(1).setCellRenderer(leftRenderer);       // Name
        columnModel.getColumn(2).setCellRenderer(blueCenterRenderer); // Credits in Blue
        columnModel.getColumn(3).setCellRenderer(leftRenderer);       // Type
        columnModel.getColumn(4).setCellRenderer(blueCenterRenderer); // Hours in Blue

        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(80);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(pureBlue);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadModules() {
        model.setRowCount(0);
        String sql = "SELECT ModuleID, Module_Name, Credit, Type, Hours FROM Module";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ModuleID"),
                        rs.getString("Module_Name"),
                        rs.getInt("Credit"),
                        rs.getString("Type"),
                        rs.getInt("Hours")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        JFrame frame = new JFrame("Module Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.add(new CourseModulePanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


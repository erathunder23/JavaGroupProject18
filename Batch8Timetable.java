import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Batch8Timetable extends JPanel {
    // Dark Blue in the DashBoard
    private Color dashboardBlue = new Color(52, 94, 141);
    private Color bg = Color.WHITE;
    private JTable table;
    private DefaultTableModel model;
    private JButton refreshBtn;

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tecmis_db";
        String user = "root";
        String password = "Apu1723";
        return DriverManager.getConnection(url, user, password);
    }

    public Batch8Timetable() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bg);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Heading Setup
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(bg);

        JLabel title = new JLabel("Batch 8 Time Table", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(dashboardBlue); // Topic in Blue Color

        JLabel subTitle = new JLabel("Department of ICT", JLabel.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subTitle.setForeground(Color.DARK_GRAY);

        headerPanel.add(title);
        headerPanel.add(subTitle);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Table Setup
        String[] cols = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(70);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(200, 200, 200));

        // Header in Blue Colour
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true); // This one is compalsary
        header.setBackground(dashboardBlue);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(0, 50));

        // 3. Table Renderer (Colour Label in blue colour)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(JLabel.CENTER);

                if (isSelected) {
                    c.setBackground(dashboardBlue);
                    c.setForeground(Color.WHITE);
                } else {
                    String text = (value != null) ? value.toString().toUpperCase() : "";

                    if (text.contains("INTERVAL")) {
                        c.setBackground(new Color(40, 40, 40));
                        c.setForeground(Color.WHITE);
                    } else if (column > 0 && !text.isEmpty()) {
                        c.setBackground(new Color(230, 240, 255)); // Light Blue Colour
                        c.setForeground(dashboardBlue);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(dashboardBlue, 2)); // Blue Colour Border
        add(scroll, BorderLayout.CENTER);

        // 4. Refresh Button (in dark blue)
        refreshBtn = new JButton("LOAD TIMETABLE");
        refreshBtn.setBackground(dashboardBlue);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setOpaque(true); // to show blue colour
        refreshBtn.setBorderPainted(false); // To good look of the Design
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshBtn.setPreferredSize(new Dimension(250, 45));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(bg);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadTimetableData());
    }

    private void loadTimetableData() {
        model.setRowCount(0);
        String sql = "SELECT Time_Slot, Monday, Tuesday, Wednesday, Thursday, Friday FROM timetable WHERE batch='8'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Time_Slot"), rs.getString("Monday"), rs.getString("Tuesday"),
                        rs.getString("Wednesday"), rs.getString("Thursday"), rs.getString("Friday")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame f = new JFrame("LMS Timetable");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1100, 750);
        f.add(new Batch8Timetable());
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

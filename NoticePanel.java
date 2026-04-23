import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class NoticePanel extends JPanel {
    // Select colours
    private Color bg = Color.WHITE;
    private Color pureBlue = new Color(0, 0, 255); // Dark Blue
    private Color textColor = Color.BLACK;

    private JTable table;
    private DefaultTableModel model;

    // Database Connection
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tecmis_db";
        String user = "root";
        String password = "Apu1723";
        return DriverManager.getConnection(url, user, password);
    }

    public NoticePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bg);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Main label in Blue color
        JLabel title = new JLabel("University Notices & Announcements");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(pureBlue);
        add(title, BorderLayout.NORTH);

        //  Creat Table
        String[] cols = {"Notice ID", "Title", "Description", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(textColor);

        // 2. Make table Grid in to blue colour
        table.setGridColor(pureBlue);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // 3. Table Header in blue color and letter in white color
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(pureBlue);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 40));

        // 4. Table border in blue colour
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(pureBlue, 2));
        add(scroll, BorderLayout.CENTER);

        // 5. Refresh Button in blue color
        JButton refreshBtn = new JButton("Refresh Notices");
        refreshBtn.setBackground(pureBlue);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setPreferredSize(new Dimension(160, 45));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(bg);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadNotices());

        loadNotices();
    }

    private void loadNotices() {
        model.setRowCount(0);
        //  column names: NoticeID, Title, Description, Date
        String sql = "SELECT NoticeID, Title, Description, Date FROM notice ORDER BY Date DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("NoticeID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getDate("Date")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    // Main Method එක
    public static void main(String[] args) {
        // Windows Look and Feel on behalf of use CrossPlatform to display blue color clearly 
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}

        JFrame frame = new JFrame("Notice Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.add(new NoticePanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class StudentDashboards extends JFrame {
    private JPanel mainPanel;
    private Image profileImage = null; // පින්තූරය තබා ගැනීමට variable එකක්

    public StudentDashboards() {
        setTitle("Student LMS Dashboard");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = new JPanel(new GridLayout(7, 1, 5, 5));
        sidebar.setBackground(new Color(40, 80, 120));
        sidebar.setPreferredSize(new Dimension(200, 600));

        String[] sections = {"Profile", "Attendance", "Medical", "Courses", "Grades & GPA", "Timetable", "Notices"};
        for (String sec : sections) {
            JButton btn = new JButton(sec);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(60, 100, 150));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            sidebar.add(btn);
            btn.addActionListener(e -> switchPanel(sec));
        }
        add(sidebar, BorderLayout.WEST);

        // Main Content Area with CardLayout
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(createProfilePanel(), "Profile");

        // අන් පැනල් සඳහා (Placeholders)
        for(int i = 1; i < sections.length; i++) {
            mainPanel.add(new JLabel(sections[i] + " Details (View Only)", JLabel.CENTER), sections[i]);
        }

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Circular Photo Display
        JLabel profilePicDisplay = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 4;
                Shape circle = new Ellipse2D.Double(2, 2, size, size);

                // පින්තූරය රවුමට කොටු කිරීම (Clipping)
                g2.setClip(circle);

                if (profileImage != null) {
                    g2.drawImage(profileImage, 2, 2, size, size, this);
                } else {
                    g2.setColor(new Color(230, 230, 230));
                    g2.fill(circle);
                    g2.setColor(Color.GRAY);
                    g2.drawString("No Photo", getWidth()/2 - 30, getHeight()/2 + 5);
                }

                g2.setClip(null);
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.draw(circle);
                g2.dispose();
            }
        };
        profilePicDisplay.setPreferredSize(new Dimension(150, 150));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        panel.add(profilePicDisplay, gbc);

        // 2. Photo Update Button
        JButton photoBtn = new JButton("Update Photo");
        photoBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) { // මෙහි APPROVE_OPTION නිවැරදිය
                try {
                    File file = fileChooser.getSelectedFile();
                    profileImage = ImageIO.read(file);
                    profilePicDisplay.repaint(); // Screen එක refresh කිරීමට
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading image!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy = 1; panel.add(photoBtn, gbc);

        // 3. Information Fields
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(new JLabel("Kasun Kalhara"), gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Reg No:"), gbc);
        gbc.gridx = 1;
        JLabel regLabel = new JLabel("TG001");
        panel.add(regLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField("kasun@gmail.com", 20);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        JTextField phoneField = new JTextField("078-4589632", 20);
        panel.add(phoneField, gbc);

        // 4. Update Button with Validations
        JButton updateBtn = new JButton("Update Contact Details");
        updateBtn.setBackground(new Color(30, 60, 90));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setPreferredSize(new Dimension(200, 35));

        updateBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            // Validation Logic
            if (email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(panel, "Invalid Email format!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!phone.matches("\\d+") || phone.length() < 10) {
                JOptionPane.showMessageDialog(panel, "Phone number must be at least 10 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Profile updated for " + regLabel.getText(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(updateBtn, gbc);

        return panel;
    }

    private void switchPanel(String name) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboards().setVisible(true));
    }
}

package ui;

import dao.UserDAO;
import model.User;
import utils.SessionManager;
import utils.ButtonStyleUtil;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginUI() {
        userDAO = new UserDAO();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Faculty of Technology Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("FACULTY OF TECHNOLOGY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(ButtonStyleUtil.PRIMARY_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        centerPanel.add(subtitleLabel, gbc);

        // Spacer
        gbc.gridy = 2;
        centerPanel.add(Box.createVerticalStrut(20), gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        centerPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        centerPanel.add(passwordField, gbc);

        // Login button - BRIGHT GREEN
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = ButtonStyleUtil.createSuccessButton("🔓 LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.addActionListener(e -> performLogin());
        centerPanel.add(loginButton, gbc);

        // Demo Accounts
        gbc.gridy = 6;
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            dispose();

            switch (user.getRole()) {
                case "admin":
                    new AdminUI().setVisible(true);
                    break;
                case "lecturer":
                    new LecturerUI().setVisible(true);
                    break;
                case "student":
                    new StudentUI().setVisible(true);
                    break;
                case "technical_officer":
                    new TechnicalOfficerUI().setVisible(true);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
            passwordField.setText("");
        }
    }
}

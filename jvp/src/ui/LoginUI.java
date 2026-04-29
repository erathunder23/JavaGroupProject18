package ui;

import dao.UserDAO;
import model.User;
import utils.SessionManager;
import utils.ButtonStyleUtil;
import utils.LogoUtil;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public LoginUI() {
        userDAO = new UserDAO();
        initComponents();
        LogoUtil.setFrameIcon(this);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Faculty of Technology Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("FACULTY OF TECHNOLOGY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(ButtonStyleUtil.PRIMARY_BLUE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        centerPanel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        centerPanel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridwidth = 1; gbc.gridy = 3; gbc.gridx = 0;
        centerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        centerPanel.add(usernameField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        centerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        centerPanel.add(passwordField, gbc);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginButton = ButtonStyleUtil.createPrimaryButton("🔓 LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.addActionListener(e -> performLogin());
        centerPanel.add(loginButton, gbc);

        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Faculty of Technology Management System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
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
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role: " + user.getRole(),
                            "Access Error", JOptionPane.ERROR_MESSAGE);
                    SessionManager.clearSession();
                    new LoginUI();
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}

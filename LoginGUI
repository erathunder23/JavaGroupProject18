import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginGUI() {
        setTitle("TECMIS Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("STUDENT LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        add(lblTitle, g);

        // Username
        g.gridwidth = 1; g.gridy = 1;
        add(new JLabel("Student ID:"), g);
        txtUser = new JTextField(15);
        g.gridx = 1; add(txtUser, g);

        // Password
        g.gridx = 0; g.gridy = 2;
        add(new JLabel("Password:"), g);
        txtPass = new JPasswordField(15);
        g.gridx = 1; add(txtPass, g);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(52, 94, 141));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        g.insets = new Insets(20, 10, 10, 10);
        add(btnLogin, g);

        // Button Action
        btnLogin.addActionListener(e -> verifyLogin());
    }

    private void verifyLogin() {
        String id = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        try (Connection conn = DBManager.getConnection()) {
            String sql = "SELECT * FROM Student WHERE StuID=? AND Password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, pass);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");

                this.dispose(); //Close Login window 

                //Open a Dashboard (By passing ID)
                new StudentDashboard(id).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid ID or Password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}


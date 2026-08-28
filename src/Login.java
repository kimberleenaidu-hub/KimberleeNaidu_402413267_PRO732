import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    JTextField username = new JTextField();
    JPasswordField password = new JPasswordField();

    public Login() {
        setTitle("HealthFirst Pharmacy - Login");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(5, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        JLabel title = new JLabel("HEALTHFIRST PHARMACY", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton login = new JButton("Login");
        p.add(title); p.add(new JLabel("Username")); p.add(username);
        p.add(new JLabel("Password")); p.add(password);
        add(p, BorderLayout.CENTER);
        add(login, BorderLayout.SOUTH);
        login.addActionListener(e -> authenticate());
    }

    void authenticate() {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.getText().trim());
            ps.setString(2, new String(password.getPassword()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("role"), rs.getString("full_name"));
                dispose();
                if (u.role.equals("Admin")) new AdminDashboard(u).setVisible(true);
                else new CashierDashboard(u).setVisible(true);
            } else JOptionPane.showMessageDialog(this, "Invalid username or password.");
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

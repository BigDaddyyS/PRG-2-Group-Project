package greenpasteures.gui;

import greenpasteures.database.StaffDAO;
import greenpasteures.models.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField    usernameField;
    private JPasswordField passwordField;
    private JLabel        errorLabel;
    private JButton       loginBtn;

    // constructor
    public LoginFrame() {
        setTitle("Green Pastures Farm - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 340);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    // builds the login screen
    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 245, 240));
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 210)),
            new EmptyBorder(28, 32, 28, 32)
        ));

        JLabel title = new JLabel("Green Pastures Farm");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.addActionListener(e -> handleLogin());

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn = new JButton("Sign in");
        loginBtn.setBackground(new Color(59, 109, 17));
        loginBtn.setForeground(Color.GREEN);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> handleLogin());

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);

        root.add(card);
    }

    // handles the login when the button is clicked
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Signing in...");

        StaffDAO dao = new StaffDAO();
        Staff staff  = dao.authenticate(username, password);

        if (staff != null) {
            new MainFrame(staff).setVisible(true);
            dispose();
        } else {
            errorLabel.setText("Incorrect username or password.");
            passwordField.setText("");
            loginBtn.setEnabled(true);
            loginBtn.setText("Sign in");
        }
    }
}
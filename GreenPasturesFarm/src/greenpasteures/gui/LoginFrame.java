package greenpasteures.gui;

import greenpasteures.database.StaffDAO;
import greenpasteures.models.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JButton        loginBtn;
    private JButton        signUpBtn;

    // constructor
    public LoginFrame() {
        setTitle("Green Pastures Farm - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
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

        // logo and title
        JLabel logo = new JLabel("\uD83C\uDF3F Green Pastures Farm");
        logo.setFont(new Font("SansSerif", Font.BOLD, 15));
        logo.setForeground(new Color(39, 80, 10));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // username field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        // password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordField.addActionListener(e -> handleLogin());

        // error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // sign in button
        loginBtn = new JButton("Sign in");
        loginBtn.setBackground(new Color(39, 80, 10));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> handleLogin());

        // divider
        JLabel orLabel = new JLabel("Don't have an account?");
        orLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        orLabel.setForeground(Color.GRAY);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // sign up button
        signUpBtn = new JButton("Create account");
        signUpBtn.setBackground(Color.WHITE);
        signUpBtn.setForeground(new Color(39, 80, 10));
        signUpBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        signUpBtn.setFocusPainted(false);
        signUpBtn.setOpaque(true);
        signUpBtn.setContentAreaFilled(true);
        signUpBtn.setBorderPainted(true);
        signUpBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 80, 10)),
            new EmptyBorder(4, 12, 4, 12)
        ));
        signUpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpBtn.addActionListener(e -> {
            new SignUpFrame(this).setVisible(true);
            setVisible(false);
        });

        // add everything to the card
        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(orLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(signUpBtn);

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
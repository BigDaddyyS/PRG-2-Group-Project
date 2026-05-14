package greenpasteures.gui;

import greenpasteures.database.StaffDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignUpFrame extends JFrame {

    private JFrame         parentFrame;
    private JTextField     firstNameField;
    private JTextField     lastNameField;
    private JTextField     phoneField;
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel         errorLabel;

    // constructor - receives the login frame so we can go back to it
    public SignUpFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Green Pastures Farm - Create Account");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(420, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        // when the user closes this window go back to login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                goBackToLogin();
            }
        });

        buildUI();
    }

    // builds the sign up screen
    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 245, 240));
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 210)),
            new EmptyBorder(24, 32, 24, 32)
        ));

        // title
        JLabel logo = new JLabel("\uD83C\uDF3F Green Pastures Farm");
        logo.setFont(new Font("SansSerif", Font.BOLD, 15));
        logo.setForeground(new Color(39, 80, 10));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Create a new staff account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // note about role
        JLabel roleNote = new JLabel("New accounts are registered as STAFF role.");
        roleNote.setFont(new Font("SansSerif", Font.ITALIC, 11));
        roleNote.setForeground(new Color(150, 100, 10));
        roleNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        // form fields
        firstNameField       = new JTextField();
        lastNameField        = new JTextField();
        phoneField           = new JTextField();
        usernameField        = new JTextField();
        passwordField        = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        // error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(180, 40, 40));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // register button
        JButton registerBtn = new JButton("Create account");
        registerBtn.setBackground(new Color(39, 80, 10));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setContentAreaFilled(true);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> handleRegister());

        // back to login button
        JButton backBtn = new JButton("Back to sign in");
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(new Color(39, 80, 10));
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        backBtn.setFocusPainted(false);
        backBtn.setOpaque(true);
        backBtn.setContentAreaFilled(true);
        backBtn.setBorderPainted(true);
        backBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(4, 12, 4, 12)
        ));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackToLogin());

        // build the card
        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(4));
        card.add(roleNote);
        card.add(Box.createVerticalStrut(18));
        card.add(makeLabel("First Name"));
        card.add(Box.createVerticalStrut(3));
        card.add(firstNameField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Last Name"));
        card.add(Box.createVerticalStrut(3));
        card.add(lastNameField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Phone Number"));
        card.add(Box.createVerticalStrut(3));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(3));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(3));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(makeLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(3));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        root.add(card);
    }

    // helper to make a consistent label
    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // handles the registration when the button is clicked
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String phone     = phoneField.getText().trim();
        String username  = usernameField.getText().trim();
        String password  = new String(passwordField.getPassword());
        String confirm   = new String(confirmPasswordField.getPassword());

        // validation checks
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("First name, last name, username and password are required.");
            return;
        }

        if (username.length() < 4) {
            errorLabel.setText("Username must be at least 4 characters.");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            confirmPasswordField.setText("");
            return;
        }

        // try to register in the database
        StaffDAO dao = new StaffDAO();

        // check if username already exists
        if (dao.usernameExists(username)) {
            errorLabel.setText("Username already taken. Please choose another.");
            return;
        }

        boolean success = dao.registerStaff(firstName, lastName, phone, username, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\n" +
                "You can now sign in with your username and password.\n\n" +
                "Note: Your account has been registered as STAFF.\n" +
                "Contact the admin if you need additional access.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            errorLabel.setText("Registration failed. Please try again.");
        }
    }

    // goes back to the login screen
    private void goBackToLogin() {
        parentFrame.setVisible(true);
        dispose();
    }
}
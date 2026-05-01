package greenpasteures.gui;

import greenpasteures.models.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel     contentArea;
    private JButton    activeNavButton;
    private Staff      loggedInStaff;

    // constructor - receives the logged in staff member
    public MainFrame(Staff staff) {
        this.loggedInStaff = staff;
        setTitle("Green Pastures Farm Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 640);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        buildUI();
    }

    // builds the main frame with sidebar and content area
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(new Color(250, 250, 248));

        contentArea.add(new DashboardPanel(),             "dashboard");
        contentArea.add(new AnimalPanel(),                "animals");
        contentArea.add(new HealthPanel(loggedInStaff),   "health");
        contentArea.add(new StockPanel(),                 "stock");
        contentArea.add(new ReportPanel(loggedInStaff),   "reports");

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    // builds the green top bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(59, 109, 17));
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        bar.setPreferredSize(new Dimension(0, 48));

        JLabel appTitle = new JLabel("Green Pastures Farm");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        appTitle.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(loggedInStaff.getFullName() + "  |  " + loggedInStaff.getRole());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(200, 230, 180));

        bar.add(appTitle,  BorderLayout.WEST);
        bar.add(userLabel, BorderLayout.EAST);
        return bar;
    }

    // builds the left sidebar with navigation buttons
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 240));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 210)),
            new EmptyBorder(12, 8, 12, 8)
        ));
        sidebar.setPreferredSize(new Dimension(185, 0));

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        menuLabel.setForeground(Color.BLACK);
        menuLabel.setBorder(new EmptyBorder(0, 8, 8, 0));
        sidebar.add(menuLabel);

        JButton dashBtn   = createNavButton("Dashboard",      "dashboard");
        JButton animalBtn = createNavButton("Animals",        "animals");
        JButton healthBtn = createNavButton("Health Records", "health");
        JButton stockBtn  = createNavButton("Inventory",      "stock");
        JButton reportBtn = createNavButton("Reports",        "reports");

        sidebar.add(dashBtn);
        sidebar.add(animalBtn);
        sidebar.add(healthBtn);
        sidebar.add(stockBtn);
        sidebar.add(reportBtn);
        sidebar.add(Box.createVerticalGlue());

        JButton signOut = new JButton("Sign out");
        styleNavBtn(signOut, false);
        signOut.setForeground(new Color(180, 40, 40));
        signOut.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Sign out?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        sidebar.add(signOut);
        setActiveButton(dashBtn);
        return sidebar;
    }

    // creates a navigation button that switches the panel on click
    private JButton createNavButton(String label, String key) {
        JButton btn = new JButton(label);
        styleNavBtn(btn, false);
        btn.addActionListener(e -> {
            cardLayout.show(contentArea, key);
            setActiveButton(btn);
        });
        return btn;
    }

    // styles a navigation button
    private void styleNavBtn(JButton btn, boolean active) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        if (active) {
            btn.setBackground(new Color(234, 243, 222));
            btn.setForeground(new Color(39, 80, 10));
        } else {
            btn.setBackground(new Color(245, 245, 240));
            btn.setForeground(new Color(80, 80, 80));
        }
    }

    // highlights the currently active nav button
    private void setActiveButton(JButton btn) {
        if (activeNavButton != null) {
            styleNavBtn(activeNavButton, false);
        }
        styleNavBtn(btn, true);
        activeNavButton = btn;
    }
}
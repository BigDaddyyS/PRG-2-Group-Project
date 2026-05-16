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

    // keep a reference to the dashboard so we can refresh it
    private DashboardPanel dashboardPanel;

    private boolean isAdmin() {
        return loggedInStaff.getRole().equalsIgnoreCase("ADMIN");
    }

    public MainFrame(Staff staff) {
        this.loggedInStaff = staff;
        setTitle("Green Pastures Farm Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 640);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(new Color(250, 250, 248));

        // create dashboard and save reference so we can refresh it later
        dashboardPanel = new DashboardPanel(loggedInStaff);

        contentArea.add(dashboardPanel,                   "dashboard");
        contentArea.add(new AnimalPanel(),                "animals");
        contentArea.add(new HealthPanel(loggedInStaff),   "health");
        contentArea.add(new StockPanel(),                 "stock");

        if (isAdmin()) {
            contentArea.add(new ReportPanel(loggedInStaff), "reports");
            contentArea.add(new StaffPanel(),               "staff");
        }

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    // this method is called by AnimalPanel after adding or deleting an animal
    // it tells the dashboard to reload its data from the database
    public void refreshDashboard() {
        dashboardPanel.loadData();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(39, 80, 10));
        bar.setBorder(new EmptyBorder(10, 16, 10, 16));
        bar.setPreferredSize(new Dimension(0, 48));

        JLabel appTitle = new JLabel("\uD83C\uDF3F Green Pastures Farm");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        appTitle.setForeground(Color.WHITE);

        String roleTag = isAdmin() ? "Admin" : "Staff";
        JLabel userLabel = new JLabel(loggedInStaff.getFullName() + "  |  " + roleTag);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(200, 230, 180));

        bar.add(appTitle,  BorderLayout.WEST);
        bar.add(userLabel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 240));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 210)),
            new EmptyBorder(12, 8, 12, 8)
        ));
        sidebar.setPreferredSize(new Dimension(185, 0));

        JLabel logo = new JLabel("\uD83C\uDF3F GP Farm");
        logo.setFont(new Font("SansSerif", Font.BOLD, 14));
        logo.setForeground(new Color(39, 80, 10));
        logo.setBorder(new EmptyBorder(4, 8, 14, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(210, 210, 200));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        menuLabel.setForeground(Color.GRAY);
        menuLabel.setBorder(new EmptyBorder(0, 8, 6, 0));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuLabel);

        JButton dashBtn   = createNavButton("Dashboard",      "dashboard");
        JButton animalBtn = createNavButton("Animals",        "animals");
        JButton healthBtn = createNavButton("Health Records", "health");
        JButton stockBtn  = createNavButton("Inventory",      "stock");

        sidebar.add(dashBtn);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(animalBtn);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(healthBtn);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(stockBtn);

        if (isAdmin()) {
            sidebar.add(Box.createVerticalStrut(14));

            JLabel adminLabel = new JLabel("ADMIN ONLY");
            adminLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
            adminLabel.setForeground(new Color(150, 100, 10));
            adminLabel.setBorder(new EmptyBorder(0, 8, 6, 0));
            adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(adminLabel);

            JButton reportBtn = createNavButton("Reports",          "reports");
            JButton staffBtn  = createNavButton("Staff Management", "staff");
            sidebar.add(reportBtn);
            sidebar.add(Box.createVerticalStrut(2));
            sidebar.add(staffBtn);
        } else {
            sidebar.add(Box.createVerticalStrut(14));
            JSeparator sep2 = new JSeparator();
            sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep2.setForeground(new Color(210, 210, 200));
            sidebar.add(sep2);
            sidebar.add(Box.createVerticalStrut(8));

            JLabel lockedLabel = new JLabel("\uD83D\uDD12 Reports & Staff");
            lockedLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lockedLabel.setForeground(new Color(180, 180, 180));
            lockedLabel.setBorder(new EmptyBorder(0, 10, 2, 0));
            lockedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lockedNote = new JLabel("   Admin access only");
            lockedNote.setFont(new Font("SansSerif", Font.ITALIC, 10));
            lockedNote.setForeground(new Color(200, 200, 200));
            lockedNote.setAlignmentX(Component.LEFT_ALIGNMENT);

            sidebar.add(lockedLabel);
            sidebar.add(lockedNote);
        }

        sidebar.add(Box.createVerticalGlue());

        JButton signOut = new JButton("Sign out");
        signOut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        signOut.setHorizontalAlignment(SwingConstants.LEFT);
        signOut.setFont(new Font("SansSerif", Font.BOLD, 12));
        signOut.setBorder(new EmptyBorder(8, 10, 8, 10));
        signOut.setFocusPainted(false);
        signOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOut.setOpaque(true);
        signOut.setContentAreaFilled(true);
        signOut.setBorderPainted(false);
        signOut.setBackground(new Color(220, 53, 53));
        signOut.setForeground(Color.WHITE);

        signOut.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?", "Sign out", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        sidebar.add(signOut);
        setActiveButton(dashBtn);
        return sidebar;
    }

    private JButton createNavButton(String label, String key) {
        JButton btn = new JButton(label);
        styleNavBtn(btn, false);
        btn.addActionListener(e -> {
            cardLayout.show(contentArea, key);
            setActiveButton(btn);
        });
        return btn;
    }

    private void styleNavBtn(JButton btn, boolean active) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        if (active) {
            btn.setBackground(new Color(39, 80, 10));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        } else {
            btn.setBackground(new Color(245, 245, 240));
            btn.setForeground(new Color(80, 80, 80));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }
    }

    private void setActiveButton(JButton btn) {
        if (activeNavButton != null) styleNavBtn(activeNavButton, false);
        styleNavBtn(btn, true);
        activeNavButton = btn;
    }
}
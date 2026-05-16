package greenpasteures.gui;

import greenpasteures.database.AnimalDAO;
import greenpasteures.database.HealthDAO;
import greenpasteures.database.StockDAO;
import greenpasteures.models.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DashboardPanel extends JPanel {

    private Staff loggedInStaff;

    // keep references so we can refresh them
    private JLabel totalLabel;
    private JLabel healthyLabel;
    private JLabel sickLabel;
    private JLabel lowStockLabel;
    private DefaultTableModel recentModel;

    public DashboardPanel(Staff staff) {
        this.loggedInStaff = staff;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    private void build() {

        // top section - title and welcome message
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome back, " + loggedInStaff.getFullName() + "!   |   Today: " + LocalDate.now());
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 12));
        welcome.setForeground(Color.GRAY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        topSection.add(title);
        topSection.add(Box.createVerticalStrut(4));
        topSection.add(welcome);
        add(topSection, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setOpaque(false);

        // metric cards row
        JPanel metrics = new JPanel(new GridLayout(1, 4, 14, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // create metric value labels so we can update them later
        totalLabel    = metricValue();
        healthyLabel  = metricValue();
        sickLabel     = metricValue();
        lowStockLabel = metricValue();

        sickLabel.setForeground(new Color(180, 40, 40));
        lowStockLabel.setForeground(new Color(150, 100, 10));

        metrics.add(metricCard("Total Animals",   totalLabel,    new Color(232, 244, 230)));
        metrics.add(metricCard("Healthy",         healthyLabel,  new Color(232, 244, 230)));
        metrics.add(metricCard("Sick",            sickLabel,     new Color(250, 230, 230)));
        metrics.add(metricCard("Low Stock Items", lowStockLabel, new Color(252, 243, 220)));

        centre.add(metrics);
        centre.add(Box.createVerticalStrut(28));

        // recent animals section
        JLabel tableTitle = new JLabel("Recently Added Animals");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        tableTitle.setForeground(new Color(40, 40, 40));
        centre.add(tableTitle);
        centre.add(Box.createVerticalStrut(10));

        String[] cols = {"Tag No.", "Type", "Breed", "Status", "Date Registered"};
        recentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(recentModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(232, 244, 230));
        table.getTableHeader().setForeground(new Color(39, 80, 10));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        centre.add(scroll);

        add(centre, BorderLayout.CENTER);

        // load data for the first time
        loadData();
    }

    // this method loads all dashboard data from the database
    // it is called on first load and every time refreshDashboard() is called
    public void loadData() {
        AnimalDAO animalDAO = new AnimalDAO();
        StockDAO  stockDAO  = new StockDAO();

        // update metric cards
        totalLabel.setText(String.valueOf(animalDAO.countAll()));
        healthyLabel.setText(String.valueOf(animalDAO.countByStatus("HEALTHY")));
        sickLabel.setText(String.valueOf(animalDAO.countByStatus("SICK")));
        lowStockLabel.setText(String.valueOf(stockDAO.countLowStock()));

        // update recent animals table
        recentModel.setRowCount(0);
        List<Object[]> recent = animalDAO.getRecentAnimals(5);
        for (int i = 0; i < recent.size(); i++) {
            recentModel.addRow(recent.get(i));
        }
    }

    // creates a metric card panel with a label and a value label inside
    private JPanel metricCard(String label, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 225, 205), 1),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    // creates a default metric value label
    private JLabel metricValue() {
        JLabel lbl = new JLabel("0");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 32));
        lbl.setForeground(new Color(39, 80, 10));
        return lbl;
    }
}
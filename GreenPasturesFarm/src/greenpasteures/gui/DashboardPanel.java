package greenpasteures.gui;

import greenpasteures.database.AnimalDAO;
import greenpasteures.database.HealthDAO;
import greenpasteures.database.StockDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import greenpasteures.models.Staff;


public class DashboardPanel extends JPanel {

    // constructor
    private Staff loggedInStaff;

public DashboardPanel(Staff staff) {
    this.loggedInStaff = staff;
    setLayout(new BorderLayout());
    setBackground(new Color(250, 250, 248));
    setBorder(new EmptyBorder(20, 24, 20, 24));
    build();
}


    // builds the dashboard
    private void build() {
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        // get counts from the database
        AnimalDAO animalDAO = new AnimalDAO();
        StockDAO  stockDAO  = new StockDAO();
        HealthDAO healthDAO = new HealthDAO();

        int total    = animalDAO.countAll();
        int healthy  = animalDAO.countByStatus("HEALTHY");
        int sick     = animalDAO.countByStatus("SICK");
        int lowStock = stockDAO.countLowStock();

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setOpaque(false);

        // metric cards row
        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metrics.add(metricCard("Total animals",    String.valueOf(total),    new Color(39, 80, 10)));
        metrics.add(metricCard("Healthy",          String.valueOf(healthy),  new Color(39, 80, 10)));
        metrics.add(metricCard("Sick",             String.valueOf(sick),     new Color(180, 40, 40)));
        metrics.add(metricCard("Low stock items",  String.valueOf(lowStock), new Color(150, 100, 10)));

        centre.add(metrics);
        centre.add(Box.createVerticalStrut(24));

        // recent health records table
        JLabel tableTitle = new JLabel("Recent health records");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        centre.add(tableTitle);
        centre.add(Box.createVerticalStrut(8));

        String[] cols = {"Tag no.", "Type", "Treatment", "Attended by", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        List<Object[]> recent = healthDAO.getRecentRecords(5);
        for (int i = 0; i < recent.size(); i++) {
            model.addRow(recent.get(i));
        }

        JTable table = buildTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        centre.add(scroll);

        add(centre, BorderLayout.CENTER);
    }

    // creates a single metric card
    private JPanel metricCard(String label, String value, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(240, 242, 236));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 24));
        val.setForeground(valueColor);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(val);
        return card;
    }

    // creates a styled JTable
    private JTable buildTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 242, 236));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));
        return table;
    }
}
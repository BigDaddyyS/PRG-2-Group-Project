package greenpasteures.gui;

import greenpasteures.database.StockDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockPanel extends JPanel {

    private DefaultTableModel tableModel;
    private StockDAO dao = new StockDAO();

    private static final String[] COLUMNS = {"ID", "Item", "Category", "Qty", "Unit", "Reorder level", "Last updated", "Status"};

    // constructor
    public StockPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    // builds the stock panel
    private void build() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add item");
        styleGreenButton(addBtn);
        addBtn.addActionListener(e -> showAddDialog());

        topBar.add(title,  BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 242, 236));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        add(scroll, BorderLayout.CENTER);

        refreshTable();
    }

    // loads stock items from the database
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Object[]> rows = dao.getAllItems();
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    // shows the add stock item dialog
    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add stock item", true);
        dialog.setSize(380, 270);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 8));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField nameField    = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Feed", "Vaccine", "Medicine", "Tool"});
        JTextField qtyField     = new JTextField();
        JTextField unitField    = new JTextField();
        JTextField reorderField = new JTextField();

        form.add(new JLabel("Item name:"));    form.add(nameField);
        form.add(new JLabel("Category:"));     form.add(catBox);
        form.add(new JLabel("Quantity:"));     form.add(qtyField);
        form.add(new JLabel("Unit:"));         form.add(unitField);
        form.add(new JLabel("Reorder level:")); form.add(reorderField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save item");
        styleGreenButton(save);

        save.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Item name is required.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double qty = 0;
            double reorder = 0;
            try {
                qty    = Double.parseDouble(qtyField.getText().trim());
                reorder = Double.parseDouble(reorderField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Quantity and reorder level must be numbers.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = dao.addItem(
                nameField.getText().trim(),
                (String) catBox.getSelectedItem(),
                qty,
                unitField.getText().trim(),
                reorder
            );

            if (ok) {
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Could not save item.", "Database error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(save);

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // styles a green button
    private void styleGreenButton(JButton btn) {
        btn.setBackground(new Color(59, 109, 17));
        btn.setForeground(Color.GREEN);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
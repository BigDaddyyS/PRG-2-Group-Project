package greenpasteures.gui;

import greenpasteures.database.StockDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final StockDAO dao = new StockDAO();

    private static final String[] COLUMNS = {"ID", "Item", "Category", "Quantity", "Unit", "Last Updated"};

    public StockPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    private void build() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add Item");
        styleGreenButton(addBtn);
        addBtn.addActionListener(e -> showAddDialog());

        topBar.add(title,  BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(232, 244, 230));
        table.getTableHeader().setForeground(new Color(39, 80, 10));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        add(scroll, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton editBtn    = new JButton("Edit Selected");
        JButton restockBtn = new JButton("Quick Restock");
        JButton deleteBtn  = new JButton("Delete Selected");
        styleOutlineButton(editBtn);
        styleOutlineButton(restockBtn);
        styleRedButton(deleteBtn);

        editBtn.addActionListener(e -> showEditDialog());
        restockBtn.addActionListener(e -> showRestockDialog());
        deleteBtn.addActionListener(e -> handleDelete());

        btnBar.add(editBtn);
        btnBar.add(restockBtn);
        btnBar.add(deleteBtn);
        add(btnBar, BorderLayout.SOUTH);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Object[]> rows = dao.getAllItems();
        for (int i = 0; i < rows.size(); i++) {
            Object[] dbRow = rows.get(i);
            Object[] displayRow = new Object[6];
            displayRow[0] = dbRow[0]; // id
            displayRow[1] = dbRow[1]; // name
            displayRow[2] = dbRow[2]; // category
            displayRow[3] = dbRow[3]; // quantity
            displayRow[4] = dbRow[4]; // unit
            displayRow[5] = dbRow[6]; // last updated (skip index 5 = old reorder level)
            tableModel.addRow(displayRow);
        }
    }

    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add Stock Item", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField nameField = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"FEED", "VACCINE", "MEDICINE", "TOOL", "OTHER"});
        JTextField qtyField  = new JTextField();
        JTextField unitField = new JTextField();

        JLabel nameErr = errorLabel();
        JLabel qtyErr  = errorLabel();
        JLabel unitErr = errorLabel();

        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != ' ' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); nameErr.setText("Letters and numbers only.");
                } else { nameErr.setText(""); }
            }
        });

        qtyField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); qtyErr.setText("Numbers only, e.g. 50 or 50.5");
                } else { qtyErr.setText(""); }
            }
        });

        unitField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); unitErr.setText("Letters only, e.g. kg, L, bags");
                } else { unitErr.setText(""); }
            }
        });

        int row = 0;
        addFormRow(form, gbc, row++, "Item Name *", nameField, nameErr);
        addFormRow(form, gbc, row++, "Category *",  catBox,    null);
        addFormRow(form, gbc, row++, "Quantity *",  qtyField,  qtyErr);
        addFormRow(form, gbc, row++, "Unit *",      unitField, unitErr);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        JButton save = new JButton("Save Item");
        styleGreenButton(save);

        save.addActionListener(e -> {
            nameErr.setText(""); qtyErr.setText(""); unitErr.setText("");
            boolean valid = true;

            String name = nameField.getText().trim();
            String unit = unitField.getText().trim();
            String qtyStr = qtyField.getText().trim();

            if (name.isEmpty()) {
                nameErr.setText("Item name is required."); valid = false;
            } else if (dao.itemNameExists(name)) {
                nameErr.setText("An item with this name already exists."); valid = false;
            }
            if (unit.isEmpty()) {
                unitErr.setText("Unit is required."); valid = false;
            }
            double qty = 0;
            if (qtyStr.isEmpty()) {
                qtyErr.setText("Quantity is required."); valid = false;
            } else {
                try {
                    qty = Double.parseDouble(qtyStr);
                    if (qty < 0) { qtyErr.setText("Cannot be negative."); valid = false; }
                } catch (NumberFormatException ex) {
                    qtyErr.setText("Must be a number."); valid = false;
                }
            }
            if (!valid) return;

            boolean ok = dao.addItem(name, (String) catBox.getSelectedItem(), qty, unit);
            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                    name + " added.\nCurrent stock: " + qty + " " + unit + ".",
                    "Item Added", JOptionPane.INFORMATION_MESSAGE);
            } else {
                nameErr.setText("Could not save. Please try again.");
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(save);
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    itemId   = (int)    tableModel.getValueAt(selectedRow, 0);
        String name     = (String) tableModel.getValueAt(selectedRow, 1);
        String category = (String) tableModel.getValueAt(selectedRow, 2);
        double qty      = (double) tableModel.getValueAt(selectedRow, 3);
        String unit     = (String) tableModel.getValueAt(selectedRow, 4);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Edit Item - " + name, true);
        dialog.setSize(400, 260);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField nameField = new JTextField(name);
        JComboBox<String> catBox = new JComboBox<>(new String[]{"FEED", "VACCINE", "MEDICINE", "TOOL", "OTHER"});
        JTextField qtyField  = new JTextField(String.valueOf(qty));
        JTextField unitField = new JTextField(unit);
        catBox.setSelectedItem(category);

        JLabel qtyErr = errorLabel();
        qtyField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); qtyErr.setText("Numbers only.");
                } else { qtyErr.setText(""); }
            }
        });

        int row = 0;
        addFormRow(form, gbc, row++, "Item Name", nameField, null);
        addFormRow(form, gbc, row++, "Category",  catBox,    null);
        addFormRow(form, gbc, row++, "Quantity *", qtyField,  qtyErr);
        addFormRow(form, gbc, row++, "Unit",      unitField, null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        JButton save = new JButton("Save Changes");
        styleGreenButton(save);

        save.addActionListener(e -> {
            qtyErr.setText("");
            double newQty = 0;
            try {
                newQty = Double.parseDouble(qtyField.getText().trim());
                if (newQty < 0) { qtyErr.setText("Cannot be negative."); return; }
            } catch (NumberFormatException ex) {
                qtyErr.setText("Must be a number."); return;
            }
            boolean ok = dao.updateItem(itemId, nameField.getText().trim(),
                (String) catBox.getSelectedItem(), newQty, unitField.getText().trim());
            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                    "Updated. Stock: " + newQty + " " + unitField.getText().trim() + ".",
                    "Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(save);
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showRestockDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    itemId  = (int)    tableModel.getValueAt(selectedRow, 0);
        String name    = (String) tableModel.getValueAt(selectedRow, 1);
        double current = (double) tableModel.getValueAt(selectedRow, 3);
        String unit    = (String) tableModel.getValueAt(selectedRow, 4);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Quick Restock - " + name, true);
        dialog.setSize(360, 185);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        JLabel currentLabel = new JLabel("Current stock: " + current + " " + unit);
        currentLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        currentLabel.setForeground(Color.GRAY);
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(currentLabel, gbc);
        gbc.gridwidth = 1;

        JTextField addQtyField = new JTextField();
        JLabel addErr = errorLabel();
        addQtyField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); addErr.setText("Numbers only.");
                } else { addErr.setText(""); }
            }
        });

        addFormRow(form, gbc, 1, "Amount to Add (" + unit + ") *", addQtyField, addErr);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());
        JButton restock = new JButton("Restock");
        styleGreenButton(restock);

        restock.addActionListener(e -> {
            addErr.setText("");
            String addStr = addQtyField.getText().trim();
            if (addStr.isEmpty()) { addErr.setText("Please enter an amount."); return; }
            double addAmt;
            try {
                addAmt = Double.parseDouble(addStr);
                if (addAmt <= 0) { addErr.setText("Must be greater than zero."); return; }
            } catch (NumberFormatException ex) {
                addErr.setText("Must be a number."); return;
            }
            double newQty = current + addAmt;
            if (dao.updateQuantity(itemId, newQty)) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                    name + " restocked!\nNew quantity: " + newQty + " " + unit + ".",
                    "Restocked", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Restock failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(restock);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    itemId = (int)    tableModel.getValueAt(selectedRow, 0);
        String name   = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + name + "\"? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteItem(itemId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, name + " deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String labelText, Component field, JLabel errLabel) {
        gbc.gridy = row * 2; gbc.gridx = 0; gbc.weightx = 0.4;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        form.add(field, gbc);
        if (errLabel != null) { gbc.gridy = row * 2 + 1; gbc.gridx = 1; form.add(errLabel, gbc); }
    }

    private JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lbl.setForeground(new Color(180, 40, 40));
        return lbl;
    }

    private void styleGreenButton(JButton btn) {
        btn.setBackground(new Color(39, 80, 10)); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12)); btn.setFocusPainted(false);
        btn.setOpaque(true); btn.setContentAreaFilled(true); btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleOutlineButton(JButton btn) {
        btn.setBackground(Color.WHITE); btn.setForeground(new Color(39, 80, 10));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12)); btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 80, 10)), new EmptyBorder(5, 12, 5, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleRedButton(JButton btn) {
        btn.setBackground(new Color(180, 40, 40)); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12)); btn.setFocusPainted(false);
        btn.setOpaque(true); btn.setContentAreaFilled(true); btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
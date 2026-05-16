package greenpasteures.gui;

import greenpasteures.database.HealthDAO;
import greenpasteures.models.Staff;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HealthPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private HealthDAO dao = new HealthDAO();
    private Staff loggedInStaff;

    private static final String[] COLUMNS = {"ID", "Tag No.", "Diagnosis", "Treatment", "Medicine", "Attended By", "Date"};

    public HealthPanel(Staff staff) {
        this.loggedInStaff = staff;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    private void build() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Health Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add Record");
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
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(232, 244, 230));
        table.getTableHeader().setForeground(new Color(39, 80, 10));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        add(scroll, BorderLayout.CENTER);

        // bottom button bar
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnBar.setOpaque(false);
        btnBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton editBtn   = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        styleOutlineButton(editBtn);
        styleRedButton(deleteBtn);

        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> handleDelete());

        btnBar.add(editBtn);
        btnBar.add(deleteBtn);
        add(btnBar, BorderLayout.SOUTH);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Object[]> rows = dao.getAllRecords();
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add Health Record", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField tagField   = new JTextField();
        JTextField dateField  = new JTextField("YYYY-MM-DD");
        JTextField diagField  = new JTextField();
        JTextField treatField = new JTextField();
        JTextField medField   = new JTextField();
        JLabel staffLabel = new JLabel(loggedInStaff.getFullName());
        staffLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel tagErr  = errorLabel();
        JLabel dateErr = errorLabel();
        JLabel diagErr = errorLabel();

        // restrict tag field - only letters, numbers and dashes
        tagField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    tagErr.setText("Tag: letters and numbers only.");
                } else {
                    tagErr.setText("");
                }
            }
        });

        // restrict medicine field - only letters, numbers and spaces
        medField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != ' ' && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });

        int row = 0;
        addFormRow(form, gbc, row++, "Animal Tag No. *", tagField,   tagErr);
        addFormRow(form, gbc, row++, "Treatment Date *", dateField,  dateErr);
        addFormRow(form, gbc, row++, "Diagnosis *",      diagField,  diagErr);
        addFormRow(form, gbc, row++, "Treatment",        treatField, null);
        addFormRow(form, gbc, row++, "Medicine Used",    medField,   null);
        addFormRow(form, gbc, row++, "Attended By",      staffLabel, null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save Record");
        styleGreenButton(save);

        save.addActionListener(e -> {
            tagErr.setText(""); dateErr.setText(""); diagErr.setText("");
            boolean valid = true;

            String tag  = tagField.getText().trim();
            String date = dateField.getText().trim();
            String diag = diagField.getText().trim();

            if (tag.isEmpty()) {
                tagErr.setText("Animal tag number is required.");
                valid = false;
            } else if (!tag.matches("[A-Za-z0-9\\-]+")) {
                tagErr.setText("Tag: letters, numbers and dashes only.");
                valid = false;
            } else if (!dao.animalExists(tag)) {
                tagErr.setText("No animal found with this tag number.");
                valid = false;
            }

            if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                dateErr.setText("Date must be YYYY-MM-DD format.");
                valid = false;
            }

            if (diag.isEmpty()) {
                diagErr.setText("Diagnosis is required.");
                valid = false;
            }

            if (!valid) return;

            boolean ok = dao.addRecord(tag, date, diag,
                treatField.getText().trim(),
                medField.getText().trim(),
                loggedInStaff.getStaffId());

            if (ok) {
                refreshTable();
                dialog.dispose();
            } else {
                tagErr.setText("Could not save. Please try again.");
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
            JOptionPane.showMessageDialog(this, "Please select a record first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    recordId  = (int)    tableModel.getValueAt(selectedRow, 0);
        String diagnosis = (String) tableModel.getValueAt(selectedRow, 2);
        String treatment = (String) tableModel.getValueAt(selectedRow, 3);
        String medicine  = (String) tableModel.getValueAt(selectedRow, 4);
        String date      = (String) tableModel.getValueAt(selectedRow, 6);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Edit Health Record #" + recordId, true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField dateField  = new JTextField(date);
        JTextField diagField  = new JTextField(diagnosis);
        JTextField treatField = new JTextField(treatment != null ? treatment : "");
        JTextField medField   = new JTextField(medicine  != null ? medicine  : "");

        JLabel dateErr = errorLabel();
        JLabel diagErr = errorLabel();

        int row = 0;
        addFormRow(form, gbc, row++, "Treatment Date *", dateField,  dateErr);
        addFormRow(form, gbc, row++, "Diagnosis *",      diagField,  diagErr);
        addFormRow(form, gbc, row++, "Treatment",        treatField, null);
        addFormRow(form, gbc, row++, "Medicine Used",    medField,   null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save Changes");
        styleGreenButton(save);

        save.addActionListener(e -> {
            dateErr.setText(""); diagErr.setText("");
            boolean valid = true;

            if (!dateField.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
                dateErr.setText("Date must be YYYY-MM-DD format.");
                valid = false;
            }
            if (diagField.getText().trim().isEmpty()) {
                diagErr.setText("Diagnosis is required.");
                valid = false;
            }
            if (!valid) return;

            boolean ok = dao.updateRecord(recordId,
                diagField.getText().trim(),
                treatField.getText().trim(),
                medField.getText().trim(),
                dateField.getText().trim());

            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent, "Record updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
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

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm  = JOptionPane.showConfirmDialog(this,
            "Delete health record #" + recordId + "? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteRecord(recordId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Record deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String labelText, Component field, JLabel errLabel) {
        gbc.gridy = row * 2; gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(field, gbc);
        if (errLabel != null) {
            gbc.gridy = row * 2 + 1; gbc.gridx = 1;
            form.add(errLabel, gbc);
        }
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
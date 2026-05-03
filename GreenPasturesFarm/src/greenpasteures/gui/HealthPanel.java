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
    private HealthDAO dao = new HealthDAO();
    private Staff loggedInStaff;

    private static final String[] COLUMNS = {"ID", "Tag no.", "Diagnosis", "Treatment", "Medicine", "Attended by", "Date"};

    // constructor - receives the logged in staff member
    public HealthPanel(Staff staff) {
        this.loggedInStaff = staff;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    // builds the health panel
    private void build() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Health Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add record");
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

    // loads health records from the database
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Object[]> rows = dao.getAllRecords();
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    // shows the add health record dialog
    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add health record", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 8));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField tagField   = new JTextField();
        JTextField dateField  = new JTextField("YYYY-MM-DD");
        JTextField diagField  = new JTextField();
        JTextField treatField = new JTextField();
        JTextField medField   = new JTextField();
        JLabel staffLabel = new JLabel(loggedInStaff.getFullName());
        staffLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        form.add(new JLabel("Animal tag no.:"));  form.add(tagField);
        form.add(new JLabel("Treatment date:"));  form.add(dateField);
        form.add(new JLabel("Diagnosis:"));       form.add(diagField);
        form.add(new JLabel("Treatment:"));       form.add(treatField);
        form.add(new JLabel("Medicine used:"));   form.add(medField);
        form.add(new JLabel("Attended by:"));     form.add(staffLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save record");
        styleGreenButton(save);

        save.addActionListener(e -> {
            if (tagField.getText().trim().isEmpty() || dateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tag number and date are required.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!dateField.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Date must be YYYY-MM-DD.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = dao.addRecord(
                tagField.getText().trim(),
                dateField.getText().trim(),
                diagField.getText().trim(),
                treatField.getText().trim(),
                medField.getText().trim(),
                loggedInStaff.getStaffId()
            );

            if (ok) {
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Could not save record. Check that the tag number exists.", "Database error", JOptionPane.ERROR_MESSAGE);
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
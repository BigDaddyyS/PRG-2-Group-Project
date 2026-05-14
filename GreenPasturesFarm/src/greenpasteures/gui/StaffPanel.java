package greenpasteures.gui;

import greenpasteures.database.StaffDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private StaffDAO dao = new StaffDAO();

    private static final String[] COLUMNS = {"ID", "First Name", "Last Name", "Role", "Phone", "Username"};

    // constructor
    public StaffPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    // builds the staff management panel
    private void build() {

        // top bar with title
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Staff Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        // admin only note
        JLabel adminNote = new JLabel("Admin access only");
        adminNote.setFont(new Font("SansSerif", Font.ITALIC, 11));
        adminNote.setForeground(new Color(150, 100, 10));

        topBar.add(title,     BorderLayout.WEST);
        topBar.add(adminNote, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
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

        // bottom button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonBar.setOpaque(false);
        buttonBar.setBorder(new EmptyBorder(14, 0, 0, 0));

        JButton promoteBtn  = new JButton("Promote to Admin");
        JButton demoteBtn   = new JButton("Demote to Staff");
        JButton deleteBtn   = new JButton("Delete Staff");
        JButton refreshBtn  = new JButton("Refresh");

        styleGreenButton(promoteBtn);
        styleOutlineButton(demoteBtn);
        styleRedButton(deleteBtn);
        styleOutlineButton(refreshBtn);

        // promote selected staff to admin
        promoteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a staff member first.", "No selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int staffId  = (int) tableModel.getValueAt(selectedRow, 0);
            String name  = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
            String role  = (String) tableModel.getValueAt(selectedRow, 3);

            if (role.equals("ADMIN")) {
                JOptionPane.showMessageDialog(this, name + " is already an Admin.", "Already Admin", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Promote " + name + " to Admin?\nThey will have full access to the system.",
                "Confirm Promotion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.updateRole(staffId, "ADMIN")) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, name + " has been promoted to Admin.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update role.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // demote selected admin back to staff
        demoteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a staff member first.", "No selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int staffId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
            String role = (String) tableModel.getValueAt(selectedRow, 3);

            if (role.equals("STAFF")) {
                JOptionPane.showMessageDialog(this, name + " is already a Staff member.", "Already Staff", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Demote " + name + " to Staff?\nThey will lose admin access.",
                "Confirm Demotion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.updateRole(staffId, "STAFF")) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, name + " has been demoted to Staff.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update role.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // delete selected staff member
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a staff member first.", "No selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int staffId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + name + " from the system?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.deleteStaff(staffId)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, name + " has been removed.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete this staff member.\nThey may have linked health records.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // refresh button
        refreshBtn.addActionListener(e -> refreshTable());

        buttonBar.add(promoteBtn);
        buttonBar.add(demoteBtn);
        buttonBar.add(deleteBtn);
        buttonBar.add(refreshBtn);

        add(buttonBar, BorderLayout.SOUTH);

        refreshTable();
    }

    // loads all staff from the database into the table
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Object[]> rows = dao.getAllStaff();
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    // button styling helpers
    private void styleGreenButton(JButton btn) {
        btn.setBackground(new Color(39, 80, 10));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleOutlineButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(39, 80, 10));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 80, 10)),
            new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleRedButton(JButton btn) {
        btn.setBackground(new Color(180, 40, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
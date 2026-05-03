package greenpasteures.gui;

import greenpasteures.database.AnimalDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AnimalPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private AnimalDAO dao = new AnimalDAO();

    private static final String[] COLUMNS = {"Tag no.", "Type", "Breed", "Gender", "Status", "Registered"};

    // constructor
    public AnimalPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    // builds the animal panel
    private void build() {
        // top bar with title and add button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Animals");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add animal");
        styleGreenButton(addBtn);
        addBtn.addActionListener(e -> showAddDialog());

        topBar.add(title,  BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);
        filterBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        typeFilter   = new JComboBox<>(new String[]{"All", "CATTLE", "SHEEP", "POULTRY"});
        statusFilter = new JComboBox<>(new String[]{"All", "HEALTHY", "SICK", "SOLD", "LOST", "DECEASED", "STOLEN"});

        typeFilter.addActionListener(e -> refreshTable());
        statusFilter.addActionListener(e -> refreshTable());

        filterBar.add(new JLabel("Type:"));   filterBar.add(typeFilter);
        filterBar.add(new JLabel("Status:")); filterBar.add(statusFilter);

        // table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = buildTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));

        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);
        centre.add(filterBar, BorderLayout.NORTH);
        centre.add(scroll,    BorderLayout.CENTER);

        add(centre, BorderLayout.CENTER);

        refreshTable();
    }

    // loads data from the database into the table
    private void refreshTable() {
        String type   = (String) typeFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        List<Object[]> rows = dao.getFilteredAnimals(type, status);
        tableModel.setRowCount(0);
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    // shows the add animal dialog
    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add new animal", true);
        dialog.setSize(430, 320);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 8));
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField tagField    = new JTextField();
        JComboBox<String> typeBox   = new JComboBox<>(new String[]{"CATTLE", "SHEEP", "POULTRY"});
        JTextField breedField  = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Female", "Male"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"HEALTHY", "SICK", "SOLD", "LOST", "DECEASED", "STOLEN"});
        JTextField dateField   = new JTextField("YYYY-MM-DD");

        form.add(new JLabel("Tag number:"));       form.add(tagField);
        form.add(new JLabel("Animal type:"));      form.add(typeBox);
        form.add(new JLabel("Breed:"));            form.add(breedField);
        form.add(new JLabel("Gender:"));           form.add(genderBox);
        form.add(new JLabel("Status:"));           form.add(statusBox);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save animal");
        styleGreenButton(save);

        save.addActionListener(e -> {
            String tag   = tagField.getText().trim();
            String breed = breedField.getText().trim();
            String date  = dateField.getText().trim();

            // validation
            if (tag.isEmpty() || breed.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Date must be in format YYYY-MM-DD.", "Validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = dao.addAnimal(
                tag,
                (String) typeBox.getSelectedItem(),
                breed,
                (String) genderBox.getSelectedItem(),
                (String) statusBox.getSelectedItem(),
                date
            );

            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent, "Animal " + tag + " added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Could not save animal. Check that the tag number is unique.", "Database error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(save);

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // creates a styled JTable
    private JTable buildTable() {
        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 242, 236));
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 235, 200));
        return table;
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
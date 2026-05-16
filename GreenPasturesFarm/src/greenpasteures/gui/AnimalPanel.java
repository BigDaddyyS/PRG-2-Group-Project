package greenpasteures.gui;

import greenpasteures.database.AnimalDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AnimalPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private final AnimalDAO dao = new AnimalDAO();

    private static final String[] COLUMNS = {"Tag No.", "Type", "Breed", "Gender", "Status", "Registered"};

    public AnimalPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    private void build() {
        // top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Animals");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton addBtn = new JButton("+ Add Animal");
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

        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);
        centre.add(filterBar, BorderLayout.NORTH);
        centre.add(scroll,    BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

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

    // refreshes the table from the database
    public void refreshTable() {
        String type   = (String) typeFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        List<Object[]> rows = dao.getFilteredAnimals(type, status);
        tableModel.setRowCount(0);
        for (int i = 0; i < rows.size(); i++) {
            tableModel.addRow(rows.get(i));
        }
    }

    // shows the add animal dialog with full validation
    private void showAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add New Animal", true);
        dialog.setSize(450, 440);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JTextField tagField    = new JTextField();
        JComboBox<String> typeBox   = new JComboBox<>(new String[]{"CATTLE", "SHEEP", "POULTRY"});
        JTextField breedField  = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Female", "Male"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"HEALTHY", "SICK", "SOLD", "LOST", "DECEASED", "STOLEN"});
        JTextField dateField   = new JTextField("YYYY-MM-DD");
        JTextField weightField = new JTextField();

        JLabel tagErr    = errorLabel();
        JLabel breedErr  = errorLabel();
        JLabel dateErr   = errorLabel();
        JLabel weightErr = errorLabel();

        // tag - letters, numbers and dashes only
        tagField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); tagErr.setText("Letters, numbers and dashes only.");
                } else { tagErr.setText(""); }
            }
        });

        // breed - letters and spaces only
        breedField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); breedErr.setText("Letters only, no numbers or special characters.");
                } else { breedErr.setText(""); }
            }
        });

        // weight - numbers and decimal only
        weightField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); weightErr.setText("Numbers only, e.g. 450 or 450.5");
                } else { weightErr.setText(""); }
            }
        });

        int row = 0;
        addFormRow(form, gbc, row++, "Tag Number *",       tagField,    tagErr);
        addFormRow(form, gbc, row++, "Animal Type *",      typeBox,     null);
        addFormRow(form, gbc, row++, "Breed *",            breedField,  breedErr);
        addFormRow(form, gbc, row++, "Gender *",           genderBox,   null);
        addFormRow(form, gbc, row++, "Status *",           statusBox,   null);
        addFormRow(form, gbc, row++, "Date (YYYY-MM-DD) *",dateField,   dateErr);
        addFormRow(form, gbc, row++, "Weight (kg)",        weightField, weightErr);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save Animal");
        styleGreenButton(save);

        save.addActionListener(e -> {
            tagErr.setText(""); breedErr.setText(""); dateErr.setText(""); weightErr.setText("");
            boolean valid = true;

            String tag      = tagField.getText().trim();
            String breed    = breedField.getText().trim();
            String date     = dateField.getText().trim();
            String weightStr = weightField.getText().trim();

            if (tag.isEmpty()) {
                tagErr.setText("Tag number is required."); valid = false;
            } else if (!tag.matches("[A-Za-z0-9\\-]+")) {
                tagErr.setText("Letters, numbers and dashes only."); valid = false;
            } else if (dao.tagExists(tag)) {
                tagErr.setText("This tag number already exists."); valid = false;
            }

            if (breed.isEmpty()) {
                breedErr.setText("Breed is required."); valid = false;
            } else if (!breed.matches("[A-Za-z ]+")) {
                breedErr.setText("Letters only."); valid = false;
            }

            if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                dateErr.setText("Date must be in format YYYY-MM-DD."); valid = false;
            }

            double weight = 0;
            if (!weightStr.isEmpty()) {
                try {
                    weight = Double.parseDouble(weightStr);
                    if (weight < 0) { weightErr.setText("Weight cannot be negative."); valid = false; }
                } catch (NumberFormatException ex) {
                    weightErr.setText("Weight must be a number."); valid = false;
                }
            }

            if (!valid) return;

            boolean ok = dao.addAnimal(tag,
                (String) typeBox.getSelectedItem(), breed,
                (String) genderBox.getSelectedItem(),
                (String) statusBox.getSelectedItem(), date, weight);

            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                    "Animal " + tag + " added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                tagErr.setText("Could not save. Tag number may already exist.");
            }
        });

        btnPanel.add(cancel);
        btnPanel.add(save);
        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // shows the edit dialog for the selected animal
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an animal from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tagNumber  = (String) tableModel.getValueAt(selectedRow, 0);
        String animalType = (String) tableModel.getValueAt(selectedRow, 1);
        String breed      = (String) tableModel.getValueAt(selectedRow, 2);
        String gender     = (String) tableModel.getValueAt(selectedRow, 3);
        String status     = (String) tableModel.getValueAt(selectedRow, 4);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Edit Animal - " + tagNumber, true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        // tag is read only
        JLabel tagLabel = new JLabel(tagNumber);
        tagLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        JTextField breedField  = new JTextField(breed);
        JComboBox<String> genderBox   = new JComboBox<>(new String[]{"Female", "Male"});
        JComboBox<String> statusBox   = new JComboBox<>(new String[]{"HEALTHY", "SICK", "SOLD", "LOST", "DECEASED", "STOLEN"});
        JTextField weightField = new JTextField();
        genderBox.setSelectedItem(gender);
        statusBox.setSelectedItem(status);

        JLabel breedErr  = errorLabel();
        JLabel weightErr = errorLabel();

        breedField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); breedErr.setText("Letters only.");
                } else { breedErr.setText(""); }
            }
        });

        weightField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume(); weightErr.setText("Numbers only.");
                } else { weightErr.setText(""); }
            }
        });

        int row = 0;
        addFormRow(form, gbc, row++, "Tag Number",  tagLabel,    null);
        addFormRow(form, gbc, row++, "Breed *",     breedField,  breedErr);
        addFormRow(form, gbc, row++, "Gender *",    genderBox,   null);
        addFormRow(form, gbc, row++, "Status *",    statusBox,   null);
        addFormRow(form, gbc, row++, "Weight (kg)", weightField, weightErr);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        JButton save = new JButton("Save Changes");
        styleGreenButton(save);

        save.addActionListener(e -> {
            breedErr.setText(""); weightErr.setText("");
            boolean valid = true;

            String newBreed  = breedField.getText().trim();
            String weightStr = weightField.getText().trim();

            if (newBreed.isEmpty() || !newBreed.matches("[A-Za-z ]+")) {
                breedErr.setText("Breed must contain letters only."); valid = false;
            }

            double weight = 0;
            if (!weightStr.isEmpty()) {
                try {
                    weight = Double.parseDouble(weightStr);
                    if (weight < 0) { weightErr.setText("Cannot be negative."); valid = false; }
                } catch (NumberFormatException ex) {
                    weightErr.setText("Must be a number."); valid = false;
                }
            }

            if (!valid) return;

            boolean ok = dao.updateAnimal(tagNumber, newBreed,
                (String) genderBox.getSelectedItem(),
                (String) statusBox.getSelectedItem(), weight, animalType);

            if (ok) {
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(parent,
                    "Animal updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
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

    // handles deleting the selected animal
    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an animal from the table first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tagNumber  = (String) tableModel.getValueAt(selectedRow, 0);
        String animalType = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete animal " + tagNumber + "?\n" +
            "This will also delete all health records for this animal.\n" +
            "This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteAnimal(tagNumber, animalType)) {
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Animal " + tagNumber + " deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row,
                            String labelText, Component field, JLabel errLabel) {
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
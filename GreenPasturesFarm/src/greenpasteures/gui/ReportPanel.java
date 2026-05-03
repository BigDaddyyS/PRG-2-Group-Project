package greenpasteures.gui;

import greenpasteures.models.Staff;
import greenpasteures.reports.CSVExporter;
import greenpasteures.reports.FarmReport;
import greenpasteures.reports.PDFExporter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class ReportPanel extends JPanel {

    private Staff loggedInStaff;
    private JTextArea reportTextArea;

    // constructor - receives the logged in staff member
    public ReportPanel(Staff staff) {
        this.loggedInStaff = staff;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 248));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        build();
    }

    // builds the report panel
    private void build() {

        // top bar with title
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // centre area - text area to preview the report
        reportTextArea = new JTextArea();
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setEditable(false);
        reportTextArea.setBackground(new Color(248, 248, 245));
        reportTextArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        reportTextArea.setText("Click 'Generate Report' to load the farm summary.");

        JScrollPane scroll = new JScrollPane(reportTextArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 210)));
        add(scroll, BorderLayout.CENTER);

        // bottom button bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonBar.setOpaque(false);
        buttonBar.setBorder(new EmptyBorder(14, 0, 0, 0));

        JButton generateBtn = new JButton("Generate Report");
        JButton csvBtn      = new JButton("Export to CSV");
        JButton pdfBtn      = new JButton("Export to PDF");

        styleGreenButton(generateBtn);
        styleOutlineButton(csvBtn);
        styleOutlineButton(pdfBtn);

        // generate the report and show it in the text area
        generateBtn.addActionListener(e -> {
            generateBtn.setEnabled(false);
            generateBtn.setText("Loading...");

            FarmReport report = new FarmReport(1, loggedInStaff.getFullName());
            report.loadData();

            reportTextArea.setText(report.generateReport());

            generateBtn.setEnabled(true);
            generateBtn.setText("Generate Report");
        });

        // export to CSV - asks user to pick a folder first
        csvBtn.addActionListener(e -> {
            FarmReport report = new FarmReport(1, loggedInStaff.getFullName());
            report.loadData();

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose folder to save CSV");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File folder = chooser.getSelectedFile();
                CSVExporter exporter = new CSVExporter(report);
                exporter.exportToCSV(folder.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                    "CSV saved to:\n" + folder.getAbsolutePath(),
                    "Export successful",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // export to PDF - asks user to pick a folder first
        pdfBtn.addActionListener(e -> {
            FarmReport report = new FarmReport(1, loggedInStaff.getFullName());
            report.loadData();

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose folder to save PDF");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File folder = chooser.getSelectedFile();
                PDFExporter exporter = new PDFExporter(report);
                exporter.exportToPDF(folder.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                    "PDF saved to:\n" + folder.getAbsolutePath(),
                    "Export successful",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonBar.add(generateBtn);
        buttonBar.add(csvBtn);
        buttonBar.add(pdfBtn);

        add(buttonBar, BorderLayout.SOUTH);
    }

    // styles a solid green button
    private void styleGreenButton(JButton btn) {
        btn.setBackground(new Color(59, 109, 17));
        btn.setForeground(Color.RED);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // styles an outline button
    private void styleOutlineButton(JButton btn) {
        btn.setBackground(Color.GREEN);
        btn.setForeground(new Color(59, 109, 17));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 109, 17)),
            new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
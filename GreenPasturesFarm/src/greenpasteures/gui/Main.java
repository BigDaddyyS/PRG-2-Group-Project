package greenpasteures.gui;

import greenpasteures.database.DatabaseManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        // connect to the database first before launching the GUI
        DatabaseManager.getInstance();

        // check if the database connected successfully
        if (!DatabaseManager.getInstance().isConnected()) {
            System.out.println("Could not connect to database. Please check your MySQL settings.");
            return;
        }

        // launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // use the system look and feel so it looks native on windows
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.out.println("Could not set look and feel: " + e.getMessage());
                }

                //login screen
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
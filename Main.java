import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the database manager
            DatabaseManager dbManager = new DatabaseManager();
            
            // Initialize with admin account
            dbManager.initializeDatabase();
            
            // Create and show login screen
            new LoginPage(dbManager);
        });
    }
}
    
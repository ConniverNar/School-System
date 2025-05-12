import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DatabaseManager dbManager;
    
    public LoginPage(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        
        // Setup frame
        setTitle("School Management System - Login");
        setSize(360, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create panel with padding and set background color to light cream/pale yellow (#FFF8DC)
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0xFF, 0xF8, 0xDC));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Load and add logo image
        try {
            ImageIcon originalIcon = new ImageIcon("SMS logo.png");
            // Scale the image to fit appropriately within the UI
            Image scaledImage = originalIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            JLabel logoLabel = new JLabel(scaledIcon);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(logoLabel, gbc);
        } catch (Exception e) {
            // Fallback to text title if image cannot be loaded
            JLabel titleLabel = new JLabel("School Management System");
            titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
            titleLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(titleLabel, gbc);
            System.err.println("Error loading logo image: " + e.getMessage());
        }
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        
        // Error message label
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(errorLabel, gbc);
        
        // Add login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                // Check credentials
                User user = dbManager.authenticateUser(username, password);
                
                if (user != null) {
                    // Close login window
                    dispose();
                    
                    // Open appropriate interface based on user type
                    switch (user.getUserType()) {
                        case ADMIN:
                            new AdminInterface(dbManager, user);
                            break;
                        case FACULTY:
                            new FacultyInterface(dbManager, user);
                            break;
                        case STUDENT:
                            new StudentInterface(dbManager, user);
                            break;
                    }
                } else {
                    errorLabel.setText("Invalid username or password!");
                }
            }
        });
        
        // Add panel to frame
        add(panel);
        setVisible(true);
    }
}

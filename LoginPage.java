import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    // Admin credentials
    private static final String ADMIN_USERNAME = "admin111";
    private static final String ADMIN_PASSWORD = "admin123";

    public LoginPage() {
        // Frame setup
        setTitle("School System");
        setSize(350, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("School System");
        titleLabel.setFont(new Font("Sitka Display", Font.BOLD | Font.ITALIC, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 128, 128));
        titleLabel.setForeground(Color.WHITE);

        // User input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(0, 128, 128));
        inputPanel.setLayout(new GridLayout(4, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        usernameField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField(15);

        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);

        // Button and message panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(0, 128, 128));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        loginButton = new JButton("Log In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(173, 216, 230));
        bottomPanel.add(loginButton);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(messageLabel);

        // Add action
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Add to frame
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            JOptionPane.showMessageDialog(this, "Login successful! Redirecting to Admin Interface...");
            // Example navigation
            SwingUtilities.invokeLater(() -> {
                MainInterface mainInterface = new MainInterface("Admin");
                mainInterface.setVisible(true);
                dispose();
            });
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}

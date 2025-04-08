import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class FacultyInterface extends JFrame {
    private static final long serialVersionUID = 1L;
    private String facultyId;
    private String facultyName;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private List<Schedule> schedules;
    private double ratePerHour;
    private int totalHours;
    
    // Buttons for navigation
    private JButton setScheduleButton;
    private JButton viewScheduleButton;
    private JButton viewSalaryButton;
    
    public FacultyInterface(String facultyId, String facultyName) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.schedules = new ArrayList<>();
        this.ratePerHour = 10000; // Default rate per hour
        
        // Initialize the frame
        setTitle("School System");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create the layout
        setupLayout();
        
        // Add initial schedule data (for demonstration)
        addSampleSchedules();
        
        // Calculate total hours
        calculateTotalHours();
    }
    
    private void setupLayout() {
        // Set up the main layout
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel titleLabel = new JLabel("School System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel facultyInfoPanel = new JPanel(new GridLayout(1, 2));
        JLabel nameLabel = new JLabel(facultyName, SwingConstants.LEFT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JLabel idLabel = new JLabel(facultyId, SwingConstants.RIGHT);
        idLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        facultyInfoPanel.add(nameLabel);
        facultyInfoPanel.add(idLabel);
        
        headerPanel.add(titleLabel);
        headerPanel.add(facultyInfoPanel);
        
        // Add the header panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        
        // Create left panel for navigation buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(5, 1, 10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        setScheduleButton = createButton("Set Schedule", Color.decode("#4CAF50"));
        viewScheduleButton = createButton("View Schedule", Color.decode("#4CAF50"));
        viewSalaryButton = createButton("View Salary", Color.decode("#4CAF50"));
        
        leftPanel.add(setScheduleButton);
        leftPanel.add(viewScheduleButton);
        leftPanel.add(viewSalaryButton);
        leftPanel.add(new JLabel()); // Empty space
        leftPanel.add(new JLabel()); // Empty space
        
        add(leftPanel, BorderLayout.WEST);
        
        // Create main content panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create and add the different panels
        mainPanel.add(createSetSchedulePanel(), "setSchedule");
        mainPanel.add(createViewSchedulePanel(), "viewSchedule");
        mainPanel.add(createViewSalaryPanel(), "viewSalary");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Add action listeners to buttons
        setScheduleButton.addActionListener(e -> cardLayout.show(mainPanel, "setSchedule"));
        viewScheduleButton.addActionListener(e -> cardLayout.show(mainPanel, "viewSchedule"));
        viewSalaryButton.addActionListener(e -> cardLayout.show(mainPanel, "viewSalary"));
    }
    
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }
    
    private JPanel createSetSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JLabel dayLabel = new JLabel("Day:");
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        
        JLabel startTimeLabel = new JLabel("Start Time:");
        JTextField startTimeField = new JTextField();
        
        JLabel endTimeLabel = new JLabel("End Time:");
        JTextField endTimeField = new JTextField();
        
        JLabel roomLabel = new JLabel("Room Number:");
        JTextField roomField = new JTextField();
        
        formPanel.add(dayLabel);
        formPanel.add(dayComboBox);
        formPanel.add(startTimeLabel);
        formPanel.add(startTimeField);
        formPanel.add(endTimeLabel);
        formPanel.add(endTimeField);
        formPanel.add(roomLabel);
        formPanel.add(roomField);
        
        JButton saveButton = new JButton("Save Schedule");
        saveButton.setBackground(Color.decode("#4CAF50"));
        saveButton.setForeground(Color.WHITE);
        
        saveButton.addActionListener(e -> {
            String day = (String) dayComboBox.getSelectedItem();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();
            String room = roomField.getText();
            
            if (startTime.isEmpty() || endTime.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create schedule in format "startTime - endTime DAY" and room number
            String timeString = startTime + " - " + endTime + " " + getDayCode(day);
            Schedule newSchedule = new Schedule(room, timeString);
            schedules.add(newSchedule);
            
            // Refresh the view schedule panel
            mainPanel.remove(1);
            mainPanel.add(createViewSchedulePanel(), "viewSchedule", 1);
            
            // Recalculate hours
            calculateTotalHours();
            
            // Refresh the salary panel
            mainPanel.remove(2);
            mainPanel.add(createViewSalaryPanel(), "viewSalary", 2);
            
            // Show success message
            JOptionPane.showMessageDialog(this, "Schedule saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear the form
            startTimeField.setText("");
            endTimeField.setText("");
            roomField.setText("");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createViewSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table-like structure for schedules
        JPanel tablePanel = new JPanel(new GridLayout(0, 3));
        tablePanel.setBackground(Color.WHITE);
        
        // Add headers
        JLabel scheduleHeader = new JLabel("Schedule", SwingConstants.CENTER);
        scheduleHeader.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel roomHeader = new JLabel("Room", SwingConstants.CENTER);
        roomHeader.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel hoursHeader = new JLabel("Hours", SwingConstants.CENTER);
        hoursHeader.setFont(new Font("Arial", Font.BOLD, 14));
        
        tablePanel.add(scheduleHeader);
        tablePanel.add(roomHeader);
        tablePanel.add(hoursHeader);
        
        // Add schedule data
        for (Schedule schedule : schedules) {
            JLabel scheduleLabel = new JLabel(schedule.getTime(), SwingConstants.CENTER);
            JLabel roomLabel = new JLabel(schedule.getRoomNumber(), SwingConstants.CENTER);
            JLabel hoursLabel = new JLabel(String.valueOf(calculateHours(schedule.getTime())), SwingConstants.CENTER);
            
            tablePanel.add(scheduleLabel);
            tablePanel.add(roomLabel);
            tablePanel.add(hoursLabel);
        }
        
        JScrollPane scrollPane = new JScrollPane(tablePanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createViewSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel salaryPanel = new JPanel(new GridLayout(0, 3));
        salaryPanel.setBackground(Color.WHITE);
        
        // Add rate per hour
        JLabel rateLabel = new JLabel("Rate per hour:", SwingConstants.RIGHT);
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel rateValueLabel = new JLabel(String.format("%,.0f", ratePerHour), SwingConstants.CENTER);
        rateValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel totalHoursLabel = new JLabel(String.valueOf(totalHours), SwingConstants.CENTER);
        totalHoursLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        salaryPanel.add(rateLabel);
        salaryPanel.add(rateValueLabel);
        salaryPanel.add(totalHoursLabel);
        
        // Add total salary
        JLabel totalLabel = new JLabel("Total:", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel totalValueLabel = new JLabel(String.format("%,.0f", ratePerHour * totalHours), SwingConstants.CENTER);
        totalValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel emptyLabel = new JLabel();
        
        salaryPanel.add(totalLabel);
        salaryPanel.add(totalValueLabel);
        salaryPanel.add(emptyLabel);
        
        panel.add(salaryPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getDayCode(String day) {
        switch (day) {
            case "Monday": return "M";
            case "Tuesday": return "T";
            case "Wednesday": return "W";
            case "Thursday": return "TH";
            case "Friday": return "F";
            case "Saturday": return "S";
            default: return "";
        }
    }
    
    private void addSampleSchedules() {
        // Add sample schedules for demonstration
        schedules.add(new Schedule("1001", "7:30 - 10:30 AM TTH"));
        schedules.add(new Schedule("1001", "1:30 - 4:30 PM MWF"));
    }
    
    private int calculateHours(String timeString) {
        // Simple calculation based on the time range
        // For example, "7:30 - 10:30 AM TTH" would be 3 hours
        // This is a simplified version for demonstration
        return 3;
    }
    
    private void calculateTotalHours() {
        totalHours = 0;
        for (Schedule schedule : schedules) {
            totalHours += calculateHours(schedule.getTime());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FacultyInterface facultyInterface = new FacultyInterface("1001", "Fame B Anore");
            facultyInterface.setVisible(true);
        });
    }
}
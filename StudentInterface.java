import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentInterface {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("School System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("School System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(Color.LIGHT_GRAY);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Student Info Panel
        JPanel studentInfoPanel = new JPanel();
        studentInfoPanel.setLayout(new GridLayout(3, 1));
        studentInfoPanel.add(new JLabel("Name: John Doe"));
        studentInfoPanel.add(new JLabel("Student ID: 123456"));
        studentInfoPanel.add(new JLabel("Department: Computer Science"));
        frame.add(studentInfoPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        
        JButton enrollmentButton = new JButton("Enrollment");
        JButton studyLoadButton = new JButton("Study Load");
        JButton tuitionBalanceButton = new JButton("Tuition Balance");

        buttonPanel.add(enrollmentButton);
        buttonPanel.add(studyLoadButton);
        buttonPanel.add(tuitionBalanceButton);
        
        // Left Side Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(buttonPanel, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(200, 0)); // 30% of the width
        frame.add(leftPanel, BorderLayout.WEST);

        // Right Side Panel (Dynamic Content Area)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        rightPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.CENTER);

        // Action Listeners for Buttons
        enrollmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentArea.setText("Enrollment functionality goes here.");
            }
        });

        studyLoadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentArea.setText("Study Load functionality goes here.");
            }
        });

        tuitionBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentArea.setText("Tuition Balance functionality goes here.");
            }
        });

        // Set frame visibility
        frame.setVisible(true);
    }
}
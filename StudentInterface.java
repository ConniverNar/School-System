import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentInterface extends JFrame {
    private DatabaseManager dbManager;
    private User studentUser;
    private JTabbedPane tabbedPane;
    
    public StudentInterface(DatabaseManager dbManager, User studentUser) {
        this.dbManager = dbManager;
        this.studentUser = studentUser;
        
        setTitle("Student Interface - " + studentUser.getUserInfo("name"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs for each functionality
        tabbedPane.addTab("Subject Enrollment", createEnrollmentPanel());
        tabbedPane.addTab("Study Load", createStudyLoadPanel());
        tabbedPane.addTab("Tuition Fee", createTuitionPanel());
        
        // Add logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(dbManager);
            }
        });
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    // Subject Enrollment Panel
    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filter options at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Get the student's department
        String studentDepartment = studentUser.getUserInfo("department");
        
        JLabel departmentLabel = new JLabel("Department:");
        JTextField departmentField = new JTextField(studentDepartment, 15);
        departmentField.setEditable(false);
        
        JButton filterButton = new JButton("Show Available Subjects");
        
        filterPanel.add(departmentLabel);
        filterPanel.add(departmentField);
        filterPanel.add(filterButton);
        
        // Available subjects list in the center
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Prerequisites", "Schedule"};
        DefaultTableModel availableSubjectsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable availableSubjectsTable = new JTable(availableSubjectsModel);
        JScrollPane availableScrollPane = new JScrollPane(availableSubjectsTable);
        availableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Subjects"));
        
        // Action panel at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton enrollButton = new JButton("Enroll in Selected Subject");
        
        actionPanel.add(enrollButton);
        
        // Add components to panel
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(availableScrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
            }
        });
        
        // Enroll button action
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = availableSubjectsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject to enroll in", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = (String) availableSubjectsModel.getValueAt(selectedRow, 0);
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject == null) {
                    JOptionPane.showMessageDialog(panel, "Subject not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if already enrolled
                if (subject.getEnrolledStudents().contains(studentUser.getUsername())) {
                    JOptionPane.showMessageDialog(panel, "You are already enrolled in this subject", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check prerequisites
                if (!dbManager.hasPrerequisites(studentUser.getUsername(), subject)) {
                    JOptionPane.showMessageDialog(panel, "You don't meet the prerequisites for this subject", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Enroll student
                subject.enrollStudent(studentUser.getUsername());
                
                // Refresh table
                refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
                
                /*// Refresh other tabs
                refreshStudyLoad();
                refreshTuition();
                */
                JOptionPane.showMessageDialog(panel, "Successfully enrolled in " + subject.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Initial load of available subjects
        refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
        
        return panel;
    }
    
    // Helper method to refresh available subjects
    private void refreshAvailableSubjects(DefaultTableModel model, String department) {
        model.setRowCount(0);
        
        List<Subject> subjects;
        if (department == null || department.isEmpty()) {
            subjects = dbManager.getAllSubjects();
        } else {
            subjects = dbManager.getSubjectsByDepartment(department);
        }
        
        for (Subject subject : subjects) {
            // Skip subjects already enrolled in
            if (subject.getEnrolledStudents().contains(studentUser.getUsername())) {
                continue;
            }
            
            // Format prerequisites
            StringBuilder prereqBuilder = new StringBuilder();
            for (String prereq : subject.getPrerequisites()) {
                if (prereqBuilder.length() > 0) {
                    prereqBuilder.append(", ");
                }
                Subject prereqSubject = dbManager.getSubject(prereq);
                if (prereqSubject != null) {
                    prereqBuilder.append(prereqSubject.getName());
                }
            }
            
            // Format schedules
            StringBuilder scheduleBuilder = new StringBuilder();
            for (Schedule schedule : subject.getSchedules()) {
                if (scheduleBuilder.length() > 0) {
                    scheduleBuilder.append("; ");
                }
                scheduleBuilder.append(schedule.toString());
            }
            
            model.addRow(new Object[]{
                subject.getId(),
                subject.getName(),
                subject.getUnits(),
                prereqBuilder.toString(),
                scheduleBuilder.toString()
            });
        }
    }
    
    // Study Load Panel
    private JPanel createStudyLoadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for enrolled subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Schedule", "Faculty"};
        DefaultTableModel studyLoadModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable studyLoadTable = new JTable(studyLoadModel);
        JScrollPane scrollPane = new JScrollPane(studyLoadTable);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalUnitsLabel = new JLabel("Total Units: 0");
        summaryPanel.add(totalUnitsLabel);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Method to refresh the study load table
        refreshStudyLoad = () -> {
            studyLoadModel.setRowCount(0);
            
            List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());
            int totalUnits = 0;
            
            for (Subject subject : enrolledSubjects) {
                // Get faculty name if assigned
                String facultyName = "";
                if (!subject.getAssignedFaculty().isEmpty()) {
                    User faculty = dbManager.getUser(subject.getAssignedFaculty());
                    if (faculty != null) {
                        facultyName = faculty.getUserInfo("name");
                    }
                }
                
                // Format schedules
                StringBuilder scheduleBuilder = new StringBuilder();
                for (Schedule schedule : subject.getSchedules()) {
                    if (scheduleBuilder.length() > 0) {
                        scheduleBuilder.append("; ");
                    }
                    scheduleBuilder.append(schedule.toString());
                }
                
                studyLoadModel.addRow(new Object[]{
                    subject.getId(),
                    subject.getName(),
                    subject.getUnits(),
                    scheduleBuilder.toString(),
                    facultyName
                });
                
                totalUnits += subject.getUnits();
            }
            
            totalUnitsLabel.setText("Total Units: " + totalUnits);
        };
        
        // Initial load of study load
        refreshStudyLoad.run();
        
        // Unenroll button
        JButton unenrollButton = new JButton("Unenroll from Selected Subject");
        unenrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studyLoadTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject to unenroll from", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = (String) studyLoadModel.getValueAt(selectedRow, 0);
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject == null) {
                    JOptionPane.showMessageDialog(panel, "Subject not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Confirm unenrollment
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to unenroll from " + subject.getName() + "?",
                    "Confirm Unenrollment",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Unenroll student
                    subject.unenrollStudent(studentUser.getUsername());
                    
                    // Refresh tables
                    refreshStudyLoad.run();
                    refreshTuition.run();
                    
                    JOptionPane.showMessageDialog(panel, "Successfully unenrolled from " + subject.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(unenrollButton);
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    // Tuition Fee Panel
    private JPanel createTuitionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for tuition breakdown
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Tuition Fee"};
        DefaultTableModel tuitionModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tuitionTable = new JTable(tuitionModel);
        JScrollPane scrollPane = new JScrollPane(tuitionTable);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tuition Summary"));
        
        JLabel totalSubjectsLabel = new JLabel("Number of Subjects:");
        JLabel totalSubjectsValueLabel = new JLabel("0");
        JLabel totalTuitionLabel = new JLabel("Total Tuition Fee:");
        JLabel totalTuitionValueLabel = new JLabel("$0.00");
        
        totalTuitionValueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalTuitionValueLabel.setForeground(new Color(0, 100, 0));
        
        summaryPanel.add(totalSubjectsLabel);
        summaryPanel.add(totalSubjectsValueLabel);
        summaryPanel.add(totalTuitionLabel);
        summaryPanel.add(totalTuitionValueLabel);
        
        // Add components to panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Method to refresh the tuition table
        refreshTuition = () -> {
            tuitionModel.setRowCount(0);
            
            List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());
            double totalTuition = 0;
            
            for (Subject subject : enrolledSubjects) {
                tuitionModel.addRow(new Object[]{
                    subject.getId(),
                    subject.getName(),
                    subject.getUnits(),
                    String.format("$%.2f", subject.getTuition())
                });
                
                totalTuition += subject.getTuition();
            }
            
            totalSubjectsValueLabel.setText(String.valueOf(enrolledSubjects.size()));
            totalTuitionValueLabel.setText(String.format("$%.2f", totalTuition));
        };
        
        // Initial load of tuition data
        refreshTuition.run();
        
        return panel;
    }
    
    // For refreshing panels from other tabs
    private Runnable refreshStudyLoad;
    private Runnable refreshTuition;
}

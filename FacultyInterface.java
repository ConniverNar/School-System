import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FacultyInterface extends JFrame {
    private DatabaseManager dbManager;
    private User facultyUser;
    private JTabbedPane tabbedPane;
    
    public FacultyInterface(DatabaseManager dbManager, User facultyUser) {
        this.dbManager = dbManager;
        this.facultyUser = facultyUser;
        
        setTitle("Faculty Interface - " + facultyUser.getUserInfo("name"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs for each functionality
        tabbedPane.addTab("Subject Assignment", createSubjectAssignmentPanel());
        tabbedPane.addTab("Course Load", createCourseLoadPanel());
        tabbedPane.addTab("Salary Information", createSalaryPanel());
        
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
    
    // Subject Assignment Panel
    private JPanel createSubjectAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Available subjects list on the left
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Subjects"));
        
        DefaultListModel<String> subjectListModel = new DefaultListModel<>();
        JList<String> subjectList = new JList<>(subjectListModel);
        JScrollPane subjectScrollPane = new JScrollPane(subjectList);
        
        leftPanel.add(subjectScrollPane, BorderLayout.CENTER);
        
        // Subject schedules on the right
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Subject Schedules"));
        
        DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
        JList<String> scheduleList = new JList<>(scheduleListModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
        
        JButton assignButton = new JButton("Assign Subject to Me");
        assignButton.setEnabled(false);
        
        rightPanel.add(scheduleScrollPane, BorderLayout.CENTER);
        rightPanel.add(assignButton, BorderLayout.SOUTH);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load the available subjects (those without assigned faculty)
        refreshAvailableSubjects(subjectListModel);
        
        // Event listeners
        subjectList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                    String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                    Subject subject = dbManager.getSubject(subjectId);
                    
                    if (subject != null) {
                        // Display schedules
                        scheduleListModel.clear();
                        for (Schedule schedule : subject.getSchedules()) {
                            scheduleListModel.addElement(schedule.toString());
                        }
                        
                        // Enable assign button if schedules exist
                        assignButton.setEnabled(!subject.getSchedules().isEmpty());
                    }
                }
            }
        });
        
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject != null) {
                    // Check if faculty already has a scheduling conflict
                    List<Subject> assignedSubjects = dbManager.getAssignedSubjects(facultyUser.getUsername());
                    boolean hasConflict = false;
                    
                    for (Schedule newSchedule : subject.getSchedules()) {
                        for (Subject assignedSubject : assignedSubjects) {
                            for (Schedule existingSchedule : assignedSubject.getSchedules()) {
                                // Simple conflict check: same day and overlapping times
                                if (existingSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek())) {
                                    // TODO: Implement more sophisticated time overlap check if needed
                                    hasConflict = true;
                                    break;
                                }
                            }
                            if (hasConflict) break;
                        }
                        if (hasConflict) break;
                    }
                    
                    if (hasConflict) {
                        JOptionPane.showMessageDialog(panel, 
                            "This subject has a schedule that conflicts with your existing assignments.", 
                            "Schedule Conflict", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Assign the subject to this faculty
                    subject.setAssignedFaculty(facultyUser.getUsername());
                    
                    // Refresh the available subjects list
                    refreshAvailableSubjects(subjectListModel);
                    scheduleListModel.clear();
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Subject '" + subject.getName() + "' has been assigned to you.", 
                        "Assignment Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    // Helper method to refresh available subjects
    private void refreshAvailableSubjects(DefaultListModel<String> model) {
        model.clear();
        List<Subject> subjects = dbManager.getAllSubjects();
        
        // Filter subjects in faculty's department and those without assigned faculty
        for (Subject subject : subjects) {
            if (subject.getDepartment().equals(facultyUser.getUserInfo("department")) && 
                (subject.getAssignedFaculty() == null || subject.getAssignedFaculty().isEmpty())) {
                model.addElement(subject.getId() + " - " + subject.getName());
            }
        }
    }
    
    // Course Load Panel
    private JPanel createCourseLoadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table for assigned subjects
        String[] columnNames = {"Subject ID", "Name", "Department", "Units", "Schedule"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        JTable subjectsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(subjectsTable);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Create panel for summary information
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalSubjectsLabel = new JLabel("Total Subjects: 0");
        JLabel totalUnitsLabel = new JLabel("Total Units: 0");
        
        summaryPanel.add(totalSubjectsLabel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(totalUnitsLabel);
        
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCourseLoad(tableModel, totalSubjectsLabel, totalUnitsLabel);
            }
        });
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Load the course load data
        refreshCourseLoad(tableModel, totalSubjectsLabel, totalUnitsLabel);
        
        return panel;
    }
    
    // Helper method to refresh course load data
    private void refreshCourseLoad(DefaultTableModel model, JLabel totalSubjectsLabel, JLabel totalUnitsLabel) {
        model.setRowCount(0);
        List<Subject> assignedSubjects = dbManager.getAssignedSubjects(facultyUser.getUsername());
        
        int totalUnits = 0;
        
        for (Subject subject : assignedSubjects) {
            // Combine all schedules into one string
            StringBuilder scheduleString = new StringBuilder();
            for (Schedule schedule : subject.getSchedules()) {
                if (scheduleString.length() > 0) {
                    scheduleString.append("; ");
                }
                scheduleString.append(schedule.toString());
            }
            
            model.addRow(new Object[]{
                subject.getId(),
                subject.getName(),
                subject.getDepartment(),
                subject.getUnits(),
                scheduleString.toString()
            });
            
            totalUnits += subject.getUnits();
        }
        
        totalSubjectsLabel.setText("Total Subjects: " + assignedSubjects.size());
        totalUnitsLabel.setText("Total Units: " + totalUnits);
    }
    
    // Salary Information Panel
    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for salary breakdown
        String[] columnNames = {"Subject ID", "Subject Name", "Salary"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        JTable salaryTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(salaryTable);
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        
        JLabel baseSalaryLabel = new JLabel("Base Salary: $0.00");
        JLabel subjectSalaryLabel = new JLabel("Subject Salary: $0.00");
        JLabel totalSalaryLabel = new JLabel("Total Salary: $0.00");
        totalSalaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        summaryPanel.add(baseSalaryLabel);
        summaryPanel.add(subjectSalaryLabel);
        summaryPanel.add(totalSalaryLabel);
        
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshSalaryInfo(tableModel, baseSalaryLabel, subjectSalaryLabel, totalSalaryLabel);
            }
        });
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Load the salary data
        refreshSalaryInfo(tableModel, baseSalaryLabel, subjectSalaryLabel, totalSalaryLabel);
        
        return panel;
    }
    
    // Helper method to refresh salary information
    private void refreshSalaryInfo(DefaultTableModel model, JLabel baseSalaryLabel, JLabel subjectSalaryLabel, JLabel totalSalaryLabel) {
        model.setRowCount(0);
        List<Subject> assignedSubjects = dbManager.getAssignedSubjects(facultyUser.getUsername());
        
        // Get base salary from user info
        String baseSalaryStr = facultyUser.getUserInfo("baseSalary");
        double baseSalary = 0;
        if (baseSalaryStr != null && !baseSalaryStr.isEmpty()) {
            try {
                baseSalary = Double.parseDouble(baseSalaryStr);
            } catch (NumberFormatException e) {
                baseSalary = 0;
            }
        }
        
        double totalSubjectSalary = 0;
        
        for (Subject subject : assignedSubjects) {
            model.addRow(new Object[]{
                subject.getId(),
                subject.getName(),
                String.format("$%.2f", subject.getSalary())
            });
            
            totalSubjectSalary += subject.getSalary();
        }
        
        // Update labels
        baseSalaryLabel.setText(String.format("Base Salary: $%.2f", baseSalary));
        subjectSalaryLabel.setText(String.format("Subject Salary: $%.2f", totalSubjectSalary));
        totalSalaryLabel.setText(String.format("Total Salary: $%.2f", baseSalary + totalSubjectSalary));
    }
}

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacultyInterface extends JFrame {
    private DatabaseManager dbManager;
    private User facultyUser;
    private JTabbedPane tabbedPane;
    
    // Map to track which schedules each faculty is assigned to for each subject
    // Key format: subjectId_scheduleIndex, Value: faculty username
    private Map<String, String> scheduleAssignments;
    
    public FacultyInterface(DatabaseManager dbManager, User facultyUser) {
        this.dbManager = dbManager;
        this.facultyUser = facultyUser;
        this.scheduleAssignments = new HashMap<>();
        
        // Load existing schedule assignments from database
        loadScheduleAssignments();
        
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
    
    // Load schedule assignments from database
    private void loadScheduleAssignments() {
        // This would ideally fetch from database - for now we'll simulate
        List<Subject> allSubjects = dbManager.getAllSubjects();
        for (Subject subject : allSubjects) {
            for (int i = 0; i < subject.getSchedules().size(); i++) {
                String key = subject.getId() + "_" + i;
                String assignedFaculty = dbManager.getScheduleAssignment(subject.getId(), i);
                if (assignedFaculty != null && !assignedFaculty.isEmpty()) {
                    scheduleAssignments.put(key, assignedFaculty);
                }
            }
        }
    }
    
    // Save schedule assignment to database
    private void saveScheduleAssignment(String subjectId, int scheduleIndex, String facultyUsername) {
        // This would save to database - for now we'll just update our local map
        String key = subjectId + "_" + scheduleIndex;
        if (facultyUsername == null) {
            scheduleAssignments.remove(key);
            dbManager.removeScheduleAssignment(subjectId, scheduleIndex);
        } else {
            scheduleAssignments.put(key, facultyUsername);
            dbManager.assignScheduleToFaculty(subjectId, scheduleIndex, facultyUsername);
        }
    }
    
    // Check if a schedule is assigned to any faculty
    private boolean isScheduleAssigned(String subjectId, int scheduleIndex) {
        String key = subjectId + "_" + scheduleIndex;
        return scheduleAssignments.containsKey(key);
    }
    
    // Get username of faculty assigned to a schedule
    private String getScheduleAssignedFaculty(String subjectId, int scheduleIndex) {
        String key = subjectId + "_" + scheduleIndex;
        return scheduleAssignments.get(key);
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
        
        // Table for schedule display with assignment status
        String[] columnNames = {"Schedule", "Assigned To", "Status"};
        DefaultTableModel scheduleTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable scheduleTable = new JTable(scheduleTableModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton assignButton = new JButton("Assign Selected Schedule");
        JButton removeButton = new JButton("Remove Assignment");
        assignButton.setEnabled(false);
        removeButton.setEnabled(false);
        
        buttonPanel.add(assignButton);
        buttonPanel.add(removeButton);
        
        rightPanel.add(scheduleScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load all subjects
        refreshAllSubjects(subjectListModel);
        
        // Event listeners
        subjectList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                    String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                    Subject subject = dbManager.getSubject(subjectId);
                    
                    if (subject != null) {
                        // Display schedules with their assignment status
                        scheduleTableModel.setRowCount(0);
                        
                        for (int i = 0; i < subject.getSchedules().size(); i++) {
                            Schedule schedule = subject.getSchedules().get(i);
                            String assignedFaculty = getScheduleAssignedFaculty(subject.getId(), i);
                            String status;
                            
                            if (assignedFaculty == null) {
                                status = "Available";
                            } else if (assignedFaculty.equals(facultyUser.getUsername())) {
                                status = "Assigned to you";
                            } else {
                                // Get the faculty name instead of just username
                                String facultyName = dbManager.getFacultyName(assignedFaculty);
                                status = "Assigned to other";
                            }
                            
                            scheduleTableModel.addRow(new Object[]{
                                schedule.toString(),
                                assignedFaculty != null ? dbManager.getFacultyName(assignedFaculty) : "None",
                                status
                            });
                        }
                        
                        // Enable both buttons by default, they'll be disabled in selection handler if needed
                        assignButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                }
            }
        });
        
        // Schedule table selection listener
        scheduleTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && scheduleTable.getSelectedRow() != -1) {
                    int selectedRow = scheduleTable.getSelectedRow();
                    String status = (String) scheduleTable.getValueAt(selectedRow, 2);
                    
                    // Set button states based on selected schedule status
                    assignButton.setEnabled(status.equals("Available"));
                    removeButton.setEnabled(status.equals("Assigned to you"));
                }
            }
        });
        
        // Add remove button action listener
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null || scheduleTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject and schedule", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                int scheduleIndex = scheduleTable.getSelectedRow();
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject != null) {
                    // Verify schedule is assigned to this faculty
                    String assignedFaculty = getScheduleAssignedFaculty(subjectId, scheduleIndex);
                    if (assignedFaculty == null || !assignedFaculty.equals(facultyUser.getUsername())) {
                        JOptionPane.showMessageDialog(panel, 
                            "This schedule is not assigned to you.", 
                            "Cannot Remove", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Remove assignment
                    saveScheduleAssignment(subjectId, scheduleIndex, null);
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Schedule removed from your assignments.", 
                        "Removal Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh views
                    refreshAllSubjects(subjectListModel);
                    
                    // Re-select the subject to refresh its schedules
                    if (subjectList.getSelectedValue() != null) {
                        subjectList.setSelectedValue(subjectList.getSelectedValue(), true);
                    }
                }
            }
        });
        
        assignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null || scheduleTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject and schedule", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                int scheduleIndex = scheduleTable.getSelectedRow();
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject != null) {
                    // Check if schedule is already assigned
                    if (isScheduleAssigned(subjectId, scheduleIndex)) {
                        JOptionPane.showMessageDialog(panel, 
                            "This schedule is already assigned to another faculty member.",
                            "Schedule Unavailable", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Check for schedule conflicts with existing assignments
                    Schedule newSchedule = subject.getSchedules().get(scheduleIndex);
                    String conflictingInfo = checkForScheduleConflicts(newSchedule);
                    
                    if (conflictingInfo != null) {
                        JOptionPane.showMessageDialog(panel, 
                            "Schedule conflict detected: " + conflictingInfo, 
                            "Schedule Conflict", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Assign the schedule to this faculty
                    saveScheduleAssignment(subjectId, scheduleIndex, facultyUser.getUsername());
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Schedule for '" + subject.getName() + "' has been assigned to you.", 
                        "Assignment Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the views
                    refreshAllSubjects(subjectListModel);
                    
                    // Re-select the subject to refresh its schedules
                    if (subjectList.getSelectedValue() != null) {
                        subjectList.setSelectedValue(subjectList.getSelectedValue(), true);
                    }
                }
            }
        });
        
        return panel;
    }
    
    // Check for schedule conflicts with faculty's existing assignments
    private String checkForScheduleConflicts(Schedule newSchedule) {
        List<Subject> allSubjects = dbManager.getAllSubjects();
        
        for (Subject subject : allSubjects) {
            for (int i = 0; i < subject.getSchedules().size(); i++) {
                // Check if this schedule is assigned to this faculty
                String assignedFaculty = getScheduleAssignedFaculty(subject.getId(), i);
                
                if (assignedFaculty != null && assignedFaculty.equals(facultyUser.getUsername())) {
                    Schedule existingSchedule = subject.getSchedules().get(i);
                    
                    // Check for time conflict
                    if (existingSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek()) &&
                        schedulesOverlap(existingSchedule, newSchedule)) {
                        return subject.getName() + " (" + existingSchedule.toString() + ")";
                    }
                }
            }
        }
        
        return null; // No conflicts
    }
    
    // Helper method to check if two schedules overlap in time
    private boolean schedulesOverlap(Schedule s1, Schedule s2) {
        // Only check for overlap if they're on the same day
        if (!s1.getDayOfWeek().equals(s2.getDayOfWeek())) {
            return false;
        }
        
        // Extract start and end times
        String[] s1StartParts = s1.getStartTime().split(":");
        String[] s1EndParts = s1.getEndTime().split(":");
        String[] s2StartParts = s2.getStartTime().split(":");
        String[] s2EndParts = s2.getEndTime().split(":");
        
        // Convert to minutes since midnight for easier comparison
        int s1Start = Integer.parseInt(s1StartParts[0]) * 60 + Integer.parseInt(s1StartParts[1]);
        int s1End = Integer.parseInt(s1EndParts[0]) * 60 + Integer.parseInt(s1EndParts[1]);
        int s2Start = Integer.parseInt(s2StartParts[0]) * 60 + Integer.parseInt(s2StartParts[1]);
        int s2End = Integer.parseInt(s2EndParts[0]) * 60 + Integer.parseInt(s2EndParts[1]);
        
        // Check for overlap: one schedule starts during the other schedule
        return (s1Start <= s2Start && s2Start < s1End) || (s2Start <= s1Start && s1Start < s2End);
    }
    
    // Helper method to refresh all subjects list
    private void refreshAllSubjects(DefaultListModel<String> model) {
        model.clear();
        List<Subject> subjects = dbManager.getAllSubjects();
        
        // Show all subjects in faculty's department
        for (Subject subject : subjects) {
            if (subject.getDepartment().equals(facultyUser.getUserInfo("department"))) {
                // Add visual indicator if any schedules are assigned to this faculty
                boolean hasAssignedSchedules = false;
                for (int i = 0; i < subject.getSchedules().size(); i++) {
                    String assignedFaculty = getScheduleAssignedFaculty(subject.getId(), i);
                    if (assignedFaculty != null && assignedFaculty.equals(facultyUser.getUsername())) {
                        hasAssignedSchedules = true;
                        break;
                    }
                }
                
                String assignmentStatus = hasAssignedSchedules ? " ✓" : "";
                model.addElement(subject.getId() + " - " + subject.getName() + assignmentStatus);
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
        JLabel totalSchedulesLabel = new JLabel("Total Schedules: 0");
        
        summaryPanel.add(totalSubjectsLabel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(totalUnitsLabel);
        summaryPanel.add(Box.createHorizontalStrut(30));
        summaryPanel.add(totalSchedulesLabel);
        
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCourseLoad(tableModel, totalSubjectsLabel, totalUnitsLabel, totalSchedulesLabel);
            }
        });
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Load the course load data
        refreshCourseLoad(tableModel, totalSubjectsLabel, totalUnitsLabel, totalSchedulesLabel);
        
        return panel;
    }
    
    // Helper method to refresh course load data
    private void refreshCourseLoad(DefaultTableModel model, JLabel totalSubjectsLabel, 
                                JLabel totalUnitsLabel, JLabel totalSchedulesLabel) {
        model.setRowCount(0);
        List<Subject> allSubjects = dbManager.getAllSubjects();
        
        // Use a map to group schedules by subject ID
        Map<String, List<Schedule>> subjectSchedules = new HashMap<>();
        Map<String, Subject> subjectMap = new HashMap<>();
        
        int totalSchedules = 0;
        int totalUnits = 0;
        
        // First collect all assigned schedules
        for (Subject subject : allSubjects) {
            boolean hasAssignedSchedule = false;
            List<Schedule> assignedSchedules = new ArrayList<>();
            
            for (int i = 0; i < subject.getSchedules().size(); i++) {
                String assignedFaculty = getScheduleAssignedFaculty(subject.getId(), i);
                if (assignedFaculty != null && assignedFaculty.equals(facultyUser.getUsername())) {
                    assignedSchedules.add(subject.getSchedules().get(i));
                    hasAssignedSchedule = true;
                    totalSchedules++;
                }
            }
            
            if (hasAssignedSchedule) {
                subjectSchedules.put(subject.getId(), assignedSchedules);
                subjectMap.put(subject.getId(), subject);
                totalUnits += subject.getUnits();
            }
        }
        
        // Now add them to the table
        for (String subjectId : subjectSchedules.keySet()) {
            Subject subject = subjectMap.get(subjectId);
            List<Schedule> schedules = subjectSchedules.get(subjectId);
            
            StringBuilder scheduleString = new StringBuilder();
            for (Schedule schedule : schedules) {
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
        }
        
        totalSubjectsLabel.setText("Total Subjects: " + subjectSchedules.size());
        totalUnitsLabel.setText("Total Units: " + totalUnits);
        totalSchedulesLabel.setText("Total Schedules: " + totalSchedules);
    }
    
    // Salary Information Panel
    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for salary breakdown
        String[] columnNames = {"Subject ID", "Subject Name", "Schedules", "Salary per Schedule", "Total"};
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
        
        JLabel baseSalaryLabel = new JLabel("Base Salary: ₱0.00");
        JLabel subjectSalaryLabel = new JLabel("Subject Salary: ₱0.00");
        JLabel totalSalaryLabel = new JLabel("Total Salary: ₱0.00");
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
    private void refreshSalaryInfo(DefaultTableModel model, JLabel baseSalaryLabel, 
                               JLabel subjectSalaryLabel, JLabel totalSalaryLabel) {
        model.setRowCount(0);
        List<Subject> allSubjects = dbManager.getAllSubjects();
        
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
        
        // Process each subject
        for (Subject subject : allSubjects) {
            int assignedSchedulesCount = 0;
            
            // Count assigned schedules for this subject
            for (int i = 0; i < subject.getSchedules().size(); i++) {
                String assignedFaculty = getScheduleAssignedFaculty(subject.getId(), i);
                if (assignedFaculty != null && assignedFaculty.equals(facultyUser.getUsername())) {
                    assignedSchedulesCount++;
                }
            }
            
            if (assignedSchedulesCount > 0) {
                // Calculate salary based on number of assigned schedules
                double salaryPerSchedule = subject.getSalary() / subject.getSchedules().size();
                double totalSubjectSalaryForThisFaculty = salaryPerSchedule * assignedSchedulesCount;
                
                model.addRow(new Object[]{
                    subject.getId(),
                    subject.getName(),
                    assignedSchedulesCount,
                    String.format("₱%.2f", salaryPerSchedule),
                    String.format("₱%.2f", totalSubjectSalaryForThisFaculty)
                });
                
                totalSubjectSalary += totalSubjectSalaryForThisFaculty;
            }
        }
        
        // Update labels
        baseSalaryLabel.setText(String.format("Base Salary: ₱%.2f", baseSalary));
        subjectSalaryLabel.setText(String.format("Subject Salary: ₱%.2f", totalSubjectSalary));
        totalSalaryLabel.setText(String.format("Total Salary: ₱%.2f", baseSalary + totalSubjectSalary));
    }
}

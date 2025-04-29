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
    private Map<String, List<Integer>> facultyScheduleAssignments;
    
    public FacultyInterface(DatabaseManager dbManager, User facultyUser) {
        this.dbManager = dbManager;
        this.facultyUser = facultyUser;
        this.facultyScheduleAssignments = new HashMap<>();
        
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
        
        // Change to checkboxes for multiple schedule selection
        DefaultListModel<ScheduleItem> scheduleListModel = new DefaultListModel<>();
        JList<ScheduleItem> scheduleList = new JList<>(scheduleListModel);
        scheduleList.setCellRenderer(new CheckboxListRenderer());
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton assignButton = new JButton("Assign Selected Schedules");
        assignButton.setEnabled(false);
        JButton removeButton = new JButton("Remove Selected Schedules");
        removeButton.setEnabled(false);
        
        buttonPanel.add(assignButton);
        buttonPanel.add(removeButton);
        
        rightPanel.add(scheduleScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load all subjects (including those with assigned faculty)
        refreshAllSubjects(subjectListModel);
        
        // Event listeners
        subjectList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                    String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                    Subject subject = dbManager.getSubject(subjectId);
                    
                    if (subject != null) {
                        boolean isAssignedToCurrentFaculty = subject.getAssignedFaculty() != null && 
                                                           subject.getAssignedFaculty().equals(facultyUser.getUsername());
                        boolean isAssignedToOtherFaculty = subject.getAssignedFaculty() != null && 
                                                         !subject.getAssignedFaculty().isEmpty() && 
                                                         !subject.getAssignedFaculty().equals(facultyUser.getUsername());
                        
                        // Display schedules with checkboxes
                        scheduleListModel.clear();
                        for (int i = 0; i < subject.getSchedules().size(); i++) {
                            Schedule schedule = subject.getSchedules().get(i);
                            
                            // Check if this specific schedule is assigned to this faculty
                            boolean scheduleSelected = false;
                            String key = subject.getId() + "_" + facultyUser.getUsername();
                            if (facultyScheduleAssignments.containsKey(key)) {
                                scheduleSelected = facultyScheduleAssignments.get(key).contains(i);
                            }
                            
                            ScheduleItem item = new ScheduleItem(schedule, i, scheduleSelected);
                            scheduleListModel.addElement(item);
                        }
                        
                        // Enable/disable buttons based on subject assignment status
                        assignButton.setEnabled(!subject.getSchedules().isEmpty() && !isAssignedToOtherFaculty);
                        removeButton.setEnabled(isAssignedToCurrentFaculty && !subject.getSchedules().isEmpty());
                    }
                }
            }
        });
        
        // Schedule list mouse listener for toggling checkboxes
        scheduleList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = scheduleList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    ScheduleItem item = scheduleListModel.getElementAt(index);
                    item.setSelected(!item.isSelected());
                    scheduleList.repaint();
                }
            }
        });
        
        // Add remove button action listener
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from list display
                Subject subject = dbManager.getSubject(subjectId);
                
                if (subject != null) {
                    // Verify subject is assigned to this faculty
                    if (!facultyUser.getUsername().equals(subject.getAssignedFaculty())) {
                        JOptionPane.showMessageDialog(panel, 
                            "This subject is not assigned to you.", 
                            "Cannot Remove", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Get selected schedules to remove
                    List<Integer> schedulesToRemove = new ArrayList<>();
                    for (int i = 0; i < scheduleListModel.size(); i++) {
                        ScheduleItem item = scheduleListModel.getElementAt(i);
                        if (item.isSelected()) {
                            schedulesToRemove.add(item.getIndex());
                        }
                    }
                    
                    if (schedulesToRemove.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, 
                            "Please select at least one schedule to remove", 
                            "No Selection", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Remove selected schedules from faculty assignments
                    String key = subject.getId() + "_" + facultyUser.getUsername();
                    List<Integer> currentAssignments = facultyScheduleAssignments.getOrDefault(key, new ArrayList<>());
                    currentAssignments.removeAll(schedulesToRemove);
                    
                    // If no schedules remain, remove the faculty from the subject entirely
                    if (currentAssignments.isEmpty()) {
                        subject.removeAssignedFaculty();
                        facultyScheduleAssignments.remove(key);
                        
                        JOptionPane.showMessageDialog(panel, 
                            "All schedules removed. You are no longer assigned to subject '" + subject.getName() + "'.", 
                            "Removal Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        facultyScheduleAssignments.put(key, currentAssignments);
                        
                        JOptionPane.showMessageDialog(panel, 
                            schedulesToRemove.size() + " schedule(s) removed from subject '" + subject.getName() + "'.", 
                            "Removal Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    // Refresh views
                    refreshAllSubjects(subjectListModel);
                    
                    // Also refresh the schedules list for the currently selected subject
                    if (subjectList.getSelectedValue() != null) {
                        String selectedSubjectId = subjectList.getSelectedValue().split(" ")[0];
                        if (selectedSubjectId.equals(subjectId)) {
                            // Re-select the subject to refresh its schedules
                            subjectList.setSelectedValue(subjectList.getSelectedValue(), true);
                        }
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
                    // Check if subject is already assigned to another faculty
                    if (subject.getAssignedFaculty() != null && 
                        !subject.getAssignedFaculty().isEmpty() && 
                        !subject.getAssignedFaculty().equals(facultyUser.getUsername())) {
                        JOptionPane.showMessageDialog(panel, 
                            "This subject is already assigned to another faculty member.",
                            "Subject Unavailable", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Get selected schedules
                    List<Integer> selectedScheduleIndices = new ArrayList<>();
                    for (int i = 0; i < scheduleListModel.size(); i++) {
                        ScheduleItem item = scheduleListModel.getElementAt(i);
                        if (item.isSelected()) {
                            selectedScheduleIndices.add(item.getIndex());
                        }
                    }
                    
                    if (selectedScheduleIndices.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, 
                            "Please select at least one schedule", 
                            "No Selection", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Check for schedule conflicts with existing assignments
                    boolean hasConflict = false;
                    String conflictingSubject = "";
                    String conflictingSchedule = "";
                    
                    List<Subject> assignedSubjects = dbManager.getAssignedSubjects(facultyUser.getUsername());
                    
                    for (Integer scheduleIndex : selectedScheduleIndices) {
                        Schedule newSchedule = subject.getSchedules().get(scheduleIndex);
                        
                        for (Subject assignedSubject : assignedSubjects) {
                            // Skip checking against the same subject (when updating schedules)
                            if (assignedSubject.getId().equals(subject.getId())) {
                                continue;
                            }
                            
                            // Check only assigned schedules for this faculty
                            String key = assignedSubject.getId() + "_" + facultyUser.getUsername();
                            List<Integer> assignedIndices = facultyScheduleAssignments.getOrDefault(key, new ArrayList<>());
                            
                            for (Integer assignedIndex : assignedIndices) {
                                if (assignedIndex < assignedSubject.getSchedules().size()) {
                                    Schedule existingSchedule = assignedSubject.getSchedules().get(assignedIndex);
                                    // Check for actual time overlap, not just same day
                                    if (existingSchedule.getDayOfWeek().equals(newSchedule.getDayOfWeek()) &&
                                        schedulesOverlap(existingSchedule, newSchedule)) {
                                        hasConflict = true;
                                        conflictingSubject = assignedSubject.getName();
                                        conflictingSchedule = existingSchedule.toString();
                                        break;
                                    }
                                }
                            }
                            if (hasConflict) break;
                        }
                        if (hasConflict) break;
                    }
                    
                    if (hasConflict) {
                        JOptionPane.showMessageDialog(panel, 
                            "Schedule conflict detected with " + conflictingSubject + 
                            " (" + conflictingSchedule + ").", 
                            "Schedule Conflict", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Assign the subject to this faculty
                    subject.setAssignedFaculty(facultyUser.getUsername());
                    
                    // Store which schedules this faculty is assigned to
                    String key = subject.getId() + "_" + facultyUser.getUsername();
                    facultyScheduleAssignments.put(key, selectedScheduleIndices);
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Subject '" + subject.getName() + "' has been assigned to you with " + 
                        selectedScheduleIndices.size() + " schedule(s).", 
                        "Assignment Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the subject list to show updated assignments
                    refreshAllSubjects(subjectListModel);
                    
                    // Also refresh the schedules list for the currently selected subject
                    if (subjectList.getSelectedValue() != null) {
                        String selectedSubjectId = subjectList.getSelectedValue().split(" ")[0];
                        if (selectedSubjectId.equals(subjectId)) {
                            // Re-select the subject to refresh its schedules
                            subjectList.setSelectedValue(subjectList.getSelectedValue(), true);
                        }
                    }
                }
            }
        });
        
        return panel;
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
    
    // Helper method to refresh all subjects (not just available ones)
    private void refreshAllSubjects(DefaultListModel<String> model) {
        model.clear();
        List<Subject> subjects = dbManager.getAllSubjects();
        
        // Show all subjects in faculty's department
        for (Subject subject : subjects) {
            if (subject.getDepartment().equals(facultyUser.getUserInfo("department"))) {
                // Add visual indicator if the subject is already assigned to this faculty
                String assignmentStatus = "";
                if (subject.getAssignedFaculty() != null && !subject.getAssignedFaculty().isEmpty()) {
                    if (subject.getAssignedFaculty().equals(facultyUser.getUsername())) {
                        assignmentStatus = " ✓"; // Checkmark for subjects assigned to current faculty
                    } else {
                        assignmentStatus = " (Assigned to another faculty)"; // For subjects assigned to other faculty
                    }
                }
                model.addElement(subject.getId() + " - " + subject.getName() + assignmentStatus);
            }
        }
    }
    
    // Inner class for schedule items with checkboxes
    private class ScheduleItem {
        private Schedule schedule;
        private int index;
        private boolean selected;
        
        public ScheduleItem(Schedule schedule, int index, boolean selected) {
            this.schedule = schedule;
            this.index = index;
            this.selected = selected;
        }
        
        public Schedule getSchedule() {
            return schedule;
        }
        
        public int getIndex() {
            return index;
        }
        
        public boolean isSelected() {
            return selected;
        }
        
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
        
        @Override
        public String toString() {
            return schedule.toString();
        }
    }
    
    // Custom renderer for checkbox list
    private class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<ScheduleItem> {
        @Override
        public Component getListCellRendererComponent(
                JList<? extends ScheduleItem> list, ScheduleItem value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            setText(value.toString());
            setSelected(value.isSelected());
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
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
            // Only show schedules that this faculty is assigned to
            String key = subject.getId() + "_" + facultyUser.getUsername();
            List<Integer> assignedScheduleIndices = facultyScheduleAssignments.getOrDefault(key, new ArrayList<>());
            
            // If there are no specifically assigned schedules, continue to next subject
            if (assignedScheduleIndices.isEmpty()) {
                continue;
            }
            
            // Get assigned schedules only
            StringBuilder scheduleString = new StringBuilder();
            for (Integer index : assignedScheduleIndices) {
                if (index < subject.getSchedules().size()) {
                    Schedule schedule = subject.getSchedules().get(index);
                    if (scheduleString.length() > 0) {
                        scheduleString.append("; ");
                    }
                    scheduleString.append(schedule.toString());
                }
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
        
        totalSubjectsLabel.setText("Total Subjects: " + model.getRowCount());
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
                String.format("₱%.2f", subject.getSalary())
            });
            
            totalSubjectSalary += subject.getSalary();
        }
        
        // Update labels
        baseSalaryLabel.setText(String.format("Base Salary: ₱%.2f", baseSalary));
        subjectSalaryLabel.setText(String.format("Subject Salary: ₱%.2f", totalSubjectSalary));
        totalSalaryLabel.setText(String.format("Total Salary: ₱%.2f", baseSalary + totalSubjectSalary));
    }
}

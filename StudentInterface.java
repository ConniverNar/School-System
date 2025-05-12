 import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        panel.setBackground(new Color(0xFFF8DC)); // Light cream/pale yellow background

        // Filter options at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(0xFFF8DC));

        // Get the student's department
        String studentDepartment = studentUser.getUserInfo("department");

        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setForeground(new Color(0x59361A)); // Dark brown text
        departmentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16)); // Serif font, regular, ~16pt

        JTextField departmentField = new JTextField(studentDepartment, 15);
        departmentField.setEditable(false);
        departmentField.setBackground(Color.WHITE);
        departmentField.setForeground(new Color(0x59361A));
        departmentField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        departmentField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Thin gray border

        JButton filterButton = new JButton("Show Available Subjects");
        filterButton.setBackground(new Color(0x664229)); // Medium-dark brown button
        filterButton.setForeground(Color.WHITE);
        filterButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        filterButton.setFocusPainted(false);

        filterPanel.add(departmentLabel);
        filterPanel.add(departmentField);
        filterPanel.add(filterButton);

        // Available subjects list in the center
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Prerequisites", "Available Schedules"};
        DefaultTableModel availableSubjectsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable availableSubjectsTable = new JTable(availableSubjectsModel);
        JScrollPane availableScrollPane = new JScrollPane(availableSubjectsTable);
        TitledBorder availableBorder = BorderFactory.createTitledBorder("Available Subjects");
        availableBorder.setTitleColor(new Color(0x59361A));
        availableBorder.setTitleFont(new Font("Times New Roman", Font.BOLD, 22));
        availableScrollPane.setBorder(availableBorder);

        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(new Color(0xFFF8DC));
        TitledBorder scheduleBorder = BorderFactory.createTitledBorder("Available Schedules");
        scheduleBorder.setTitleColor(new Color(0x59361A));
        scheduleBorder.setTitleFont(new Font("Times New Roman", Font.BOLD, 22));
        schedulePanel.setBorder(scheduleBorder);

        DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
        JList<String> scheduleList = new JList<>(scheduleListModel);
        scheduleList.setBackground(Color.WHITE);
        scheduleList.setForeground(new Color(0x59361A));
        scheduleList.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        scheduleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Allow multiple selection
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
        schedulePanel.add(scheduleScrollPane, BorderLayout.CENTER);
        schedulePanel.setVisible(false); // Initially hidden

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                availableScrollPane, schedulePanel);
        splitPane.setResizeWeight(0.7);
        schedulePanel.setPreferredSize(new Dimension(800, 150)); // Set preferred size for visibility

        // Action panel at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setBackground(new Color(0xFFF8DC));

        JButton enrollButton = new JButton("Enroll in Selected Subject");
        enrollButton.setBackground(new Color(0x664229));
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        enrollButton.setFocusPainted(false);

        JButton batchEnrollButton = new JButton("Enroll in Multiple Subjects");
        batchEnrollButton.setBackground(new Color(0x664229));
        batchEnrollButton.setForeground(Color.WHITE);
        batchEnrollButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        batchEnrollButton.setFocusPainted(false);

        actionPanel.add(enrollButton);
        actionPanel.add(batchEnrollButton);

        // Add action listener for enrollButton
        enrollButton.addActionListener(e -> {
            int selectedRow = availableSubjectsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a subject to enroll in",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String subjectId = (String) availableSubjectsModel.getValueAt(selectedRow, 0);
            Subject subject = dbManager.getSubject(subjectId);

            if (subject == null) {
                JOptionPane.showMessageDialog(panel, "Selected subject not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if student meets prerequisites
            if (!dbManager.hasOrIsEnrollingInPrerequisites(studentUser.getUsername(), subject, new ArrayList<>())) {
                JOptionPane.showMessageDialog(panel,
                        "Cannot enroll in " + subject.getName() + " due to missing prerequisites.",
                        "Prerequisites Not Met", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Enroll student in the selected schedules if any, else default to first schedule or no schedule
            int[] selectedScheduleIndices;
            List<Schedule> schedules = subject.getSchedules();
            if (!schedules.isEmpty()) {
                if (schedulePanel.isVisible() && !scheduleList.isSelectionEmpty()) {
                    // Map selected indices in scheduleListModel to actual schedule indices in subject.getSchedules()
                    List<Integer> actualScheduleIndices = new ArrayList<>();
                    List<Schedule> studentSchedules = subject.getStudentSchedules(studentUser.getUsername());
                    int modelIndex = 0;
                    for (int i = 0; i < schedules.size(); i++) {
                        Schedule schedule = schedules.get(i);
                        if (studentSchedules.contains(schedule)) {
                            continue; // skipped in scheduleListModel
                        }
                        if (scheduleList.isSelectedIndex(modelIndex)) {
                            actualScheduleIndices.add(i);
                        }
                        modelIndex++;
                    }
                    selectedScheduleIndices = actualScheduleIndices.stream().mapToInt(Integer::intValue).toArray();
                } else {
                    selectedScheduleIndices = new int[]{0}; // default to first schedule
                }
            } else {
                selectedScheduleIndices = new int[0]; // no schedule
            }

            // Get all currently enrolled subjects with their schedules
            List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());

            // Check for conflicts with existing enrollments for each selected schedule
            for (int scheduleIndex : selectedScheduleIndices) {
                Schedule newSchedule = subject.getSchedules().get(scheduleIndex);

                for (Subject enrolledSubject : enrolledSubjects) {
                    List<Schedule> enrolledSchedules = enrolledSubject.getStudentSchedules(studentUser.getUsername());
                    for (Schedule enrolledSchedule : enrolledSchedules) {
                        if (enrolledSchedule != null && !enrolledSchedule.equals(newSchedule) && newSchedule.conflictsWith(enrolledSchedule)) {
                            JOptionPane.showMessageDialog(panel,
                                    "Cannot enroll in " + subject.getName() + " due to schedule conflict with " +
                                    enrolledSubject.getName() + ".\n\n" +
                                    "Conflict details:\n" +
                                    "- New schedule: " + newSchedule.toString() + "\n" +
                                    "- Conflicting schedule: " + enrolledSchedule.toString(),
                                    "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }

            // Enroll student in all selected schedules
            for (int scheduleIndex : selectedScheduleIndices) {
                subject.enrollStudent(studentUser.getUsername(), scheduleIndex);
            }

            JOptionPane.showMessageDialog(panel,
                    "Successfully enrolled in " + subject.getName(),
                    "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);

            // Refresh available subjects and other views
            refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
            refreshStudyLoad.run();
            refreshTuition.run();
        });

        // Add components to panel
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // Variable to track currently selected subject
        final Subject[] selectedSubject = {null};

        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
                schedulePanel.setVisible(false);
                scheduleListModel.clear();
            }
        });

        // Subject selection listener
        availableSubjectsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = availableSubjectsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String subjectId = (String) availableSubjectsModel.getValueAt(selectedRow, 0);
                    selectedSubject[0] = dbManager.getSubject(subjectId);

                    if (selectedSubject[0] != null) {
                        // Show available schedules
                        scheduleListModel.clear();
                        List<Schedule> schedules = selectedSubject[0].getSchedules();

                        // Get enrolled subjects to check for potential conflicts
                        List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());

                        // Get student's enrolled schedules for this subject
                        List<Schedule> studentSchedules = selectedSubject[0].getStudentSchedules(studentUser.getUsername());

                        for (int i = 0; i < schedules.size(); i++) {
                            Schedule schedule = schedules.get(i);

                            // Skip schedules the student is already enrolled in for this subject
                            if (studentSchedules.contains(schedule)) {
                                continue;
                            }

                            boolean hasConflict = false;
                            String conflictInfo = "";

                            // Check if this schedule conflicts with any enrolled subject
                            for (Subject enrolledSubject : enrolledSubjects) {
                                Schedule enrolledSchedule = enrolledSubject.getStudentSchedule(studentUser.getUsername());
                                if (enrolledSchedule != null && schedule.conflictsWith(enrolledSchedule)) {
                                    hasConflict = true;
                                    conflictInfo = " (CONFLICTS with " + enrolledSubject.getName() + ")";
                                    break;
                                }
                            }

                    // Add schedule to list with conflict warning if applicable
                    scheduleListModel.addElement("Schedule " + (i + 1) + ": " + schedules.get(i).toString() +
                            (hasConflict ? conflictInfo : ""));
                }
                schedulePanel.setVisible(scheduleListModel.getSize() > 0);
                scheduleList.setEnabled(scheduleListModel.getSize() > 0); // Enable schedule list if schedules exist
                schedulePanel.revalidate();
                schedulePanel.repaint();
                if (scheduleListModel.getSize() > 0) {
                    splitPane.setDividerLocation(0.7); // Ensure divider shows schedule panel
                }
                    } else {
                        schedulePanel.setVisible(false);
                        scheduleListModel.clear();
                        schedulePanel.revalidate();
                        schedulePanel.repaint();
                    }
                } else {
                    schedulePanel.setVisible(false);
                    scheduleListModel.clear();
                    schedulePanel.revalidate();
                    schedulePanel.repaint();
                }
            }
        });

        // Batch enrollment button action for simultaneous enrollment in a subject and its prerequisite
        batchEnrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog for batch enrollment
                JDialog batchDialog = new JDialog(StudentInterface.this, "Batch Enrollment", true);
                batchDialog.setSize(600, 400);
                batchDialog.setLocationRelativeTo(StudentInterface.this);

                // Create a panel with checkboxes for available subjects
                JPanel subjectsPanel = new JPanel();
                subjectsPanel.setLayout(new BoxLayout(subjectsPanel, BoxLayout.Y_AXIS));

                List<Subject> availableSubjects = dbManager.getSubjectsByDepartment(studentDepartment);
                List<JCheckBox> subjectCheckboxes = new ArrayList<>();
                Map<String, JComboBox<String>> scheduleSelections = new HashMap<>();

                // Add checkboxes for each available subject
                for (Subject subject : availableSubjects) {
                    // Skip subjects already enrolled in
                    if (subject.getEnrolledStudents().contains(studentUser.getUsername())) {
                        continue;
                    }

                    JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    JCheckBox checkbox = new JCheckBox(subject.getId() + " - " + subject.getName());
                    subjectCheckboxes.add(checkbox);
                    subjectPanel.add(checkbox);

                    // Add schedule selection combobox if the subject has schedules
                    if (!subject.getSchedules().isEmpty()) {
                        JComboBox<String> scheduleCombo = new JComboBox<>();
                        
                        // Get enrolled subjects to check for potential conflicts
                        List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());
                        
                        for (int i = 0; i < subject.getSchedules().size(); i++) {
                            Schedule schedule = subject.getSchedules().get(i);
                            boolean hasConflict = false;
                            String conflictInfo = "";
                            
                            // Check if this schedule conflicts with any enrolled subject
                            for (Subject enrolledSubject : enrolledSubjects) {
                                Schedule enrolledSchedule = enrolledSubject.getStudentSchedule(studentUser.getUsername());
                                if (enrolledSchedule != null && schedule.conflictsWith(enrolledSchedule)) {
                                    hasConflict = true;
                                    conflictInfo = " (CONFLICTS with " + enrolledSubject.getName() + ")";
                                    break;
                                }
                            }
                            
                            scheduleCombo.addItem("Schedule " + (i + 1) + 
                                                (hasConflict ? conflictInfo : ""));
                        }
                        
                        scheduleSelections.put(subject.getId(), scheduleCombo);
                        subjectPanel.add(scheduleCombo);
                    }

                    subjectsPanel.add(subjectPanel);
                }

                JScrollPane scrollPane = new JScrollPane(subjectsPanel);

                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton enrollSelectedButton = new JButton("Enroll in Selected Subjects");
                JButton cancelButton = new JButton("Cancel");
                bottomPanel.add(cancelButton);
                bottomPanel.add(enrollSelectedButton);

                batchDialog.setLayout(new BorderLayout());
                batchDialog.add(new JLabel("Select subjects to enroll in:"), BorderLayout.NORTH);
                batchDialog.add(scrollPane, BorderLayout.CENTER);
                batchDialog.add(bottomPanel, BorderLayout.SOUTH);

                // Cancel button action
                cancelButton.addActionListener(ae -> batchDialog.dispose());

                // Enroll selected button action
                enrollSelectedButton.addActionListener(ae -> {
                    List<String> selectedSubjectIds = new ArrayList<>();
                    Map<String, Integer> selectedSchedules = new HashMap<>();

                    // Collect selected subjects
                    for (JCheckBox checkbox : subjectCheckboxes) {
                        if (checkbox.isSelected()) {
                            String subjectId = checkbox.getText().split(" - ")[0];
                            selectedSubjectIds.add(subjectId);

                            // Get selected schedule
                            JComboBox<String> scheduleCombo = scheduleSelections.get(subjectId);
                            if (scheduleCombo != null) {
                                selectedSchedules.put(subjectId, scheduleCombo.getSelectedIndex());
                            } else {
                                selectedSchedules.put(subjectId, 0); // Default to first schedule
                            }
                        }
                    }

                    // Process enrollments
                    if (selectedSubjectIds.isEmpty()) {
                        JOptionPane.showMessageDialog(batchDialog,
                                "Please select at least one subject",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate and order batch enrollment
                    if (!validateAndOrderBatchEnrollment(selectedSubjectIds)) {
                        return;
                    }

                    // Check prerequisites with the batch of subjects
                    boolean allPrerequisitesMet = true;
                    StringBuilder errorMessage = new StringBuilder("Missing prerequisites:\n");

                    for (String subjectId : selectedSubjectIds) {
                        Subject subject = dbManager.getSubject(subjectId);
                        if (!dbManager.hasOrIsEnrollingInPrerequisites(
                                studentUser.getUsername(), subject, selectedSubjectIds)) {
                            allPrerequisitesMet = false;
                            errorMessage.append("- ").append(subject.getName()).append("\n");
                        }
                    }

                    if (!allPrerequisitesMet) {
                        JOptionPane.showMessageDialog(batchDialog, errorMessage.toString(),
                                "Prerequisites Not Met", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check schedule conflicts
                    boolean hasConflicts = false;
                    StringBuilder conflictMessage = new StringBuilder("Schedule conflicts detected:\n");

                    // First check conflicts with existing enrolled subjects
                    List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUser.getUsername());
                    Map<String, Schedule> selectedScheduleMap = new HashMap<>();
                    
                    // Create map of selected subject schedules
                    for (String subjectId : selectedSubjectIds) {
                        Subject subject = dbManager.getSubject(subjectId);
                        int scheduleIndex = selectedSchedules.get(subjectId);
                        
                        if (!subject.getSchedules().isEmpty()) {
                            selectedScheduleMap.put(subjectId, subject.getSchedules().get(scheduleIndex));
                        }
                    }
                    
                    // Check conflicts with existing enrolled subjects
                    for (String subjectId : selectedSubjectIds) {
                        Subject subject = dbManager.getSubject(subjectId);
                        Schedule newSchedule = selectedScheduleMap.get(subjectId);
                        
                        if (newSchedule == null) continue;
                        
                        for (Subject enrolledSubject : enrolledSubjects) {
                            Schedule enrolledSchedule = enrolledSubject.getStudentSchedule(studentUser.getUsername());
                            
                            if (enrolledSchedule != null && newSchedule.conflictsWith(enrolledSchedule)) {
                                hasConflicts = true;
                                conflictMessage.append("- ").append(subject.getName())
                                        .append(" conflicts with enrolled subject ")
                                        .append(enrolledSubject.getName()).append("\n");
                            }
                        }
                    }

                    // Then check conflicts between selected subjects
                    for (int i = 0; i < selectedSubjectIds.size(); i++) {
                        String subjectId1 = selectedSubjectIds.get(i);
                        Schedule schedule1 = selectedScheduleMap.get(subjectId1);
                        
                        if (schedule1 == null) continue;
                        
                        Subject subject1 = dbManager.getSubject(subjectId1);

                        for (int j = i + 1; j < selectedSubjectIds.size(); j++) {
                            String subjectId2 = selectedSubjectIds.get(j);
                            Schedule schedule2 = selectedScheduleMap.get(subjectId2);
                            
                            if (schedule2 == null) continue;
                            
                            Subject subject2 = dbManager.getSubject(subjectId2);

                            if (schedule1.conflictsWith(schedule2)) {
                                hasConflicts = true;
                                conflictMessage.append("- ").append(subject1.getName())
                                        .append(" conflicts with ").append(subject2.getName())
                                        .append("\n  Schedule 1: ").append(schedule1.toString())
                                        .append("\n  Schedule 2: ").append(schedule2.toString()).append("\n");
                            }
                        }
                    }

                    if (hasConflicts) {
                        JOptionPane.showMessageDialog(batchDialog, conflictMessage.toString(),
                                "Schedule Conflicts", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Enroll in all selected subjects
                    for (String subjectId : selectedSubjectIds) {
                        Subject subject = dbManager.getSubject(subjectId);
                        int scheduleIndex = selectedSchedules.get(subjectId);
                        subject.enrollStudent(studentUser.getUsername(), scheduleIndex);
                    }

                    JOptionPane.showMessageDialog(batchDialog,
                            "Successfully enrolled in " + selectedSubjectIds.size() + " subjects",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh all views
                    refreshAvailableSubjects(availableSubjectsModel, studentDepartment);
                    refreshStudyLoad.run();
                    refreshTuition.run();

                    batchDialog.dispose();
                });

                batchDialog.setVisible(true);
            }
        });

        // Initial load of available subjects
        refreshAvailableSubjects(availableSubjectsModel, studentDepartment);

        return panel;
    }

    // Method to validate and order batch enrollment
    private boolean validateAndOrderBatchEnrollment(List<String> selectedSubjectIds) {
        // Check for circular prerequisites
        Map<String, List<String>> prereqGraph = new HashMap<>();

        // Build the prerequisite graph
        for (String subjectId : selectedSubjectIds) {
            Subject subject = dbManager.getSubject(subjectId);
            List<String> prereqs = new ArrayList<>();

            for (String prereqId : subject.getPrerequisites()) {
                if (selectedSubjectIds.contains(prereqId)) {
                    prereqs.add(prereqId);
                }
            }

            prereqGraph.put(subjectId, prereqs);
        }

        // Check for cycles in the graph
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();

        for (String subjectId : selectedSubjectIds) {
            if (hasCycle(subjectId, visited, recStack, prereqGraph)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot enroll in these subjects together due to circular prerequisites",
                        "Circular Prerequisites", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Order the subjects for enrollment
        List<String> enrollmentOrder = dbManager.getOptimalEnrollmentOrder(selectedSubjectIds);
        selectedSubjectIds.clear();
        selectedSubjectIds.addAll(enrollmentOrder);

        return true;
    }

    // Helper method to detect cycles in prerequisite graph
    private boolean hasCycle(String subjectId, Set<String> visited, Set<String> recStack, Map<String, List<String>> graph) {
        // If already in recursion stack, we found a cycle
        if (recStack.contains(subjectId)) {
            return true;
        }

        // If already visited and not in recursion stack, no cycle through this node
        if (visited.contains(subjectId)) {
            return false;
        }

        // Add to visited and recursion stack
        visited.add(subjectId);
        recStack.add(subjectId);

        // Check all prerequisites
        List<String> prereqs = graph.get(subjectId);
        if (prereqs != null) {
            for (String prereq : prereqs) {
                if (hasCycle(prereq, visited, recStack, graph)) {
                    return true;
                }
            }
        }

        // Remove from recursion stack
        recStack.remove(subjectId);
        return false;
    }


    // Study Load Panel
    private JPanel createStudyLoadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xFFF8DC)); // Light cream/pale yellow background

        // Table for enrolled subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Schedule", "Faculty"};
        DefaultTableModel studyLoadModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable studyLoadTable = new JTable(studyLoadModel);
        studyLoadTable.setBackground(Color.WHITE);
        studyLoadTable.setForeground(new Color(0x59361A));
        studyLoadTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(studyLoadTable);
        TitledBorder scrollPaneBorder = BorderFactory.createTitledBorder("Enrolled Subjects");
        scrollPaneBorder.setTitleColor(new Color(0x59361A));
        scrollPaneBorder.setTitleFont(new Font("Times New Roman", Font.BOLD, 22));
        scrollPane.setBorder(scrollPaneBorder);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(0xFFF8DC));
        JLabel totalUnitsLabel = new JLabel("Total Units: 0");
        totalUnitsLabel.setForeground(new Color(0x59361A));
        totalUnitsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
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
        if (subject.getAssignedFaculty() != null && !subject.getAssignedFaculty().isEmpty()) {
            User faculty = dbManager.getUser(subject.getAssignedFaculty());
            if (faculty != null) {
                facultyName = faculty.getUserInfo("name");
            }
        }

        // Get student's selected schedules for this subject
        List<Schedule> selectedSchedules = subject.getStudentSchedules(studentUser.getUsername());
        if (selectedSchedules.isEmpty()) {
            studyLoadModel.addRow(new Object[]{
                    subject.getId(),
                    subject.getName(),
                    subject.getUnits(),
                    "No schedule selected",
                    facultyName
            });
            totalUnits += subject.getUnits();
        } else {
            for (Schedule selectedSchedule : selectedSchedules) {
                studyLoadModel.addRow(new Object[]{
                        subject.getId(),
                        subject.getName(),
                        subject.getUnits(),
                        selectedSchedule.toString(),
                        facultyName
                });
            }
            // Add units only once per subject regardless of number of schedules
            totalUnits += subject.getUnits();
        }
    }

    totalUnitsLabel.setText("Total Units: " + totalUnits);
};

        // Initial load of study load
        refreshStudyLoad.run();

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(0xFFF8DC));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0x664229));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshStudyLoad.run());

        JButton unenrollButton = new JButton("Unenroll from Selected Subject");
        unenrollButton.setBackground(new Color(0x664229));
        unenrollButton.setForeground(Color.WHITE);
        unenrollButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        unenrollButton.setFocusPainted(false);
        unenrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studyLoadTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject to unenroll from",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String subjectId = (String) studyLoadModel.getValueAt(selectedRow, 0);
                String scheduleString = (String) studyLoadModel.getValueAt(selectedRow, 3);
                Subject subject = dbManager.getSubject(subjectId);

                if (subject == null) {
                    JOptionPane.showMessageDialog(panel, "Subject not found",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Confirm unenrollment
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Are you sure you want to unenroll from the selected schedule of " + subject.getName() + "?",
                        "Confirm Unenrollment",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Unenroll only the selected schedule
                    List<Schedule> studentSchedules = subject.getStudentSchedules(studentUser.getUsername());
                    Schedule scheduleToRemove = null;
                    int scheduleIndexToRemove = -1;
                    for (int i = 0; i < studentSchedules.size(); i++) {
                        Schedule schedule = studentSchedules.get(i);
                        if (schedule.toString().equals(scheduleString)) {
                            scheduleToRemove = schedule;
                            scheduleIndexToRemove = subject.getSchedules().indexOf(schedule);
                            break;
                        }
                    }
                    if (scheduleToRemove != null && scheduleIndexToRemove != -1) {
                        subject.unenrollStudentSchedule(studentUser.getUsername(), scheduleIndexToRemove);
                    } else {
                        // If no schedule matched, fallback to unenroll all (previous behavior)
                        subject.unenrollStudent(studentUser.getUsername());
                    }

                    // Refresh tables
                    refreshStudyLoad.run();
                    refreshTuition.run();

                    JOptionPane.showMessageDialog(panel, "Successfully unenrolled from the selected schedule of " + subject.getName(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton changeScheduleButton = new JButton("Change Selected Schedule");
        changeScheduleButton.setBackground(new Color(0x664229));
        changeScheduleButton.setForeground(Color.WHITE);
        changeScheduleButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        changeScheduleButton.setFocusPainted(false);
        changeScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studyLoadTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String subjectId = (String) studyLoadModel.getValueAt(selectedRow, 0);
                String currentScheduleString = (String) studyLoadModel.getValueAt(selectedRow, 3);
                Subject subject = dbManager.getSubject(subjectId);

                if (subject == null) {
                    JOptionPane.showMessageDialog(panel, "Subject not found",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (subject.getSchedules().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "This subject has no available schedules",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create a dialog for schedule selection
                JDialog scheduleDialog = new JDialog(StudentInterface.this, "Select Schedule", true);
                scheduleDialog.setSize(400, 300);
                scheduleDialog.setLocationRelativeTo(StudentInterface.this);

                JPanel dialogPanel = new JPanel(new BorderLayout());

                DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
                JList<String> scheduleList = new JList<>(scheduleListModel);

                // Get all enrolled subjects except the current one being changed
                List<Subject> otherEnrolledSubjects = new ArrayList<>();
                for (Subject s : dbManager.getEnrolledSubjects(studentUser.getUsername())) {
                    if (!s.getId().equals(subjectId)) {
                        otherEnrolledSubjects.add(s);
                    }
                }

                // Add all schedules to the list with conflict information
                List<Schedule> schedules = subject.getSchedules();
                for (int i = 0; i < schedules.size(); i++) {
                    Schedule schedule = schedules.get(i);
                    boolean hasConflict = false;
                    String conflictInfo = "";

                    // Check for conflicts with other enrolled subjects
                    for (Subject other : otherEnrolledSubjects) {
                        Schedule otherSchedule = other.getStudentSchedule(studentUser.getUsername());
                        if (otherSchedule != null && schedule.conflictsWith(otherSchedule)) {
                            hasConflict = true;
                            conflictInfo = " (CONFLICTS with " + other.getName() + ")";
                            break;
                        }
                    }

                    scheduleListModel.addElement("Schedule " + (i + 1) + ": " + schedule.toString() +
                            (hasConflict ? conflictInfo : ""));
                }

                // Highlight currently selected schedule
                int currentScheduleIndex = -1;
                for (int i = 0; i < schedules.size(); i++) {
                    if (schedules.get(i).toString().equals(currentScheduleString)) {
                        currentScheduleIndex = i;
                        break;
                    }
                }
                if (currentScheduleIndex != -1) {
                    scheduleList.setSelectedIndex(currentScheduleIndex);
                }

                JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
                dialogPanel.add(scheduleScrollPane, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton cancelButton = new JButton("Cancel");
                JButton selectButton = new JButton("Select Schedule");

                buttonPanel.add(cancelButton);
                buttonPanel.add(selectButton);
                dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

                cancelButton.addActionListener(ae -> scheduleDialog.dispose());
                selectButton.addActionListener(ae -> {
                    int scheduleIndex = scheduleList.getSelectedIndex();
                    if (scheduleIndex == -1) {
                        JOptionPane.showMessageDialog(scheduleDialog, "Please select a schedule",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check for schedule conflicts
                    Schedule newSchedule = subject.getSchedules().get(scheduleIndex);
                    boolean hasConflict = false;
                    String conflictSubject = "";

                    // Check conflicts with other enrolled subjects
                    for (Subject other : otherEnrolledSubjects) {
                        Schedule otherSchedule = other.getStudentSchedule(studentUser.getUsername());
                        if (otherSchedule != null && newSchedule.conflictsWith(otherSchedule)) {
                            hasConflict = true;
                            conflictSubject = other.getName();
                            break;
                        }
                    }

                    if (hasConflict) {
                        JOptionPane.showMessageDialog(scheduleDialog,
                                "Cannot select this schedule due to conflict with " + conflictSubject,
                                "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Replace the old schedule with the new one
                    List<Schedule> studentSchedules = subject.getStudentSchedules(studentUser.getUsername());
                    Schedule oldSchedule = null;
                    for (Schedule schedule : studentSchedules) {
                        if (schedule.toString().equals(currentScheduleString)) {
                            oldSchedule = schedule;
                            break;
                        }
                    }
                    if (oldSchedule != null) {
                        int oldScheduleIndex = subject.getSchedules().indexOf(oldSchedule);
                        if (oldScheduleIndex != -1) {
                            subject.unenrollStudentSchedule(studentUser.getUsername(), oldScheduleIndex);
                        }
                    }
                    subject.enrollStudent(studentUser.getUsername(), scheduleIndex);

                    // Refresh the study load view
                    refreshStudyLoad.run();

                    JOptionPane.showMessageDialog(scheduleDialog,
                            "Schedule updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    scheduleDialog.dispose();
                });

                scheduleDialog.setContentPane(dialogPanel);
                scheduleDialog.setVisible(true);
            }
        });

        topPanel.add(refreshButton);
        topPanel.add(unenrollButton);
        topPanel.add(changeScheduleButton);
        panel.add(topPanel, BorderLayout.NORTH);

        return panel;
    }

    // Tuition Fee Panel
    private JPanel createTuitionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xFFF8DC)); // Light cream/pale yellow background

        // Table for tuition breakdown
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Tuition Fee"};
        DefaultTableModel tuitionModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tuitionTable = new JTable(tuitionModel);
        tuitionTable.setBackground(Color.WHITE);
        tuitionTable.setForeground(new Color(0x59361A));
        tuitionTable.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(tuitionTable);
        TitledBorder scrollPaneBorder = BorderFactory.createTitledBorder("Tuition Breakdown");
        scrollPaneBorder.setTitleColor(new Color(0x59361A));
        scrollPaneBorder.setTitleFont(new Font("Times New Roman", Font.BOLD, 22));
        scrollPane.setBorder(scrollPaneBorder);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        summaryPanel.setBackground(new Color(0xFFF8DC));
        TitledBorder summaryBorder = BorderFactory.createTitledBorder("Tuition Summary");
        summaryBorder.setTitleColor(new Color(0x59361A));
        summaryBorder.setTitleFont(new Font("Times New Roman", Font.BOLD, 22));
        summaryPanel.setBorder(summaryBorder);

        JLabel totalSubjectsLabel = new JLabel("Number of Subjects:");
        totalSubjectsLabel.setForeground(new Color(0x59361A));
        totalSubjectsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JLabel totalSubjectsValueLabel = new JLabel("0");
        totalSubjectsValueLabel.setForeground(new Color(0x59361A));
        totalSubjectsValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JLabel totalTuitionLabel = new JLabel("Total Tuition Fee:");
        totalTuitionLabel.setForeground(new Color(0x59361A));
        totalTuitionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JLabel totalTuitionValueLabel = new JLabel("₱0.00");
        totalTuitionValueLabel.setForeground(new Color(0x59361A));
        totalTuitionValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));

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
                        String.format("₱%.2f", subject.getTuition())
                });

                totalTuition += subject.getTuition();
            }

            totalSubjectsValueLabel.setText(String.valueOf(enrolledSubjects.size()));
            totalTuitionValueLabel.setText(String.format("₱%.2f", totalTuition));
        };

        // Initial load of tuition data
        refreshTuition.run();

        return panel;
    }

    // Method to refresh available subjects
    private void refreshAvailableSubjects(DefaultTableModel model, String department) {
        model.setRowCount(0);

        List<Subject> subjects;
        if (department == null || department.isEmpty()) {
            subjects = dbManager.getAllSubjects();
        } else {
            // Use case-insensitive filtering for department
            String departmentLower = department.toLowerCase();
            subjects = new ArrayList<>();
            for (Subject subject : dbManager.getAllSubjects()) {
                if (subject.getDepartment() != null && subject.getDepartment().toLowerCase().equals(departmentLower)) {
                    subjects.add(subject);
                }
            }
        }

        for (Subject subject : subjects) {
            // Do not skip subjects already enrolled in, allow multiple schedule enrollments
            // Instead, we will filter schedules in the schedule list

            // Format prerequisites
            StringBuilder prereqBuilder = new StringBuilder();
            for (String prereq : subject.getPrerequisites()) {
                if (prereqBuilder.length() > 0) {
                    prereqBuilder.append(", ");
                }
                Subject prereqSubject = dbManager.getSubject(prereq);
                if (prereqSubject != null) {
                    prereqBuilder.append(prereqSubject.getName());

                    // Indicate if student is enrolled in this prerequisite
                    if (prereqSubject.getEnrolledStudents().contains(studentUser.getUsername())) {
                        prereqBuilder.append(" (Enrolled)");
                    }
                }
            }

            // Count only schedules not yet enrolled by the student
            int availableScheduleCount = 0;
            List<Schedule> studentSchedules = subject.getStudentSchedules(studentUser.getUsername());
            for (int i = 0; i < subject.getSchedules().size(); i++) {
                Schedule schedule = subject.getSchedules().get(i);
                if (!studentSchedules.contains(schedule)) {
                    availableScheduleCount++;
                }
            }

            String scheduleCount = availableScheduleCount + " available";

            model.addRow(new Object[]{
                    subject.getId(),
                    subject.getName(),
                    subject.getUnits(),
                    prereqBuilder.toString(),
                    scheduleCount
            });
        }
    }

    // For refreshing panels from other tabs
    private Runnable refreshStudyLoad;
    private Runnable refreshTuition;
}

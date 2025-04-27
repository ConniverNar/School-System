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
        String[] columnNames = {"Subject ID", "Subject Name", "Units", "Prerequisites", "Available Schedules"};
        DefaultTableModel availableSubjectsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable availableSubjectsTable = new JTable(availableSubjectsModel);
        JScrollPane availableScrollPane = new JScrollPane(availableSubjectsTable);
        availableScrollPane.setBorder(BorderFactory.createTitledBorder("Available Subjects"));
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Available Schedules"));

        DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
        JList<String> scheduleList = new JList<>(scheduleListModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
        schedulePanel.add(scheduleScrollPane, BorderLayout.CENTER);
        schedulePanel.setVisible(false); // Initially hidden

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                availableScrollPane, schedulePanel);
        splitPane.setResizeWeight(0.7);

        // Action panel at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton enrollButton = new JButton("Enroll in Selected Subject");
        JButton batchEnrollButton = new JButton("Enroll in Multiple Subjects");
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

            // Enroll student in the selected schedule if any, else default to first schedule or no schedule
            int scheduleIndex = -1;
            if (!subject.getSchedules().isEmpty()) {
                // Get selected schedule index from scheduleList if visible and a selection is made
                if (schedulePanel.isVisible() && scheduleList.getSelectedIndex() != -1) {
                    scheduleIndex = scheduleList.getSelectedIndex();
                } else {
                    scheduleIndex = 0; // default to first schedule
                }
            } else {
                scheduleIndex = -1; // no schedule
            }

            // Check for schedule conflict if schedule exists
            if (scheduleIndex >= 0 && dbManager.hasScheduleConflict(studentUser.getUsername(), subject, scheduleIndex)) {
                JOptionPane.showMessageDialog(panel,
                        "Cannot enroll in " + subject.getName() + " due to schedule conflict.",
                        "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Enroll student
            subject.enrollStudent(studentUser.getUsername(), scheduleIndex);

            // Ensure the schedule choice is saved correctly by updating studentScheduleChoices map
            // (This is already handled inside enrollStudent method in Subject.java)

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
                        for (int i = 0; i < schedules.size(); i++) {
                            scheduleListModel.addElement("Schedule " + (i + 1) + ": " + schedules.get(i).toString());
                        }
                        schedulePanel.setVisible(!schedules.isEmpty());
                    }
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
                        for (int i = 0; i < subject.getSchedules().size(); i++) {
                            scheduleCombo.addItem("Schedule " + (i + 1));
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
                    for (String subjectId : selectedSubjectIds) {
                        Subject subject = dbManager.getSubject(subjectId);
                        int scheduleIndex = selectedSchedules.get(subjectId);

                        if (dbManager.hasScheduleConflict(
                                studentUser.getUsername(), subject, scheduleIndex)) {
                            hasConflicts = true;
                            conflictMessage.append("- ").append(subject.getName())
                                    .append(" conflicts with an enrolled subject\n");
                        }
                    }

                    // Then check conflicts between selected subjects
                    for (int i = 0; i < selectedSubjectIds.size(); i++) {
                        String subjectId1 = selectedSubjectIds.get(i);
                        Subject subject1 = dbManager.getSubject(subjectId1);
                        int scheduleIndex1 = selectedSchedules.get(subjectId1);
                        Schedule schedule1 = subject1.getSchedules().get(scheduleIndex1);

                        for (int j = i + 1; j < selectedSubjectIds.size(); j++) {
                            String subjectId2 = selectedSubjectIds.get(j);
                            Subject subject2 = dbManager.getSubject(subjectId2);
                            int scheduleIndex2 = selectedSchedules.get(subjectId2);
                            Schedule schedule2 = subject2.getSchedules().get(scheduleIndex2);

                            if (schedule1.conflictsWith(schedule2)) {
                                hasConflicts = true;
                                conflictMessage.append("- ").append(subject1.getName())
                                        .append(" conflicts with ").append(subject2.getName()).append("\n");
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

                    // After enrollment, ensure the schedule choices are saved correctly (handled in enrollStudent)

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
                if (subject.getAssignedFaculty() != null && !subject.getAssignedFaculty().isEmpty()) {
                    User faculty = dbManager.getUser(subject.getAssignedFaculty());
                    if (faculty != null) {
                        facultyName = faculty.getUserInfo("name");
                    }
                }

                // Get student's selected schedule for this subject
                Schedule selectedSchedule = subject.getStudentSchedule(studentUser.getUsername());
                String scheduleStr = selectedSchedule != null ? selectedSchedule.toString() : "No schedule selected";

                studyLoadModel.addRow(new Object[]{
                        subject.getId(),
                        subject.getName(),
                        subject.getUnits(),
                        scheduleStr,
                        facultyName
                });

                totalUnits += subject.getUnits();
            }

            totalUnitsLabel.setText("Total Units: " + totalUnits);
        };

        // Initial load of study load
        refreshStudyLoad.run();

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshStudyLoad.run());

        JButton unenrollButton = new JButton("Unenroll from Selected Subject");
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
                Subject subject = dbManager.getSubject(subjectId);

                if (subject == null) {
                    JOptionPane.showMessageDialog(panel, "Subject not found",
                            "Error", JOptionPane.ERROR_MESSAGE);
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

                    JOptionPane.showMessageDialog(panel, "Successfully unenrolled from " + subject.getName(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton changeScheduleButton = new JButton("Change Selected Schedule");
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

                // Add all schedules to the list
                List<Schedule> schedules = subject.getSchedules();
                for (int i = 0; i < schedules.size(); i++) {
                    scheduleListModel.addElement("Schedule " + (i + 1) + ": " + schedules.get(i).toString());
                }

                // Highlight currently selected schedule
                Schedule currentSchedule = subject.getStudentSchedule(studentUser.getUsername());
                if (currentSchedule != null) {
                    for (int i = 0; i < schedules.size(); i++) {
                        if (schedules.get(i).equals(currentSchedule)) {
                            scheduleList.setSelectedIndex(i);
                            break;
                        }
                    }
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
                    if (dbManager.hasScheduleConflict(studentUser.getUsername(), subject, scheduleIndex)) {
                        JOptionPane.showMessageDialog(scheduleDialog,
                                "This schedule conflicts with your other enrolled subjects",
                                "Schedule Conflict", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update the schedule selection
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

    // Method to refresh available subjects
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

                    // Indicate if student is enrolled in this prerequisite
                    if (prereqSubject.getEnrolledStudents().contains(studentUser.getUsername())) {
                        prereqBuilder.append(" (Enrolled)");
                    }
                }
            }

            // Now we just show the count of available schedules, details shown when selected
            String scheduleCount = subject.getSchedules().size() + " available";

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

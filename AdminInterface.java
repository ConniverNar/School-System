import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminInterface extends JFrame {
    private DatabaseManager dbManager;
    private User adminUser;
    private JTabbedPane tabbedPane;
    
    public AdminInterface(DatabaseManager dbManager, User adminUser) {
        this.dbManager = dbManager;
        this.adminUser = adminUser;
        
        setTitle("Admin Interface - " + adminUser.getUserInfo("name"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs for each functionality
        tabbedPane.addTab("Student Management", createStudentPanel());
        tabbedPane.addTab("Faculty Management", createFacultyPanel());
        tabbedPane.addTab("Subject Management", createSubjectPanel());
        tabbedPane.addTab("Tuition Management", createTuitionPanel());
        tabbedPane.addTab("Salary Management", createSalaryPanel());
        
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
    
    // Student Management Panel
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Student list on the left
        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(studentListModel);
        JScrollPane studentScrollPane = new JScrollPane(studentList);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        // Refresh the student list
        refreshStudentList(studentListModel);
        
        // Student details and actions on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        
        // Fields for student details
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(15);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField departmentField = new JTextField(15);
        JTextField schoolYearField = new JTextField(15);
        
        fieldsPanel.add(new JLabel("Username:"));
        fieldsPanel.add(usernameField);
        fieldsPanel.add(new JLabel("Password:"));
        fieldsPanel.add(passwordField);
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Age:"));
        fieldsPanel.add(ageField);
        fieldsPanel.add(new JLabel("Gender:"));
        fieldsPanel.add(genderComboBox);
        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(departmentField);
        fieldsPanel.add(new JLabel("School Year:"));
        fieldsPanel.add(schoolYearField);
        
        detailsPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel for actions
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        JButton createButton = new JButton("Create Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton clearButton = new JButton("Clear Fields");
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        
        detailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        

        final JTextField finalUsernameField = usernameField;
        final JPasswordField finalPasswordField = passwordField;
        final JTextField finalNameField = nameField;
        final JTextField finalAgeField = ageField;
        final JComboBox<String> finalGenderComboBox = genderComboBox;
        final JTextField finalDepartmentField = departmentField;
        final JTextField finalSchoolYearField = schoolYearField;
        final JList<String> finalStudentList = studentList;
    
    ActionListener clearFieldsAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearStudentFields(finalUsernameField, finalPasswordField, finalNameField, finalAgeField, finalGenderComboBox, finalDepartmentField, finalSchoolYearField, finalStudentList);
        }
    };
    
    clearButton.addActionListener(clearFieldsAction);

        // Event listeners
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
                    String username = studentList.getSelectedValue();
                    User student = dbManager.getUser(username);
                    
                    if (student != null) {
                        usernameField.setText(student.getUsername());
                        passwordField.setText(student.getPassword());
                        nameField.setText(student.getUserInfo("name"));
                        ageField.setText(student.getUserInfo("age"));
                        genderComboBox.setSelectedItem(student.getUserInfo("gender"));
                        departmentField.setText(student.getUserInfo("department"));
                        schoolYearField.setText(student.getUserInfo("schoolYear"));
                    }
                }
            }
        });
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String age = ageField.getText().trim();
            String schoolYear = schoolYearField.getText().trim();
        
            // Validate username and password
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Username and password are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            if (dbManager.getUser(username) != null) {
                JOptionPane.showMessageDialog(panel, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Validate age - must be numeric
            if (!age.isEmpty() && !isNumeric(age)) {
                JOptionPane.showMessageDialog(panel, "Age must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
        
            // Validate school year - must be numeric
            if (!schoolYear.isEmpty() && !isNumeric(schoolYear)) {
                JOptionPane.showMessageDialog(panel, "School year must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            User student = new User(username, password, User.UserType.STUDENT);
            student.setUserInfo("name", nameField.getText().trim());
            student.setUserInfo("age", age);
            student.setUserInfo("gender", (String) genderComboBox.getSelectedItem());
            student.setUserInfo("department", departmentField.getText().trim());
            student.setUserInfo("schoolYear", schoolYear);
        
            dbManager.addUser(student);
             refreshStudentList(studentListModel);
        
             JOptionPane.showMessageDialog(panel, "Student created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
             }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String selectedUsername = studentList.getSelectedValue();
            if (selectedUsername == null) {
                JOptionPane.showMessageDialog(panel, "Please select a student to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            User student = dbManager.getUser(selectedUsername);
            String newUsername = usernameField.getText().trim();
            String age = ageField.getText().trim();
            String schoolYear = schoolYearField.getText().trim();
        
            if (!selectedUsername.equals(newUsername) && dbManager.getUser(newUsername) != null) {
                JOptionPane.showMessageDialog(panel, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Validate age - must be numeric
            if (!age.isEmpty() && !isNumeric(age)) {
                JOptionPane.showMessageDialog(panel, "Age must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Validate school year - must be numeric
            if (!schoolYear.isEmpty() && !isNumeric(schoolYear)) {
                JOptionPane.showMessageDialog(panel, "School year must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // If username is changing, need to remove old entry and add new one
            if (!selectedUsername.equals(newUsername)) {
                dbManager.removeUser(selectedUsername);
                student.setUsername(newUsername);
            }
        
            student.setPassword(new String(passwordField.getPassword()));
            student.setUserInfo("name", nameField.getText().trim());
            student.setUserInfo("age", age);
            student.setUserInfo("gender", (String) genderComboBox.getSelectedItem());
            student.setUserInfo("department", departmentField.getText().trim());
            student.setUserInfo("schoolYear", schoolYear);
        
            dbManager.addUser(student);
            refreshStudentList(studentListModel);
        
            JOptionPane.showMessageDialog(panel, "Student updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUsername = studentList.getSelectedValue();
                if (selectedUsername == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a student to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete student '" + selectedUsername + "'?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dbManager.removeUser(selectedUsername);
                    refreshStudentList(studentListModel);
                    JOptionPane.showMessageDialog(panel, "Student deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    //  helper method clear of createStudentPanel()
    private void clearStudentFields(JTextField usernameField, JPasswordField passwordField,   JTextField nameField, JTextField ageField,  JComboBox<String> genderComboBox, JTextField departmentField,   JTextField schoolYearField, JList<String> studentList) {
    usernameField.setText("");
    passwordField.setText("");
    nameField.setText("");
    ageField.setText("");
    genderComboBox.setSelectedIndex(0);
    departmentField.setText("");
    schoolYearField.setText("");
    studentList.clearSelection();
}
    // Helper method to refresh student list
    private void refreshStudentList(DefaultListModel<String> model) {
        model.clear();
        List<User> students = dbManager.getUsersByType(User.UserType.STUDENT);
        for (User student : students) {
            model.addElement(student.getUsername());
        }
    }
    
    // Faculty Management Panel
    private JPanel createFacultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Faculty list on the left
        DefaultListModel<String> facultyListModel = new DefaultListModel<>();
        JList<String> facultyList = new JList<>(facultyListModel);
        JScrollPane facultyScrollPane = new JScrollPane(facultyList);
        facultyScrollPane.setBorder(BorderFactory.createTitledBorder("Faculty List"));
        
        // Refresh the faculty list
        refreshFacultyList(facultyListModel);
        
        // Faculty details and actions on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Faculty Details"));
        
        // Fields for faculty details
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField nameField = new JTextField(15);
        JTextField ageField = new JTextField(15);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField departmentField = new JTextField(15);
        
        fieldsPanel.add(new JLabel("Username:"));
        fieldsPanel.add(usernameField);
        fieldsPanel.add(new JLabel("Password:"));
        fieldsPanel.add(passwordField);
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Age:"));
        fieldsPanel.add(ageField);
        fieldsPanel.add(new JLabel("Gender:"));
        fieldsPanel.add(genderComboBox);
        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(departmentField);
        
        detailsPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel for actions
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        JButton createButton = new JButton("Create Faculty");
        JButton updateButton = new JButton("Update Faculty");
        JButton deleteButton = new JButton("Delete Faculty");
        JButton clearButton = new JButton("Clear Fields");
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        
        detailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, facultyScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        

        final JTextField finalUsernameField = usernameField;
        final JPasswordField finalPasswordField = passwordField;
        final JTextField finalNameField = nameField;
        final JTextField finalAgeField = ageField;
        final JComboBox<String> finalGenderComboBox = genderComboBox;
        final JTextField finalDepartmentField = departmentField;
        final JList<String> finalFacultyList = facultyList;
    
    ActionListener clearFieldsAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearFacultyFields(finalUsernameField, finalPasswordField, finalNameField, 
                               finalAgeField, finalGenderComboBox, finalDepartmentField, 
                               finalFacultyList);
        }
    };
    
    clearButton.addActionListener(clearFieldsAction);
        // Event listeners
        facultyList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && facultyList.getSelectedValue() != null) {
                    String username = facultyList.getSelectedValue();
                    User faculty = dbManager.getUser(username);
                    
                    if (faculty != null) {
                        usernameField.setText(faculty.getUsername());
                        passwordField.setText(faculty.getPassword());
                        nameField.setText(faculty.getUserInfo("name"));
                        ageField.setText(faculty.getUserInfo("age"));
                        genderComboBox.setSelectedItem(faculty.getUserInfo("gender"));
                        departmentField.setText(faculty.getUserInfo("department"));
                    }
                }
            }
        });
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String age = ageField.getText().trim();
        
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Username and password are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            if (dbManager.getUser(username) != null) {
                JOptionPane.showMessageDialog(panel, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Validate age - must be numeric
            if (!age.isEmpty() && !isNumeric(age)) {
                JOptionPane.showMessageDialog(panel, "Age must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            User faculty = new User(username, password, User.UserType.FACULTY);
            faculty.setUserInfo("name", nameField.getText().trim());
            faculty.setUserInfo("age", age);
            faculty.setUserInfo("gender", (String) genderComboBox.getSelectedItem());
            faculty.setUserInfo("department", departmentField.getText().trim());
        
            dbManager.addUser(faculty);
            refreshFacultyList(facultyListModel);
        
            JOptionPane.showMessageDialog(panel, "Faculty created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    });
        
        updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedUsername = facultyList.getSelectedValue();
            if (selectedUsername == null) {
                JOptionPane.showMessageDialog(panel, "Please select a faculty to update", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            User faculty = dbManager.getUser(selectedUsername);
            String newUsername = usernameField.getText().trim();
            String age = ageField.getText().trim();
        
            if (!selectedUsername.equals(newUsername) && dbManager.getUser(newUsername) != null) {
                JOptionPane.showMessageDialog(panel, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Validate age - must be numeric
            if (!age.isEmpty() && !isNumeric(age)) {
                JOptionPane.showMessageDialog(panel, "Age must contain only numeric characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // If username is changing, need to remove old entry and add new one
            if (!selectedUsername.equals(newUsername)) {
                dbManager.removeUser(selectedUsername);
                faculty.setUsername(newUsername);
            }
        
            faculty.setPassword(new String(passwordField.getPassword()));
            faculty.setUserInfo("name", nameField.getText().trim());
            faculty.setUserInfo("age", age);
            faculty.setUserInfo("gender", (String) genderComboBox.getSelectedItem());
            faculty.setUserInfo("department", departmentField.getText().trim());
        
            dbManager.addUser(faculty);
            refreshFacultyList(facultyListModel);
        
            JOptionPane.showMessageDialog(panel, "Faculty updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    });
        
       deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUsername = facultyList.getSelectedValue();
                if (selectedUsername == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a faculty to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete faculty '" + selectedUsername + "'?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dbManager.removeUser(selectedUsername);
                    refreshFacultyList(facultyListModel);
                    JOptionPane.showMessageDialog(panel, "Faculty deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    // Helper method to refresh faculty list
    private void refreshFacultyList(DefaultListModel<String> model) {
        model.clear();
        List<User> faculty = dbManager.getUsersByType(User.UserType.FACULTY);
        for (User facultyMember : faculty) {
            model.addElement(facultyMember.getUsername());
        }
    }
    
    // Subject Management Panel
    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Subject list on the left
        DefaultListModel<String> subjectListModel = new DefaultListModel<>();
        JList<String> subjectList = new JList<>(subjectListModel);
        JScrollPane subjectScrollPane = new JScrollPane(subjectList);
        subjectScrollPane.setBorder(BorderFactory.createTitledBorder("Subject List"));
        
        // Refresh the subject list
        refreshSubjectList(subjectListModel);
        
        // Create panels for subject details, enrolled students, and schedules
        JTabbedPane detailsTabbedPane = new JTabbedPane();
        
        // Subject details panel
        JPanel subjectDetailsPanel = new JPanel(new BorderLayout());
        
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField departmentField = new JTextField(15);
        JTextField tuitionField = new JTextField(15);
        JTextField salaryField = new JTextField(15);
        JTextField unitsField = new JTextField(15);
        JTextArea prerequisitesArea = new JTextArea(4, 15);
        JScrollPane prerequisitesScrollPane = new JScrollPane(prerequisitesArea);
        
        fieldsPanel.add(new JLabel("Subject ID:"));
        fieldsPanel.add(idField);
        fieldsPanel.add(new JLabel("Name:"));
        fieldsPanel.add(nameField);
        fieldsPanel.add(new JLabel("Department:"));
        fieldsPanel.add(departmentField);
        fieldsPanel.add(new JLabel("Tuition:"));
        fieldsPanel.add(tuitionField);
        fieldsPanel.add(new JLabel("Salary:"));
        fieldsPanel.add(salaryField);
        fieldsPanel.add(new JLabel("Units:"));
        fieldsPanel.add(unitsField);
        fieldsPanel.add(new JLabel("Prerequisites (IDs, one per line):"));
        fieldsPanel.add(prerequisitesScrollPane);
        
        subjectDetailsPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel for actions
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        
        JButton createButton = new JButton("Create Subject");
        JButton updateButton = new JButton("Update Subject");
        JButton deleteButton = new JButton("Delete Subject");
        JButton clearButton = new JButton("Clear Fields");
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        
        subjectDetailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Enrolled students panel
        JPanel enrolledPanel = new JPanel(new BorderLayout());
        
        DefaultListModel<String> enrolledListModel = new DefaultListModel<>();
        JList<String> enrolledList = new JList<>(enrolledListModel);
        JScrollPane enrolledScrollPane = new JScrollPane(enrolledList);
        
        enrolledPanel.add(enrolledScrollPane, BorderLayout.CENTER);
        
        // Schedules panel
        JPanel schedulesPanel = new JPanel(new BorderLayout());
        
        DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
        JList<String> scheduleList = new JList<>(scheduleListModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
        
        JPanel scheduleDetailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField roomField = new JTextField(15);
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        JTextField startTimeField = new JTextField(15);
        JTextField endTimeField = new JTextField(15);
        
        scheduleDetailsPanel.add(new JLabel("Room Number:"));
        scheduleDetailsPanel.add(roomField);
        scheduleDetailsPanel.add(new JLabel("Day:"));
        scheduleDetailsPanel.add(dayComboBox);
        scheduleDetailsPanel.add(new JLabel("Start Time:"));
        scheduleDetailsPanel.add(startTimeField);
        scheduleDetailsPanel.add(new JLabel("End Time:"));
        scheduleDetailsPanel.add(endTimeField);
        
        JPanel scheduleButtonsPanel = new JPanel(new FlowLayout());
        
        JButton addScheduleButton = new JButton("Add Schedule");
        JButton removeScheduleButton = new JButton("Remove Schedule");
        JButton clearScheduleButton = new JButton("Clear Fields");
        
        scheduleButtonsPanel.add(addScheduleButton);
        scheduleButtonsPanel.add(removeScheduleButton);
        scheduleButtonsPanel.add(clearScheduleButton);
        
        JPanel scheduleControlPanel = new JPanel(new BorderLayout());
        scheduleControlPanel.add(scheduleDetailsPanel, BorderLayout.CENTER);
        scheduleControlPanel.add(scheduleButtonsPanel, BorderLayout.SOUTH);
        
        schedulesPanel.add(scheduleScrollPane, BorderLayout.CENTER);
        schedulesPanel.add(scheduleControlPanel, BorderLayout.SOUTH);
        
        // Add panels to tabbed pane
        detailsTabbedPane.addTab("Subject Details", subjectDetailsPanel);
        detailsTabbedPane.addTab("Enrolled Students", enrolledPanel);
        detailsTabbedPane.addTab("Schedules", schedulesPanel);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subjectScrollPane, detailsTabbedPane);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Move clearFields to be accessible by all action listeners
        final JTextField finalIdField = idField;
        final JTextField finalNameField = nameField;
        final JTextField finalDepartmentField = departmentField;
        final JTextField finalTuitionField = tuitionField;
        final JTextField finalSalaryField = salaryField;
        final JTextField finalUnitsField = unitsField;
        final JTextArea finalPrerequisitesArea = prerequisitesArea;
        final DefaultListModel<String> finalEnrolledListModel = enrolledListModel;
        final DefaultListModel<String> finalScheduleListModel = scheduleListModel;
        final JTextField finalRoomField = roomField;
        final JTextField finalStartTimeField = startTimeField;
        final JTextField finalEndTimeField = endTimeField;
        final JList<String> finalSubjectList = subjectList;
    
    ActionListener clearFieldsAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearSubjectFields(finalIdField, finalNameField, finalDepartmentField, finalTuitionField, finalSalaryField, finalUnitsField,  finalPrerequisitesArea, finalEnrolledListModel,   finalScheduleListModel, finalRoomField,   finalStartTimeField, finalEndTimeField, finalSubjectList);
        }
    };
    
    clearButton.addActionListener(clearFieldsAction);
        // Event listeners
        subjectList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                    String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from the list display
                    Subject subject = dbManager.getSubject(subjectId);
                    
                    if (subject != null) {
                        // Fill subject details
                        idField.setText(subject.getId());
                        nameField.setText(subject.getName());
                        departmentField.setText(subject.getDepartment());
                        tuitionField.setText(String.valueOf(subject.getTuition()));
                        salaryField.setText(String.valueOf(subject.getSalary()));
                        unitsField.setText(String.valueOf(subject.getUnits()));
                        
                        StringBuilder prereqs = new StringBuilder();
                        for (String prereq : subject.getPrerequisites()) {
                            prereqs.append(prereq).append("\n");
                        }
                        prerequisitesArea.setText(prereqs.toString());
                        
                        // Fill enrolled students
                        enrolledListModel.clear();
                        for (String student : subject.getEnrolledStudents()) {
                            User studentUser = dbManager.getUser(student);
                            if (studentUser != null) {
                                enrolledListModel.addElement(student + " - " + studentUser.getUserInfo("name"));
                            }
                        }
                        
                        // Fill schedules
                        scheduleListModel.clear();
                        for (Schedule schedule : subject.getSchedules()) {
                            scheduleListModel.addElement(schedule.toString());
                        }
                    }
                }
            }
        });
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String department = departmentField.getText().trim();
                
                if (id.isEmpty() || name.isEmpty() || department.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Subject ID, name, and department are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (dbManager.getSubject(id) != null) {
                    JOptionPane.showMessageDialog(panel, "Subject ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double tuition = 0;
                double salary = 0;
                int units = 0;
                
                try {
                    tuition = Double.parseDouble(tuitionField.getText().trim());
                    salary = Double.parseDouble(salaryField.getText().trim());
                    units = Integer.parseInt(unitsField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid number format for tuition, salary, or units", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Subject subject = new Subject(id, name, department, tuition, salary, units);
                
                // Add prerequisites
                String[] prereqLines = prerequisitesArea.getText().split("\n");
                for (String prereq : prereqLines) {
                    prereq = prereq.trim();
                    if (!prereq.isEmpty()) {
                        subject.addPrerequisite(prereq);
                    }
                }
                
                dbManager.addSubject(subject);
                refreshSubjectList(subjectListModel);
                
                JOptionPane.showMessageDialog(panel, "Subject created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject to update", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String oldId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from the list display
                String newId = idField.getText().trim();
                
                if (!oldId.equals(newId) && dbManager.getSubject(newId) != null) {
                    JOptionPane.showMessageDialog(panel, "Subject ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double tuition = 0;
                double salary = 0;
                int units = 0;
                
                try {
                    tuition = Double.parseDouble(tuitionField.getText().trim());
                    salary = Double.parseDouble(salaryField.getText().trim());
                    units = Integer.parseInt(unitsField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid number format for tuition, salary, or units", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // If ID changed, need to remove old subject and create new one
                if (!oldId.equals(newId)) {
                    Subject oldSubject = dbManager.getSubject(oldId);
                    dbManager.removeSubject(oldId);
                    
                    Subject newSubject = new Subject(newId, nameField.getText().trim(), departmentField.getText().trim(), tuition, salary, units);
                    
                    // Copy over enrolled students and assigned faculty
                    for (String student : oldSubject.getEnrolledStudents()) {
                        newSubject.enrollStudent(student);
                    }
                    newSubject.setAssignedFaculty(oldSubject.getAssignedFaculty());
                    
                    // Copy over schedules
                    for (Schedule schedule : oldSubject.getSchedules()) {
                        Schedule newSchedule = new Schedule(newId, schedule.getRoomNumber(), schedule.getDayOfWeek(), schedule.getStartTime(), schedule.getEndTime());
                        newSubject.addSchedule(newSchedule);
                    }
                    
                    // Add prerequisites
                    String[] prereqLines = prerequisitesArea.getText().split("\n");
                    for (String prereq : prereqLines) {
                        prereq = prereq.trim();
                        if (!prereq.isEmpty()) {
                            newSubject.addPrerequisite(prereq);
                        }
                    }
                    
                    dbManager.addSubject(newSubject);
                } else {
                    Subject subject = dbManager.getSubject(oldId);
                    
                    // Update basic info
                    subject.setTuition(tuition);
                    subject.setSalary(salary);
                    
                    // Clear and re-add prerequisites
                    subject.getPrerequisites().clear();
                    String[] prereqLines = prerequisitesArea.getText().split("\n");
                    for (String prereq : prereqLines) {
                        prereq = prereq.trim();
                        if (!prereq.isEmpty()) {
                            subject.addPrerequisite(prereq);
                        }
                    }
                }
                
                refreshSubjectList(subjectListModel);
                JOptionPane.showMessageDialog(panel, "Subject updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from the list display
                
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Are you sure you want to delete subject '" + subjectId + "'?", 
                    "Confirm Deletion", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dbManager.removeSubject(subjectId);
                    refreshSubjectList(subjectListModel);
                    JOptionPane.showMessageDialog(panel, "Subject deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        
        // Schedule management
        addScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from the list display
                Subject subject = dbManager.getSubject(subjectId);
                
                String room = roomField.getText().trim();
                String day = (String) dayComboBox.getSelectedItem();
                String startTime = startTimeField.getText().trim();
                String endTime = endTimeField.getText().trim();
                
                if (room.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Room, start time, and end time are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Schedule schedule = new Schedule(subjectId, room, day, startTime, endTime);
                subject.addSchedule(schedule);
                
                // Refresh schedule list
                scheduleListModel.clear();
                for (Schedule s : subject.getSchedules()) {
                    scheduleListModel.addElement(s.toString());
                }
                
                // Clear fields
                roomField.setText("");
                startTimeField.setText("");
                endTimeField.setText("");
                
                JOptionPane.showMessageDialog(panel, "Schedule added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        removeScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (subjectList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a subject first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (scheduleList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a schedule to remove", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String subjectId = subjectList.getSelectedValue().split(" ")[0]; // Get ID from the list display
                Subject subject = dbManager.getSubject(subjectId);
                
                int selectedIndex = scheduleList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < subject.getSchedules().size()) {
                    subject.removeSchedule(subject.getSchedules().get(selectedIndex));
                    
                    // Refresh schedule list
                    scheduleListModel.clear();
                    for (Schedule s : subject.getSchedules()) {
                        scheduleListModel.addElement(s.toString());
                    }
                    
                    JOptionPane.showMessageDialog(panel, "Schedule removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        clearScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomField.setText("");
                startTimeField.setText("");
                endTimeField.setText("");
                scheduleList.clearSelection();
            }
        });
        
        
        return panel;
    }
    
    //  helper method clear of createSubjectPanel()
    private void clearSubjectFields(JTextField idField, JTextField nameField,  JTextField departmentField, JTextField tuitionField, JTextField salaryField, JTextField unitsField,  JTextArea prerequisitesArea, DefaultListModel<String> enrolledListModel,  DefaultListModel<String> scheduleListModel, JTextField roomField,  JTextField startTimeField, JTextField endTimeField,  JList<String> subjectList) {
    idField.setText("");
    nameField.setText("");
    departmentField.setText("");
    tuitionField.setText("");
    salaryField.setText("");
    unitsField.setText("");
    prerequisitesArea.setText("");
    enrolledListModel.clear();
    scheduleListModel.clear();
    roomField.setText("");
    startTimeField.setText("");
    endTimeField.setText("");
    subjectList.clearSelection();
    }

    // helper method clear of createFacultyPanel()
    private void clearFacultyFields(JTextField usernameField, JPasswordField passwordField, JTextField nameField, JTextField ageField, JComboBox<String> genderComboBox, JTextField departmentField, JList<String> facultyList) {
    usernameField.setText("");
    passwordField.setText("");
    nameField.setText("");
    ageField.setText("");
    genderComboBox.setSelectedIndex(0);
    departmentField.setText("");
    facultyList.clearSelection();
    }

    // Helper method to refresh subject list
    private void refreshSubjectList(DefaultListModel<String> model) {
        model.clear();
        List<Subject> subjects = dbManager.getAllSubjects();
        for (Subject subject : subjects) {
            model.addElement(subject.getId() + " - " + subject.getName());
        }
    }
    
    // Tuition Management Panel
    private JPanel createTuitionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Student list on the left
        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(studentListModel);
        JScrollPane studentScrollPane = new JScrollPane(studentList);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        // Refresh the student list
        refreshStudentList(studentListModel);
        
        // Tuition details on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Tuition Details"));
        
        // Table for enrolled subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Tuition"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable subjectsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(subjectsTable);
        
        // Tuition summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total Tuition: ₱0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalLabel);
        
        detailsPanel.add(tableScrollPane, BorderLayout.CENTER);
        detailsPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Event listeners
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
                    String username = studentList.getSelectedValue();
                    User student = dbManager.getUser(username);
                    
                    if (student != null) {
                        // Clear table
                        tableModel.setRowCount(0);
                        
                        // Fill table with enrolled subjects
                        List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(username);
                        double totalTuition = 0;
                        
                        for (Subject subject : enrolledSubjects) {
                            tableModel.addRow(new Object[]{
                                subject.getId(),
                                subject.getName(),
                                String.format("₱%.2f", subject.getTuition())
                            });
                            
                            totalTuition += subject.getTuition();
                        }
                        
                        // Update total
                        totalLabel.setText(String.format("Total Tuition: ₱%.2f", totalTuition));
                    }
                }
            }
        });
        
        return panel;
    }
    
    // Salary Management Panel
    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Faculty list on the left
        DefaultListModel<String> facultyListModel = new DefaultListModel<>();
        JList<String> facultyList = new JList<>(facultyListModel);
        JScrollPane facultyScrollPane = new JScrollPane(facultyList);
        facultyScrollPane.setBorder(BorderFactory.createTitledBorder("Faculty List"));
        
        // Refresh the faculty list
        refreshFacultyList(facultyListModel);
        
        // Salary details on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Salary Details"));
        
        // Table for assigned subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Salary"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable subjectsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(subjectsTable);
        
        // Salary summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel baseSalaryLabel = new JLabel("Base Salary:");
        JTextField baseSalaryField = new JTextField("0.00");
        JLabel totalSubjectsLabel = new JLabel("Total from Subjects:");
        JLabel totalSubjectsValueLabel = new JLabel("₱0.00");
        JLabel grandTotalLabel = new JLabel("Grand Total:");
        JLabel grandTotalValueLabel = new JLabel("₱0.00");
        
        summaryPanel.add(baseSalaryLabel);
        summaryPanel.add(baseSalaryField);
        summaryPanel.add(totalSubjectsLabel);
        summaryPanel.add(totalSubjectsValueLabel);
        summaryPanel.add(grandTotalLabel);
        summaryPanel.add(grandTotalValueLabel);
        
        // Button to update salary
        JButton updateButton = new JButton("Update Base Salary");
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(summaryPanel, BorderLayout.CENTER);
        controlPanel.add(updateButton, BorderLayout.SOUTH);
        
        detailsPanel.add(tableScrollPane, BorderLayout.CENTER);
        detailsPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, facultyScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        
        
        // Event listeners
        facultyList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && facultyList.getSelectedValue() != null) {
                    String username = facultyList.getSelectedValue();
                    User faculty = dbManager.getUser(username);
                    
                    if (faculty != null) {
                        // Clear table
                        tableModel.setRowCount(0);
                        
                        // Get base salary from user info
                        String baseSalaryStr = faculty.getUserInfo("baseSalary");
                        double baseSalary = 0;
                        if (baseSalaryStr != null && !baseSalaryStr.isEmpty()) {
                            try {
                                baseSalary = Double.parseDouble(baseSalaryStr);
                            } catch (NumberFormatException ex) {
                                baseSalary = 0;
                            }
                        }
                        baseSalaryField.setText(String.format("%.2f", baseSalary));
                        
                        // Fill table with assigned subjects
                        List<Subject> assignedSubjects = dbManager.getAssignedSubjects(username);
                        double totalSubjectsSalary = 0;
                        
                        for (Subject subject : assignedSubjects) {
                            tableModel.addRow(new Object[]{
                                subject.getId(),
                                subject.getName(),
                                String.format("₱%.2f", subject.getSalary())
                            });
                            
                            totalSubjectsSalary += subject.getSalary();
                        }
                        
                        // Update totals
                        totalSubjectsValueLabel.setText(String.format("₱%.2f", totalSubjectsSalary));
                        grandTotalValueLabel.setText(String.format("₱%.2f", baseSalary + totalSubjectsSalary));
                    }
                }
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (facultyList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(panel, "Please select a faculty member first", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String username = facultyList.getSelectedValue();
                User faculty = dbManager.getUser(username);
                
                try {
                    double baseSalary = Double.parseDouble(baseSalaryField.getText().trim());
                    faculty.setUserInfo("baseSalary", String.valueOf(baseSalary));
                    
                    // Update grand total
                    double totalSubjectsSalary = Double.parseDouble(totalSubjectsValueLabel.getText().replace("₱", ""));
                    grandTotalValueLabel.setText(String.format("₱%.2f", baseSalary + totalSubjectsSalary));
                    
                    JOptionPane.showMessageDialog(panel, "Base salary updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Invalid base salary format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
          
        return panel;
    }
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
    
        for (char c : str.toCharArray()) {
             if (!Character.isDigit(c)) {
                 return false;
             }
         }
    
        return true;
        }
}



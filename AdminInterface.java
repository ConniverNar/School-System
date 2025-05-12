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
    
    private boolean schedulesConflict(Schedule s1, Schedule s2) {
        if (!s1.getDayOfWeek().equals(s2.getDayOfWeek())) {
            return false;
        }
        try {
            int s1Start = parseTimeToMinutes(s1.getStartTime());
            int s1End = parseTimeToMinutes(s1.getEndTime());
            int s2Start = parseTimeToMinutes(s2.getStartTime());
            int s2End = parseTimeToMinutes(s2.getEndTime());
            
            // Check if time intervals overlap
            if (s1Start < s2End && s2Start < s1End) {
                return true;
            }
        } catch (Exception e) {
            // In case of parsing error, assume no conflict
            return false;
        }
        return false;
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
    
    private int parseTimeToMinutes(String timeStr) throws Exception {
        // Parse time strings like "7:30 AM", "10:00 AM", "13:00", "8:00 PM"
        timeStr = timeStr.trim().toUpperCase();
        int minutes = 0;
        boolean isPM = false;
        if (timeStr.endsWith("AM")) {
            timeStr = timeStr.replace("AM", "").trim();
        } else if (timeStr.endsWith("PM")) {
            timeStr = timeStr.replace("PM", "").trim();
            isPM = true;
        }
        String[] parts = timeStr.split(":");
        if (parts.length != 2) {
            throw new Exception("Invalid time format: " + timeStr);
        }
        int hour = Integer.parseInt(parts[0].trim());
        int minute = Integer.parseInt(parts[1].trim());
        if (hour < 1 || hour > 12 || minute < 0 || minute > 59) {
            throw new Exception("Invalid time value: " + timeStr);
        }
        if (isPM && hour != 12) {
            hour += 12;
        }
        if (!isPM && hour == 12) {
            hour = 0;
        }
        minutes = hour * 60 + minute;
        return minutes;
    }
    
    public AdminInterface(DatabaseManager dbManager, User adminUser) {
        this.dbManager = dbManager;
        this.adminUser = adminUser;
        
        setTitle("Admin Interface - " + adminUser.getUserInfo("name"));
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Times New Roman", Font.BOLD, 22));
        tabbedPane.setForeground(new Color(89, 54, 26)); // Dark brown #59361A
        
        // Add tabs for each functionality
        tabbedPane.addTab("Student Management", createStudentPanel());
        tabbedPane.addTab("Faculty Management", createFacultyPanel());
        tabbedPane.addTab("Subject Management", createSubjectPanel());
        tabbedPane.addTab("Tuition Management", createTuitionPanel());
        tabbedPane.addTab("Salary Management", createSalaryPanel());
        
        // Add logout button at bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage(dbManager);
            }
        });
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    // Student Management Panel
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        // Student list on the left
        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(studentListModel);
        JScrollPane studentScrollPane = new JScrollPane(studentList);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(89, 54, 26)), "Student List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(89, 54, 26)));
        
        // Refresh the student list
        refreshStudentList(studentListModel);
        
        // Student details and actions on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(89, 54, 26)), "Student Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(89, 54, 26)));
        
        // Fields for student details
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        fieldsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField ageField = new JTextField(15);
        ageField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        ageField.setBackground(Color.WHITE);
        ageField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JTextField departmentField = new JTextField(15);
        departmentField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        departmentField.setBackground(Color.WHITE);
        departmentField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField schoolYearField = new JTextField(15);
        schoolYearField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        schoolYearField.setBackground(Color.WHITE);
        schoolYearField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(89, 54, 26));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(89, 54, 26));
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(89, 54, 26));
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        ageLabel.setForeground(new Color(89, 54, 26));
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        genderLabel.setForeground(new Color(89, 54, 26));
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        departmentLabel.setForeground(new Color(89, 54, 26));
        JLabel schoolYearLabel = new JLabel("School Year:");
        schoolYearLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        schoolYearLabel.setForeground(new Color(89, 54, 26));
        
        fieldsPanel.add(usernameLabel);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(nameLabel);
        fieldsPanel.add(nameField);
        fieldsPanel.add(ageLabel);
        fieldsPanel.add(ageField);
        fieldsPanel.add(genderLabel);
        fieldsPanel.add(genderComboBox);
        fieldsPanel.add(departmentLabel);
        fieldsPanel.add(departmentField);
        fieldsPanel.add(schoolYearLabel);
        fieldsPanel.add(schoolYearField);
        
        detailsPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel for actions
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        JButton createButton = new JButton("Create Student");
        createButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        createButton.setFocusPainted(false);
        JButton updateButton = new JButton("Update Student");
        updateButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        updateButton.setFocusPainted(false);
        JButton deleteButton = new JButton("Delete Student");
        deleteButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        deleteButton.setFocusPainted(false);
        JButton clearButton = new JButton("Clear Fields");
        clearButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        clearButton.setFocusPainted(false);
        
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
        panel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        // Faculty list on the left
        DefaultListModel<String> facultyListModel = new DefaultListModel<>();
        JList<String> facultyList = new JList<>(facultyListModel);
        JScrollPane facultyScrollPane = new JScrollPane(facultyList);
        facultyScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(89, 54, 26)), "Faculty List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(89, 54, 26)));
        
        // Refresh the faculty list
        refreshFacultyList(facultyListModel);
        
        // Faculty details and actions on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(89, 54, 26)), "Faculty Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(89, 54, 26)));
        
        // Fields for faculty details
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        fieldsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField ageField = new JTextField(15);
        ageField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        ageField.setBackground(Color.WHITE);
        ageField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        JTextField departmentField = new JTextField(15);
        departmentField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        departmentField.setBackground(Color.WHITE);
        departmentField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(89, 54, 26));
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(89, 54, 26));
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(89, 54, 26));
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        ageLabel.setForeground(new Color(89, 54, 26));
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        genderLabel.setForeground(new Color(89, 54, 26));
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        departmentLabel.setForeground(new Color(89, 54, 26));
        
        fieldsPanel.add(usernameLabel);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);
        fieldsPanel.add(nameLabel);
        fieldsPanel.add(nameField);
        fieldsPanel.add(ageLabel);
        fieldsPanel.add(ageField);
        fieldsPanel.add(genderLabel);
        fieldsPanel.add(genderComboBox);
        fieldsPanel.add(departmentLabel);
        fieldsPanel.add(departmentField);
        
        detailsPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons panel for actions
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setBackground(new Color(255, 248, 220)); // Light cream/pale yellow #FFF8DC
        
        JButton createButton = new JButton("Create Faculty");
        createButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        createButton.setForeground(Color.WHITE);
        createButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        createButton.setFocusPainted(false);
        JButton updateButton = new JButton("Update Faculty");
        updateButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        updateButton.setFocusPainted(false);
        JButton deleteButton = new JButton("Delete Faculty");
        deleteButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        deleteButton.setFocusPainted(false);
        JButton clearButton = new JButton("Clear Fields");
        clearButton.setBackground(new Color(102, 66, 41)); // Medium-dark brown #664229
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        clearButton.setFocusPainted(false);
        
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
            String selectedValue = facultyList.getSelectedValue();
            String username = selectedValue.split(" - ")[0].trim();
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
        String selectedValue = facultyList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(panel, "Please select a faculty to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String selectedUsername = selectedValue.split(" - ")[0].trim();
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
        String selectedValue = facultyList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(panel, "Please select a faculty to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedUsername = selectedValue.split(" - ")[0].trim();
        
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
            model.addElement(facultyMember.getUsername() + " - " + facultyMember.getUserInfo("name"));
        }
    }
    
    // Subject Management Panel
private JPanel createSubjectPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Subject list on the left
    DefaultListModel<String> subjectListModel = new DefaultListModel<>();
    JList<String> subjectList = new JList<>(subjectListModel);
    JScrollPane subjectScrollPane = new JScrollPane(subjectList);
    subjectScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Subject List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 22), new Color(0x59, 0x36, 0x1A)));
    
    // Refresh the subject list
    refreshSubjectList(subjectListModel);
    
    // Create panels for subject details, enrolled students, and schedules
    JTabbedPane detailsTabbedPane = new JTabbedPane();
    
    // Subject details panel
    JPanel subjectDetailsPanel = new JPanel(new BorderLayout());
    subjectDetailsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    subjectDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Subject Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 22), new Color(0x59, 0x36, 0x1A))); // Dark brown #59361A
    
    JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    fieldsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    JTextField idField = new JTextField(15);
    idField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    idField.setBackground(Color.WHITE);
    idField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField nameField = new JTextField(15);
    nameField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    nameField.setBackground(Color.WHITE);
    nameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField departmentField = new JTextField(15);
    departmentField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    departmentField.setBackground(Color.WHITE);
    departmentField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField tuitionField = new JTextField(15);
    tuitionField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    tuitionField.setBackground(Color.WHITE);
    tuitionField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField salaryField = new JTextField(15);
    salaryField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    salaryField.setBackground(Color.WHITE);
    salaryField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField unitsField = new JTextField(15);
    unitsField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    unitsField.setBackground(Color.WHITE);
    unitsField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextArea prerequisitesArea = new JTextArea(4, 15);
    prerequisitesArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    prerequisitesArea.setBackground(Color.WHITE);
    prerequisitesArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JScrollPane prerequisitesScrollPane = new JScrollPane(prerequisitesArea);
    
    JLabel idLabel = new JLabel("Subject ID:");
    idLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    idLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    JLabel nameLabel = new JLabel("Name:");
    nameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    nameLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    JLabel departmentLabel = new JLabel("Department:");
    departmentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    departmentLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    JLabel tuitionLabel = new JLabel("Tuition:");
    tuitionLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    tuitionLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    JLabel salaryLabel = new JLabel("Salary:");
    salaryLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    salaryLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    JLabel unitsLabel = new JLabel("Units:");
    unitsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    unitsLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    JLabel prereqLabel = new JLabel("Prerequisites (IDs, one per line):");
    prereqLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    prereqLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    
    fieldsPanel.add(idLabel);
    fieldsPanel.add(idField);
    fieldsPanel.add(nameLabel);
    fieldsPanel.add(nameField);
    fieldsPanel.add(departmentLabel);
    fieldsPanel.add(departmentField);
    fieldsPanel.add(tuitionLabel);
    fieldsPanel.add(tuitionField);
    fieldsPanel.add(salaryLabel);
    fieldsPanel.add(salaryField);
    fieldsPanel.add(unitsLabel);
    fieldsPanel.add(unitsField);
    fieldsPanel.add(prereqLabel);
    fieldsPanel.add(prerequisitesScrollPane);
    
    subjectDetailsPanel.add(fieldsPanel, BorderLayout.CENTER);
    
    // Buttons panel for actions
    JPanel buttonsPanel = new JPanel(new FlowLayout());
    buttonsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    JButton createButton = new JButton("Create Subject");
    createButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    createButton.setForeground(Color.WHITE);
    createButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    createButton.setFocusPainted(false);
    JButton updateButton = new JButton("Update Subject");
    updateButton.setBackground(new Color(0x66, 0x42, 0x29));
    updateButton.setForeground(Color.WHITE);
    updateButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    updateButton.setFocusPainted(false);
    JButton deleteButton = new JButton("Delete Subject");
    deleteButton.setBackground(new Color(0x66, 0x42, 0x29));
    deleteButton.setForeground(Color.WHITE);
    deleteButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    deleteButton.setFocusPainted(false);
    JButton clearButton = new JButton("Clear Fields");
    clearButton.setBackground(new Color(0x66, 0x42, 0x29));
    clearButton.setForeground(Color.WHITE);
    clearButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    clearButton.setFocusPainted(false);
    
    buttonsPanel.add(createButton);
    buttonsPanel.add(updateButton);
    buttonsPanel.add(deleteButton);
    buttonsPanel.add(clearButton);
    
    subjectDetailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
    // Enrolled students panel
    JPanel enrolledPanel = new JPanel(new BorderLayout());
    enrolledPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    DefaultListModel<String> enrolledListModel = new DefaultListModel<>();
    JList<String> enrolledList = new JList<>(enrolledListModel);
    enrolledList.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    enrolledList.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    JScrollPane enrolledScrollPane = new JScrollPane(enrolledList);
    
    // Add new panel for enrolling students
    JPanel enrollStudentPanel = new JPanel(new BorderLayout());
    enrollStudentPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    // ComboBox for selecting students
    DefaultComboBoxModel<String> studentComboModel = new DefaultComboBoxModel<>();
    JComboBox<String> studentComboBox = new JComboBox<>(studentComboModel);
    studentComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    studentComboBox.setBackground(Color.WHITE);
    studentComboBox.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    studentComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    // Populate student combo box
    List<User> allStudents = dbManager.getUsersByType(User.UserType.STUDENT);
    for (User student : allStudents) {
        studentComboModel.addElement(student.getUsername() + " - " + student.getUserInfo("name"));
    }
    
    // Button to enroll selected student
    JButton enrollButton = new JButton("Enroll Student");
    JButton unenrollButton = new JButton("Unenroll Selected Student");
    JButton refreshEnrolledButton = new JButton("Refresh Enrolled Students");
    
    enrollButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    enrollButton.setForeground(Color.WHITE);
    enrollButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    enrollButton.setFocusPainted(false);
    
    unenrollButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    unenrollButton.setForeground(Color.WHITE);
    unenrollButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    unenrollButton.setFocusPainted(false);
    
    refreshEnrolledButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    refreshEnrolledButton.setForeground(Color.WHITE);
    refreshEnrolledButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    refreshEnrolledButton.setFocusPainted(false);
    
    JPanel enrollActionPanel = new JPanel(new FlowLayout());
    enrollActionPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    enrollActionPanel.add(enrollButton);
    enrollActionPanel.add(unenrollButton);
    enrollActionPanel.add(refreshEnrolledButton);
    
    enrollStudentPanel.add(studentComboBox, BorderLayout.CENTER);
    enrollStudentPanel.add(enrollActionPanel, BorderLayout.SOUTH);
    
    enrolledPanel.add(enrolledScrollPane, BorderLayout.CENTER);
    enrolledPanel.add(enrollStudentPanel, BorderLayout.SOUTH);
    
    // Schedules panel
    JPanel schedulesPanel = new JPanel(new BorderLayout());
    schedulesPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    DefaultListModel<String> scheduleListModel = new DefaultListModel<>();
    JList<String> scheduleList = new JList<>(scheduleListModel);
    JScrollPane scheduleScrollPane = new JScrollPane(scheduleList);
    
    JPanel scheduleDetailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    scheduleDetailsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    JTextField roomField = new JTextField(15);
    roomField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    roomField.setBackground(Color.WHITE);
    roomField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    // Replace dayList JList with a dropdown button with checkboxes for days
    String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    JButton dayDropdownButton = new JButton("Select Days");
    dayDropdownButton.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    dayDropdownButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    dayDropdownButton.setForeground(Color.WHITE);
    dayDropdownButton.setFocusPainted(false);
    JPopupMenu dayPopupMenu = new JPopupMenu();
    JCheckBoxMenuItem[] dayCheckBoxes = new JCheckBoxMenuItem[days.length];
    for (int i = 0; i < days.length; i++) {
        dayCheckBoxes[i] = new JCheckBoxMenuItem(days[i]);
        dayCheckBoxes[i].setFont(new Font("Times New Roman", Font.PLAIN, 16));
        dayPopupMenu.add(dayCheckBoxes[i]);
    }
    dayDropdownButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            dayPopupMenu.show(dayDropdownButton, 0, dayDropdownButton.getHeight());
        }
    });

    // Update button text based on selected checkboxes
    ActionListener updateButtonTextListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder selectedDaysText = new StringBuilder();
            for (JCheckBoxMenuItem cb : dayCheckBoxes) {
                if (cb.isSelected()) {
                    if (selectedDaysText.length() > 0) {
                        selectedDaysText.append(", ");
                    }
                    selectedDaysText.append(cb.getText());
                }
            }
            if (selectedDaysText.length() == 0) {
                dayDropdownButton.setText("Select Days");
            } else {
                dayDropdownButton.setText(selectedDaysText.toString());
            }
        }
    };
    for (JCheckBoxMenuItem cb : dayCheckBoxes) {
        cb.addActionListener(updateButtonTextListener);
    }

    dayDropdownButton.setPreferredSize(new Dimension(120, 25));

    JTextField startTimeField = new JTextField(15);
    startTimeField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    startTimeField.setBackground(Color.WHITE);
    startTimeField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    JTextField endTimeField = new JTextField(15);
    endTimeField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    endTimeField.setBackground(Color.WHITE);
    endTimeField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    
    scheduleDetailsPanel.add(new JLabel("Room Number:"));
    scheduleDetailsPanel.add(roomField);
    JLabel dayLabel = new JLabel("Day:");
    dayLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    dayLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    scheduleDetailsPanel.add(dayLabel);
    scheduleDetailsPanel.add(dayDropdownButton);
    JLabel startTimeLabel = new JLabel("Start Time:");
    startTimeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    startTimeLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    scheduleDetailsPanel.add(startTimeLabel);
    scheduleDetailsPanel.add(startTimeField);
    JLabel endTimeLabel = new JLabel("End Time:");
    endTimeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    endTimeLabel.setForeground(new Color(0x59, 0x36, 0x1A));
    scheduleDetailsPanel.add(endTimeLabel);
    scheduleDetailsPanel.add(endTimeField);
    
    JPanel scheduleButtonsPanel = new JPanel(new FlowLayout());
    scheduleButtonsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    
    JButton addScheduleButton = new JButton("Add Schedule");
    addScheduleButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    addScheduleButton.setForeground(Color.WHITE);
    addScheduleButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    addScheduleButton.setFocusPainted(false);
    JButton removeScheduleButton = new JButton("Remove Schedule");
    removeScheduleButton.setBackground(new Color(0x66, 0x42, 0x29));
    removeScheduleButton.setForeground(Color.WHITE);
    removeScheduleButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    removeScheduleButton.setFocusPainted(false);
    JButton clearScheduleButton = new JButton("Clear Fields");
    clearScheduleButton.setBackground(new Color(0x66, 0x42, 0x29));
    clearScheduleButton.setForeground(Color.WHITE);
    clearScheduleButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    clearScheduleButton.setFocusPainted(false);
    
    scheduleButtonsPanel.add(addScheduleButton);
    scheduleButtonsPanel.add(removeScheduleButton);
    scheduleButtonsPanel.add(clearScheduleButton);
    
    JPanel scheduleControlPanel = new JPanel(new BorderLayout());
    scheduleControlPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    scheduleControlPanel.add(scheduleDetailsPanel, BorderLayout.CENTER);
    scheduleControlPanel.add(scheduleButtonsPanel, BorderLayout.SOUTH);
    
    schedulesPanel.add(scheduleScrollPane, BorderLayout.CENTER);
    schedulesPanel.add(scheduleControlPanel, BorderLayout.SOUTH);
    
    // Add Faculty Assignment panel
    JPanel facultyPanel = new JPanel(new BorderLayout());
    facultyPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    facultyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Faculty Assignment", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(0x59, 0x36, 0x1A))); // Dark brown #59361A

    // Current faculty assignment
    JPanel currentFacultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    currentFacultyPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    JLabel currentFacultyLabel = new JLabel("Current Faculty: None");
    currentFacultyLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    currentFacultyLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    currentFacultyPanel.add(currentFacultyLabel);

    // Schedule list for faculty assignment
    DefaultListModel<String> facultyScheduleListModel = new DefaultListModel<>();
    JList<String> facultyScheduleList = new JList<>(facultyScheduleListModel);
    facultyScheduleList.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    facultyScheduleList.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    JScrollPane facultyScheduleScrollPane = new JScrollPane(facultyScheduleList);
    facultyScheduleScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Schedules", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 16), new Color(0x59, 0x36, 0x1A)));

    // Faculty selection
    DefaultComboBoxModel<String> facultyComboModel = new DefaultComboBoxModel<>();
    JComboBox<String> facultyComboBox = new JComboBox<>(facultyComboModel);
    facultyComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    facultyComboBox.setBackground(Color.WHITE);
    facultyComboBox.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown #59361A
    facultyComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // Populate faculty combo box
    List<User> allFaculty = dbManager.getUsersByType(User.UserType.FACULTY);
    facultyComboModel.addElement("None"); // Option to remove faculty assignment
    for (User faculty : allFaculty) {
        facultyComboModel.addElement(faculty.getUsername() + " - " + faculty.getUserInfo("name"));
    }

    JButton assignFacultyButton = new JButton("Assign Faculty");
    assignFacultyButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    assignFacultyButton.setForeground(Color.WHITE);
    assignFacultyButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    assignFacultyButton.setFocusPainted(false);

    JButton refreshFacultyButton = new JButton("Refresh Faculty Assignment");
    refreshFacultyButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
    refreshFacultyButton.setForeground(Color.WHITE);
    refreshFacultyButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
    refreshFacultyButton.setFocusPainted(false);

    JPanel assignFacultyPanel = new JPanel(new BorderLayout());
    assignFacultyPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    assignFacultyPanel.add(facultyComboBox, BorderLayout.CENTER);
    JPanel assignFacultyButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    assignFacultyButtonPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
    assignFacultyButtonPanel.add(assignFacultyButton);
    assignFacultyButtonPanel.add(refreshFacultyButton);
    assignFacultyPanel.add(assignFacultyButtonPanel, BorderLayout.EAST);

    facultyPanel.add(currentFacultyPanel, BorderLayout.NORTH);
    facultyPanel.add(facultyScheduleScrollPane, BorderLayout.CENTER);
    facultyPanel.add(assignFacultyPanel, BorderLayout.SOUTH);
    
    // Add panels to tabbed pane
    detailsTabbedPane.addTab("Subject Details", subjectDetailsPanel);
    detailsTabbedPane.addTab("Enrolled Students", enrolledPanel);
    detailsTabbedPane.addTab("Schedules", schedulesPanel);
    detailsTabbedPane.addTab("Faculty Assignment", facultyPanel);
    
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
    final JLabel finalCurrentFacultyLabel = currentFacultyLabel;

    ActionListener clearFieldsAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearSubjectFields(finalIdField, finalNameField, finalDepartmentField, finalTuitionField, finalSalaryField, finalUnitsField,  finalPrerequisitesArea, finalEnrolledListModel,   finalScheduleListModel, finalRoomField,   finalStartTimeField, finalEndTimeField, finalSubjectList);
            finalCurrentFacultyLabel.setText("Current Faculty: None");
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
                    
                    // Fill enrolled students with schedule info
                    enrolledListModel.clear();
                    for (String student : subject.getEnrolledStudents()) {
                        User studentUser = dbManager.getUser(student);
                        if (studentUser != null) {
                            List<Schedule> studentSchedules = subject.getStudentSchedules(student);
                            StringBuilder scheduleInfoBuilder = new StringBuilder();
                            if (!studentSchedules.isEmpty()) {
                                scheduleInfoBuilder.append(" (");
                                for (int i = 0; i < studentSchedules.size(); i++) {
                                    scheduleInfoBuilder.append(studentSchedules.get(i).toString());
                                    if (i < studentSchedules.size() - 1) {
                                        scheduleInfoBuilder.append(", ");
                                    }
                                }
                                scheduleInfoBuilder.append(")");
                            }
                            enrolledListModel.addElement(student + " - " + studentUser.getUserInfo("name") + scheduleInfoBuilder.toString());
                        }
                    }
                    
                    // Fill schedules
                    scheduleListModel.clear();
                    for (Schedule schedule : subject.getSchedules()) {
                        scheduleListModel.addElement(schedule.toString());
                    }
                    
                    // Fill faculty schedule list for faculty assignment tab
                    facultyScheduleListModel.clear();
                    for (Schedule schedule : subject.getSchedules()) {
                        facultyScheduleListModel.addElement(schedule.toString());
                    }
                    
                    // Update faculty assignment
                    String assignedFaculty = subject.getAssignedFaculty();
                    if (assignedFaculty != null && !assignedFaculty.isEmpty()) {
                        User faculty = dbManager.getUser(assignedFaculty);
                        if (faculty != null) {
                            currentFacultyLabel.setText("Current Faculty: " + assignedFaculty + " - " + faculty.getUserInfo("name"));
                        } else {
                            currentFacultyLabel.setText("Current Faculty: " + assignedFaculty);
                        }
                    } else {
                        currentFacultyLabel.setText("Current Faculty: None");
                    }
                    
                    // Refresh forcibly enroll student combo box with students not enrolled in this subject
                    studentComboModel.removeAllElements();
                    List<User> allStudents = dbManager.getUsersByType(User.UserType.STUDENT);
                    for (User student : allStudents) {
                        if (!subject.getEnrolledStudents().contains(student.getUsername())) {
                            studentComboModel.addElement(student.getUsername() + " - " + student.getUserInfo("name"));
                        }
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
        String department = departmentField.getText().trim().toLowerCase();
        
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
            
            Subject newSubject = new Subject(newId, nameField.getText().trim(), departmentField.getText().trim().toLowerCase(), tuition, salary, units);
            
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
            // subject.setUnits(units); // Removed because setUnits(int) method is undefined in Subject class
            
            // Clear and re-add prerequisites
            subject.getPrerequisites().clear();
            String[] prereqLines = prerequisitesArea.getText().split("\n");
            for (String prereq : prereqLines) {
                prereq = prereq.trim();
                if (!prereq.isEmpty()) {
                    subject.addPrerequisite(prereq);
                }
            }
            
            // Update name and department as well
            subject.setName(nameField.getText().trim());
            subject.setDepartment(departmentField.getText().trim().toLowerCase());
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
    
            // Force enroll student
            enrollButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (subjectList.getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(panel, "Please select a subject first", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (studentComboBox.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(panel, "Please select a student to enroll", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    String subjectId = subjectList.getSelectedValue().split(" ")[0];
                    Subject subject = dbManager.getSubject(subjectId);
                    
                    String studentEntry = (String) studentComboBox.getSelectedItem();
                    String studentUsername = studentEntry.split(" - ")[0];
                    
                    // Check prerequisites before enrollment
                    List<String> prerequisites = subject.getPrerequisites();
                    List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUsername);
                    for (String prereqId : prerequisites) {
                        boolean hasPrereq = false;
                        for (Subject enrolledSubject : enrolledSubjects) {
                            if (enrolledSubject.getId().equals(prereqId)) {
                                hasPrereq = true;
                                break;
                            }
                        }
                        if (!hasPrereq) {
                            JOptionPane.showMessageDialog(panel, "Student does not meet prerequisite: " + prereqId, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    
                    // Show dialog to select schedules for enrollment (multi-selection)
                    List<Schedule> schedules = subject.getSchedules();
                    if (schedules.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "No schedules available for this subject", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    JDialog scheduleDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel), "Select Schedules", true);
                    DefaultListModel<String> scheduleListModelDialog = new DefaultListModel<>();
                    for (Schedule s : schedules) {
                        scheduleListModelDialog.addElement(s.toString());
                    }
                    JList<String> scheduleJList = new JList<>(scheduleListModelDialog);
                    scheduleJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    JScrollPane scrollPane = new JScrollPane(scheduleJList);
                    scrollPane.setPreferredSize(new Dimension(400, 200));
                    
                    JButton confirmButton = new JButton("Enroll");
                    JButton cancelButton = new JButton("Cancel");
                    
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(confirmButton);
                    buttonPanel.add(cancelButton);
                    
                    JPanel dialogPanel = new JPanel(new BorderLayout());
                    dialogPanel.add(scrollPane, BorderLayout.CENTER);
                    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
                    
                    scheduleDialog.getContentPane().add(dialogPanel);
                    scheduleDialog.pack();
                    scheduleDialog.setLocationRelativeTo(panel);
                    
                    final boolean[] enrolled = {false};
                    
                    confirmButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int[] selectedIndices = scheduleJList.getSelectedIndices();
                            if (selectedIndices.length == 0) {
                                JOptionPane.showMessageDialog(scheduleDialog, "Please select at least one schedule", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            // Check for schedule conflicts before enrolling
                            List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(studentUsername);
                            for (int selectedIndex : selectedIndices) {
                                Schedule selectedSchedule = schedules.get(selectedIndex);
                                boolean conflictDetected = false;
                                for (Subject enrolledSubject : enrolledSubjects) {
                                    List<Schedule> studentSchedules = enrolledSubject.getStudentSchedules(studentUsername);
                                    for (Schedule studentSchedule : studentSchedules) {
                                        if (studentSchedule != null && AdminInterface.this.schedulesConflict(selectedSchedule, studentSchedule)) {
                                            conflictDetected = true;
                                            break;
                                        }
                                    }
                                    if (conflictDetected) {
                                        break;
                                    }
                                }
                                if (conflictDetected) {
                                    JOptionPane.showMessageDialog(scheduleDialog, "Schedule conflict detected for schedule: " + selectedSchedule.toString() + ". Enrollment cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                            // Enroll student in all selected schedules
                            for (int selectedIndex : selectedIndices) {
                                subject.enrollStudent(studentUsername, selectedIndex);
                            }
                            enrolled[0] = true;
                            scheduleDialog.dispose();
                        }
                    });
                    
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            scheduleDialog.dispose();
                        }
                    });
                    
                    scheduleDialog.setVisible(true);
                    
    if (enrolled[0]) {
    // Refresh enrolled students list with schedule info showing all schedules the student is enrolled in
    enrolledListModel.clear();
    for (String student : subject.getEnrolledStudents()) {
        User studentUser = dbManager.getUser(student);
        if (studentUser != null) {
            List<Schedule> studentSchedules = subject.getStudentSchedules(student);
            StringBuilder scheduleInfoBuilder = new StringBuilder();
            if (!studentSchedules.isEmpty()) {
                scheduleInfoBuilder.append(" (");
                for (int i = 0; i < studentSchedules.size(); i++) {
                    scheduleInfoBuilder.append(studentSchedules.get(i).toString());
                    if (i < studentSchedules.size() - 1) {
                        scheduleInfoBuilder.append(", ");
                    }
                }
                scheduleInfoBuilder.append(")");
            }
            enrolledListModel.addElement(student + " - " + studentUser.getUserInfo("name") + scheduleInfoBuilder.toString());
        }
    }
    
    // Refresh student combo box to keep all students (allow multiple schedule enrollments)
    studentComboModel.removeAllElements();
    List<User> allStudents = dbManager.getUsersByType(User.UserType.STUDENT);
    for (User student : allStudents) {
        studentComboModel.addElement(student.getUsername() + " - " + student.getUserInfo("name"));
    }
    
    JOptionPane.showMessageDialog(panel, "Student enrolled successfully in the selected schedules", "Success", JOptionPane.INFORMATION_MESSAGE);
}
                }
            });
    
    // Unenroll student
    unenrollButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (subjectList.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(panel, "Please select a subject first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (enrolledList.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(panel, "Please select a student to unenroll", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String subjectId = subjectList.getSelectedValue().split(" ")[0];
            Subject subject = dbManager.getSubject(subjectId);
            
            String studentEntry = enrolledList.getSelectedValue();
            String studentUsername = studentEntry.split(" - ")[0];
            
            // Unenroll student
            subject.unenrollStudent(studentUsername);
            
            // Refresh enrolled students list
            enrolledListModel.clear();
            for (String student : subject.getEnrolledStudents()) {
                User studentUser = dbManager.getUser(student);
                if (studentUser != null) {
                    List<Schedule> studentSchedules = subject.getStudentSchedules(student);
                    StringBuilder scheduleInfoBuilder = new StringBuilder();
                    if (!studentSchedules.isEmpty()) {
                        scheduleInfoBuilder.append(" (");
                        for (int i = 0; i < studentSchedules.size(); i++) {
                            scheduleInfoBuilder.append(studentSchedules.get(i).toString());
                            if (i < studentSchedules.size() - 1) {
                                scheduleInfoBuilder.append(", ");
                            }
                        }
                        scheduleInfoBuilder.append(")");
                    }
                    enrolledListModel.addElement(student + " - " + studentUser.getUserInfo("name") + scheduleInfoBuilder.toString());
                }
            }
            
            JOptionPane.showMessageDialog(panel, "Student unenrolled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
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

        // Get selected days from the checkboxes
        java.util.List<String> selectedDays = new java.util.ArrayList<>();
        for (JCheckBoxMenuItem cb : dayCheckBoxes) {
            if (cb.isSelected()) {
                selectedDays.add(cb.getText());
            }
        }

        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        
        if (room.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Room, start time, and end time are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (selectedDays.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please select at least one day", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        for (String day : selectedDays) {
            Schedule schedule = new Schedule(subjectId, room, day, startTime, endTime);
            subject.addSchedule(schedule);
        }
        
        // Refresh schedule list
        scheduleListModel.clear();
        for (Schedule s : subject.getSchedules()) {
            scheduleListModel.addElement(s.toString());
        }
        
        // Clear fields
        roomField.setText("");
        startTimeField.setText("");
        endTimeField.setText("");
        // Clear checkboxes selection and update button text
        for (JCheckBoxMenuItem cb : dayCheckBoxes) {
            cb.setSelected(false);
        }
        dayDropdownButton.setText("Select Days");
        
        JOptionPane.showMessageDialog(panel, "Schedule(s) added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
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
    
    // Faculty assignment
assignFacultyButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (subjectList.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(panel, "Please select a subject first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (facultyScheduleList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(panel, "Please select a schedule to assign the faculty to", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String subjectId = subjectList.getSelectedValue().split(" ")[0];
        Subject subject = dbManager.getSubject(subjectId);
        
        String facultyEntry = (String) facultyComboBox.getSelectedItem();
        
        int scheduleIndex = facultyScheduleList.getSelectedIndex();
        
        if (facultyEntry.equals("None")) {
            // Remove faculty assignment for the selected schedule
            dbManager.removeScheduleAssignment(subjectId, scheduleIndex);
            currentFacultyLabel.setText("Current Faculty: None");
            JOptionPane.showMessageDialog(panel, "Faculty assignment removed from the selected schedule", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Assign new faculty to the selected schedule
            String facultyUsername = facultyEntry.split(" - ")[0];
            
            // Check for schedule conflict before assigning
            List<Subject> allSubjects = dbManager.getAllSubjects();
            Schedule selectedSchedule = null;
            if (scheduleIndex >= 0 && scheduleIndex < subject.getSchedules().size()) {
                selectedSchedule = subject.getSchedules().get(scheduleIndex);
            }
            if (selectedSchedule == null) {
                JOptionPane.showMessageDialog(panel, "Selected schedule is invalid", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean conflictDetected = false;
            for (Subject s : allSubjects) {
                List<Schedule> schedules = s.getSchedules();
                for (int i = 0; i < schedules.size(); i++) {
                    String assignedFaculty = dbManager.getScheduleAssignment(s.getId(), i);
                    if (assignedFaculty != null && assignedFaculty.equals(facultyUsername)) {
                        Schedule facultySchedule = schedules.get(i);
                        if (AdminInterface.this.schedulesConflict(selectedSchedule, facultySchedule)) {
                            conflictDetected = true;
                            break;
                        }
                    }
                }
                if (conflictDetected) {
                    break;
                }
            }
            
            if (conflictDetected) {
                JOptionPane.showMessageDialog(panel, "Schedule conflict detected. Faculty assignment cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            dbManager.assignScheduleToFaculty(subjectId, scheduleIndex, facultyUsername);
            currentFacultyLabel.setText("Current Faculty: " + facultyEntry);
            JOptionPane.showMessageDialog(panel, "Faculty assigned successfully to the selected schedule", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
});

// Add listener to update currentFacultyLabel when a schedule is selected in facultyScheduleList
facultyScheduleList.addListSelectionListener(new ListSelectionListener() {
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && facultyScheduleList.getSelectedIndex() != -1 && subjectList.getSelectedValue() != null) {
            String subjectId = subjectList.getSelectedValue().split(" ")[0];
            int scheduleIndex = facultyScheduleList.getSelectedIndex();
            String assignedFaculty = dbManager.getScheduleAssignment(subjectId, scheduleIndex);
            if (assignedFaculty != null && !assignedFaculty.isEmpty()) {
                User faculty = dbManager.getUser(assignedFaculty);
                if (faculty != null) {
                    currentFacultyLabel.setText("Current Faculty: " + assignedFaculty + " - " + faculty.getUserInfo("name"));
                } else {
                    currentFacultyLabel.setText("Current Faculty: " + assignedFaculty);
                }
            } else {
                currentFacultyLabel.setText("Current Faculty: None");
            }
        }
    }
});
    
    // Refresh enrolled students button action listener
refreshEnrolledButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (subjectList.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(panel, "Please select a subject first to refresh enrolled students", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String subjectId = subjectList.getSelectedValue().split(" ")[0];
        Subject subject = dbManager.getSubject(subjectId);
        if (subject != null) {
            enrolledListModel.clear();
            for (String student : subject.getEnrolledStudents()) {
                User studentUser = dbManager.getUser(student);
                if (studentUser != null) {
                    List<Schedule> studentSchedules = subject.getStudentSchedules(student);
                    StringBuilder scheduleInfoBuilder = new StringBuilder();
                    if (!studentSchedules.isEmpty()) {
                        scheduleInfoBuilder.append(" (");
                        for (int i = 0; i < studentSchedules.size(); i++) {
                            scheduleInfoBuilder.append(studentSchedules.get(i).toString());
                            if (i < studentSchedules.size() - 1) {
                                scheduleInfoBuilder.append(", ");
                            }
                        }
                        scheduleInfoBuilder.append(")");
                    }
                    enrolledListModel.addElement(student + " - " + studentUser.getUserInfo("name") + scheduleInfoBuilder.toString());
                }
            }
        }
    }
});
    
    // Refresh faculty assignment button action listener
    refreshFacultyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (subjectList.getSelectedValue() != null) {
                String subjectId = subjectList.getSelectedValue().split(" ")[0];
                Subject subject = dbManager.getSubject(subjectId);
                if (subject != null) {
                    // Refresh faculty combo box
                    facultyComboModel.removeAllElements();
                    facultyComboModel.addElement("None");
                    List<User> allFaculty = dbManager.getUsersByType(User.UserType.FACULTY);
                    for (User faculty : allFaculty) {
                        facultyComboModel.addElement(faculty.getUsername() + " - " + faculty.getUserInfo("name"));
                    }
                    // Update current faculty label
                    String assignedFaculty = subject.getAssignedFaculty();
                    if (assignedFaculty != null && !assignedFaculty.isEmpty()) {
                        User faculty = dbManager.getUser(assignedFaculty);
                        if (faculty != null) {
                            currentFacultyLabel.setText("Current Faculty: " + assignedFaculty + " - " + faculty.getUserInfo("name"));
                        } else {
                            currentFacultyLabel.setText("Current Faculty: " + assignedFaculty);
                        }
                    } else {
                        currentFacultyLabel.setText("Current Faculty: None");
                    }
                }
            }
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
        panel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        
        // Student list on the left
        DefaultListModel<String> studentListModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(studentListModel);
        JScrollPane studentScrollPane = new JScrollPane(studentList);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Student List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(0x59, 0x36, 0x1A)));
        
        // Refresh the student list with username and name
        Runnable refreshStudentList = () -> {
            studentListModel.clear();
            List<User> students = dbManager.getUsersByType(User.UserType.STUDENT);
            for (User student : students) {
                studentListModel.addElement(student.getUsername() + " - " + student.getUserInfo("name"));
            }
        };
        refreshStudentList.run();
        
        // Tuition details on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Tuition Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(0x59, 0x36, 0x1A)));
        
        // Table for enrolled subjects
        String[] columnNames = {"Subject ID", "Subject Name", "Tuition"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable subjectsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(subjectsTable);
        
        // Tuition summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        JLabel totalLabel = new JLabel("Total Tuition: ₱0.00");
        totalLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        summaryPanel.add(totalLabel);
        
        detailsPanel.add(tableScrollPane, BorderLayout.CENTER);
        detailsPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        // Refresh button panel
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        refreshButton.setFocusPainted(false);
        refreshPanel.add(refreshButton);
        panel.add(refreshPanel, BorderLayout.NORTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, studentScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Event listeners
        studentList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && studentList.getSelectedValue() != null) {
                    String selectedValue = studentList.getSelectedValue();
                    String username = selectedValue.split(" - ")[0].trim();
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
        
        // Refresh button action listener
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Refresh student list
                refreshStudentList.run();
                // Clear tuition details table and total label
                tableModel.setRowCount(0);
                totalLabel.setText("Total Tuition: ₱0.00");
                studentList.clearSelection();
            }
        });
        
        return panel;
    }
    
    // Salary Management Panel
    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        
        // Faculty list on the left
        DefaultListModel<String> facultyListModel = new DefaultListModel<>();
        JList<String> facultyList = new JList<>(facultyListModel);
        JScrollPane facultyScrollPane = new JScrollPane(facultyList);
        facultyScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Faculty List", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(0x59, 0x36, 0x1A)));
        
        // Refresh the faculty list
        refreshFacultyList(facultyListModel);
        
        // Salary details on the right
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0x59, 0x36, 0x1A)), "Salary Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Times New Roman", Font.BOLD, 20), new Color(0x59, 0x36, 0x1A)));
        
        // Table for assigned subjects - Adding Schedule column
        String[] columnNames = {"Subject ID", "Subject Name", "Schedule", "Salary"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable subjectsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(subjectsTable);
        
        // Salary summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        summaryPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        
        JLabel baseSalaryLabel = new JLabel("Base Salary:");
        baseSalaryLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        baseSalaryLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        
        JTextField baseSalaryField = new JTextField("0.00");
        baseSalaryField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        baseSalaryField.setBackground(Color.WHITE);
        baseSalaryField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel totalSubjectsLabel = new JLabel("Total from Subjects:");
        totalSubjectsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        totalSubjectsLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        
        JLabel totalSubjectsValueLabel = new JLabel("₱0.00");
        totalSubjectsValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        totalSubjectsValueLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        
        JLabel grandTotalLabel = new JLabel("Grand Total:");
        grandTotalLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        grandTotalLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        
        JLabel grandTotalValueLabel = new JLabel("₱0.00");
        grandTotalValueLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        grandTotalValueLabel.setForeground(new Color(0x59, 0x36, 0x1A)); // Dark brown
        
        summaryPanel.add(baseSalaryLabel);
        summaryPanel.add(baseSalaryField);
        summaryPanel.add(totalSubjectsLabel);
        summaryPanel.add(totalSubjectsValueLabel);
        summaryPanel.add(grandTotalLabel);
        summaryPanel.add(grandTotalValueLabel);
        
        // Button panel for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        
        JButton updateButton = new JButton("Update Base Salary");
        updateButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        updateButton.setFocusPainted(false);
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(new Color(0x66, 0x42, 0x29)); // Medium-dark brown #664229
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        refreshButton.setFocusPainted(false);
        
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(0xFF, 0xF8, 0xDC)); // Light cream/pale yellow #FFF8DC
        controlPanel.add(summaryPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsPanel.add(tableScrollPane, BorderLayout.CENTER);
        detailsPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, facultyScrollPane, detailsPanel);
        splitPane.setDividerLocation(200);
        panel.add(splitPane, BorderLayout.CENTER);
        
    // Create a method to populate faculty data
    final DefaultTableModel finalTableModel = tableModel;
    final JLabel finalTotalSubjectsValueLabel = totalSubjectsValueLabel;
    final JLabel finalGrandTotalValueLabel = grandTotalValueLabel;
    final JTextField finalBaseSalaryField = baseSalaryField;
    
    // Method to update the faculty data in the table
    ActionListener updateFacultyDataAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (facultyList.getSelectedValue() != null) {
                String selectedValue = facultyList.getSelectedValue();
                String username = selectedValue.split(" - ")[0].trim();
                updateFacultyData(username, finalTableModel, 
                                  finalBaseSalaryField, finalTotalSubjectsValueLabel, 
                                  finalGrandTotalValueLabel);
            }
        }
    };
    
    // Event listeners
    facultyList.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && facultyList.getSelectedValue() != null) {
                String selectedValue = facultyList.getSelectedValue();
                String username = selectedValue.split(" - ")[0].trim();
                updateFacultyData(username, finalTableModel, 
                                 finalBaseSalaryField, finalTotalSubjectsValueLabel, 
                                 finalGrandTotalValueLabel);
            }
        }
    });
    
        // Refresh button action listener
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Refresh faculty list
                refreshFacultyList(facultyListModel);
                // Clear salary details table and totals
                tableModel.setRowCount(0);
                baseSalaryField.setText("0.00");
                totalSubjectsValueLabel.setText("₱0.00");
                grandTotalValueLabel.setText("₱0.00");
                facultyList.clearSelection();
            }
        });
    
    updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (facultyList.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(panel, "Please select a faculty member first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String selectedValue = facultyList.getSelectedValue();
            String username = selectedValue.split(" - ")[0].trim();
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
    
    private void updateFacultyData(String username, DefaultTableModel tableModel, 
                             JTextField baseSalaryField, JLabel totalSubjectsValueLabel, 
                             JLabel grandTotalValueLabel) {
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
            
            // Fill table with subjects that have schedules assigned to this faculty
            List<Subject> allSubjects = dbManager.getAllSubjects();
            double totalSubjectsSalary = 0;
            
            for (Subject subject : allSubjects) {
                List<Schedule> schedules = subject.getSchedules();
                StringBuilder assignedSchedulesInfo = new StringBuilder();
                boolean hasAssignedSchedule = false;
                double subjectSalaryTotal = 0.0;
                
                for (int i = 0; i < schedules.size(); i++) {
                    String assignedFaculty = dbManager.getScheduleAssignment(subject.getId(), i);
                    if (assignedFaculty != null && assignedFaculty.equals(username)) {
                        if (assignedSchedulesInfo.length() > 0) {
                            assignedSchedulesInfo.append(", ");
                        }
                        Schedule schedule = schedules.get(i);
                        assignedSchedulesInfo.append(schedule.getDayOfWeek())
                                             .append(" ")
                                             .append(schedule.getStartTime())
                                             .append("-")
                                             .append(schedule.getEndTime());
                        hasAssignedSchedule = true;
                        // Add salary per schedule (unit)
                        subjectSalaryTotal += subject.getSalary() / schedules.size();
                    }
                }
                
                if (hasAssignedSchedule) {
                    tableModel.addRow(new Object[]{
                        subject.getId(),
                        subject.getName(),
                        assignedSchedulesInfo.toString(),
                        String.format("₱%.2f", subjectSalaryTotal)
                    });
                    totalSubjectsSalary += subjectSalaryTotal;
                }
            }
            
            // Update totals
            totalSubjectsValueLabel.setText(String.format("₱%.2f", totalSubjectsSalary));
            grandTotalValueLabel.setText(String.format("₱%.2f", baseSalary + totalSubjectsSalary));
        }
    }
}

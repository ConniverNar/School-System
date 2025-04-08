import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MainInterface extends JFrame {
	private static final long serialVersionUID = 1L;
	private String accountType;
    private FacultyManager facultyManager;
    private TuitionManager tuitionManager;
    private SalaryManager salaryManager;
    private SubjectManager subjectManager;

    public MainInterface(String accountType) {
        this.accountType = accountType;
        this.facultyManager = new FacultyManager(); // Initialize FacultyManager
        setTitle("Main Interface - " + accountType);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up layout
        setLayout(new BorderLayout());

        // Create a welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + accountType + "!", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Create buttons for different management functionalities
        JPanel buttonPanel = new JPanel();
        if (accountType.equals("Admin")) {
            JButton manageStudentsButton = new JButton("Manage Students");
            JButton manageFacultyButton = new JButton("Manage Faculty");
            JButton manageTuitionButton = new JButton("Manage Tuition");
            JButton manageSalaryButton = new JButton("Manage Salary");
            JButton manageSubjectsButton = new JButton("Manage Subjects");
            JButton subjectsListButton = new JButton("Subjects List");

            buttonPanel.add(manageStudentsButton);
            buttonPanel.add(manageFacultyButton);
            buttonPanel.add(manageTuitionButton);
            buttonPanel.add(manageSalaryButton);
            buttonPanel.add(manageSubjectsButton);
            buttonPanel.add(subjectsListButton);

            // Add action listener for managing faculty
            manageFacultyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manageFaculty();
                }
            });

            manageStudentsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //manageStudent();
                }
            });
            
            manageTuitionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manageTuition();
                }
            });
          
            manageSalaryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manageSalary();
                }
            });
            
            manageSubjectsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manageSubjects();
                }
            });
            
            subjectsListButton.addActionListener(new ActionListener() {
            	@Override
            	public void actionPerformed(ActionEvent e) {
            		viewSubjects();
            	}
            });
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void manageFaculty() {
        // Create a dialog for managing faculty
        JDialog dialog = new JDialog(this, "Manage Faculty", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Input fields for creating faculty
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField departmentField = new JTextField();

        // Add components to dialog
        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Age:"));
        dialog.add(ageField);
        dialog.add(new JLabel("Gender:"));
        dialog.add(genderField);
        dialog.add(new JLabel("Department:"));
        dialog.add(departmentField);

        // Create button to submit the form
        JButton createButton = new JButton("Create Faculty");
        dialog.add(createButton);

        // Add action listener for the create button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();
                String department = departmentField.getText();

                // Create faculty account
                facultyManager.createFaculty(username, password);
                // You can also store additional information like name, age, gender, and department in a separate structure if needed.

                JOptionPane.showMessageDialog(dialog, "Faculty account created successfully!");
                dialog.dispose(); // Close the dialog
            }
        });

        // Create button to display faculty accounts
        JButton displayButton = new JButton("Display Faculty");
        dialog.add(displayButton);

        // Add action listener for the display button
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                facultyManager.displayFaculties();
            }
        });

        // Create button to delete faculty account
        JButton deleteButton = new JButton("Delete Faculty ");
        dialog.add(deleteButton);

        // Input field for deleting faculty
        JTextField deleteUsernameField = new JTextField();
        dialog.add(new JLabel("Username to delete:"));
        dialog.add(deleteUsernameField);

        // Add action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameToDelete = deleteUsernameField.getText();
                facultyManager.deleteFaculty(usernameToDelete);
                JOptionPane.showMessageDialog(dialog, "Faculty account deleted successfully!");
            }
        });

        dialog.setVisible(true); // Show the dialog
    }
    
    private void manageTuition() {
        // Create a dialog for managing tuition
        JDialog dialog = new JDialog(this, "Manage Tuition", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Input fields for tuition management
        JTextField studentIdField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dueDateField = new JTextField();

        // Add components to dialog
        dialog.add(new JLabel("Student ID:"));
        dialog.add(studentIdField);
        dialog.add(new JLabel("Amount:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Due Date:"));
        dialog.add(dueDateField);

        // Create button to add tuition
        JButton addButton = new JButton("Add Tuition");
        dialog.add(addButton);

        // Add action listener for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String dueDate = dueDateField.getText();

                tuitionManager.addTuition(studentId, amount, dueDate);
                JOptionPane.showMessageDialog(dialog, "Tuition record added successfully!");
            }
        });

        // Create button to display tuition records
        JButton displayButton = new JButton("Display Tuition");
        dialog.add(displayButton);

        // Add action listener for the display button
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tuitionManager.displayTuitionRecords();
            }
        });

        // Create button to delete tuition record
        JButton deleteButton = new JButton("Delete Tuition");
        dialog.add(deleteButton);

        // Input field for deleting tuition
        JTextField deleteStudentIdField = new JTextField();
        dialog.add(new JLabel("Student ID to delete:"));
        dialog.add(deleteStudentIdField);

        // Add action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentIdToDelete = deleteStudentIdField.getText();
                tuitionManager.deleteTuition(studentIdToDelete);
                JOptionPane.showMessageDialog(dialog, "Tuition record deleted successfully!");
            }
        });

        // Create button to mark tuition as paid
        JButton markPaidButton = new JButton("Mark as Paid");
        dialog.add(markPaidButton);

        // Add action listener for the mark as paid button
        markPaidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentIdToMark = studentIdField.getText();
                tuitionManager.markAsPaid(studentIdToMark);
                JOptionPane.showMessageDialog(dialog, "Tuition marked as paid successfully!");
            }
        });

        dialog.setVisible(true); // Show the dialog
    }
    
    private void manageSalary() {
        // Create a dialog for managing salaries
        JDialog dialog = new JDialog(this, "Manage Salary", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Input fields for salary management
        JTextField facultyIdField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField paymentDateField = new JTextField();

        // Add components to dialog
        dialog.add(new JLabel("Faculty ID:"));
        dialog.add(facultyIdField);
        dialog.add(new JLabel("Amount:"));
        dialog.add(amountField);
        dialog.add(new JLabel("Payment Date:"));
        dialog.add(paymentDateField);

        // Create button to add salary
        JButton addButton = new JButton("Add Salary");
        dialog.add(addButton);

        // Add action listener for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String facultyId = facultyIdField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String paymentDate = paymentDateField.getText();

                salaryManager.addSalary(facultyId, amount, paymentDate);
                JOptionPane.showMessageDialog(dialog, "Salary record added successfully!");
            }
        });

        // Create button to display salary records
        JButton displayButton = new JButton("Display Salary");
        dialog.add(displayButton);

        // Add action listener for the display button
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salaryManager.displaySalaryRecords();
            }
        });

        // Create button to delete salary record
        JButton deleteButton = new JButton("Delete Salary");
        dialog.add(deleteButton);

        // Input field for deleting salary
        JTextField deleteFacultyIdField = new JTextField();
        dialog.add(new JLabel("Faculty ID to delete:"));
        dialog.add(deleteFacultyIdField);

        // Add action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String facultyIdToDelete = deleteFacultyIdField.getText();
                salaryManager.deleteSalary(facultyIdToDelete);
                JOptionPane.showMessageDialog(dialog, "Salary record deleted successfully!");
            }
        });

        // Create button to mark salary as paid
        JButton markPaidButton = new JButton("Mark as Paid");
        dialog.add(markPaidButton);

        // Add action listener for the mark as paid button
        markPaidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String facultyIdToMark = facultyIdField.getText();
                salaryManager.markAsPaid(facultyIdToMark);
                JOptionPane.showMessageDialog(dialog, "Salary marked as paid successfully!");
            }
        });

        dialog.setVisible(true); // Show the dialog
    }
    
    private void manageSubjects() {
        // Create a dialog for managing subjects
        JDialog dialog = new JDialog(this, "Manage Subjects", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(0, 2));

        // Input fields for subject management
        JTextField subjectIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField prerequisiteField = new JTextField();
        
        // Add components to dialog
        dialog.add(new JLabel("Subject ID:"));
        dialog.add(subjectIdField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Department:"));
        dialog.add(departmentField);
        dialog.add(new JLabel("Credits:"));
        dialog.add(creditsField);
        dialog.add(new JLabel("Prerequisite (Subject ID):"));
        dialog.add(prerequisiteField);
        
        // Create button to add subject
        JButton addButton = new JButton("Add Subject");
        dialog.add(addButton);

     // Add action listener for the add button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String subjectId = subjectIdField.getText();
                String name = nameField.getText();
                String department = departmentField.getText();
                int credits = Integer.parseInt(creditsField.getText());

                // Get prerequisites from a text field (assuming it's a comma-separated string)
                String prerequisiteInput = prerequisiteField.getText(); // Get prerequisite input
                List<String> prerequisites = new ArrayList<>();

                // Split the input string by commas and trim whitespace
                if (!prerequisiteInput.trim().isEmpty()) {
                    String[] prerequisiteArray = prerequisiteInput.split(",");
                    for (String prerequisite : prerequisiteArray) {
                        prerequisites.add(prerequisite.trim()); // Add each prerequisite to the list
                    }
                }

                // Add the subject with the list of prerequisites
                subjectManager.addSubject(subjectId, name, department, credits, prerequisites);
                JOptionPane.showMessageDialog(dialog, "Subject record added successfully!");
            }
        });

        // Create button to display subject records
        JButton displayButton = new JButton("Display Subjects");
        dialog.add(displayButton);

        // Add action listener for the display button
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subjectManager.getSubjectRecords();
            }
        });

        // Create button to delete subject record
        JButton deleteButton = new JButton("Delete Subject");
        dialog.add(deleteButton);

        // Input field for deleting subject
        JTextField deleteSubjectIdField = new JTextField();
        dialog.add(new JLabel("Subject ID to delete:"));
        dialog.add(deleteSubjectIdField);

        // Add action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String subjectIdToDelete = deleteSubjectIdField.getText();
                subjectManager.deleteSubject(subjectIdToDelete);
                JOptionPane.showMessageDialog(dialog, "Subject record deleted successfully!");
            }
        });

        // Create button to update subject record
        JButton updateButton = new JButton("Update Subject");
        dialog.add(updateButton);

        // Add action listener for the update button
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String subjectIdToUpdate = subjectIdField.getText();
                String newName = nameField.getText();
                String newDepartment = departmentField.getText();
                int newCredits = Integer.parseInt(creditsField.getText());

                // Get new prerequisites from the text field (assuming it's a comma-separated string)
                String prerequisiteInput = prerequisiteField.getText(); // Get new prerequisite input
                List<String> newPrerequisites = new ArrayList<>(); // Initialize the list

                // Split the input string by commas and trim whitespace
                if (!prerequisiteInput.trim().isEmpty()) {
                    String[] prerequisiteArray = prerequisiteInput.split(",");
                    for (String prerequisite : prerequisiteArray) {
                        newPrerequisites.add(prerequisite.trim()); // Add each prerequisite to the list
                    }
                }

                // Update the subject with the new details
                subjectManager.updateSubject(subjectIdToUpdate, newName, newDepartment, newCredits, newPrerequisites);
                JOptionPane.showMessageDialog(dialog, "Subject record updated successfully!");
            }
        });
        
        setVisible(true); // Show the dialog
    }
    
    private void viewSubjects() {
        // Create a dialog for viewing subjects
        JDialog dialog = new JDialog(this, "View Subjects", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        // Create a list to display subjects
        JList<Subject> subjectList = new JList<>(subjectManager.getSubjectRecords().toArray(new Subject[0]));
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(subjectList);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button to view enrolled students
        JButton viewStudentsButton = new JButton("View Enrolled Students");
        buttonPanel.add(viewStudentsButton);

        // Button to create a schedule
        JButton createScheduleButton = new JButton("Create Schedule");
        buttonPanel.add(createScheduleButton);

        // Button to delete a schedule
        JButton deleteScheduleButton = new JButton("Delete Schedule");
        buttonPanel.add(deleteScheduleButton);

        // Action listener for viewing enrolled students
        viewStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Subject selectedSubject = subjectList.getSelectedValue();
                if (selectedSubject != null) {
                    // Show enrolled students in a popup
                    StringBuilder enrolledStudents = new StringBuilder("Enrolled Students:\n");
                    // Here you would retrieve the list of students enrolled in the selected subject
                    // For demonstration, we will just show a placeholder
                    enrolledStudents.append("Student1\nStudent2\nStudent3"); // Placeholder
                    JOptionPane.showMessageDialog(dialog, enrolledStudents.toString());
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a subject.");
                }
            }
        });

        // Action listener for creating a schedule
        createScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Subject selectedSubject = subjectList.getSelectedValue();
                if (selectedSubject != null) {
                    // Create a dialog to input schedule details
                    JDialog scheduleDialog = new JDialog(dialog, "Create Schedule", true);
                    scheduleDialog.setSize(300, 200);
                    scheduleDialog.setLayout(new GridLayout(0, 2));

                    JTextField roomField = new JTextField();
                    JTextField timeField = new JTextField();

                    scheduleDialog.add(new JLabel("Room Number:"));
                    scheduleDialog.add(roomField);
                    scheduleDialog.add(new JLabel("Time:"));
                    scheduleDialog.add(timeField);

                    JButton addButton = new JButton("Add Schedule");
                    scheduleDialog.add(addButton);

                    addButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String roomNumber = roomField.getText();
                            String time = timeField.getText();
                            Schedule newSchedule = new Schedule(roomNumber, time);
                            subjectManager.addScheduleToSubject(selectedSubject.getSubjectId(), newSchedule);
                            JOptionPane.showMessageDialog(scheduleDialog, "Schedule added successfully!");
                            scheduleDialog.dispose();
                        }
                    });

                    scheduleDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a subject.");
                }
            }
        });

        // Action listener for deleting a schedule
        deleteScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Subject selectedSubject = subjectList.getSelectedValue();
                if (selectedSubject != null) {
                    // Show existing schedules in a dialog
                    JDialog deleteScheduleDialog = new JDialog(dialog, "Delete Schedule", true);
                    deleteScheduleDialog.setSize(300, 200);
                    deleteScheduleDialog.setLayout(new BorderLayout());

                    JList<Schedule> scheduleList = new JList<>(selectedSubject.getSchedules().toArray(new Schedule[0]));
                    scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    deleteScheduleDialog.add(new JScrollPane(scheduleList), BorderLayout.CENTER);

                    JButton deleteButton = new JButton("Delete Schedule");
                    deleteScheduleDialog.add(deleteButton, BorderLayout.SOUTH);

                    deleteButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Schedule selectedSchedule = scheduleList.getSelectedValue();
                            if (selectedSchedule != null) {
                                subjectManager.removeScheduleFromSubject(selectedSubject.getSubjectId(), selectedSchedule);
                                JOptionPane.showMessageDialog(deleteScheduleDialog, "Schedule deleted successfully!");
                                deleteScheduleDialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(deleteScheduleDialog, "Please select a schedule.");
                            }
                        }
                    });

                    deleteScheduleDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a subject.");
                }
            }
        });

        dialog.setVisible(true); // Show the dialog
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainInterface mainInterface = new MainInterface("Admin");
            mainInterface.setVisible(true);
        });
    }
}
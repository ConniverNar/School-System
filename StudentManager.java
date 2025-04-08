import java.util.ArrayList;
import java.util.List;

public class StudentManager {
    private List<Student> students;

    public StudentManager() {
        this.students = new ArrayList<>();
    }

    // Method to add a new student
    public void addStudent(String id, String name, int age, String gender, String department, String schoolYear) {
        Student newStudent = new Student(id, name, age, gender, department, schoolYear);
        students.add(newStudent);
        System.out.println("Student added: " + newStudent);
    }

    // Method to delete a student by ID
    public void deleteStudent(String id) {
        students.removeIf(student -> student.getId().equals(id));
        System.out.println("Student with ID " + id + " has been deleted.");
    }

    // Method to update student information
    public void updateStudent(String id, String newName, int newAge, String newGender, String newDepartment, String newSchoolYear) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                student.setName(newName);
                student.setAge(newAge);
                student.setGender(newGender);
                student.setDepartment(newDepartment);
                student.setSchoolYear(newSchoolYear);
                System.out.println("Student updated: " + student);
                return;
            }
        }
        System.out.println("Student with ID " + id + " not found.");
    }

    // Method to display all students
    public void displayStudents() {
        System.out.println("Current Students:");
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
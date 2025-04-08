import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubjectManager {
    private List<Subject> subjectRecords;
    private HashMap<String, List<String>> prerequisites; // Maps subject IDs to their prerequisites
    private HashMap<String, List<String>> studentEnrollments; // Maps student IDs to their enrolled subjects
    private HashMap<String, String> studentDepartments; // Maps student IDs to their departments

    public SubjectManager() {
        this.subjectRecords = new ArrayList<>();
        this.prerequisites = new HashMap<>();
        this.studentEnrollments = new HashMap<>();
        this.studentDepartments = new HashMap<>(); // Initialize the student departments map
    }

    // Method to add a new subject
    public void addSubject(String subjectId, String name, String department, int credits, List<String> prerequisiteList) {
        Subject newSubject = new Subject(subjectId, name, department, credits, prerequisiteList);
        subjectRecords.add(newSubject);
        prerequisites.put(subjectId, prerequisiteList); // Store prerequisites
        System.out.println("Subject record added: " + newSubject);
    }

    // Method to delete a subject by ID
    public void deleteSubject(String subjectId) {
        for (Subject subject : subjectRecords) {
            if (subject.getSubjectId().equals(subjectId)) {
                subjectRecords.remove(subject);
                prerequisites.remove(subjectId); // Remove prerequisites
                System.out.println("Subject record deleted: " + subject);
                return;
            }
        }
        System.out.println("Subject record for Subject ID " + subjectId + " not found.");
    }

    // Method to update an existing subject
    public void updateSubject(String subjectId, String newName, String newDepartment, int newCredits, List<String> newPrerequisiteList) {
        for (Subject subject : subjectRecords) {
            if (subject.getSubjectId().equals(subjectId)) {
                subjectRecords.remove(subject);
                Subject updatedSubject = new Subject(subjectId, newName, newDepartment, newCredits, newPrerequisiteList);
                subjectRecords.add(updatedSubject);
                prerequisites.put(subjectId, newPrerequisiteList); // Update prerequisites
                System.out.println("Subject record updated: " + updatedSubject);
                return;
            }
        }
        System.out.println("Subject record for Subject ID " + subjectId + " not found.");
    }

    // Method to find a subject by ID
    public Subject findSubjectById(String subjectId) {
        for (Subject subject : subjectRecords) {
            if (subject.getSubjectId().equals(subjectId)) {
                return subject;
            }
        }
        return null;
    }

    // Method to add a schedule to a subject
    public void addScheduleToSubject(String subjectId, Schedule schedule) {
        for (Subject subject : subjectRecords) {
            if (subject.getSubjectId().equals(subjectId)) {
                subject.addSchedule(schedule);
                System.out.println("Schedule added to subject " + subjectId);
                return;
            }
        }
        System.out.println("Subject ID " + subjectId + " not found.");
    }

    // Method to remove a schedule from a subject
    public void removeScheduleFromSubject(String subjectId, Schedule schedule) {
        for (Subject subject : subjectRecords) {
            if (subject.getSubjectId().equals(subjectId)) {
                subject.removeSchedule(schedule);
                System.out.println("Schedule removed from subject " + subjectId);
                return;
            }
        }
        System.out.println("Subject ID " + subjectId + " not found.");
    }

    // Method to enroll a student in a subject
    public boolean enrollStudent(String studentId, String studentDepartment, String subjectId) {
        if (!studentEnrollments.containsKey(studentId)) {
            studentEnrollments.put(studentId, new ArrayList<>());
        }

        // Check if the subject belongs to the student's department
        Subject subject = findSubjectById(subjectId);
        if (subject == null) {
            System.out.println("Enrollment failed: Subject ID " + subjectId + " not found.");
            return false; // Subject not found
        }

        if (!subject.getDepartment().equalsIgnoreCase(studentDepartment)) {
            System.out.println("Enrollment failed: Student " + studentId + " is not allowed to enroll in subject " + subjectId + " (Department mismatch).");
            return false; // Enrollment failed due to department mismatch
        }

        // Check prerequisites
        List<String> requiredPrerequisites = prerequisites.get(subjectId);
        if (requiredPrerequisites != null && !studentEnrollments.get(studentId).containsAll(requiredPrerequisites)) {
            System.out.println("Enrollment failed: Missing prerequisites for " + subjectId);
            return false; // Enrollment failed due to unmet prerequisites
        }

        studentEnrollments.get(studentId).add(subjectId);
        System.out.println("Successfully enrolled " + studentId + " in " + subjectId);
        return true; // Enrollment successful
    }

    // Method to get all subjects
    public List<Subject> getSubjectRecords() {
        return subjectRecords;
    }

    // Method to get enrolled subjects for a student
    public List<String> getStudentEnrollments(String studentId) {
        return studentEnrollments.getOrDefault(studentId, new ArrayList<>());
    }

    // Method to set a student's department
    public void setStudentDepartment(String studentId, String department) {
        studentDepartments.put(studentId, department);
    }

    // Method to get a student's department
    public String getStudentDepartment(String studentId) {
        return studentDepartments.get(studentId);
    }
}
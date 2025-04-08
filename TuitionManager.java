import java.util.ArrayList;
import java.util.List;

public class TuitionManager {
    private List<Tuition> tuitionRecords;

    public TuitionManager() {
        this.tuitionRecords = new ArrayList<>();
    }

    // Method to add a new tuition record
    public void addTuition(String studentId, double amount, String dueDate) {
        Tuition newTuition = new Tuition(studentId, amount, dueDate, false);
        tuitionRecords.add(newTuition);
        System.out.println("Tuition record added: " + newTuition);
    }

    // Method to update a tuition record
    public void updateTuition(String studentId, double newAmount, String newDueDate) {
        for (Tuition tuition : tuitionRecords) {
            if (tuition.getStudentId().equals(studentId)) {
                tuition.setPaid(false); // Reset paid status when updating
                tuition = new Tuition(studentId, newAmount, newDueDate, false);
                System.out.println("Tuition record updated: " + tuition);
                return;
            }
        }
        System.out.println("Tuition record for Student ID " + studentId + " not found.");
    }

    // Method to mark tuition as paid
    public void markAsPaid(String studentId) {
        for (Tuition tuition : tuitionRecords) {
            if (tuition.getStudentId().equals(studentId)) {
                tuition.setPaid(true);
                System.out.println("Tuition marked as paid for Student ID " + studentId);
                return;
            }
        }
        System.out.println("Tuition record for Student ID " + studentId + " not found.");
    }

    // Method to delete a tuition record
    public void deleteTuition(String studentId) {
        tuitionRecords.removeIf(tuition -> tuition.getStudentId().equals(studentId));
        System.out.println("Tuition record for Student ID " + studentId + " has been deleted.");
    }

    // Method to display all tuition records
    public void displayTuitionRecords() {
        System.out.println("Current Tuition Records:");
        for (Tuition tuition : tuitionRecords) {
            System.out.println(tuition);
        }
    }
}
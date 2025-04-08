import java.util.ArrayList;
import java.util.List;

public class SalaryManager {
    private List<Salary> salaryRecords;

    public SalaryManager() {
        this.salaryRecords = new ArrayList<>();
    }

    // Method to add a new salary record
    public void addSalary(String facultyId, double amount, String paymentDate) {
        Salary newSalary = new Salary(facultyId, amount, paymentDate, false);
        salaryRecords.add(newSalary);
        System.out.println("Salary record added: " + newSalary);
    }

    // Method to update a salary record
    public void updateSalary(String facultyId, double newAmount, String newPaymentDate) {
        for (Salary salary : salaryRecords) {
            if (salary.getFacultyId().equals(facultyId)) {
                salary.setPaid(false); // Reset paid status when updating
                salaryRecords.remove(salary);
                Salary updatedSalary = new Salary(facultyId, newAmount, newPaymentDate, false);
                salaryRecords.add(updatedSalary);
                System.out.println("Salary record updated: " + updatedSalary);
                return;
            }
        }
        System.out.println("Salary record for Faculty ID " + facultyId + " not found.");
    }

    // Method to mark salary as paid
    public void markAsPaid(String facultyId) {
        for (Salary salary : salaryRecords) {
            if (salary.getFacultyId().equals(facultyId)) {
                salary.setPaid(true);
                System.out.println("Salary marked as paid for Faculty ID " + facultyId);
                return;
            }
        }
        System.out.println("Salary record for Faculty ID " + facultyId + " not found.");
    }

    // Method to delete a salary record
    public void deleteSalary(String facultyId) {
        salaryRecords.removeIf(salary -> salary.getFacultyId().equals(facultyId));
        System.out.println("Salary record for Faculty ID " + facultyId + " has been deleted.");
    }

    // Method to display all salary records
    public void displaySalaryRecords() {
        System.out.println("Current Salary Records:");
        for (Salary salary : salaryRecords) {
            System.out.println(salary);
        }
    }
}
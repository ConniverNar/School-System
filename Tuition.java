public class Tuition {
    private String studentId;
    private double amount;
    private String dueDate;
    private boolean isPaid;

    public Tuition(String studentId, double amount, String dueDate, boolean isPaid) {
        this.studentId = studentId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    @Override
    public String toString() {
        return "Student ID: " + studentId + ", Amount: " + amount + ", Due Date: " + dueDate + ", Paid: " + (isPaid ? "Yes" : "No");
    }
}
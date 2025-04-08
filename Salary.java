public class Salary {
    private String facultyId;
    private double amount;
    private String paymentDate;
    private boolean isPaid;

    public Salary(String facultyId, double amount, String paymentDate, boolean isPaid) {
        this.facultyId = facultyId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.isPaid = isPaid;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    @Override
    public String toString() {
        return "Faculty ID: " + facultyId + ", Amount: " + amount + ", Payment Date: " + paymentDate + ", Paid: " + (isPaid ? "Yes" : "No");
    }
}
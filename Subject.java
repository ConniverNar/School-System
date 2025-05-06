import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String department;
    private double tuition;
    private double salary;
    private int units;
    private List<String> prerequisites;
    private List<String> enrolledStudents;
    private String assignedFaculty; // Kept for backwards compatibility, but no longer primary assignment method
    private List<Schedule> schedules;
    
    public Subject(String id, String name, String department, double tuition, double salary, int units) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.tuition = tuition;
        this.salary = salary;
        this.units = units;
        this.prerequisites = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
        this.assignedFaculty = "";
        this.schedules = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public double getTuition() {
        return tuition;
    }
    
    public void setTuition(double tuition) {
        this.tuition = tuition;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public int getUnits() {
        return units;
    }
    
    public void setUnits(int units) {
        this.units = units;
    }
    
    public List<String> getPrerequisites() {
        return prerequisites;
    }
    
    public void addPrerequisite(String prerequisiteId) {
        if (!prerequisites.contains(prerequisiteId)) {
            prerequisites.add(prerequisiteId);
        }
    }
    
    public void removePrerequisite(String prerequisiteId) {
        prerequisites.remove(prerequisiteId);
    }
    
    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }
    
    public void enrollStudent(String studentUsername) {
        if (!enrolledStudents.contains(studentUsername)) {
            enrolledStudents.add(studentUsername);
        }
    }
    
    public void unenrollStudent(String studentUsername) {
        enrolledStudents.remove(studentUsername);
    }
    
    /**
     * @deprecated This method is kept for backwards compatibility.
     * Use FacultyScheduleAssignment system instead.
     */
    @Deprecated
    public String getAssignedFaculty() {
        return assignedFaculty;
    }
    
    /**
     * @deprecated This method is kept for backwards compatibility.
     * Use FacultyScheduleAssignment system instead.
     */
    @Deprecated
    public void setAssignedFaculty(String facultyUsername) {
        this.assignedFaculty = facultyUsername;
    }
    
    public List<Schedule> getSchedules() {
        return schedules;
    }
    
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
    
    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }
    
    @Override
    public String toString() {
        return id + " - " + name;
    }
}
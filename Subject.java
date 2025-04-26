import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String id;
    private String name;
    private String department;
    private double tuition;
    private double salary;
    private List<String> prerequisites;
    private int units;
    private List<Schedule> schedules;
    private List<String> enrolledStudents;
    private String assignedFaculty;
    
    public Subject(String id, String name, String department, double tuition, double salary, int units) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.tuition = tuition;
        this.salary = salary;
        this.units = units;
        this.prerequisites = new ArrayList<>();
        this.schedules = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
        this.assignedFaculty = "";
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDepartment() {
        return department;
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
    
    public List<String> getPrerequisites() {
        return prerequisites;
    }
    
    public void addPrerequisite(String prerequisite) {
        prerequisites.add(prerequisite);
    }
    
    public int getUnits() {
        return units;
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
    
    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }
    
    public void enrollStudent(String username) {
        if (!enrolledStudents.contains(username)) {
            enrolledStudents.add(username);
        }
    }
    
    public void unenrollStudent(String username) {
        enrolledStudents.remove(username);
    }
    
    public String getAssignedFaculty() {
        return assignedFaculty;
    }
    
    public void setAssignedFaculty(String assignedFaculty) {
        this.assignedFaculty = assignedFaculty;
    }
    
    public void removeAssignedFaculty() {
        this.assignedFaculty = "";
    }
    
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String subjectId;
    private String name;
    private String department;
    private int credits;
    private List<String> prerequisites; // Change from String to List<String>
    private List<Schedule> schedules;

    // Constructor
    public Subject(String subjectId, String name, String department, int credits, List<String> prerequisites) {
        this.subjectId = subjectId;
        this.name = name;
        this.department = department;
        this.credits = credits;
        this.prerequisites = prerequisites; // Initialize the prerequisites list
        this.schedules = new ArrayList<>(); 
    }
 

    // Getters
    public String getSubjectId() {
        return subjectId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getCredits() {
        return credits;
    }

    public List<String> getPrerequisites() {
        return prerequisites; // Return the list of prerequisites
    }
    
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }
    
    public List<Schedule> getSchedules() {
        return schedules;
    }
    // toString method for displaying subject information
    @Override
    public String toString() {
        return "ID: " + subjectId + ", Name: " + name + ", Department: " + department + ", Credits: " + credits + ", Prerequisites: " + prerequisites;
    }
}
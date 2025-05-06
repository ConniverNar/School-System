import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Integer> studentScheduleChoices;

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
        this.studentScheduleChoices = new HashMap<>();
    }
    
    // Getters and setters
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
    
    public List<String> getPrerequisites() {
        return prerequisites;
    }
    
    public void addPrerequisite(String subjectId) {
        prerequisites.add(subjectId);
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
            if (!schedules.isEmpty()) {
                studentScheduleChoices.put(username, 0); // Default to first schedule
            }
        }
    }
    
    public void enrollStudent(String username, int scheduleIndex) {
        if (!enrolledStudents.contains(username) && scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
            enrolledStudents.add(username);
            studentScheduleChoices.put(username, scheduleIndex);
        } else if (enrolledStudents.contains(username)) {
            // Update schedule choice if already enrolled
            if (scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
                studentScheduleChoices.put(username, scheduleIndex);
            }
        }
    }
    
    public void unenrollStudent(String username) {
        enrolledStudents.remove(username);
        studentScheduleChoices.remove(username);
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
    
    public Schedule getStudentSchedule(String username) {
        Integer scheduleIndex = studentScheduleChoices.get(username);
        if (scheduleIndex != null && scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
            return schedules.get(scheduleIndex);
        }
        return null;
    }
    
    public boolean canEnrollInSchedule(String username, int scheduleIndex, DatabaseManager dbManager) {
        // Check schedule index bounds
        if (scheduleIndex < 0 || scheduleIndex >= schedules.size()) {
            return false;
        }
        
        Schedule scheduleToCheck = schedules.get(scheduleIndex);
        List<Subject> enrolledSubjects = dbManager.getEnrolledSubjects(username);
        
        // Check against all schedules in enrolled subjects
        for (Subject enrolledSubject : enrolledSubjects) {
            // Skip checking against self if already enrolled and just changing schedule
            if (enrolledSubject.getId().equals(this.id)) {
                continue;
            }
            
            Schedule enrolledSchedule = enrolledSubject.getStudentSchedule(username);
            if (enrolledSchedule != null && enrolledSchedule.conflictsWith(scheduleToCheck)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}

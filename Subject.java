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
    private Map<String, List<Integer>> studentScheduleChoices;

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
                List<Integer> scheduleIndices = new ArrayList<>();
                scheduleIndices.add(0); // Default to first schedule
                studentScheduleChoices.put(username, scheduleIndices);
            }
        }
    }
    
    public void enrollStudent(String username, int scheduleIndex) {
        if (!enrolledStudents.contains(username) && scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
            enrolledStudents.add(username);
            List<Integer> scheduleIndices = new ArrayList<>();
            scheduleIndices.add(scheduleIndex);
            studentScheduleChoices.put(username, scheduleIndices);
        } else if (enrolledStudents.contains(username)) {
            // Add schedule choice if not already present
            if (scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
                List<Integer> scheduleIndices = studentScheduleChoices.get(username);
                if (scheduleIndices == null) {
                    scheduleIndices = new ArrayList<>();
                }
                if (!scheduleIndices.contains(scheduleIndex)) {
                    scheduleIndices.add(scheduleIndex);
                    studentScheduleChoices.put(username, scheduleIndices);
                }
            }
        }
    }
    
    public void unenrollStudent(String username) {
        enrolledStudents.remove(username);
        studentScheduleChoices.remove(username);
    }
    
    public List<Schedule> getStudentSchedules(String username) {
        List<Schedule> result = new ArrayList<>();
        List<Integer> scheduleIndices = studentScheduleChoices.get(username);
        if (scheduleIndices != null) {
            for (Integer index : scheduleIndices) {
                if (index >= 0 && index < schedules.size()) {
                    result.add(schedules.get(index));
                }
            }
        }
        return result;
    }
    
    public Schedule getStudentSchedule(String username) {
        List<Integer> scheduleIndices = studentScheduleChoices.get(username);
        if (scheduleIndices != null && !scheduleIndices.isEmpty()) {
            Integer scheduleIndex = scheduleIndices.get(0);
            if (scheduleIndex >= 0 && scheduleIndex < schedules.size()) {
                return schedules.get(scheduleIndex);
            }
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
            
            List<Schedule> enrolledSchedules = enrolledSubject.getStudentSchedules(username);
            for (Schedule enrolledSchedule : enrolledSchedules) {
                if (enrolledSchedule != null && enrolledSchedule.conflictsWith(scheduleToCheck)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public String getAssignedFaculty() {
        return assignedFaculty;
    }
    
    public void setAssignedFaculty(String assignedFaculty) {
        this.assignedFaculty = assignedFaculty;
    }

    public void unenrollStudentSchedule(String username, int scheduleIndex) {
        List<Integer> scheduleIndices = studentScheduleChoices.get(username);
        if (scheduleIndices != null) {
            scheduleIndices.remove(Integer.valueOf(scheduleIndex));
            if (scheduleIndices.isEmpty()) {
                enrolledStudents.remove(username);
                studentScheduleChoices.remove(username);
            } else {
                studentScheduleChoices.put(username, scheduleIndices);
            }
        }
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}

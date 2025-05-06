import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseManager {
    private Map<String, User> users;
    private Map<String, Subject> subjects;
    // Map to track schedule assignments: key = subjectId_scheduleIndex, value = faculty username
    private Map<String, String> scheduleAssignments;

    // New list to track FacultyScheduleAssignment objects
    private List<FacultyScheduleAssignment> facultyScheduleAssignments;

    public DatabaseManager() {
        users = new HashMap<>();
        subjects = new HashMap<>();
        scheduleAssignments = new HashMap<>();
        facultyScheduleAssignments = new ArrayList<>();
    }

    // Initialize the database with admin account
    public void initializeDatabase() {
        // Create admin account
        User admin = new User("admin111", "admin123", User.UserType.ADMIN);
        admin.setUserInfo("name", "System Administrator");
        admin.setUserInfo("gender", "Not Specified");
        admin.setUserInfo("age", "Not Specified");
        admin.setUserInfo("department", "Administration");

        users.put(admin.getUsername(), admin);
    }
    
    // New methods to manage FacultyScheduleAssignment

    public List<FacultyScheduleAssignment> getFacultyScheduleAssignmentsBySubject(String subjectId) {
        List<FacultyScheduleAssignment> result = new ArrayList<>();
        for (FacultyScheduleAssignment assignment : facultyScheduleAssignments) {
            if (assignment.getSubjectId().equals(subjectId)) {
                result.add(assignment);
            }
        }
        return result;
    }

    public List<FacultyScheduleAssignment> getFacultyScheduleAssignmentsByFaculty(String facultyUsername) {
        List<FacultyScheduleAssignment> result = new ArrayList<>();
        for (FacultyScheduleAssignment assignment : facultyScheduleAssignments) {
            if (assignment.getFacultyUsername().equals(facultyUsername)) {
                result.add(assignment);
            }
        }
        return result;
    }

    public void addFacultyScheduleAssignment(FacultyScheduleAssignment assignment) {
        if (!facultyScheduleAssignments.contains(assignment)) {
            facultyScheduleAssignments.add(assignment);
            // Also update the scheduleAssignments map for backward compatibility
            String key = assignment.getSubjectId() + "_" + assignment.getSchedule().getScheduleIndex();
            scheduleAssignments.put(key, assignment.getFacultyUsername());
        }
    }

    public void removeFacultyScheduleAssignment(String subjectId, String facultyUsername, Schedule schedule) {
        FacultyScheduleAssignment toRemove = null;
        for (FacultyScheduleAssignment assignment : facultyScheduleAssignments) {
            if (assignment.getSubjectId().equals(subjectId) &&
                assignment.getFacultyUsername().equals(facultyUsername) &&
                assignment.getSchedule().equals(schedule)) {
                toRemove = assignment;
                break;
            }
        }
        if (toRemove != null) {
            facultyScheduleAssignments.remove(toRemove);
            String key = subjectId + "_" + schedule.getScheduleIndex();
            scheduleAssignments.remove(key);
        }
    }

    // Authenticate a user based on credentials
    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean canTakeSimultaneously(String subjectId, List<String> enrollingSubjects) {
        Subject subject = subjects.get(subjectId);
        if (subject == null) {
            return false;
        }

        // Check if all prerequisites are either already enrolled or being enrolled now
        for (String prereqId : subject.getPrerequisites()) {
            Subject prereq = subjects.get(prereqId);
            if (prereq == null) {
                continue;
            }

            // If this prerequisite is not in the currently enrolling list, 
            // the student must already be enrolled in it
            if (!enrollingSubjects.contains(prereqId)) {
                boolean anyStudentEnrolled = false;
                for (String studentId : prereq.getEnrolledStudents()) {
                    if (users.containsKey(studentId)) {
                        anyStudentEnrolled = true;
                        break;
                    }
                }
                if (!anyStudentEnrolled) {
                    return false;
                }
            }
        }

        return true;
    }

    public List<String> getOptimalEnrollmentOrder(List<String> selectedSubjects) {
        List<String> result = new ArrayList<>();
        List<String> remaining = new ArrayList<>(selectedSubjects);

        // Keep adding subjects that can be taken with what's already in the result
        while (!remaining.isEmpty()) {
            boolean added = false;

            for (int i = 0; i < remaining.size(); i++) {
                String subjectId = remaining.get(i);
                Subject subject = subjects.get(subjectId);

                if (subject == null) {
                    remaining.remove(i--);
                    continue;
                }

                // Check if all prerequisites are either in the result or remaining
                boolean allPrereqsCovered = true;
                for (String prereqId : subject.getPrerequisites()) {
                    if (!result.contains(prereqId) && !remaining.contains(prereqId)) {
                        allPrereqsCovered = false;
                        break;
                    }
                }

                if (allPrereqsCovered) {
                    result.add(subjectId);
                    remaining.remove(i);
                    added = true;
                    break;
                }
            }

            // If we couldn't add any more subjects, there might be a cycle
            if (!added) {
                // Just add the first remaining subject and hope for the best
                result.add(remaining.get(0));
                remaining.remove(0);
            }
        }

        return result;
    }

    // User management methods
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void removeUser(String username) {
        users.remove(username);

        // Remove user from enrolled subjects
        for (Subject subject : subjects.values()) {
            subject.unenrollStudent(username);
            if (subject.getAssignedFaculty().equals(username)) {
                subject.removeAssignedFaculty();
            }
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<User> getUsersByType(User.UserType type) {
        return users.values().stream()
                .filter(user -> user.getUserType() == type)
                .collect(Collectors.toList());
    }

    // Subject management methods
    public void addSubject(Subject subject) {
        subjects.put(subject.getId(), subject);
    }

    public void removeSubject(String id) {
        subjects.remove(id);
    }

    public Subject getSubject(String id) {
        return subjects.get(id);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects.values());
    }

    public List<Subject> getSubjectsByDepartment(String department) {
        return subjects.values().stream()
                .filter(subject -> subject.getDepartment().equals(department))
                .collect(Collectors.toList());
    }

    public List<Subject> getEnrolledSubjects(String username) {
        return subjects.values().stream()
                .filter(subject -> subject.getEnrolledStudents().contains(username))
                .collect(Collectors.toList());
    }

    public List<Subject> getAssignedSubjects(String facultyUsername) {
        List<Subject> assignedSubjects = new ArrayList<>();
        List<Subject> allSubjects = getAllSubjects();
        
        for (Subject subject : allSubjects) {
            if (facultyUsername.equals(subject.getAssignedFaculty())) {
                assignedSubjects.add(subject);
            }
        }
        
        return assignedSubjects;
    }

    // Check if a student has all prerequisites for a subject
    public boolean hasPrerequisites(String username, Subject subject) {
        User user = users.get(username);
        if (user == null || user.getUserType() != User.UserType.STUDENT) {
            return false;
        }

        for (String prerequisiteId : subject.getPrerequisites()) {
            Subject prerequisite = subjects.get(prerequisiteId);
            if (prerequisite != null && !prerequisite.getEnrolledStudents().contains(username)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasScheduleConflict(String username, Subject newSubject, int scheduleIndex) {
        if (scheduleIndex < 0 || scheduleIndex >= newSubject.getSchedules().size()) {
            return true; // Invalid schedule index is treated as a conflict
        }

        Schedule newSchedule = newSubject.getSchedules().get(scheduleIndex);
        List<Subject> enrolledSubjects = getEnrolledSubjects(username);

        for (Subject enrolledSubject : enrolledSubjects) {
            // Skip checking against the same subject (for when editing schedule choice)
            if (enrolledSubject.getId().equals(newSubject.getId())) {
                continue;
            }

            Schedule enrolledSchedule = enrolledSubject.getStudentSchedule(username);
            if (enrolledSchedule != null && enrolledSchedule.conflictsWith(newSchedule)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasOrIsEnrollingInPrerequisites(String username, Subject subject, List<String> currentlyEnrolling) {
        User user = users.get(username);
        if (user == null || user.getUserType() != User.UserType.STUDENT) {
            return false;
        }

        for (String prerequisiteId : subject.getPrerequisites()) {
            Subject prerequisite = subjects.get(prerequisiteId);
            // Skip if this prerequisite is one we're currently trying to enroll in
            if (currentlyEnrolling.contains(prerequisiteId)) {
                continue;
            }

            // Check if student is already enrolled in this prerequisite
            if (prerequisite != null && !prerequisite.getEnrolledStudents().contains(username)) {
                return false;
            }
        }

        return true;
    }

    // New methods to support FacultyInterface

    public String getScheduleAssignment(String subjectId, int scheduleIndex) {
        String key = subjectId + "_" + scheduleIndex;
        return scheduleAssignments.get(key);
    }

    public void removeScheduleAssignment(String subjectId, int scheduleIndex) {
        String key = subjectId + "_" + scheduleIndex;
        scheduleAssignments.remove(key);
    }

    public void assignScheduleToFaculty(String subjectId, int scheduleIndex, String facultyUsername) {
        String key = subjectId + "_" + scheduleIndex;
        scheduleAssignments.put(key, facultyUsername);
    }

    public String getFacultyName(String username) {
        User user = users.get(username);
        if (user != null) {
            return user.getUserInfo("name");
        }
        return null;
    }
}

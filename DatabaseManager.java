import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseManager {
    private Map<String, User> users;
    private Map<String, Subject> subjects;
    
    public DatabaseManager() {
        users = new HashMap<>();
        subjects = new HashMap<>();
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
    
    // Authenticate a user based on credentials
    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
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
    
    public List<Subject> getAssignedSubjects(String username) {
        return subjects.values().stream()
                .filter(subject -> subject.getAssignedFaculty().equals(username))
                .collect(Collectors.toList());
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
}
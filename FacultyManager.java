import java.util.ArrayList;
import java.util.List;

public class FacultyManager {
    private List<Faculty> facultyList;

    public FacultyManager() {
        this.facultyList = new ArrayList<>();
    }

    // Method to create a new faculty account
    public void createFaculty(String username, String password) {
        if (getFaculty(username) != null) {
            System.out.println("Faculty with this username already exists.");
            return;
        }
        Faculty newFaculty = new Faculty(username, password);
        facultyList.add(newFaculty);
        System.out.println("Faculty account created: " + newFaculty);
    }

    // Method to delete a faculty account by username
    public void deleteFaculty(String username) {
        facultyList.removeIf(faculty -> faculty.getUsername().equals(username));
        System.out.println("Faculty account with username " + username + " has been deleted.");
    }

    // Method to authenticate a faculty account
    public boolean authenticate(String username, String password) {
        Faculty faculty = getFaculty(username);
        if (faculty != null && faculty.getPassword().equals(password)) {
            System.out.println("Authentication successful for " + username);
            return true;
        }
        System.out.println("Authentication failed for " + username);
        return false;
    }

    // Method to retrieve a faculty account by username
    private Faculty getFaculty(String username) {
        for (Faculty faculty : facultyList) {
            if (faculty.getUsername().equals(username)) {
                return faculty;
            }
        }
        return null;
    }

    // Method to display all faculty accounts
    public void displayFaculties() {
        System.out.println("Current Faculty Accounts:");
        for (Faculty faculty : facultyList) {
            System.out.println(faculty);
        }
    }
}
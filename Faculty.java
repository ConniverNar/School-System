public class Faculty {
    private String username;
    private String password;

    public Faculty(String username, String password) {
        this.username = username;
        this.password = password; // In a real application, consider hashing the password
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // In a real application, consider hashing the password
    }

    @Override
    public String toString() {
        return "Username: " + username;
    }
}
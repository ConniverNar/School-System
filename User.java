import java.util.HashMap;
import java.util.Map;

public class User {
    public enum UserType {
        ADMIN, FACULTY, STUDENT
    }
    
    private String username;
    private String password;
    private UserType userType;
    private Map<String, String> userInfo;
    
    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.userInfo = new HashMap<>();
    }
    
    // Getters and setters
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
        this.password = password;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public Map<String, String> getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(String key, String value) {
        userInfo.put(key, value);
    }
    
    public String getUserInfo(String key) {
        return userInfo.getOrDefault(key, "");
    }
}
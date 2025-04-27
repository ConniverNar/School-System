public class Schedule {
    private String subjectId;
    private String roomNumber;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    
    public Schedule(String subjectId, String roomNumber, String dayOfWeek, String startTime, String endTime) {
        this.subjectId = subjectId;
        this.roomNumber = roomNumber;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and setters
    public String getSubjectId() {
        return subjectId;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + "-" + endTime + " (Room: " + roomNumber + ")";
    }

    public boolean conflictsWith(Schedule other) {
        // If not on the same day, no conflict
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        
        // Parse times as integers for comparison (HH:mm format)
        int thisStart = parseTimeToMinutes(this.startTime);
        int thisEnd = parseTimeToMinutes(this.endTime);
        int otherStart = parseTimeToMinutes(other.startTime);
        int otherEnd = parseTimeToMinutes(other.endTime);
        
        // Check if time periods overlap, ignoring room numbers
        return !(thisEnd <= otherStart || otherEnd <= thisStart);
    }
    
    private int parseTimeToMinutes(String time) {
        try {
            // Handle AM/PM format
            boolean isPM = time.toUpperCase().contains("PM") && !time.toUpperCase().contains("12:");
            boolean isAM12 = time.toUpperCase().contains("12:") && time.toUpperCase().contains("AM");
            
            // Remove AM/PM suffix
            time = time.replaceAll("(?i)\\s*[AP]M", "");
            
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1].trim());
            
            // Convert to 24-hour format
            if (isPM) {
                hours += 12;
            }
            if (isAM12) {
                hours = 0; // 12 AM is 0 in 24-hour format
            }
            
            return hours * 60 + minutes;
        } catch (Exception e) {
            System.out.println("Error parsing time: " + time + ", " + e.getMessage());
            return -1; // Invalid time format
        }
    }
}

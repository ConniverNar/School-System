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
}

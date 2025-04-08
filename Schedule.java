public class Schedule {
    private String roomNumber;
    private String time;

    public Schedule(String roomNumber, String time) {
        this.roomNumber = roomNumber;
        this.time = time;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Room: " + roomNumber + ", Time: " + time;
    }
}
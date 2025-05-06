public class FacultyScheduleAssignment {
    private String subjectId;
    private String facultyUsername;
    private Schedule schedule;

    public FacultyScheduleAssignment(String subjectId, String facultyUsername, Schedule schedule) {
        this.subjectId = subjectId;
        this.facultyUsername = facultyUsername;
        this.schedule = schedule;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getFacultyUsername() {
        return facultyUsername;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FacultyScheduleAssignment other = (FacultyScheduleAssignment) obj;
        return subjectId.equals(other.subjectId) &&
               facultyUsername.equals(other.facultyUsername) &&
               schedule.equals(other.schedule);
    }

    @Override
    public int hashCode() {
        return subjectId.hashCode() + facultyUsername.hashCode() + schedule.hashCode();
    }
}

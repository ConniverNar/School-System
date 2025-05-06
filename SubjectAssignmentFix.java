import java.util.List;

public class SubjectAssignmentFix {
    private DatabaseManager dbManager;
    
    public SubjectAssignmentFix(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public void verifyFacultySubjectAssignments() {
    
        List<Subject> allSubjects = dbManager.getAllSubjects();
        
        for (Subject subject : allSubjects) {
            String assignedFaculty = subject.getAssignedFaculty();
            
            // If we have a legacy assignedFaculty field set but no schedule assignments
            if (assignedFaculty != null && !assignedFaculty.isEmpty()) {
                List<FacultyScheduleAssignment> existingAssignments = 
                    dbManager.getFacultyScheduleAssignmentsBySubject(subject.getId());
                
                if (existingAssignments.isEmpty()) {
                    // Convert the legacy assignment to the new system
                    // by creating a schedule assignment for each schedule
                    for (Schedule schedule : subject.getSchedules()) {
                        FacultyScheduleAssignment newAssignment = new FacultyScheduleAssignment(
                            subject.getId(), assignedFaculty, schedule);
                        dbManager.addFacultyScheduleAssignment(newAssignment);
                    }
                    
                    // Clear the legacy assignedFaculty field
                    subject.setAssignedFaculty("");
                }
            }
        }
    }
}

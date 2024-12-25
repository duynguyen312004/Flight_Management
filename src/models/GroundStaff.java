package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GroundStaff extends Employee {
    private String assignedGate;
    private LocalDateTime assignmentDate;

    // Constructor cập nhật
    public GroundStaff(String id, String name, String address, String role, String assignedGate, LocalDateTime assignmentDate) {
        super(id, name, address, role);
        this.assignedGate = assignedGate;
        this.assignmentDate = assignmentDate;
    }

    // Getter và setter cho assignedGate
    public String getAssignedGate() {
        return assignedGate;
    }

    public void setAssignedGate(String assignedGate) {
        this.assignedGate = assignedGate;
    }

    // Getter và setter cho assignmentDate
    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    // Phương thức getDetails cập nhật
    @Override
    public String getDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = (assignmentDate != null) ? assignmentDate.format(formatter) : "N/A";
        return super.getDetails() + ", Assigned Gate=" + assignedGate + ", Assignment Date=" + formattedDate;
    }
}

package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GroundStaff extends Employee {
    private final StringProperty assignedGate;
    private final StringProperty assignmentDate;

    // Constructor
    public GroundStaff(String id, String name, String address, String role, String assignedGate, LocalDateTime assignmentDate) {
        super(id, name, address, role);
        this.assignedGate = new SimpleStringProperty(assignedGate);
        this.assignmentDate = new SimpleStringProperty(
                assignmentDate != null ? assignmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"
        );
    }

    // Property Getters
    public StringProperty assignedGateProperty() {
        return assignedGate;
    }

    public StringProperty assignmentDateProperty() {
        return assignmentDate;
    }

    // String Getters
    public String getAssignedGate() {
        return assignedGate.get();
    }

    public String getAssignmentDate() {
        return assignmentDate.get();
    }

    // Setters
    public void setAssignedGate(String assignedGate) {
        this.assignedGate.set(assignedGate);
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate.set(
                assignmentDate != null ? assignmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"
        );
    }

    @Override
    public String getDetails() {
        return super.getDetails() + ", Assigned Gate=" + getAssignedGate() + ", Assignment Date=" + getAssignmentDate();
    }
}

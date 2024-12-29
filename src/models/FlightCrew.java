package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightCrew extends Employee {
    private final StringProperty crewRole;
    private final StringProperty flightNumber;
    private final StringProperty assignmentDate;

    // Constructor
    public FlightCrew(String id, String name, String address, String role, String crewRole, String flightNumber, LocalDateTime assignmentDate) {
        super(id, name, address, role);
        this.crewRole = new SimpleStringProperty(crewRole);
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.assignmentDate = new SimpleStringProperty(
                assignmentDate != null ? assignmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
    }

    // Properties
    public StringProperty crewRoleProperty() {
        return crewRole;
    }

    public StringProperty flightNumberProperty() {
        return flightNumber;
    }

    public StringProperty assignmentDateProperty() {
        return assignmentDate;
    }

    // Getters
    public String getCrewRole() {
        return crewRole.get();
    }

    public String getFlightNumber() {
        return flightNumber.get();
    }

    public String getAssignmentDate() {
        return assignmentDate.get();
    }

    // Setters
    public void setCrewRole(String crewRole) {
        this.crewRole.set(crewRole);
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber.set(flightNumber);
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate.set(assignmentDate != null
                ? assignmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "N/A");
    }

    @Override
    public String getDetails() {
        return super.getDetails() + ", Crew Role=" + getCrewRole() + ", Flight Number=" + getFlightNumber() + ", Assignment Date=" + getAssignmentDate();
    }
}

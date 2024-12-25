package models;

import java.time.LocalDateTime;

public class FlightCrew extends Employee {
    private String crewRole;
    private String flightNumber;
    private LocalDateTime assignmentDate;

    // Constructor
    public FlightCrew(String id, String name, String address, String role, String crewRole, String flightNumber, LocalDateTime assignmentDate) {
        super(id, name, address, role);
        this.crewRole = crewRole;
        this.flightNumber = flightNumber;
        this.assignmentDate = assignmentDate;
    }

    // Getters and Setters
    public String getCrewRole() {
        return crewRole;
    }

    public void setCrewRole(String crewRole) {
        this.crewRole = crewRole;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + ", Crew Role=" + crewRole + ", Flight Number=" + flightNumber + ", Assignment Date=" + assignmentDate;
    }
}

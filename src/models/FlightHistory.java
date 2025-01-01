package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlightHistory {
    private final StringProperty flightNumber;
    private final StringProperty departureLocation;
    private final StringProperty arrivalLocation;
    private final StringProperty departureTime;
    private final StringProperty arrivalTime;
    private final StringProperty status;
    private final StringProperty airplaneId;
    private final StringProperty assignedGate;
    private final StringProperty tickets; // Lưu JSON của vé
    private final StringProperty crew; // Lưu JSON của phi hành đoàn
    private final StringProperty groundStaff; // Lưu JSON của nhân viên mặt đất

    public FlightHistory(String flightNumber, String departureLocation, String arrivalLocation,
            String departureTime, String arrivalTime, String status,
            String airplaneId, String assignedGate,
            String tickets, String crew, String groundStaff) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.departureLocation = new SimpleStringProperty(departureLocation);
        this.arrivalLocation = new SimpleStringProperty(arrivalLocation);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
        this.status = new SimpleStringProperty(status);
        this.airplaneId = new SimpleStringProperty(airplaneId);
        this.assignedGate = new SimpleStringProperty(assignedGate);
        this.tickets = new SimpleStringProperty(tickets);
        this.crew = new SimpleStringProperty(crew);
        this.groundStaff = new SimpleStringProperty(groundStaff);
    }

    // Getters
    public String getFlightNumber() {
        return flightNumber.get();
    }

    public String getDepartureLocation() {
        return departureLocation.get();
    }

    public String getArrivalLocation() {
        return arrivalLocation.get();
    }

    public String getDepartureTime() {
        return departureTime.get();
    }

    public String getArrivalTime() {
        return arrivalTime.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getAirplaneId() {
        return airplaneId.get();
    }

    public String getAssignedGate() {
        return assignedGate.get();
    }

    public String getTickets() {
        return tickets.get();
    }

    public String getCrew() {
        return crew.get();
    }

    public String getGroundStaff() {
        return groundStaff.get();
    }

    // Property Getters for TableView binding
    public StringProperty flightNumberProperty() {
        return flightNumber;
    }

    public StringProperty departureLocationProperty() {
        return departureLocation;
    }

    public StringProperty arrivalLocationProperty() {
        return arrivalLocation;
    }

    public StringProperty departureTimeProperty() {
        return departureTime;
    }

    public StringProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty airplaneIdProperty() {
        return airplaneId;
    }

    public StringProperty assignedGateProperty() {
        return assignedGate;
    }

    public StringProperty ticketsProperty() {
        return tickets;
    }

    public StringProperty crewProperty() {
        return crew;
    }

    public StringProperty groundStaffProperty() {
        return groundStaff;
    }
}

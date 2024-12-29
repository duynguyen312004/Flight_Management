package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Flight {
    private final StringProperty flightNumber;
    private final StringProperty departureLocation;
    private final StringProperty arrivalLocation;
    private final StringProperty departureTime;
    private final StringProperty arrivalTime;
    private final StringProperty status;
    private final StringProperty assignedGate; // Đổi tên từ "gate" thành "assignedGate"
    private final StringProperty airplaneId;

    public Flight(String flightNumber, String departureLocation, String arrivalLocation, String departureTime,
                  String arrivalTime, String status, String assignedGate, String airplaneId) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.departureLocation = new SimpleStringProperty(departureLocation);
        this.arrivalLocation = new SimpleStringProperty(arrivalLocation);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
        this.status = new SimpleStringProperty(status);
        this.assignedGate = new SimpleStringProperty(assignedGate);
        this.airplaneId = new SimpleStringProperty(airplaneId);
    }

    // Property Getters
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

    public StringProperty assignedGateProperty() {
        return assignedGate;
    }

    public StringProperty airplaneIdProperty() {
        return airplaneId;
    }

    // String Getters
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

    public String getAssignedGate() {
        return assignedGate.get();
    }

    public String getAirplaneId() {
        return airplaneId.get();
    }

    // String Setters
    public void setFlightNumber(String flightNumber) {
        this.flightNumber.set(flightNumber);
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation.set(departureLocation);
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation.set(arrivalLocation);
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime.set(departureTime);
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setAssignedGate(String assignedGate) {
        this.assignedGate.set(assignedGate);
    }

    public void setAirplaneId(String airplaneId) {
        this.airplaneId.set(airplaneId);
    }

    @Override
    public String toString() {
        return String.format(
                "Flight [flightNumber=%s, departureLocation=%s, arrivalLocation=%s, departureTime=%s, arrivalTime=%s, status=%s, assignedGate=%s, airplaneId=%s]",
                getFlightNumber(), getDepartureLocation(), getArrivalLocation(), getDepartureTime(), getArrivalTime(),
                getStatus(), getAssignedGate(), getAirplaneId());
    }
}

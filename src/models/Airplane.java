package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Airplane {
    private final StringProperty airplaneId;
    private final StringProperty seatCapacity;
    private final StringProperty status;

    public Airplane(String airplaneId, int seatCapacity) {
        this(airplaneId, seatCapacity, "Available"); // Default status
    }

    public Airplane(String airplaneId, int seatCapacity, String status) {
        this.airplaneId = new SimpleStringProperty(airplaneId);
        this.seatCapacity = new SimpleStringProperty(String.valueOf(seatCapacity));
        this.status = new SimpleStringProperty(status);
    }

    // Getters for StringProperty
    public StringProperty airplaneIdProperty() {
        return airplaneId;
    }

    public StringProperty seatCapacityProperty() {
        return seatCapacity;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Getters for values
    public String getAirplaneId() {
        return airplaneId.get();
    }

    public int getSeatCapacity() {
        return Integer.parseInt(seatCapacity.get());
    }

    public String getStatus() {
        return status.get();
    }

    // Setters
    public void setAirplaneId(String airplaneId) {
        this.airplaneId.set(airplaneId);
    }

    public void setSeatCapacity(int seatCapacity) {
        if (seatCapacity < 0) {
            throw new IllegalArgumentException("Seat capacity must be >= 0");
        }
        this.seatCapacity.set(String.valueOf(seatCapacity));
    }

    public void setStatus(String status) {
        if (!status.equals("Available") && !status.equals("In Use")) {
            throw new IllegalArgumentException("Invalid status. Must be 'Available' or 'In Use'");
        }
        this.status.set(status);
    }

    @Override
    public String toString() {
        return "Airplane ID: " + getAirplaneId() +
                ", Seat Capacity: " + getSeatCapacity() +
                ", Status: " + getStatus();
    }

    public boolean isAvailable() {
        return "Available".equals(this.status.get());
    }

}

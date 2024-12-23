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

    public Flight(String flightNumber, String departureLocation, String arrivalLocation, String departureTime,
            String arrivalTime, String status) {
        this.flightNumber = new SimpleStringProperty(flightNumber);
        this.departureLocation = new SimpleStringProperty(departureLocation);
        this.arrivalLocation = new SimpleStringProperty(arrivalLocation);
        this.departureTime = new SimpleStringProperty(departureTime);
        this.arrivalTime = new SimpleStringProperty(arrivalTime);
        this.status = new SimpleStringProperty(status);
    }

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
}

package models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Gate {
    private final StringProperty gateNumber;
    private final BooleanProperty isAvailable;

    public Gate(String gateNumber, boolean isAvailable) {
        this.gateNumber = new SimpleStringProperty(gateNumber);
        this.isAvailable = new SimpleBooleanProperty(isAvailable);
    }

    // Property Getters
    public StringProperty gateNumberProperty() {
        return gateNumber;
    }

    public BooleanProperty isAvailableProperty() {
        return isAvailable;
    }

    // Standard Getters and Setters
    public String getGateNumber() {
        return gateNumber.get();
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber.set(gateNumber);
    }

    public boolean isAvailable() {
        return isAvailable.get();
    }

    public void setAvailable(boolean available) {
        this.isAvailable.set(available);
    }

    @Override
    public String toString() {
        return "Gate{" +
                "gateNumber='" + getGateNumber() + '\'' +
                ", isAvailable=" + isAvailable() +
                '}';
    }
}

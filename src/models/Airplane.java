package models;

public class Airplane {
    private String airplaneId;
    private int seatCapacity;
    private String status;

    // Constructor
    public Airplane(String airplaneId, int seatCapacity, String status) {
        this.airplaneId = airplaneId;
        this.seatCapacity = seatCapacity;
        this.status = status;
    }

    public String getAirplaneId() {
        return airplaneId;
    }

    public void setAirplaneId(String airplaneId) {
        this.airplaneId = airplaneId;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        if (seatCapacity < 0) {
            throw new IllegalArgumentException("Seat capacity must be >= 0");
        }
        this.seatCapacity = seatCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.equals("Available") && !status.equals("In Use")) {
            throw new IllegalArgumentException("Invalid status. Must be 'Available' or 'In Use'");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "Airplane ID: " + airplaneId + 
               ", Seat Capacity: " + seatCapacity + 
               ", Status: " + status;
    }
}

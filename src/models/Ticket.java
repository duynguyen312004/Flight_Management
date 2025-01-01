package models;

public class Ticket {
    private String ticketID;
    private String flightNumber;
    private String passengerID;
    private String seatNumber;
    private String seatClass;
    private double price;

    // Constructor
    public Ticket(String ticketID, String flightNumber, String passengerID, String seatNumber, String seatClass,
            double price) {
        this.ticketID = ticketID;
        this.flightNumber = flightNumber;
        this.passengerID = passengerID;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.price = price;
    }

    // Getters and Setters
    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(String passengerID) {
        this.passengerID = passengerID;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketID='" + ticketID + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", passengerID='" + passengerID + '\'' +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatClass='" + seatClass + '\'' +
                ", price=" + price +
                '}';
    }
}

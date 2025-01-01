package models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class TicketAdapter {
    private final SimpleStringProperty ticketId;
    private final SimpleStringProperty flightNumber;
    private final SimpleStringProperty seatNumber;
    private final SimpleStringProperty seatClass;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty passengerName;
    private final SimpleStringProperty passengerEmail;
    private final SimpleStringProperty passengerPhone;

    public TicketAdapter(Ticket ticket, String passengerName, String passengerEmail, String passengerPhone) {
        this.ticketId = new SimpleStringProperty(ticket.getTicketID());
        this.flightNumber = new SimpleStringProperty(ticket.getFlightNumber());
        this.seatNumber = new SimpleStringProperty(ticket.getSeatNumber());
        this.seatClass = new SimpleStringProperty(ticket.getSeatClass());
        this.price = new SimpleDoubleProperty(ticket.getPrice());
        this.passengerName = new SimpleStringProperty(passengerName);
        this.passengerEmail = new SimpleStringProperty(passengerEmail);
        this.passengerPhone = new SimpleStringProperty(passengerPhone);
    }

    // Các phương thức Property để liên kết với bảng
    public SimpleStringProperty ticketIdProperty() {
        return ticketId;
    }

    public SimpleStringProperty flightNumberProperty() {
        return flightNumber;
    }

    public SimpleStringProperty seatNumberProperty() {
        return seatNumber;
    }

    public SimpleStringProperty seatClassProperty() {
        return seatClass;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public SimpleStringProperty passengerNameProperty() {
        return passengerName;
    }

    public SimpleStringProperty passengerEmailProperty() {
        return passengerEmail;
    }

    public SimpleStringProperty passengerPhoneProperty() {
        return passengerPhone;
    }
}

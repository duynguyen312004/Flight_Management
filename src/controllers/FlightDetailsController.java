package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Flight;
import models.Gate;
import services.GateService;
import services.TicketService;
import services.PassengerService;

public class FlightDetailsController {

    @FXML
    private Label flightInfoLabel;

    @FXML
    private Label gateInfoLabel;

    @FXML
    private Label ticketCountLabel;

    @FXML
    private Label passengerCountLabel;

    private final GateService gateService = new GateService();
    private final TicketService ticketService = new TicketService();
    private final PassengerService passengerService = new PassengerService();

    public void loadFlightDetails(Flight flight) {
        // Hiển thị thông tin chuyến bay
        flightInfoLabel.setText(String.format("Flight %s\n %s to %s\nStatus: %s",
                flight.getFlightNumber(),
                flight.getDepartureLocation(),
                flight.getArrivalLocation(),
                flight.getStatus()));

        // Hiển thị thông tin cổng
        Gate gate = gateService.getGateByFlight(flight.getFlightNumber());
        if (gate != null) {
            gateInfoLabel.setText(String.format("Gate: %s (%s)",
                    gate.getGateNumber(),
                    gate.isAvailable() ? "Not Assigned" : "Assigned"));
        } else {
            gateInfoLabel.setText("Gate: No Gate Assigned");
        }

        // Hiển thị tổng số vé
        int ticketCount = ticketService.getTicketCountByFlight(flight.getFlightNumber());
        ticketCountLabel.setText("Total Tickets: " + ticketCount);

        // Hiển thị tổng số hành khách
        int passengerCount = passengerService.getPassengerCountByFlight(flight.getFlightNumber());
        passengerCountLabel.setText("Total Passengers: " + passengerCount);
    }

    @FXML
    private void handleViewTicketDetails() {
        System.out.println("View Ticket Details clicked.");
        // Add logic to open ticket details here
    }

    @FXML
    private void handleViewPassengerDetails() {
        System.out.println("View Passenger Details clicked.");
        // Add logic to open passenger details here
    }
}

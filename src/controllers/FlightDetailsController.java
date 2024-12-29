package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.*;
import services.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightDetailsController {

    @FXML
    private Label flightInfoLabel;

    @FXML
    private Label gateInfoLabel;

    @FXML
    private Label ticketCountLabel;

    @FXML
    private Label passengerCountLabel;

    @FXML
    private Label airplaneIdLabel;

    @FXML
    private Label seatCapacityLabel;

    @FXML
    private Label airplaneStatusLabel;

    @FXML
    private TableView<FlightCrew> flightCrewTable;

    @FXML
    private TableColumn<FlightCrew, String> crewNameColumn;

    @FXML
    private TableColumn<FlightCrew, String> crewRoleColumn;

    @FXML
    private TableColumn<FlightCrew, String> assignmentDateColumn;

    private final GateService gateService = new GateService();
    private final TicketService ticketService = new TicketService();
    private final PassengerService passengerService = new PassengerService();
    private final FlightCrewService flightCrewService = new FlightCrewService();
    private final AirplaneService airplaneService = new AirplaneService();

    @FXML
    public void initialize() {
    crewNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
    crewRoleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCrewRole()));
    assignmentDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAssignmentDate().toString()));

    // Gán tỷ lệ các cột cho bảng flightCrewTable
    crewNameColumn.prefWidthProperty().bind(flightCrewTable.widthProperty().multiply(0.25)); 
    crewRoleColumn.prefWidthProperty().bind(flightCrewTable.widthProperty().multiply(0.35)); 
    assignmentDateColumn.prefWidthProperty().bind(flightCrewTable.widthProperty().multiply(0.4)); 
}

    public void loadFlightDetails(Flight flight) {
        flightInfoLabel.setText(String.format("Flight %s: (%s -> %s) - %s",
                flight.getFlightNumber(),
                flight.getDepartureLocation(),
                flight.getArrivalLocation(),
                flight.getStatus()));

        Gate gate = gateService.getGateByFlight(flight.getFlightNumber());
        gateInfoLabel.setText(gate != null ? String.format("Gate: %s (%s)", gate.getGateNumber(), gate.isAvailable() ? "Not Assigned" : "Assigned") : "Gate: No Gate Assigned");

        ticketCountLabel.setText("Total Tickets: " + ticketService.getTicketCountByFlight(flight.getFlightNumber()));
        passengerCountLabel.setText("Total Passengers: " + passengerService.getPassengerCountByFlight(flight.getFlightNumber()));

        Airplane airplane = airplaneService.getAirplaneById(flight.getAirplaneId());
        if (airplane != null) {
            airplaneIdLabel.setText("ID: " + airplane.getAirplaneId());
            seatCapacityLabel.setText("Seat Capacity: " + airplane.getSeatCapacity());
            airplaneStatusLabel.setText("Status: " + airplane.getStatus());
        }

        List<FlightCrew> flightCrewList = flightCrewService.getFlightCrewByFlight(flight.getFlightNumber());
        flightCrewTable.getItems().setAll(flightCrewList);
    }

    @SuppressWarnings("unchecked")
    @FXML
private void handleViewTicketDetails() {
    String flightNumber = flightInfoLabel.getText().split(" ")[1].replace(":", "").trim();

    // Lấy danh sách vé theo flightNumber
    List<Ticket> tickets = ticketService.getTicketsByFlight(flightNumber);
    if (tickets.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Tickets Found");
        alert.setHeaderText(null);
        alert.setContentText("No tickets available for flight: " + flightNumber);
        alert.showAndWait();
        return;
    }

    // Lấy danh sách Passenger ID từ tickets
    List<String> passengerIds = tickets.stream()
            .map(Ticket::getPassengerID)
            .collect(Collectors.toList());

    // Lấy thông tin hành khách từ danh sách Passenger ID
    Map<String, Passenger> passengerMap = passengerService.getPassengersByIds(passengerIds).stream()
            .collect(Collectors.toMap(Passenger::getId, passenger -> passenger));

    // Tạo Stage để hiển thị bảng
    Stage stage = new Stage();
    stage.setTitle("Ticket Details - Flight " + flightNumber);
    stage.getIcons()
                        .add(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/flight.png")));
    stage.initModality(Modality.APPLICATION_MODAL);

    // Tạo TableView cho thông tin vé
    TableView<Ticket> ticketTable = new TableView<>();

    // Cột Ticket ID
    TableColumn<Ticket, String> ticketIdColumn = new TableColumn<>("Ticket ID");
    ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketID"));

    // Cột Seat Number
    TableColumn<Ticket, String> seatNumberColumn = new TableColumn<>("Seat Number");
    seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

    // Cột Seat Class
    TableColumn<Ticket, String> seatClassColumn = new TableColumn<>("Seat Class");
    seatClassColumn.setCellValueFactory(new PropertyValueFactory<>("seatClass"));

    // Cột Passenger Name
    TableColumn<Ticket, String> passengerNameColumn = new TableColumn<>("Passenger Name");
    passengerNameColumn.setCellValueFactory(data -> {
        Passenger passenger = passengerMap.get(data.getValue().getPassengerID());
        return new SimpleStringProperty(passenger != null ? passenger.getName() : "N/A");
    });

    // Cột Passenger Email
    TableColumn<Ticket, String> passengerEmailColumn = new TableColumn<>("Passenger Email");
    passengerEmailColumn.setCellValueFactory(data -> {
        Passenger passenger = passengerMap.get(data.getValue().getPassengerID());
        return new SimpleStringProperty(passenger != null ? passenger.getEmail() : "N/A");
    });

    // Cột Passenger Phone
    TableColumn<Ticket, String> passengerPhoneColumn = new TableColumn<>("Passenger Phone");
    passengerPhoneColumn.setCellValueFactory(data -> {
        Passenger passenger = passengerMap.get(data.getValue().getPassengerID());
        return new SimpleStringProperty(passenger != null ? passenger.getPhone() : "N/A");
    });

    // Cột Price
    TableColumn<Ticket, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

    // Chia tỷ lệ các cột để không có thanh cuộn ngang
    ticketIdColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.15));
    seatNumberColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.1));
    seatClassColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.1));
    passengerNameColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.2));
    passengerEmailColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.2));
    passengerPhoneColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.15));
    priceColumn.prefWidthProperty().bind(ticketTable.widthProperty().multiply(0.1));

    // Thêm các cột vào bảng
    ticketTable.getColumns().addAll(ticketIdColumn, seatNumberColumn, seatClassColumn,
            passengerNameColumn, passengerEmailColumn, passengerPhoneColumn, priceColumn);

    // Thêm dữ liệu vào bảng
    ticketTable.getItems().addAll(tickets);

    // Hiển thị bảng trong một VBox
    VBox vBox = new VBox(ticketTable);
    Scene scene = new Scene(vBox, 1000, 600);
    stage.setScene(scene);
    stage.show();
}

}
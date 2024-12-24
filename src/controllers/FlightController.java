package controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import services.FlightService;
import models.Flight;

public class FlightController {

    @FXML
    private TableView<Flight> flightTable;
    @FXML
    private TableColumn<Flight, String> flightNumberColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> arrivalColumn;
    @FXML
    private TableColumn<Flight, String> departureTimeColumn;
    @FXML
    private TableColumn<Flight, String> arrivalTimeColumn;
    @FXML
    private TableColumn<Flight, String> statusColumn;

    private final FlightService flightService = new FlightService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Liên kết cột với thuộc tính
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Đặt kích thước cột theo tỷ lệ
        flightTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            flightNumberColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            departureColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            arrivalColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            departureTimeColumn.setPrefWidth(tableWidth * 0.2); // 20% bảng
            arrivalTimeColumn.setPrefWidth(tableWidth * 0.2); // 20% bảng
            statusColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
        });

        loadFlightData();
    }

    private void loadFlightData() {
        List<Flight> flights = flightService.getAllFlights();
        if (flights.isEmpty()) {
            System.out.println("No flights found in the database.");
        } else {
            System.out.println("Flights loaded successfully: " + flights.size());
        }
        flightTable.getItems().setAll(flights);
    }

    @FXML
    private void handleAddFlight() {
        System.out.println("Add Flight clicked");
    }

    @FXML
    private void handleEditFlight() {
        System.out.println("Edit Flight clicked");
    }

    @FXML
    private void handleDeleteFlight() {
        System.out.println("Delete Flight clicked");
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refresh clicked");
        loadFlightData();
    }
}

package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
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

    @FXML
    public void initialize() {
        // Liên kết các cột trong TableView với các thuộc tính của lớp Flight
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
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
    }
}

package controllers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.FlightService;
import models.Flight;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

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
    private TableColumn<Flight, Void> actionColumn;

    @FXML
    private TableColumn<Flight, String> gateColumn;

    @FXML
    private TableColumn<Flight, String> airplaneColumn;

    private final FlightService flightService = new FlightService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Disable row clicking
        flightTable.setSelectionModel(null);

        // Liên kết cột với thuộc tính
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        gateColumn.setCellValueFactory(cellData -> cellData.getValue().assignedGateProperty());
        airplaneColumn.setCellValueFactory(cellData -> cellData.getValue().airplaneIdProperty());

        addActionButtonsToTable();

        // Đặt kích thước cột theo tỷ lệ
        flightTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            flightNumberColumn.setPrefWidth(tableWidth * 0.1);
            departureColumn.setPrefWidth(tableWidth * 0.1);
            arrivalColumn.setPrefWidth(tableWidth * 0.1);
            departureTimeColumn.setPrefWidth(tableWidth * 0.12);
            arrivalTimeColumn.setPrefWidth(tableWidth * 0.12);
            statusColumn.setPrefWidth(tableWidth * 0.1);
            gateColumn.setPrefWidth(tableWidth * 0.08);
            airplaneColumn.setPrefWidth(tableWidth * 0.1);
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

    @SuppressWarnings("unused")
    private void addActionButtonsToTable() {
        Callback<TableColumn<Flight, Void>, TableCell<Flight, Void>> cellFactory = param -> new TableCell<>() {
            private final HBox actionBox = new HBox(10);
            private final javafx.scene.control.Button editButton = new javafx.scene.control.Button("Edit");
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Delete");
            private final javafx.scene.control.Button viewDetailsButton = new javafx.scene.control.Button(
                    "View Details");

            {
                editButton.setStyle("-fx-background-color: #f3a520; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                viewDetailsButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Flight flight = getTableView().getItems().get(getIndex());
                    System.out.println("Editing flight: " + flight);
                    handleEditFlight();
                });

                deleteButton.setOnAction(event -> {
                    Flight flight = getTableView().getItems().get(getIndex());
                    System.out.println("Deleting flight: " + flight);
                    handleDeleteFlight();
                });

                viewDetailsButton.setOnAction(event -> {
                    Flight flight = getTableView().getItems().get(getIndex());
                    System.out.println("Viewing details for flight: " + flight);
                    handleViewDetails(flight);
                });

                actionBox.getChildren().addAll(editButton, deleteButton, viewDetailsButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void handleViewDetails(Flight flight) {
        if (flight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightDetails.fxml"));
                Parent root = loader.load();

                FlightDetailsController controller = loader.getController();
                controller.loadFlightDetails(flight);

                Stage stage = new Stage();
                stage.setTitle("Flight Details - " + flight.getFlightNumber());
                stage.getIcons()
                        .add(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/flight.png")));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Flight data is null!");
        }
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

    @FXML
    private void handleManageEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EmployeeManagement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Manage Employee");
            stage.getIcons()
                    .add(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/flight.png")));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageGate() {
        try {
            // Load FXML cho Manage Gate
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ManageGate.fxml"));
            Parent root = loader.load();

            // Hiển thị cửa sổ Manage Gate
            Stage stage = new Stage();
            stage.setTitle("Manage Gate");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ khác
            stage.showAndWait(); // Đợi cho đến khi cửa sổ Manage Gate đóng lại

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Manage Gate window.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleManageAirplane() {
        try {
            // Load FXML cho Manage Gate
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ManageAirplane.fxml"));
            Parent root = loader.load();

            // Hiển thị cửa sổ Manage Gate
            Stage stage = new Stage();
            stage.setTitle("Manage Airplane");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ khác
            stage.showAndWait(); // Đợi cho đến khi cửa sổ Manage Gate đóng lại

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Manage Airplane window.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

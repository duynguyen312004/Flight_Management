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
    private TableColumn<Flight, String> gateColumn;

    @FXML
    private TableColumn<Flight, String> airplaneColumn;

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
        gateColumn.setCellValueFactory(cellData -> cellData.getValue().assignedGateProperty());
        airplaneColumn.setCellValueFactory(cellData -> cellData.getValue().airplaneIdProperty());

        // Đặt kích thước cột theo tỷ lệ
        flightTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            flightNumberColumn.setPrefWidth(tableWidth * 0.1); // 10% bảng
            departureColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            arrivalColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            departureTimeColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            arrivalTimeColumn.setPrefWidth(tableWidth * 0.15); // 15% bảng
            statusColumn.setPrefWidth(tableWidth * 0.1); // 10% bảng
            gateColumn.setPrefWidth(tableWidth * 0.1); // 10% bảng
            airplaneColumn.setPrefWidth(tableWidth * 0.1); // 10% bảng
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
    private void handleViewDetails() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightDetails.fxml"));
                Parent root = loader.load();

                FlightDetailsController controller = loader.getController();
                controller.loadFlightDetails(selectedFlight);

                Stage stage = new Stage();
                stage.setTitle("Flight Details");
                stage.getIcons()
                        .add(new javafx.scene.image.Image(getClass().getResourceAsStream("/assets/flight.png")));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No flight selected!");
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

package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Airplane;
import services.AirplaneService;

import java.util.List;

public class ManageAirplaneController {

    @FXML
    private TableView<Airplane> airplaneTable;

    @FXML
    private TableColumn<Airplane, String> airplaneIdColumn;

    @FXML
    private TableColumn<Airplane, String> seatCapacityColumn;

    @FXML
    private TableColumn<Airplane, String> statusColumn;
    @FXML
    private TableColumn<Airplane, String> flightUsingColumn;

    private final AirplaneService airplaneService = new AirplaneService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Liên kết cột
        airplaneIdColumn.setCellValueFactory(data -> data.getValue().airplaneIdProperty());
        seatCapacityColumn.setCellValueFactory(data -> data.getValue().seatCapacityProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        flightUsingColumn.setCellValueFactory(data -> {
            String flightUsing = airplaneService.getFlightUsingAirplane(data.getValue().getAirplaneId());
            return new SimpleStringProperty(flightUsing);
        });

        // Tỷ lệ cột
        airplaneTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            airplaneIdColumn.setPrefWidth(totalWidth * 0.25); // 25%
            seatCapacityColumn.setPrefWidth(totalWidth * 0.25); // 25%
            statusColumn.setPrefWidth(totalWidth * 0.25); // 25%
            flightUsingColumn.setPrefWidth(totalWidth * 0.25);
        });
        // Tải dữ liệu
        loadAirplaneData();
    }

    private void loadAirplaneData() {
        List<Airplane> airplanes = airplaneService.getAllAirplanes();
        airplaneTable.getItems().setAll(airplanes);
    }

    @FXML
    private void handleAddAirplane() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Airplane");
        dialog.setHeaderText("Enter Airplane Details:");
        dialog.setContentText("Format: AirplaneID,SeatCapacity");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length != 2) {
                showAlert("Input Error", "Invalid format. Use: AirplaneID,SeatCapacity", Alert.AlertType.ERROR);
                return;
            }

            String airplaneId = parts[0].trim();
            int seatCapacity;

            try {
                seatCapacity = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Seat Capacity must be a valid number.", Alert.AlertType.ERROR);
                return;
            }

            if (airplaneId.isEmpty() || seatCapacity <= 0) {
                showAlert("Input Error", "Airplane ID and Seat Capacity must be valid.", Alert.AlertType.ERROR);
                return;
            }

            boolean success = airplaneService.addAirplane(new Airplane(airplaneId, seatCapacity));
            if (success) {
                showAlert("Success", "Airplane added successfully.", Alert.AlertType.INFORMATION);
                loadAirplaneData();
            } else {
                showAlert("Error", "Failed to add airplane. It may already exist.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleDeleteAirplane() {
        Airplane selectedAirplane = airplaneTable.getSelectionModel().getSelectedItem();
        if (selectedAirplane != null) {
            if (!"Available".equals(selectedAirplane.getStatus())) {
                showAlert("Error", "Cannot delete an airplane that is currently in use.", Alert.AlertType.ERROR);
                return;
            }

            boolean success = airplaneService.deleteAirplane(selectedAirplane.getAirplaneId());
            if (success) {
                showAlert("Success", "Airplane deleted successfully.", Alert.AlertType.INFORMATION);
                loadAirplaneData();
            } else {
                showAlert("Error", "Failed to delete airplane.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select an airplane to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefresh() {
        loadAirplaneData();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

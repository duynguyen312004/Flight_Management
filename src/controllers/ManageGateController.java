package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Gate;
import services.GateService;

import java.util.List;

public class ManageGateController {

    @FXML
    private TableView<Gate> gateTable;

    @FXML
    private TableColumn<Gate, String> gateNumberColumn;

    @FXML
    private TableColumn<Gate, String> statusColumn;
    @FXML
    private TableColumn<Gate, String> flightUsingColumn;

    private final GateService gateService = new GateService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Liên kết cột
        gateNumberColumn.setCellValueFactory(data -> data.getValue().gateNumberProperty());
        statusColumn.setCellValueFactory(data -> {
            boolean isAvailable = data.getValue().isAvailable();
            String status = isAvailable ? "Available" : "Occupied";
            return new SimpleStringProperty(status);
        });

        flightUsingColumn.setCellValueFactory(data -> {
            String flightUsing = gateService.getFlightUsingGate(data.getValue().getGateNumber());
            return new SimpleStringProperty(flightUsing);
        });

        // Đặt tỷ lệ cột để chiếm toàn bộ chiều rộng bảng
        gateTable.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            gateNumberColumn.setPrefWidth(totalWidth * 0.3); // 50% cho cột Gate Number
            statusColumn.setPrefWidth(totalWidth * 0.3); // 50% cho cột Status
            flightUsingColumn.setPrefWidth(totalWidth * 0.4);
        });

        // Tải dữ liệu
        loadGateData();
    }

    private void loadGateData() {
        List<Gate> gates = gateService.getAllGates();
        gateTable.getItems().setAll(gates);
    }

    @FXML
    private void handleAddGate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Gate");
        dialog.setHeaderText("Enter Gate Number:");
        dialog.setContentText("Gate Number:");

        dialog.showAndWait().ifPresent(gateNumber -> {
            if (gateNumber.trim().isEmpty()) {
                showAlert("Input Error", "Gate number cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            boolean success = gateService.addGate(new Gate(gateNumber, true)); // Mặc định trạng thái là Available
            if (success) {
                showAlert("Success", "Gate added successfully.", Alert.AlertType.INFORMATION);
                loadGateData();
            } else {
                showAlert("Error", "Failed to add gate. It may already exist.", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleDeleteGate() {
        Gate selectedGate = gateTable.getSelectionModel().getSelectedItem();
        if (selectedGate != null) {
            if (!selectedGate.isAvailable()) {
                showAlert("Error", "Cannot delete a gate that is currently occupied.", Alert.AlertType.ERROR);
                return;
            }

            boolean success = gateService.deleteGate(selectedGate.getGateNumber());
            if (success) {
                showAlert("Success", "Gate deleted successfully.", Alert.AlertType.INFORMATION);
                loadGateData();
            } else {
                showAlert("Error", "Failed to delete gate.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select a gate to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefresh() {
        loadGateData();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.FlightCrew;
import models.GroundStaff;
import models.Flight;
import services.FlightCrewService;
import services.GroundStaffService;

import java.util.List;

public class AssignEmployeeController {

    @FXML
    private TextField flightNumberField;

    @FXML
    private TextField gateField;

    @FXML
    private TableView<FlightCrew> crewTable;

    @FXML
    private TableColumn<FlightCrew, String> crewIdColumn;

    @FXML
    private TableColumn<FlightCrew, String> crewNameColumn;

    @FXML
    private TableColumn<FlightCrew, String> crewRoleColumn;

    @FXML
    private ComboBox<String> crewRoleComboBox;

    @FXML
    private TableView<GroundStaff> staffTable;

    @FXML
    private TableColumn<GroundStaff, String> staffIdColumn;

    @FXML
    private TableColumn<GroundStaff, String> staffNameColumn;

    @FXML
    private TableColumn<GroundStaff, String> staffRoleColumn;

    private final FlightCrewService flightCrewService = new FlightCrewService();
    private final GroundStaffService groundStaffService = new GroundStaffService();

    private Flight flight;
    private boolean isAssignmentComplete = false; // Trạng thái phân công

    public void setFlight(Flight flight) {
        this.flight = flight;
        flightNumberField.setText(flight.getFlightNumber());
        gateField.setText(flight.getAssignedGate());
        loadAvailableEmployees();
    }

    public boolean isAssignmentComplete() {
        return isAssignmentComplete;
    }

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Thiết lập bảng phi hành đoàn
        crewIdColumn.setCellValueFactory(data -> data.getValue().idProperty());
        crewNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        crewRoleColumn.setCellValueFactory(data -> data.getValue().crewRoleProperty());

        // Thiết lập bảng nhân viên mặt đất
        staffIdColumn.setCellValueFactory(data -> data.getValue().idProperty());
        staffNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        staffRoleColumn.setCellValueFactory(data -> data.getValue().roleProperty());

        // Thiết lập ComboBox cho vai trò phi hành đoàn
        crewRoleComboBox.setItems(FXCollections.observableArrayList("Pilot", "Co-Pilot", "Flight Attendant"));

        // Điều chỉnh tỷ lệ cột trong bảng crewTable
        crewTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            crewIdColumn.setPrefWidth(tableWidth * 0.3); // 30% cho cột ID
            crewNameColumn.setPrefWidth(tableWidth * 0.4); // 40% cho cột Name
            crewRoleColumn.setPrefWidth(tableWidth * 0.3); // 30% cho cột Role
        });

        // Điều chỉnh tỷ lệ cột trong bảng staffTable
        staffTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            staffIdColumn.setPrefWidth(tableWidth * 0.3); // 30% cho cột ID
            staffNameColumn.setPrefWidth(tableWidth * 0.4); // 40% cho cột Name
            staffRoleColumn.setPrefWidth(tableWidth * 0.3); // 30% cho cột Role
        });
    }

    private void loadAvailableEmployees() {
        // Lấy danh sách phi hành đoàn khả dụng
        List<FlightCrew> availableCrew = flightCrewService.getAvailableCrew();
        crewTable.getItems().setAll(availableCrew);

        // Lấy danh sách nhân viên mặt đất khả dụng
        List<GroundStaff> availableStaff = groundStaffService.getAvailableGroundStaff();
        staffTable.getItems().setAll(availableStaff);
    }

    @FXML
    private void handleAssignCrew() {
        FlightCrew selectedCrew = crewTable.getSelectionModel().getSelectedItem();
        String crewRole = crewRoleComboBox.getValue();

        if (selectedCrew == null || crewRole == null) {
            showAlert("Input Error", "Please select a crew member and role.", Alert.AlertType.ERROR);
            return;
        }

        boolean success = flightCrewService.assignCrewToFlight(selectedCrew.getId(), flight.getFlightNumber(),
                crewRole);

        if (success) {
            showAlert("Success", "Crew assigned successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to assign crew.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAssignStaff() {
        GroundStaff selectedStaff = staffTable.getSelectionModel().getSelectedItem();

        if (selectedStaff == null) {
            showAlert("Input Error", "Please select a staff member.", Alert.AlertType.ERROR);
            return;
        }

        boolean success = groundStaffService.assignStaffToGate(selectedStaff.getId(), flight.getAssignedGate());

        if (success) {
            showAlert("Success", "Staff assigned to gate successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to assign staff to gate.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleConfirm() {
        // Đánh dấu quy trình phân công hoàn tất
        isAssignmentComplete = true;
        closeWindow();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) flightNumberField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

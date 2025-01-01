package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Airplane;
import models.Flight;
import models.Gate;
import services.AirplaneService;
import services.FlightService;
import services.GateService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;

public class EditFlightController {

    @FXML
    private TextField flightNumberField;

    @FXML
    private TextField departureField;

    @FXML
    private TextField arrivalField;

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private Spinner<Integer> departureHourSpinner;

    @FXML
    private Spinner<Integer> departureMinuteSpinner;

    @FXML
    private DatePicker arrivalDatePicker;

    @FXML
    private Spinner<Integer> arrivalHourSpinner;

    @FXML
    private Spinner<Integer> arrivalMinuteSpinner;

    @FXML
    private ComboBox<Airplane> airplaneComboBox;

    @FXML
    private ComboBox<Gate> gateComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    private final FlightService flightService = new FlightService();
    private final AirplaneService airplaneService = new AirplaneService();
    private final GateService gateService = new GateService();

    private Flight flight;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void setFlight(Flight flight) {
        this.flight = flight;
        loadFlightData();
    }

    @FXML
    public void initialize() {
        initTimeSpinners();
        loadAvailableAirplanes();
        loadAvailableGates();
        gateComboBox.setConverter(new javafx.util.StringConverter<Gate>() {
            @Override
            public String toString(Gate gate) {
                return gate != null ? gate.getGateNumber() : ""; // Hiển thị Gate Number
            }

            @Override
            public Gate fromString(String string) {
                return null; // Không cần thiết trong trường hợp này
            }
        });

        // Khởi tạo các trạng thái khả dụng
        statusComboBox.setItems(FXCollections.observableArrayList("Scheduled", "Delayed", "Cancelled", "Landed"));
    }

    private void loadFlightData() {
        // Hiển thị thông tin chuyến bay hiện tại lên form
        flightNumberField.setText(flight.getFlightNumber());
        flightNumberField.setDisable(true); // Không cho chỉnh sửa số hiệu chuyến bay
        departureField.setText(flight.getDepartureLocation());
        arrivalField.setText(flight.getArrivalLocation());

        // Tải thời gian khởi hành và đến
        LocalDateTime departureTime = LocalDateTime.parse(flight.getDepartureTime(), TIMESTAMP_FORMATTER);
        departureDatePicker.setValue(departureTime.toLocalDate());
        departureHourSpinner.getValueFactory().setValue(departureTime.getHour());
        departureMinuteSpinner.getValueFactory().setValue(departureTime.getMinute());

        LocalDateTime arrivalTime = LocalDateTime.parse(flight.getArrivalTime(), TIMESTAMP_FORMATTER);
        arrivalDatePicker.setValue(arrivalTime.toLocalDate());
        arrivalHourSpinner.getValueFactory().setValue(arrivalTime.getHour());
        arrivalMinuteSpinner.getValueFactory().setValue(arrivalTime.getMinute());

        // Chọn trạng thái hiện tại
        statusComboBox.setValue(flight.getStatus());

        // Không thiết lập giá trị ban đầu cho Gate và Airplane
        gateComboBox.getSelectionModel().clearSelection();
        airplaneComboBox.getSelectionModel().clearSelection();
    }

    private void loadAvailableAirplanes() {
        List<Airplane> availableAirplanes = airplaneService.getAvailableAirplanes();
        airplaneComboBox.getItems().setAll(availableAirplanes);
    }

    private void loadAvailableGates() {
        List<Gate> availableGates = gateService.getAvailableGates();
        gateComboBox.getItems().setAll(availableGates);
    }

    private void initTimeSpinners() {
        initSpinner(departureHourSpinner, 0, 23);
        initSpinner(departureMinuteSpinner, 0, 59);
        initSpinner(arrivalHourSpinner, 0, 23);
        initSpinner(arrivalMinuteSpinner, 0, 59);
    }

    private void initSpinner(Spinner<Integer> spinner, int min, int max) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
    }

    @FXML
    private void handleSave() {
        try {
            System.out.println("[DEBUG] Starting handleSave process...");

            String departureLocation = departureField.getText().trim();
            String arrivalLocation = arrivalField.getText().trim();
            LocalDate departureDate = departureDatePicker.getValue();
            int departureHour = departureHourSpinner.getValue();
            int departureMinute = departureMinuteSpinner.getValue();
            LocalDate arrivalDate = arrivalDatePicker.getValue();
            int arrivalHour = arrivalHourSpinner.getValue();
            int arrivalMinute = arrivalMinuteSpinner.getValue();
            String status = statusComboBox.getValue();

            // Log thông tin đầu vào
            System.out.println("[DEBUG] Input Details:");
            System.out.println("- Departure Location: " + departureLocation);
            System.out.println("- Arrival Location: " + arrivalLocation);
            System.out.println("- Departure Time: " + departureDate + " " + departureHour + ":" + departureMinute);
            System.out.println("- Arrival Time: " + arrivalDate + " " + arrivalHour + ":" + arrivalMinute);
            System.out.println("- Status: " + status);

            // Validate thông tin
            if (departureLocation.isEmpty() || arrivalLocation.isEmpty() || departureDate == null
                    || arrivalDate == null) {
                showAlert("Validation Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
                return;
            }

            // Kiểm tra logic thời gian
            LocalDateTime departureTime = LocalDateTime.of(departureDate, LocalTime.of(departureHour, departureMinute));
            LocalDateTime arrivalTime = LocalDateTime.of(arrivalDate, LocalTime.of(arrivalHour, arrivalMinute));
            if (!arrivalTime.isAfter(departureTime)) {
                showAlert("Validation Error", "Arrival time must be after departure time.", Alert.AlertType.ERROR);
                return;
            }

            // Cập nhật thông tin chuyến bay
            flight.setDepartureLocation(departureLocation);
            flight.setArrivalLocation(arrivalLocation);
            flight.setDepartureTime(departureTime.format(TIMESTAMP_FORMATTER));
            flight.setArrivalTime(arrivalTime.format(TIMESTAMP_FORMATTER));
            flight.setStatus(status);

            System.out.println("[DEBUG] Updating flight in database...");
            boolean updateSuccess = flightService.updateFlight(flight);
            if (!updateSuccess) {
                System.err.println("[ERROR] Failed to update flight in database.");
                showAlert("Error", "Failed to update flight.", Alert.AlertType.ERROR);
                return;
            }
            System.out.println("[DEBUG] Flight updated successfully!");

            // Xử lý trạng thái đặc biệt
            if (status.equals("Landed") || status.equals("Cancelled")) {
                handleSpecialStatus();
            } else {
                handleGateAndAirplaneUpdates();
            }

            showAlert("Success", "Flight updated successfully.", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            System.err.println("[ERROR] Exception occurred during handleSave:");
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the flight.", Alert.AlertType.ERROR);
        }
    }

    private void handleSpecialStatus() throws Exception {
        System.out.println("[DEBUG] Handling special status: " + flight.getStatus());

        // Lưu vào lịch sử
        System.out.println("[DEBUG] Archiving flight...");
        boolean archived = flightService.archiveFlight(flight.getFlightNumber());
        if (!archived) {
            System.err.println("[ERROR] Failed to archive flight.");
            showAlert("Error", "Failed to archive flight history.", Alert.AlertType.ERROR);
            return;
        }
        System.out.println("[DEBUG] Flight archived successfully!");

        // Giải phóng tài nguyên
        if (flight.getAssignedGate() != null) {
            System.out.println("[DEBUG] Releasing gate: " + flight.getAssignedGate());
            gateService.releaseGate(flight.getAssignedGate());
            gateService.releaseStaffAssignedToGate(flight.getAssignedGate());
            flight.setAssignedGate(null);
        }

        if (flight.getAirplaneId() != null) {
            System.out.println("[DEBUG] Releasing airplane: " + flight.getAirplaneId());
            airplaneService.releaseAirplane(flight.getAirplaneId());
            flight.setAirplaneId(null);
        }

        System.out.println("[DEBUG] Releasing flight crew...");
        flightService.releaseFlightCrew(flight.getFlightNumber());

        // Xóa chuyến bay khỏi bảng flight
        System.out.println("[DEBUG] Deleting flight from active flights...");
        boolean deleteSuccess = flightService.deleteFlight(flight.getFlightNumber());
        if (!deleteSuccess) {
            System.err.println("[ERROR] Failed to delete flight after archiving.");
            showAlert("Error", "Failed to delete flight after archiving.", Alert.AlertType.ERROR);
        } else {
            System.out.println("[DEBUG] Flight deleted successfully!");
        }
    }

    private void handleGateAndAirplaneUpdates() throws Exception {
        // Xử lý cổng
        Gate selectedGate = gateComboBox.getValue();
        if (selectedGate != null && !selectedGate.getGateNumber().equals(flight.getAssignedGate())) {
            System.out.println("[DEBUG] Changing assigned gate...");
            if (flight.getAssignedGate() != null) {
                gateService.releaseGate(flight.getAssignedGate());
            }
            gateService.assignGate(selectedGate.getGateNumber());
            flight.setAssignedGate(selectedGate.getGateNumber());
        }

        // Xử lý máy bay
        Airplane selectedAirplane = airplaneComboBox.getValue();
        if (selectedAirplane != null && !selectedAirplane.getAirplaneId().equals(flight.getAirplaneId())) {
            System.out.println("[DEBUG] Changing assigned airplane...");
            if (flight.getAirplaneId() != null) {
                airplaneService.releaseAirplane(flight.getAirplaneId());
            }
            airplaneService.assignAirplane(selectedAirplane.getAirplaneId());
            flight.setAirplaneId(selectedAirplane.getAirplaneId());
        }
    }

    @FXML
    private void handleCancel() {
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

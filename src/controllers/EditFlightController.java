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
            String departureLocation = departureField.getText().trim();
            String arrivalLocation = arrivalField.getText().trim();
            LocalDate departureDate = departureDatePicker.getValue();
            int departureHour = departureHourSpinner.getValue();
            int departureMinute = departureMinuteSpinner.getValue();
            LocalDate arrivalDate = arrivalDatePicker.getValue();
            int arrivalHour = arrivalHourSpinner.getValue();
            int arrivalMinute = arrivalMinuteSpinner.getValue();
            String status = statusComboBox.getValue();

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

            // **Logic giải phóng và cập nhật tài nguyên**

            // 1. Kiểm tra cổng có thay đổi không
            Gate selectedGate = gateComboBox.getValue();
            if (selectedGate != null && !selectedGate.getGateNumber().equals(flight.getAssignedGate())) {
                // Giải phóng cổng cũ
                if (flight.getAssignedGate() != null) {
                    gateService.releaseGate(flight.getAssignedGate());
                    gateService.releaseStaffAssignedToGate(flight.getAssignedGate()); // Giải phóng nhân viên liên quan
                }
                // Gán cổng mới
                gateService.assignGate(selectedGate.getGateNumber());
                flight.setAssignedGate(selectedGate.getGateNumber());
            }

            // 2. Kiểm tra máy bay có thay đổi không
            Airplane selectedAirplane = airplaneComboBox.getValue();
            if (selectedAirplane != null && !selectedAirplane.getAirplaneId().equals(flight.getAirplaneId())) {
                // Giải phóng máy bay cũ
                if (flight.getAirplaneId() != null) {
                    airplaneService.releaseAirplane(flight.getAirplaneId());
                }
                // Gán máy bay mới
                airplaneService.assignAirplane(selectedAirplane.getAirplaneId());
                flight.setAirplaneId(selectedAirplane.getAirplaneId());
            }

            // 3. Logic trạng thái đặc biệt (Landed, Cancelled)
            if (status.equals("Landed") || status.equals("Cancelled")) {
                // Giải phóng cổng và nhân viên liên quan
                if (flight.getAssignedGate() != null) {
                    gateService.releaseGate(flight.getAssignedGate());
                    gateService.releaseStaffAssignedToGate(flight.getAssignedGate());
                    flight.setAssignedGate(null); // Xóa cổng khỏi chuyến bay
                }

                // Giải phóng máy bay
                if (flight.getAirplaneId() != null) {
                    airplaneService.releaseAirplane(flight.getAirplaneId());
                    flight.setAirplaneId(null); // Xóa máy bay khỏi chuyến bay
                }

                // Giải phóng phi hành đoàn
                flightService.releaseFlightCrew(flight.getFlightNumber());
            }

            // Lưu chuyến bay đã chỉnh sửa
            boolean success = flightService.updateFlight(flight);

            if (success) {
                showAlert("Success", "Flight updated successfully.", Alert.AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Error", "Failed to update flight.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the flight.", Alert.AlertType.ERROR);
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

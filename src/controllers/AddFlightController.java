package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Flight;
import services.AirplaneService;
import services.FlightService;
import services.GateService;
import models.Airplane;
import models.Gate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddFlightController {

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
    private Spinner<Integer> departureSecondSpinner;

    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private Spinner<Integer> arrivalHourSpinner;
    @FXML
    private Spinner<Integer> arrivalMinuteSpinner;
    @FXML
    private Spinner<Integer> arrivalSecondSpinner;
    @FXML
    private ComboBox<Airplane> airplaneComboBox;
    @FXML
    private ComboBox<Gate> gateComboBox;
    @FXML
    private ComboBox<String> statusComboBox;

    private final FlightService flightService = new FlightService();
    private final AirplaneService airplaneService = new AirplaneService();
    private final GateService gateService = new GateService();
    // Định nghĩa formatter cho định dạng chuẩn
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        // Tải danh sách máy bay và cổng hợp lệ
        loadAvailableAirplanes();
        loadAvailableGates();
        // Thiết lập StringConverter cho ComboBox Gate
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
        // Khởi tạo Spinner cho giờ/phút/giây
        initTimeSpinner(departureHourSpinner, 0, 23);
        initTimeSpinner(departureMinuteSpinner, 0, 59);
        initTimeSpinner(departureSecondSpinner, 0, 59);

        initTimeSpinner(arrivalHourSpinner, 0, 23);
        initTimeSpinner(arrivalMinuteSpinner, 0, 59);
        initTimeSpinner(arrivalSecondSpinner, 0, 59);
    }

    private void loadAvailableAirplanes() {
        List<Airplane> availableAirplanes = airplaneService.getAvailableAirplanes();
        if (availableAirplanes.isEmpty()) {
            showAlert("Warning", "No available airplanes found.", Alert.AlertType.WARNING);
        }
        airplaneComboBox.getItems().setAll(availableAirplanes);
    }

    private void loadAvailableGates() {
        List<Gate> availableGates = gateService.getAvailableGates();
        if (availableGates.isEmpty()) {
            showAlert("Warning", "No available gates found.", Alert.AlertType.WARNING);
        }
        gateComboBox.getItems().setAll(availableGates);
    }

    @FXML
    private void handleSave() {
        // Lấy giá trị từ DatePicker và Spinner
        LocalDate departureDate = departureDatePicker.getValue();
        int departureHour = departureHourSpinner.getValue();
        int departureMinute = departureMinuteSpinner.getValue();
        int departureSecond = departureSecondSpinner.getValue();

        LocalDate arrivalDate = arrivalDatePicker.getValue();
        int arrivalHour = arrivalHourSpinner.getValue();
        int arrivalMinute = arrivalMinuteSpinner.getValue();
        int arrivalSecond = arrivalSecondSpinner.getValue();

        // Kiểm tra nếu không có giá trị được chọn
        if (airplaneComboBox.getValue() == null) {
            showAlert("Input Error", "Please select a valid airplane.", Alert.AlertType.ERROR);
            return;
        }
        // Lấy Airplane ID từ ComboBox
        String airplaneId = airplaneComboBox.getValue().getAirplaneId();
        System.out.println("Selected Airplane ID: " + airplaneId);

        // Kiểm tra giá trị null
        if (departureDate == null || arrivalDate == null) {
            showAlert("Input Error", "Please select both departure and arrival dates.", Alert.AlertType.ERROR);
            return;
        }

        if (departureHourSpinner.getValue() == null || departureMinuteSpinner.getValue() == null
                || departureSecondSpinner.getValue() == null) {
            showAlert("Input Error", "Please provide valid departure time.", Alert.AlertType.ERROR);
            return;
        }

        if (arrivalHourSpinner.getValue() == null || arrivalMinuteSpinner.getValue() == null
                || arrivalSecondSpinner.getValue() == null) {
            showAlert("Input Error", "Please provide valid arrival time.", Alert.AlertType.ERROR);
            return;
        }

        // Tạo LocalDateTime
        LocalDateTime departureTime = LocalDateTime.of(
                departureDate,
                LocalTime.of(departureHour, departureMinute, departureSecond));

        LocalDateTime arrivalTime = LocalDateTime.of(
                arrivalDate,
                LocalTime.of(arrivalHour, arrivalMinute, arrivalSecond));

        // Kiểm tra logic thời gian
        if (!arrivalTime.isAfter(departureTime)) {
            showAlert("Input Error", "Arrival time must be after departure time.", Alert.AlertType.ERROR);
            return;
        }

        // Định dạng thời gian trước khi lưu
        String departureTimeString = departureTime.format(TIMESTAMP_FORMATTER);
        String arrivalTimeString = arrivalTime.format(TIMESTAMP_FORMATTER);

        // Tạo đối tượng Flight
        Flight flight = new Flight(
                flightNumberField.getText().trim(),
                departureField.getText().trim(),
                arrivalField.getText().trim(),
                departureTimeString,
                arrivalTimeString,
                "Scheduled",
                gateComboBox.getValue().getGateNumber(),
                airplaneComboBox.getValue().getAirplaneId());

        boolean success = flightService.addFlight(flight);

        if (success) {
            showAlert("Success", "Flight added successfully.", Alert.AlertType.INFORMATION);

            // Mở màn hình phân công nhân viên
            boolean assignmentComplete = openAssignEmployeeScreen(flight);

            // Nếu phân công thành công, đóng cửa sổ Add Flight
            if (assignmentComplete) {
                closeWindow();
            } else {
                showAlert("Warning",
                        "Assignment incomplete. Flight is saved, but crew and staff are not fully assigned.",
                        Alert.AlertType.WARNING);
            }
        } else {
            showAlert("Error", "Failed to add flight.", Alert.AlertType.ERROR);
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

    private void initTimeSpinner(Spinner<Integer> spinner, int min, int max) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, 0);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true); // Cho phép người dùng nhập tay
    }

    private boolean openAssignEmployeeScreen(Flight flight) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AssignEmployee.fxml"));
            Parent root = loader.load();

            AssignEmployeeController controller = loader.getController();
            controller.setFlight(flight);

            Stage stage = new Stage();
            stage.setTitle("Assign Employees");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return controller.isAssignmentComplete(); // Trả về kết quả phân công từ controller
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Assign Employee screen.", Alert.AlertType.ERROR);
            return false;
        }
    }

}

package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.FlightCrew;
import models.GroundStaff;
import services.FlightCrewService;
import services.GroundStaffService;

public class AddEmployeeController {

    @FXML
    private ComboBox<String> employeeTypeComboBox;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField roleField;
    @FXML
    private Label crewRoleLabel;
    @FXML
    private ComboBox<String> crewRoleComboBox;
    @FXML
    private Button addButton;

    private final FlightCrewService flightCrewService = new FlightCrewService();
    private final GroundStaffService groundStaffService = new GroundStaffService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Thiết lập ComboBox loại nhân viên
        employeeTypeComboBox.getItems().addAll("Flight Crew", "Ground Staff");
        employeeTypeComboBox.setValue("Ground Staff"); // Mặc định chọn

        // Thiết lập ComboBox Crew Role
        crewRoleComboBox.getItems().addAll("Pilot", "Co-Pilot", "Flight Attendant");

        // Cập nhật hiển thị Crew Role khi chọn Flight Crew
        employeeTypeComboBox.setOnAction(event -> {
            boolean isFlightCrew = "Flight Crew".equals(employeeTypeComboBox.getValue());
            crewRoleLabel.setVisible(isFlightCrew);
            crewRoleComboBox.setVisible(isFlightCrew);

            if (isFlightCrew) {
                crewRoleComboBox.getItems().setAll("Pilot", "Co-Pilot", "Flight Attendant");
            } else {
                crewRoleComboBox.getItems().clear();
            }
        });
    }

    @FXML
    private void handleAddEmployee() {
        String type = employeeTypeComboBox.getValue(); // Lấy loại nhân viên (Flight Crew hoặc Ground Staff)
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();

        // Kiểm tra các trường bắt buộc
        if (id.isEmpty() || name.isEmpty() || address.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        boolean success;
        if ("Flight Crew".equals(type)) {
            // Lấy giá trị từ crewRoleComboBox
            String crewRole = crewRoleComboBox.getValue();
            if (crewRole == null || crewRole.isEmpty()) {
                showAlert("Input Error", "Please select a Crew Role.", Alert.AlertType.ERROR);
                return;
            }

            // Tạo đối tượng FlightCrew với role là "Flight Crew"
            FlightCrew newCrew = new FlightCrew(
                    id,
                    name,
                    address,
                    type, // "Flight Crew" sẽ được gán vào thuộc tính role
                    crewRole, // Crew Role như "Pilot", "Co-Pilot", ...
                    "Not Assigned", // Flight Number ban đầu chưa được gán
                    null // Assignment Date ban đầu chưa được gán
            );
            success = flightCrewService.addFlightCrew(newCrew);

        } else { // Ground Staff
            // Tạo đối tượng GroundStaff với role là "Ground Staff"
            GroundStaff newStaff = new GroundStaff(
                    id,
                    name,
                    address,
                    type, // "Ground Staff" sẽ được gán vào thuộc tính role
                    "Not Assigned", // Assigned Gate ban đầu chưa được gán
                    null // Assignment Date ban đầu chưa được gán
            );
            success = groundStaffService.addGroundStaff(newStaff);
        }

        // Kết quả xử lý
        if (success) {
            showAlert("Success", "Employee added successfully.", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Error", "Failed to add employee. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }
}

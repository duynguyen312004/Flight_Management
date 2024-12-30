package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Employee;
import models.FlightCrew;
import models.GroundStaff;
import services.FlightCrewService;
import services.GroundStaffService;

public class EditEmployeeController {

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

    private Employee currentEmployee;

    private final FlightCrewService flightCrewService = new FlightCrewService();
    private final GroundStaffService groundStaffService = new GroundStaffService();

    public void setEmployee(Employee employee) {
        this.currentEmployee = employee;

        // Điền thông tin ban đầu vào các trường
        idField.setText(employee.getId());
        idField.setEditable(false); // Không cho phép chỉnh sửa ID
        nameField.setText(employee.getName());
        addressField.setText(employee.getAddress());
        roleField.setText(employee.getRole());

        // Kiểm tra loại nhân viên để hiển thị các trường phù hợp
        if (employee instanceof FlightCrew) {
            setupFlightCrewFields((FlightCrew) employee);
        } else {
            hideCrewRoleFields();
        }
    }

    private void setupFlightCrewFields(FlightCrew crew) {
        crewRoleLabel.setVisible(true);
        crewRoleComboBox.setVisible(true);
        crewRoleComboBox.setItems(FXCollections.observableArrayList("Pilot", "Co-Pilot", "Flight Attendant"));
        crewRoleComboBox.setValue(crew.getCrewRole());
    }

    private void hideCrewRoleFields() {
        crewRoleLabel.setVisible(false);
        crewRoleComboBox.setVisible(false);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String role = roleField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || role.isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.", Alert.AlertType.ERROR);
            return;
        }

        // Cập nhật thông tin chung
        currentEmployee.setName(name);
        currentEmployee.setAddress(address);
        currentEmployee.setRole(role);

        boolean success;
        if (currentEmployee instanceof FlightCrew) {
            success = handleFlightCrewUpdate((FlightCrew) currentEmployee);
        } else if (currentEmployee instanceof GroundStaff) {
            success = handleGroundStaffUpdate((GroundStaff) currentEmployee);
        } else {
            showAlert("Error", "Unknown employee type.", Alert.AlertType.ERROR);
            return;
        }

        if (success) {
            showAlert("Success", "Employee updated successfully.", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Error", "Failed to update employee. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private boolean handleFlightCrewUpdate(FlightCrew crew) {
        String crewRole = crewRoleComboBox.getValue();
        if (crewRole == null || crewRole.isEmpty()) {
            showAlert("Input Error", "Please select a Crew Role.", Alert.AlertType.ERROR);
            return false;
        }
        crew.setCrewRole(crewRole);
        return flightCrewService.updateFlightCrew(crew);
    }

    private boolean handleGroundStaffUpdate(GroundStaff staff) {
        return groundStaffService.updateGroundStaff(staff);
    }

    private void closeWindow() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

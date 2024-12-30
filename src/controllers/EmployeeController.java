package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Employee;
import models.FlightCrew;
import models.GroundStaff;
import services.FlightCrewService;
import services.GroundStaffService;

import java.io.IOException;
import java.util.List;

public class EmployeeController {

    @FXML
    private ComboBox<String> employeeTypeComboBox;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> idColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TableColumn<Employee, String> roleColumn;

    @FXML
    private TableColumn<Employee, String> additionalInfoColumn;

    private final FlightCrewService flightCrewService = new FlightCrewService();
    private final GroundStaffService groundStaffService = new GroundStaffService();

    @SuppressWarnings("unused")
    @FXML
    public void initialize() {
        // Thiết lập ComboBox loại nhân viên
        employeeTypeComboBox.setItems(FXCollections.observableArrayList("Flight Crew", "Ground Staff"));
        employeeTypeComboBox.setValue("Flight Crew"); // Mặc định chọn Flight Crew
        employeeTypeComboBox.setOnAction(event -> loadEmployeeData());

        // Gán thuộc tính cột với dữ liệu
        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        roleColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof FlightCrew) {
                FlightCrew crew = (FlightCrew) data.getValue();
                return new SimpleStringProperty(crew.getCrewRole() != null ? crew.getCrewRole() : "Not Assigned");
            } else if (data.getValue() instanceof GroundStaff) {
                GroundStaff staff = (GroundStaff) data.getValue();
                return new SimpleStringProperty(staff.getRole());
            } else {
                return new SimpleStringProperty("Unknown");
            }
        });

        additionalInfoColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof FlightCrew) {
                FlightCrew crew = (FlightCrew) data.getValue();
                return new SimpleStringProperty(crew.getFlightNumber() != null && !crew.getFlightNumber().isEmpty()
                        ? crew.getFlightNumber()
                        : "Not Assigned");
            } else if (data.getValue() instanceof GroundStaff) {
                GroundStaff staff = (GroundStaff) data.getValue();
                return new SimpleStringProperty(staff.getAssignedGate() != null && !staff.getAssignedGate().isEmpty()
                        ? staff.getAssignedGate()
                        : "Not Assigned");
            } else {
                return new SimpleStringProperty("Unknown");
            }
        });

        // Gán tỷ lệ cột cho bảng
        employeeTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            idColumn.setPrefWidth(totalWidth * 0.2); // 20% cho cột ID
            nameColumn.setPrefWidth(totalWidth * 0.3); // 30% cho cột Name
            roleColumn.setPrefWidth(totalWidth * 0.2); // 20% cho cột Role
            additionalInfoColumn.setPrefWidth(totalWidth * 0.3); // 30% cho cột Additional Info
        });
        // Tải dữ liệu ban đầu
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        String selectedType = employeeTypeComboBox.getValue();
        List<? extends Employee> employees;
        if ("Flight Crew".equals(selectedType)) {
            employees = flightCrewService.getAllFlightCrews();
        } else {
            employees = groundStaffService.getAllGroundStaff();
        }
        employeeTable.getItems().setAll(employees);
    }

    @FXML
    private void handleAddEmployee() {
        try {
            // Load FXML cho AddEmployee
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddEmployee.fxml"));
            Parent root = loader.load();

            // Hiển thị cửa sổ thêm nhân viên
            Stage stage = new Stage();
            stage.setTitle("Add New Employee");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ khác
            stage.showAndWait(); // Chờ cho đến khi cửa sổ AddEmployee đóng

            // Làm mới dữ liệu trong bảng sau khi thêm
            loadEmployeeData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Add Employee window.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEditEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            try {
                // Load FXML cho EditEmployee
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditEmployee.fxml"));
                Parent root = loader.load();

                // Lấy controller của EditEmployee để truyền dữ liệu
                EditEmployeeController controller = loader.getController();
                controller.setEmployee(selectedEmployee); // Truyền nhân viên được chọn

                // Hiển thị cửa sổ chỉnh sửa
                Stage stage = new Stage();
                stage.setTitle("Edit Employee");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ khác
                stage.showAndWait(); // Chờ cho đến khi cửa sổ chỉnh sửa đóng

                // Làm mới dữ liệu trong bảng sau khi chỉnh sửa
                loadEmployeeData();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to open Edit Employee window.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("No Selection", "Please select an employee to edit.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Delete Employee");
            confirmAlert.setContentText("Are you sure you want to delete this employee?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                String id = selectedEmployee.getId();
                boolean success;
                if (selectedEmployee instanceof FlightCrew) {
                    success = flightCrewService.deleteFlightCrew(id);
                } else {
                    success = groundStaffService.deleteGroundStaff(id);
                }

                if (success) {
                    showAlert("Success", "Employee deleted successfully.", Alert.AlertType.INFORMATION);
                    loadEmployeeData();
                } else {
                    showAlert("Error", "Failed to delete employee.", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("No Selection", "Please select an employee to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRefresh() {
        loadEmployeeData();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.FlightCrew;
import models.FlightHistory;
import models.GroundStaff;
import models.Passenger;
import models.Ticket;
import models.TicketAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import services.PassengerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FlightHistoryDetailsController {

    // Bảng vé
    @FXML
    private TableView<TicketAdapter> ticketTable;
    @FXML
    private TableColumn<TicketAdapter, String> ticketIdColumn;
    @FXML
    private TableColumn<TicketAdapter, String> seatNumberColumn;
    @FXML
    private TableColumn<TicketAdapter, String> seatClassColumn;
    @FXML
    private TableColumn<TicketAdapter, Double> priceColumn;
    @FXML
    private TableColumn<TicketAdapter, String> passengerNameColumn;
    @FXML
    private TableColumn<TicketAdapter, String> passengerEmailColumn;
    @FXML
    private TableColumn<TicketAdapter, String> passengerPhoneColumn;

    // Bảng phi hành đoàn
    @FXML
    private TableView<FlightCrew> crewTable;
    @FXML
    private TableColumn<FlightCrew, String> crewIdColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewNameColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewAddressColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewRoleColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewSpecificRoleColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewFlightNumberColumn;
    @FXML
    private TableColumn<FlightCrew, String> crewAssignmentDateColumn;

    // Bảng nhân viên mặt đất
    @FXML
    private TableView<GroundStaff> groundStaffTable;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffIdColumn;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffNameColumn;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffAddressColumn;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffRoleColumn;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffGateColumn;
    @FXML
    private TableColumn<GroundStaff, String> groundStaffAssignmentDateColumn;

    private final PassengerService passengerService = new PassengerService();

    /**
     * Hàm set dữ liệu chuyến bay vào bảng.
     */
    public void setFlightHistory(FlightHistory flightHistory) {
        // Hiển thị dữ liệu vé
        ticketTable.setItems(parseTicketsJson(flightHistory.getTickets()));

        // Hiển thị dữ liệu phi hành đoàn
        crewTable.setItems(parseCrewJson(flightHistory.getCrew()));

        // Hiển thị dữ liệu nhân viên mặt đất
        groundStaffTable.setItems(parseGroundStaffJson(flightHistory.getGroundStaff()));
    }

    private ObservableList<TicketAdapter> parseTicketsJson(String json) {
        ObservableList<TicketAdapter> ticketAdapters = FXCollections.observableArrayList();
        Map<String, Passenger> passengerMap = new HashMap<>();

        if (json != null && !json.isEmpty()) {
            JSONArray jsonArray = new JSONArray(json);
            List<String> passengerIds = new ArrayList<>();

            // Lấy danh sách passenger_id từ JSON
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    String passengerId = obj.getString("passenger_id");
                    passengerIds.add(passengerId);
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to extract passenger_id from ticket JSON: " + obj);
                    e.printStackTrace();
                }
            }

            // Lấy thông tin hành khách qua PassengerService
            List<Passenger> passengers = passengerService.getPassengersByIds(passengerIds);

            // Lưu vào Map để ánh xạ
            for (Passenger passenger : passengers) {
                passengerMap.put(passenger.getId(), passenger);
            }

            // Tạo danh sách TicketAdapter
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    String passengerId = obj.getString("passenger_id");
                    Passenger passenger = passengerMap.getOrDefault(passengerId, null);

                    String passengerName = (passenger != null) ? passenger.getName() : "N/A";
                    String passengerEmail = (passenger != null) ? passenger.getEmail() : "N/A";
                    String passengerPhone = (passenger != null) ? passenger.getPhone() : "N/A";

                    Ticket ticket = new Ticket(
                            obj.getString("ticket_id"),
                            obj.getString("flight_number"),
                            passengerId,
                            obj.getString("seat_number"),
                            obj.getString("seat_class"),
                            obj.getDouble("price"));

                    ticketAdapters.add(new TicketAdapter(ticket, passengerName, passengerEmail, passengerPhone));
                } catch (Exception e) {
                    System.err.println("[ERROR] Parsing ticket JSON: " + obj);
                    e.printStackTrace();
                }
            }
        }
        return ticketAdapters;
    }

    private ObservableList<FlightCrew> parseCrewJson(String json) {
        ObservableList<FlightCrew> crewList = FXCollections.observableArrayList();
        if (json != null && !json.isEmpty()) {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    // Parse assignment_date từ String sang LocalDateTime
                    String assignmentDateStr = obj.optString("assignment_date", "N/A");
                    LocalDateTime assignmentDate = null;
                    if (!assignmentDateStr.equals("N/A") && !assignmentDateStr.isEmpty()) {
                        if (assignmentDateStr.contains(".")) {
                            assignmentDateStr = assignmentDateStr.split("\\.")[0]; // Loại bỏ microseconds
                        }
                        assignmentDate = LocalDateTime.parse(assignmentDateStr,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    crewList.add(new FlightCrew(
                            obj.getString("id"),
                            obj.getString("name"),
                            obj.getString("address"),
                            obj.getString("role"),
                            obj.getString("crew_role"),
                            obj.getString("flight_number"),
                            assignmentDate));
                } catch (Exception e) {
                    System.err.println("[ERROR] Parsing crew JSON: " + obj);
                    e.printStackTrace();
                }
            }
        }
        return crewList;
    }

    private ObservableList<GroundStaff> parseGroundStaffJson(String json) {
        ObservableList<GroundStaff> groundStaffList = FXCollections.observableArrayList();
        if (json != null && !json.isEmpty()) {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    // Parse assignment_date từ String sang LocalDateTime
                    String assignmentDateStr = obj.optString("assignment_date", "N/A");
                    LocalDateTime assignmentDate = null;
                    if (!assignmentDateStr.equals("N/A") && !assignmentDateStr.isEmpty()) {
                        if (assignmentDateStr.contains(".")) {
                            assignmentDateStr = assignmentDateStr.split("\\.")[0]; // Loại bỏ microseconds
                        }
                        assignmentDate = LocalDateTime.parse(assignmentDateStr,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    groundStaffList.add(new GroundStaff(
                            obj.getString("id"),
                            obj.getString("name"),
                            obj.getString("address"),
                            obj.getString("role"),
                            obj.getString("assigned_gate"),
                            assignmentDate));
                } catch (Exception e) {
                    System.err.println("[ERROR] Parsing ground staff JSON: " + obj);
                    e.printStackTrace();
                }
            }
        }
        return groundStaffList;
    }

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        // Đặt bảng co giãn theo chiều dọc
        VBox.setVgrow(ticketTable, Priority.ALWAYS);
        VBox.setVgrow(crewTable, Priority.ALWAYS);
        VBox.setVgrow(groundStaffTable, Priority.ALWAYS);
        // Liên kết cột cho bảng vé
        ticketIdColumn.setCellValueFactory(cellData -> cellData.getValue().ticketIdProperty());
        seatNumberColumn.setCellValueFactory(cellData -> cellData.getValue().seatNumberProperty());
        seatClassColumn.setCellValueFactory(cellData -> cellData.getValue().seatClassProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        passengerNameColumn.setCellValueFactory(cellData -> cellData.getValue().passengerNameProperty());
        passengerEmailColumn.setCellValueFactory(cellData -> cellData.getValue().passengerEmailProperty());
        passengerPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().passengerPhoneProperty());

        // Liên kết cột cho bảng phi hành đoàn
        crewIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        crewNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        crewAddressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        crewRoleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        crewSpecificRoleColumn.setCellValueFactory(cellData -> cellData.getValue().crewRoleProperty());
        crewFlightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        crewAssignmentDateColumn.setCellValueFactory(cellData -> cellData.getValue().assignmentDateProperty());

        // Liên kết cột cho bảng nhân viên mặt đất
        groundStaffIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        groundStaffNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        groundStaffAddressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        groundStaffRoleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        groundStaffGateColumn.setCellValueFactory(cellData -> cellData.getValue().assignedGateProperty());
        groundStaffAssignmentDateColumn.setCellValueFactory(cellData -> cellData.getValue().assignmentDateProperty());

        // Lắng nghe sự thay đổi kích thước bảng vé
        ticketTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            ticketIdColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Ticket ID
            seatNumberColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Seat Number
            seatClassColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Seat Class
            priceColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Price
            passengerNameColumn.setPrefWidth(tableWidth * 0.2); // 20% cho Passenger Name
            passengerEmailColumn.setPrefWidth(tableWidth * 0.1); // 10% cho Passenger Email
            passengerPhoneColumn.setPrefWidth(tableWidth * 0.1); // 10% cho Passenger Phone
        });

        // Lắng nghe sự thay đổi kích thước bảng phi hành đoàn
        crewTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            crewIdColumn.setPrefWidth(tableWidth * 0.1); // 10% cho Crew ID
            crewNameColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Name
            crewAddressColumn.setPrefWidth(tableWidth * 0.2); // 20% cho Address
            crewRoleColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Role
            crewSpecificRoleColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Crew Role
            crewFlightNumberColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Flight Number
            crewAssignmentDateColumn.setPrefWidth(tableWidth * 0.1); // 10% cho Assignment Date
        });

        // Lắng nghe sự thay đổi kích thước bảng nhân viên mặt đất
        groundStaffTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double tableWidth = newWidth.doubleValue();
            groundStaffIdColumn.setPrefWidth(tableWidth * 0.1); // 10% cho Staff ID
            groundStaffNameColumn.setPrefWidth(tableWidth * 0.2); // 20% cho Name
            groundStaffAddressColumn.setPrefWidth(tableWidth * 0.2); // 20% cho Address
            groundStaffRoleColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Role
            groundStaffGateColumn.setPrefWidth(tableWidth * 0.15); // 15% cho Assigned Gate
            groundStaffAssignmentDateColumn.setPrefWidth(tableWidth * 0.2); // 20% cho Assignment Date
        });
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) ticketTable.getScene().getWindow();
        stage.close();
    }
}

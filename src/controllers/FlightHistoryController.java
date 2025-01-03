package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.FlightHistory;
import services.FlightHistoryService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class FlightHistoryController {

    @FXML
    private TableView<FlightHistory> flightHistoryTable;

    @FXML
    private TableColumn<FlightHistory, String> flightNumberColumn;
    @FXML
    private TableColumn<FlightHistory, String> departureColumn;
    @FXML
    private TableColumn<FlightHistory, String> arrivalColumn;
    @FXML
    private TableColumn<FlightHistory, String> departureTimeColumn;
    @FXML
    private TableColumn<FlightHistory, String> arrivalTimeColumn;
    @FXML
    private TableColumn<FlightHistory, String> gateColumn;
    @FXML
    private TableColumn<FlightHistory, String> airplaneColumn;
    @FXML
    private TableColumn<FlightHistory, String> statusColumn;
    @FXML
    private TableColumn<FlightHistory, Void> actionColumn;

    private final FlightHistoryService flightHistoryService = new FlightHistoryService();

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        // Liên kết cột dữ liệu
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        gateColumn.setCellValueFactory(cellData -> cellData.getValue().assignedGateProperty());
        airplaneColumn.setCellValueFactory(cellData -> cellData.getValue().airplaneIdProperty());

        // Thêm nút "View Details" vào cột Actions
        addActionButtonsToTable();

        // Đặt kích thước cột theo tỷ lệ
        flightHistoryTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double tableWidth = newValue.doubleValue();
            flightNumberColumn.setPrefWidth(tableWidth * 0.1);
            departureColumn.setPrefWidth(tableWidth * 0.1);
            arrivalColumn.setPrefWidth(tableWidth * 0.1);
            departureTimeColumn.setPrefWidth(tableWidth * 0.15);
            arrivalTimeColumn.setPrefWidth(tableWidth * 0.15);
            statusColumn.setPrefWidth(tableWidth * 0.1);
            gateColumn.setPrefWidth(tableWidth * 0.1);
            airplaneColumn.setPrefWidth(tableWidth * 0.1);
            actionColumn.setPrefWidth(tableWidth * 0.1);
        });

        // Tải dữ liệu
        loadFlightHistoryData();
    }

    public void loadFlightHistoryData() {
        ObservableList<FlightHistory> historyList = FXCollections.observableArrayList();
        historyList.addAll(flightHistoryService.getAllFlightHistories());
        flightHistoryTable.setItems(historyList);
    }

    @SuppressWarnings("unused")
    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewDetailsButton = new Button("View Details");

            {
                viewDetailsButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                viewDetailsButton.setOnAction(event -> {
                    FlightHistory flightHistory = getTableView().getItems().get(getIndex());
                    handleViewDetails(flightHistory);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewDetailsButton);
                    setAlignment(javafx.geometry.Pos.CENTER); // Đặt nút vào giữa cột

                }
            }
        });

        // Đảm bảo `actionColumn` đã được thêm vào bảng
        if (!flightHistoryTable.getColumns().contains(actionColumn)) {
            flightHistoryTable.getColumns().add(actionColumn);
        }
    }

    @FXML
    private void handleViewDetails(FlightHistory flightHistory) {
        try {
            System.out.println("[DEBUG] Viewing details for flight: " + flightHistory.getFlightNumber());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FlightHistoryDetails.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình chi tiết lịch sử
            FlightHistoryDetailsController controller = loader.getController();

            // Truyền dữ liệu chi tiết chuyến bay
            controller.setFlightHistory(flightHistory);

            // Hiển thị cửa sổ mới
            Stage stage = new Stage();
            stage.setTitle("Flight Details - " + flightHistory.getFlightNumber());
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open Flight Details view:");
            e.printStackTrace();
        }
    }

}

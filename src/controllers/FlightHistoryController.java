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
    private TableColumn<FlightHistory, String> statusColumn;
    @FXML
    private TableColumn<FlightHistory, Void> actionColumn;

    private final FlightHistoryService flightHistoryService = new FlightHistoryService();

    @FXML
    private void initialize() {
        // Liên kết cột dữ liệu
        flightNumberColumn.setCellValueFactory(cellData -> cellData.getValue().flightNumberProperty());
        departureColumn.setCellValueFactory(cellData -> cellData.getValue().departureLocationProperty());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalLocationProperty());
        departureTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departureTimeProperty());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Thêm nút `View Details` vào cột Actions
        addActionButtonsToTable();

        // Tải dữ liệu ban đầu
        loadFlightHistoryData();
    }

    public void loadFlightHistoryData() {
        ObservableList<FlightHistory> historyList = FXCollections.observableArrayList();
        historyList.addAll(flightHistoryService.getAllFlightHistories());
        flightHistoryTable.setItems(historyList);
    }

    @SuppressWarnings("unused")
    private void addActionButtonsToTable() {
        TableColumn<FlightHistory, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewDetailsButton = new Button("View Details");

            {
                viewDetailsButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
                viewDetailsButton.setOnAction(event -> handleViewDetails(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewDetailsButton);
                }
            }
        });

        flightHistoryTable.getColumns().add(actionColumn);
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
            stage.show();

        } catch (IOException e) {
            System.err.println("[ERROR] Failed to open Flight Details view:");
            e.printStackTrace();
        }
    }
}

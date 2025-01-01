package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import models.FlightHistory;

public class FlightHistoryService {
    public List<FlightHistory> getAllFlightHistories() {
        String query = """
                    SELECT flight_number, departure_location, arrival_location,
                           departure_time, arrival_time, status,
                           airplane_id, assigned_gate, tickets, crew, ground_staff
                    FROM flight_history
                """;

        List<FlightHistory> historyList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                FlightHistory history = new FlightHistory(
                        resultSet.getString("flight_number"),
                        resultSet.getString("departure_location"),
                        resultSet.getString("arrival_location"),
                        resultSet.getTimestamp("departure_time").toString(),
                        resultSet.getTimestamp("arrival_time").toString(),
                        resultSet.getString("status"),
                        resultSet.getString("airplane_id"),
                        resultSet.getString("assigned_gate"),
                        resultSet.getString("tickets"),
                        resultSet.getString("crew"),
                        resultSet.getString("ground_staff"));
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyList;
    }

}

package services;

import config.DatabaseConnection;
import models.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    // Lấy tất cả chuyến bay từ cơ sở dữ liệu
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flight";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Flight flight = mapResultSetToFlight(resultSet);
                flights.add(flight);

                // Log dữ liệu chuyến bay
                System.out.printf("[FlightService] Loaded flight: %s%n", flight);
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flights from the database. Query: " + query);
            e.printStackTrace();
        }

        return flights;
    }

    // Lấy chuyến bay theo trạng thái
    public List<Flight> getFlightsByStatus(String status) {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flight WHERE status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flights by status: " + status);
            e.printStackTrace();
        }

        return flights;
    }

    // Ánh xạ từ ResultSet sang đối tượng Flight
    private Flight mapResultSetToFlight(ResultSet resultSet) throws SQLException {
        return new Flight(
                resultSet.getString("flight_number"),
                resultSet.getString("departure_location"),
                resultSet.getString("arrival_location"),
                resultSet.getString("departure_time"),
                resultSet.getString("arrival_time"),
                resultSet.getString("status"));
    }
}

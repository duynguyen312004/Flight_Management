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
                Flight flight = new Flight(
                        resultSet.getString("flight_number"),
                        resultSet.getString("departure_location"),
                        resultSet.getString("arrival_location"),
                        resultSet.getString("departure_time"),
                        resultSet.getString("arrival_time"),
                        resultSet.getString("status"));
                flights.add(flight);

                // Log dữ liệu chuyến bay
                System.out.printf(
                        "Loaded flight: [Flight Number: %s, Departure: %s, Arrival: %s, Departure Time: %s, Arrival Time: %s, Status: %s]%n",
                        resultSet.getString("flight_number"),
                        resultSet.getString("departure_location"),
                        resultSet.getString("arrival_location"),
                        resultSet.getString("departure_time"),
                        resultSet.getString("arrival_time"),
                        resultSet.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching flights from the database.");
            e.printStackTrace();
        }

        return flights;
    }
}

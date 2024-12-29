package services;

import config.DatabaseConnection;
import models.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    // 1. Lấy tất cả chuyến bay
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Flight flight = mapResultSetToFlight(resultSet);
                flights.add(flight);
                System.out.printf("[FlightService] Loaded flight: %s%n", flight);
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flights.");
            e.printStackTrace();
        }

        return flights;
    }

    // 2. Lấy chuyến bay theo trạng thái
    public List<Flight> getFlightsByStatus(String status) {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight WHERE status = ?";

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

    // 3. Lấy chuyến bay theo mã chuyến bay
    public Flight getFlightByNumber(String flightNumber) {
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToFlight(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flight by number: " + flightNumber);
            e.printStackTrace();
        }
        return null;
    }

    // 4. Thêm chuyến bay mới
    public boolean addFlight(Flight flight) {
        String query = "INSERT INTO flight (flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flight.getFlightNumber());
            statement.setString(2, flight.getDepartureLocation());
            statement.setString(3, flight.getArrivalLocation());
            statement.setString(4, flight.getDepartureTime());
            statement.setString(5, flight.getArrivalTime());
            statement.setString(6, flight.getStatus());
            statement.setString(7, flight.getAssignedGate());
            statement.setString(8, flight.getAirplaneId());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while adding new flight.");
            e.printStackTrace();
        }
        return false;
    }

    // 5. Cập nhật thông tin chuyến bay
    public boolean updateFlight(Flight flight) {
        String query = "UPDATE flight SET departure_location = ?, arrival_location = ?, departure_time = ?, arrival_time = ?, status = ?, assigned_gate = ?, airplane_id = ? WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flight.getDepartureLocation());
            statement.setString(2, flight.getArrivalLocation());
            statement.setString(3, flight.getDepartureTime());
            statement.setString(4, flight.getArrivalTime());
            statement.setString(5, flight.getStatus());
            statement.setString(6, flight.getAssignedGate());
            statement.setString(7, flight.getAirplaneId());
            statement.setString(8, flight.getFlightNumber());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while updating flight.");
            e.printStackTrace();
        }
        return false;
    }

    // 6. Xóa chuyến bay
    public boolean deleteFlight(String flightNumber) {
        String query = "DELETE FROM flight WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightNumber);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while deleting flight.");
            e.printStackTrace();
        }
        return false;
    }

    // 7. Ánh xạ từ ResultSet sang đối tượng Flight
    private Flight mapResultSetToFlight(ResultSet resultSet) throws SQLException {
        return new Flight(
                resultSet.getString("flight_number"),
                resultSet.getString("departure_location"),
                resultSet.getString("arrival_location"),
                resultSet.getString("departure_time"),
                resultSet.getString("arrival_time"),
                resultSet.getString("status"),
                resultSet.getString("assigned_gate"),
                resultSet.getString("airplane_id")
        );
    }
}

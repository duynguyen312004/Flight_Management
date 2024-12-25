package services;

import config.DatabaseConnection;
import models.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassengerService {

    // 1. Lấy tất cả các hành khách từ database
    public List<Passenger> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        String query = "SELECT id, name, email, phone FROM passenger";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                passengers.add(new Passenger(id, name, email, phone));
            }

            System.out.println("[PassengerService] Loaded " + passengers.size() + " passengers.");
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while fetching passengers.");
            e.printStackTrace();
        }

        return passengers;
    }

    public int getPassengerCountByFlight(String flightNumber) {
        String query = "SELECT COUNT(DISTINCT passenger_id) FROM ticket WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. Thêm hành khách mới vào database
    public boolean addPassenger(Passenger passenger) {
        String query = "INSERT INTO passenger (id, name, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passenger.getId());
            statement.setString(2, passenger.getName());
            statement.setString(3, passenger.getEmail());
            statement.setString(4, passenger.getPhone());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("[PassengerService] Passenger added successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while adding passenger.");
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin hành khách
    public boolean updatePassenger(Passenger passenger) {
        String query = "UPDATE passenger SET name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passenger.getName());
            statement.setString(2, passenger.getEmail());
            statement.setString(3, passenger.getPhone());
            statement.setString(4, passenger.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("[PassengerService] Passenger updated successfully.");
                return true;
            } else {
                System.out.println("[PassengerService] Passenger with ID " + passenger.getId() + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while updating passenger.");
            e.printStackTrace();
        }

        return false;
    }

    // 4. Xóa hành khách theo ID
    public boolean deletePassenger(String passengerId) {
        String query = "DELETE FROM passenger WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("[PassengerService] Passenger deleted successfully.");
                return true;
            } else {
                System.out.println("[PassengerService] Passenger with ID " + passengerId + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while deleting passenger.");
            e.printStackTrace();
        }

        return false;
    }

    // 5. Lấy thông tin vé của hành khách
    public String getTicketsByPassenger(String passengerId) {
        String query = "SELECT ticket_id, flight_number, seat_number, seat_class, price FROM ticket WHERE passenger_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder tickets = new StringBuilder();

            if (!resultSet.isBeforeFirst()) {
                return "[PassengerService] No tickets found for passenger with ID: " + passengerId;
            }

            while (resultSet.next()) {
                tickets.append(String.format(
                        "Ticket ID: %s, Flight Number: %s, Seat Number: %s, Seat Class: %s, Price: %.2f%n",
                        resultSet.getString("ticket_id"),
                        resultSet.getString("flight_number"),
                        resultSet.getString("seat_number"),
                        resultSet.getString("seat_class"),
                        resultSet.getDouble("price")));
            }

            return tickets.toString();
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while fetching tickets for passenger with ID: " + passengerId);
            e.printStackTrace();
        }

        return "[PassengerService] An error occurred while fetching tickets.";
    }
}

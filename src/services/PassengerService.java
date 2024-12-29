package services;

import config.DatabaseConnection;
import models.Passenger;
import models.Ticket;

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
                passengers.add(new Passenger(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone")
                ));
            }

            System.out.println("[PassengerService] Loaded " + passengers.size() + " passengers.");
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while fetching passengers.");
            e.printStackTrace();
        }

        return passengers;
    }

    // 2. Lấy số lượng hành khách trên chuyến bay
    public int getPassengerCountByFlight(String flightNumber) {
        String query = "SELECT COUNT(DISTINCT passenger_id) FROM ticket WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("[PassengerService] Passenger count for flight " + flightNumber + ": " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while counting passengers for flight: " + flightNumber);
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Thêm hành khách mới vào database
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
                System.out.println("[PassengerService] Passenger added successfully: " + passenger.getName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while adding passenger with ID: " + passenger.getId());
            e.printStackTrace();
        }

        return false;
    }

    // 4. Cập nhật thông tin hành khách
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
                System.out.println("[PassengerService] Passenger updated successfully: " + passenger.getId());
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

    // 5. Xóa hành khách theo ID
    public boolean deletePassenger(String passengerId) {
        String query = "DELETE FROM passenger WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("[PassengerService] Passenger deleted successfully: " + passengerId);
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

    // 6. Lấy danh sách vé của hành khách
    public List<Ticket> getTicketsByPassenger(String passengerId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_id, flight_number, seat_number, seat_class, price FROM ticket WHERE passenger_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tickets.add(new Ticket(
                        resultSet.getString("ticket_id"),
                        resultSet.getString("flight_number"),
                        passengerId,
                        resultSet.getString("seat_number"),
                        resultSet.getString("seat_class"),
                        resultSet.getDouble("price")
                ));
            }

            System.out.println("[PassengerService] Loaded " + tickets.size() + " tickets for passenger ID: " + passengerId);
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while fetching tickets for passenger ID: " + passengerId);
            e.printStackTrace();
        }

        return tickets;
    }

    // 7. Lấy danh sách hành khách theo danh sách ID
    public List<Passenger> getPassengersByIds(List<String> passengerIds) {
        List<Passenger> passengers = new ArrayList<>();
        if (passengerIds == null || passengerIds.isEmpty()) {
            System.out.println("[PassengerService] No passenger IDs provided.");
            return passengers;
        }

        // Tạo câu truy vấn động
        StringBuilder queryBuilder = new StringBuilder("SELECT id, name, email, phone FROM passenger WHERE id IN (");
        for (int i = 0; i < passengerIds.size(); i++) {
            queryBuilder.append("?");
            if (i < passengerIds.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        String query = queryBuilder.toString();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < passengerIds.size(); i++) {
                statement.setString(i + 1, passengerIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                passengers.add(new Passenger(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone")
                ));
            }

            System.out.println("[PassengerService] Loaded " + passengers.size() + " passengers for the provided IDs.");
        } catch (SQLException e) {
            System.err.println("[PassengerService] Error while fetching passengers by IDs.");
            e.printStackTrace();
        }

        return passengers;
    }
}

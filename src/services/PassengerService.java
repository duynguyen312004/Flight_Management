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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return passengers;
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
                System.out.println("Passenger added successfully.");
                return true;
            }
        } catch (SQLException e) {
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
                System.out.println("Passenger updated successfully.");
                return true;
            }
        } catch (SQLException e) {
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
                System.out.println("Passenger deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
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
    
            // Kiểm tra nếu có vé của hành khách
            if (!resultSet.isBeforeFirst()) {
                return "No tickets found for this passenger."; // Trả về thông báo nếu không tìm thấy vé
            }
    
            // Duyệt qua các vé của hành khách và lấy thông tin
            while (resultSet.next()) {
                String ticketID = resultSet.getString("ticket_id");
                String flightNumber = resultSet.getString("flight_number");
                String seatNumber = resultSet.getString("seat_number");
                String seatClass = resultSet.getString("seat_class");
                double price = resultSet.getDouble("price");
    
                // Thêm thông tin vé vào StringBuilder
                tickets.append("Ticket ID: ").append(ticketID)
                        .append(", Flight Number: ").append(flightNumber)
                        .append(", Seat Number: ").append(seatNumber)
                        .append(", Seat Class: ").append(seatClass)
                        .append(", Price: ").append(price).append("\n");
            }
    
            return tickets.toString(); // Trả về thông tin của tất cả các vé
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return "An error occurred while fetching the tickets."; // Nếu có lỗi trong quá trình truy vấn
    }
}

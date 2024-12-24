package services;

import config.DatabaseConnection;
import models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    // 1. Lấy tất cả các vé từ cơ sở dữ liệu
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_id, flight_number, passenger_id, seat_number, seat_class, price FROM ticket";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String ticketID = resultSet.getString("ticket_id");
                String flightNumber = resultSet.getString("flight_number");
                String passengerID = resultSet.getString("passenger_id");
                String seatNumber = resultSet.getString("seat_number");
                String seatClass = resultSet.getString("seat_class");
                double price = resultSet.getDouble("price");
                tickets.add(new Ticket(ticketID, flightNumber, passengerID, seatNumber, seatClass, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // 2. Thêm vé mới vào cơ sở dữ liệu
    public boolean addTicket(Ticket ticket) {
        // Kiểm tra sự tồn tại của flight_number trong bảng flight
        String checkFlightQuery = "SELECT COUNT(*) FROM flight WHERE flight_number = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkFlightQuery)) {
            
            checkStmt.setString(1, ticket.getFlightNumber());
            ResultSet resultSet = checkStmt.executeQuery();
            
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                // Nếu không tìm thấy chuyến bay tương ứng
                System.out.println("Flight number " + ticket.getFlightNumber() + " does not exist.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Nếu chuyến bay tồn tại, thực hiện thêm vé mới
        String insertQuery = "INSERT INTO ticket (ticket_id, flight_number, passenger_id, seat_number, seat_class, price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setString(1, ticket.getTicketID());
            statement.setString(2, ticket.getFlightNumber());
            statement.setString(3, ticket.getPassengerID());
            statement.setString(4, ticket.getSeatNumber());
            statement.setString(5, ticket.getSeatClass());
            statement.setDouble(6, ticket.getPrice());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Ticket added successfully.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin vé trong cơ sở dữ liệu
    public boolean updateTicket(Ticket ticket) {
        String query = "UPDATE ticket SET flight_number = ?, passenger_id = ?, seat_number = ?, seat_class = ?, price = ? WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ticket.getFlightNumber());
            statement.setString(2, ticket.getPassengerID());
            statement.setString(3, ticket.getSeatNumber());
            statement.setString(4, ticket.getSeatClass());
            statement.setDouble(5, ticket.getPrice());
            statement.setString(6, ticket.getTicketID());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Ticket updated successfully.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 4. Xóa vé khỏi cơ sở dữ liệu
    public boolean deleteTicket(String ticketID) {
        String query = "DELETE FROM ticket WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ticketID);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Ticket deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 5. Lấy vé của hành khách từ cơ sở dữ liệu
    public List<Ticket> getTicketsByPassenger(String passengerId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_id, flight_number, seat_number, seat_class, price FROM ticket WHERE passenger_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String ticketID = resultSet.getString("ticket_id");
                String flightNumber = resultSet.getString("flight_number");
                String seatNumber = resultSet.getString("seat_number");
                String seatClass = resultSet.getString("seat_class");
                double price = resultSet.getDouble("price");
                tickets.add(new Ticket(ticketID, flightNumber, passengerId, seatNumber, seatClass, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }
}